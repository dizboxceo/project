package kr.dizbox.service;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.dizbox.domain.CardPayTxtVO;
import kr.dizbox.domain.CardPaymentReqVO;
import kr.dizbox.domain.CardPaymentResVO;
import kr.dizbox.domain.CardPaymentVO;
import kr.dizbox.domain.CardTxResult;
import kr.dizbox.domain.DataCls;
import kr.dizbox.domain.JobCls;
import kr.dizbox.domain.ResultCode;
import kr.dizbox.domain.State;
import kr.dizbox.mapper.CardPaymentMapper;
import kr.dizbox.util.CardPaymentException;
import kr.dizbox.util.CodeUtil;
import kr.dizbox.util.PayUtil;

@Service
public class CardPaymentServiceImpl implements CardPaymentService {
	
	private static final String CARD_PAY_ONGOING = "카드결제가 진행중입니다.";
	private static final String CARD_CANCEL_ONGOING = "카드취소가 진행중입니다.";
	
	private Logger log = Logger.getLogger(CardPaymentServiceImpl.class);
	@Autowired
	private CardPaymentMapper mapper;

	/**
	 * 카드결제를 수행한다.
	 */
	@Override
	@Transactional
	public CardPaymentResVO pay(CardPaymentReqVO cardPaymentReqVO) {
		log.info("pay...."+cardPaymentReqVO);
		
		//--카드번호 정합성체크 -숫자 10~16 자리수
		if(!PayUtil.isNumber(cardPaymentReqVO.getCardNo()) || cardPaymentReqVO.getCardNo().length()<10 || cardPaymentReqVO.getCardNo().length()>16) {
			CardPaymentException.throwException(ResultCode.INVALID_CARD_NO);
		}
		//--카드유효기간 정합성체크 - 숫자 4자리
		if(!PayUtil.isNumber(cardPaymentReqVO.getValidDt()) || cardPaymentReqVO.getValidDt().length()!=4) {
			CardPaymentException.throwException(ResultCode.INVALID_VALID_DT);
		}
		//--카드CVC 정합성체크 - 숫자3자리
		if(!PayUtil.isNumber(cardPaymentReqVO.getCvc()) || cardPaymentReqVO.getCvc().length()!=3) {
			CardPaymentException.throwException(ResultCode.INVALID_CVC);
		}
		//--할부개월수 정합성체크 - 12개월이내
		if(cardPaymentReqVO.getInstPlanNum()>12) {
			CardPaymentException.throwException(ResultCode.INVALID_INST_PLAN);
		}
		//--결제금액 정합성체크 - 100원~10억 사이
		if(cardPaymentReqVO.getPayAmt().compareTo(BigDecimal.valueOf(100))<0 || cardPaymentReqVO.getPayAmt().compareTo(BigDecimal.valueOf(1000000000))>0 ) {
			CardPaymentException.throwException(ResultCode.INVALID_PAY_AMT);
		}
		
		//--카드정보 암호화처리
		String cardInfo = PayUtil.encryptCardInfo(cardPaymentReqVO);
		
		//--해당 카드번호의 기결제진행중인건 조회
		CardPaymentVO cardPaymentVOOfProcessing = mapper.readCardPaymentByCardInfo(CardPaymentVO.builder().jobCls(JobCls.PAYMENT.getVal())
				.cardInfo(cardInfo).state(State.ONGOING.getVal()));
		if(cardPaymentVOOfProcessing!=null) {//--기결제진행중인건이 있으면 예외처리(중복결제방지)
			CardPaymentException.throwException(ResultCode.PAYING);
		}
		
		boolean isVatCalculated = false;//--부가세자동계산여부
		if(PayUtil.isEmpty(cardPaymentReqVO.getInputVat())) {//--부가세없는경우 자동계산
			cardPaymentReqVO.setVat(PayUtil.calcVat(cardPaymentReqVO.getPayAmt()));
			isVatCalculated = true;
		}else {
			if(!PayUtil.isNumber(cardPaymentReqVO.getInputVat())) {//--인풋부가세가 숫자가 아닌경우 예외처리
				CardPaymentException.throwException(ResultCode.INVALID_VAT);
			}
			cardPaymentReqVO.setVat(new BigDecimal(cardPaymentReqVO.getInputVat()));
		}
		
		//--DB 데이터입력위한 카드결제VO 구성
		CardPaymentVO cardPaymentVO = CardPaymentVO.builder().uid(CodeUtil.genUid())
				 .jobCls(JobCls.PAYMENT.getVal())
				 .cardInfo(cardInfo)
	             .instPlan(PayUtil.refineInstPlan(cardPaymentReqVO.getInstPlanNum()))
	             .payAmt(cardPaymentReqVO.getPayAmt())
	             .vat(cardPaymentReqVO.getVat())
	             .state(State.ONGOING.getVal())
	             .dataCls(DataCls.PAYMENT.getVal())
	             .refUid("")
	             .comment(CARD_PAY_ONGOING)
	             .pdate(PayUtil.getPdate())
	             .ptime(PayUtil.getPtime());
		
		//--카드사전송전 DB 인서트 - 처리상태 9(진행중)
		mapper.insertCardPayment(cardPaymentVO);
		
		//--카드사 전송서비스 호출
		CardPayTxtVO payTxtVO  = this.callPay(cardPaymentReqVO, cardPaymentVO);
		PayUtil.setResult(cardPaymentVO,payTxtVO,isVatCalculated);
		if(CardTxResult.isFail(payTxtVO.getTxResultCd())) {//--카드사 전송오류가 발생한경우 예외처리
			mapper.updateCardPayment(cardPaymentVO);//--예외처리전 DB처리상태 업데이트
			CardPaymentException.throwException(ResultCode.TX_FAIL);
		}
		
		/*
		 * 중복결제테스트를 위해 요청 테스트토근 Y 인경우 상태를 진행중 9 로 업데이트한다.
		 */
		if("Y".equals(cardPaymentReqVO.getTestToken())){
			cardPaymentVO.setState(State.ONGOING.getVal());
			cardPaymentVO.setComment(CARD_PAY_ONGOING);
		}
		//--최종 처리결과 DB업데이트
		mapper.updateCardPayment(cardPaymentVO);
		//--리턴정보구성
		return CardPaymentResVO.builder()
				.jobCls(JobCls.PAYMENT.getVal())
				.payAmt(cardPaymentVO.getPayAmt())
				.vat(cardPaymentVO.getVat())
				.uid(cardPaymentVO.getUid())
				.payTxt(payTxtVO.getPayTxt())
				.resultCd(ResultCode.OK.getCode())
				.resultMsg(cardPaymentVO.getComment());
	}
	
	/**
	 * 카드사 전송서비스를 수행한다.
	 * @param cardPaymentReqVO 요청VO
	 * @param cardPaymentVO 처리정보VO
	 * @return 처리결과 VO
	 */
	@Transactional
	private CardPayTxtVO callPay(CardPaymentReqVO cardPaymentReqVO,CardPaymentVO cardPaymentVO) {
		CardPayTxtVO cardPayTxtVO = CardPayTxtVO.builder().payTxt(PayUtil.genPayTxt(cardPaymentReqVO, cardPaymentVO));
		mapper.insertPayTxt(cardPayTxtVO);
		cardPayTxtVO.setTxResultCd(CardTxResult.SUCCESS.getVal()); //--카드사통신결과 무조건 정상처리
		return cardPayTxtVO;
	}

	/**
	 * 결제 취소를 수행한다.
	 */
	@Override
	public CardPaymentResVO cancel(CardPaymentReqVO cardPaymentReqVO) {
		log.info("cancel...."+cardPaymentReqVO);
		
		if(PayUtil.isEmpty(cardPaymentReqVO.getUid())) {//--관리번호체크
			CardPaymentException.throwException(ResultCode.NOT_UID);
		}
		
		if(cardPaymentReqVO.getPayAmt().signum()==0) {//--취소금액체크
			CardPaymentException.throwException(ResultCode.NOT_CANCEL_AMT);
		}
		
		//--해당관리번호에 카드결제내역을 조회한다.
		CardPaymentVO cardPaymentVOOfPay = mapper.readCardPayment(cardPaymentReqVO.getUid());
		
		if(cardPaymentVOOfPay==null) {//--결제내역이 없는경우 예외처리
			CardPaymentException.throwException(ResultCode.NO_DATA,String.format(ResultCode.NO_DATA.getMessage(), cardPaymentReqVO.getUid()));
		}else {
			if(!cardPaymentVOOfPay.getJobCls().equals(JobCls.PAYMENT.getVal())){//--처리대상관리번호 결제내역이 아닌경우 예외처리
				CardPaymentException.throwException(ResultCode.NO_DATA,String.format(ResultCode.NO_DATA.getMessage(), cardPaymentReqVO.getUid()));
			}
			if(!State.isComplate(cardPaymentVOOfPay.getState())) {//--처리대상관리번호 결제내역이 완료건이 아닌경우 예외처리
				CardPaymentException.throwException(ResultCode.NO_DATA,String.format(ResultCode.NO_DATA.getMessage(), cardPaymentReqVO.getUid()));
			}
		}
		
		//--취소금액이 결제금액초과하는지 체크
		if(cardPaymentReqVO.getPayAmt().compareTo(cardPaymentVOOfPay.getPayAmt())>0) {
			CardPaymentException.throwException(ResultCode.INVALID_CANCEL_AMT);
		}
		
		//--해당 카드번호의 기전체취소 진행중인건 조회
		CardPaymentVO cardPaymentVOOfTotCanceling = mapper.readCardPaymentByCardInfo(CardPaymentVO.builder().jobCls(JobCls.TOT_CANCEL.getVal())
				.cardInfo(cardPaymentVOOfPay.getCardInfo()).state(State.ONGOING.getVal()));
		if(cardPaymentVOOfTotCanceling!=null) {//--전체취소 중복진행중인건이 있으면 예외처리
			CardPaymentException.throwException(ResultCode.TOT_CANCELING);
		}
		//--해당 카드번호의 기부분취소 진행중인건 조회
		CardPaymentVO cardPaymentVOOfPartialCanceling = mapper.readCardPaymentByCardInfo(CardPaymentVO.builder().jobCls(JobCls.PARTIAL_CANCEL.getVal())
				.cardInfo(cardPaymentVOOfPay.getCardInfo()).state(State.ONGOING.getVal()));
		if(cardPaymentVOOfPartialCanceling!=null) {//--부분취소 중복진행중인건이 있으면 예외처리
			CardPaymentException.throwException(ResultCode.PARTIAL_CANCELING);
		}
		
		boolean isVatCalculated = false;//--부가세자동계산여부
		if(PayUtil.isEmpty(cardPaymentReqVO.getInputVat())) {//--부가세없는경우 자동계산
			cardPaymentReqVO.setVat(PayUtil.calcVat(cardPaymentReqVO.getPayAmt()));
			isVatCalculated = true;
		}else {
			if(!PayUtil.isNumber(cardPaymentReqVO.getInputVat())) {
				CardPaymentException.throwException(ResultCode.INVALID_VAT);
			}
			cardPaymentReqVO.setVat(new BigDecimal(cardPaymentReqVO.getInputVat()));
		}
		
		
		JobCls jobCls = null;//--작업구분및 취소금액 체크
		//--기취소내역정보 조회
		CardPaymentVO cancelCardPaymentVO = mapper.readCancelCardPayment(cardPaymentReqVO.getUid());
		if(cancelCardPaymentVO==null) {//--기취소내역정보가 없는경우
			if(cardPaymentReqVO.getPayAmt().compareTo(cardPaymentVOOfPay.getPayAmt())==0) {
				//--결제금액과 취소요청금액이 동일한경우 전체취소프로세스
				jobCls = JobCls.TOT_CANCEL;//--작업구분 전체취소 세팅
				if(cardPaymentReqVO.getVat().compareTo(cardPaymentVOOfPay.getVat())>0) {
					//--요청부가세가 결제부가세보다 큰경우 예외처리
					CardPaymentException.throwException(ResultCode.INVALID_PARTIAL_CANCEL_VAT
							,String.format(ResultCode.INVALID_PARTIAL_CANCEL_VAT.getMessage()
									, PayUtil.comma(cardPaymentReqVO.getPayAmt())
									, PayUtil.comma(cardPaymentReqVO.getVat())));
				}else {
					//--그외는 결제시부가세로 세팅
					cardPaymentReqVO.setVat(cardPaymentVOOfPay.getVat());
				}
			}else {//--결제금액과 취소요청금액이 다른경우
				jobCls = JobCls.PARTIAL_CANCEL;//--작업구분 부분취소
				if(cardPaymentReqVO.getVat().compareTo(cardPaymentVOOfPay.getVat())>0) {
					//--요청부가세가 결제부가세보다 큰경우 예외처리
					CardPaymentException.throwException(ResultCode.INVALID_PARTIAL_CANCEL_VAT
							,String.format(ResultCode.INVALID_PARTIAL_CANCEL_VAT.getMessage()
									, PayUtil.comma(cardPaymentReqVO.getPayAmt())
									, PayUtil.comma(cardPaymentReqVO.getVat())));
				}
			}
		}else{//--기취소내역이 있는경우
			jobCls = JobCls.PARTIAL_CANCEL; //--작업구분 부분취소 세팅
			//--잔여결제금액 계산 = 결제금액-기취소금액
			BigDecimal remainPayAmt = cardPaymentVOOfPay.getPayAmt().subtract(cancelCardPaymentVO.getPayAmt());
			//--잔여부가세 계산 = 결제금액-기취소부가세
			BigDecimal remainVat = cardPaymentVOOfPay.getVat().subtract(cancelCardPaymentVO.getVat());
			
			if(remainPayAmt.signum()==0) {//--잔여결제금액이 없는경우 취소완료 에외처리
				CardPaymentException.throwException(ResultCode.ALEADY_CANCEL);
			}
			
			//--취소요청금액이 잔여결제금액보다 큰경우 예외처리
			if(cardPaymentReqVO.getPayAmt().compareTo(remainPayAmt)>0) {
				CardPaymentException.throwException(ResultCode.INVALID_PARTIAL_CANCEL_AMT
						,String.format(ResultCode.INVALID_PARTIAL_CANCEL_AMT.getMessage(), PayUtil.comma(cardPaymentReqVO.getPayAmt())));
			}
			
			//--계산된 부가가치세가 잔여 부가가치세보다 큰경우 잔여부가가치세로 세팅
			if(isVatCalculated && cardPaymentReqVO.getVat().compareTo(remainVat)>0) {
				cardPaymentReqVO.setVat(remainVat);
			}
			
			//--요청부가세가 잔여부가세보다 큰경우 예외처리
			if(cardPaymentReqVO.getVat().compareTo(remainVat)>0) {
				CardPaymentException.throwException(ResultCode.INVALID_PARTIAL_CANCEL_VAT
						,String.format(ResultCode.INVALID_PARTIAL_CANCEL_VAT.getMessage()
								, PayUtil.comma(cardPaymentReqVO.getPayAmt())
								, PayUtil.comma(cardPaymentReqVO.getVat())));
			}
		}
		
		//--카드정보 복호화
		PayUtil.decryptCardInfo(cardPaymentReqVO, cardPaymentVOOfPay.getCardInfo());
		
		//--DB 데이터입력위한 카드결제VO 구성
		CardPaymentVO cardPaymentVO = CardPaymentVO.builder().uid(CodeUtil.genUid())
				 .jobCls(jobCls.getVal())
				 .cardInfo(cardPaymentVOOfPay.getCardInfo())
	             .instPlan(cardPaymentVOOfPay.getInstPlan())
	             .payAmt(cardPaymentReqVO.getPayAmt())
	             .vat(cardPaymentReqVO.getVat())
	             .state(State.ONGOING.getVal())
	             .dataCls(DataCls.CANCEL.getVal())
	             .refUid(cardPaymentReqVO.getUid())
	             .comment(CARD_CANCEL_ONGOING)
	             .pdate(PayUtil.getPdate())
	             .ptime(PayUtil.getPtime());
		//--카드사전송전 DB 인서트 - 처리상태 9(진행중)
		mapper.insertCardPayment(cardPaymentVO);
		
		//--카드사 전송서비스 호출
		CardPayTxtVO payTxtVO  = this.callPay(cardPaymentReqVO, cardPaymentVO);
		PayUtil.setResult(cardPaymentVO,payTxtVO,isVatCalculated);
		if(CardTxResult.isFail(payTxtVO.getTxResultCd())) {//--카드사 전송오류가 발생한경우 예외처리
			mapper.updateCardPayment(cardPaymentVO);//--예외처리전 DB처리상태 업데이트
			CardPaymentException.throwException(ResultCode.TX_FAIL);
		}
		
		/*
		 * 중복결제테스트를 위해 요청 테스트토근 Y 인경우 상태를 진행중 9 로 업데이트한다.
		 */
		if("Y".equals(cardPaymentReqVO.getTestToken())){
			cardPaymentVO.setState(State.ONGOING.getVal());
			cardPaymentVO.setComment(CARD_CANCEL_ONGOING);
		}
		//--최종 처리결과 DB업데이트
		mapper.updateCardPayment(cardPaymentVO);
		//--리턴정보구성
		return CardPaymentResVO.builder()
				.jobCls(jobCls.getVal())
				.payAmt(cardPaymentVO.getPayAmt())
				.vat(cardPaymentVO.getVat())
				.uid(cardPaymentVO.getUid())
				.payTxt(payTxtVO.getPayTxt())
				.resultCd(ResultCode.OK.getCode())
				.resultMsg(cardPaymentVO.getComment());
	}

	/**
	 * 카드결제내역정보 조회 서비스
	 */
	@Override
	public CardPaymentResVO find(CardPaymentReqVO cardPaymentReqVO) {
		log.info("cancel...."+cardPaymentReqVO);
		
		//--관리번호가 없는경우 예외처리
		if(PayUtil.isEmpty(cardPaymentReqVO.getUid())) {
			CardPaymentException.throwException(ResultCode.NOT_UID);
		}
		
		//--데이터조회
		CardPaymentVO cardPaymentVOOfPay = mapper.readCardPayment(cardPaymentReqVO.getUid());
		
		//--조회결과 없는경우 예외처리
		if(cardPaymentVOOfPay==null) {
			CardPaymentException.throwException(ResultCode.NO_DATA,String.format(ResultCode.NO_DATA.getMessage(), cardPaymentReqVO.getUid()));
		}
		
		//--카드정보 복호화
		PayUtil.decryptCardInfo(cardPaymentReqVO, cardPaymentVOOfPay.getCardInfo());
		//--리턴정보구성
		return CardPaymentResVO.builder().uid(cardPaymentVOOfPay.getUid())
				.dataCls(cardPaymentVOOfPay.getDataCls())
				.cardNo(cardPaymentReqVO.getCardNo())
				.validDt(cardPaymentReqVO.getValidDt())
				.cvc(cardPaymentReqVO.getCvc())
				.instPlanNum(Integer.parseInt(cardPaymentVOOfPay.getInstPlan()))
				.payAmt(cardPaymentVOOfPay.getPayAmt())
				.vat(cardPaymentVOOfPay.getVat())
				.comment(cardPaymentVOOfPay.getComment())
				.resultCd(ResultCode.OK.getCode())
				.resultMsg(ResultCode.OK.getMessage())
				;
	}

}
