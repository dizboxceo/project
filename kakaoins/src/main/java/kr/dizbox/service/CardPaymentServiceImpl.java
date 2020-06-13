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
		
		if(!PayUtil.isNumber(cardPaymentReqVO.getCardNo()) || cardPaymentReqVO.getCardNo().length()<10 || cardPaymentReqVO.getCardNo().length()>16) {
			CardPaymentException.throwException(ResultCode.INVALID_CARD_NO);
		}
		if(!PayUtil.isNumber(cardPaymentReqVO.getValidDt()) || cardPaymentReqVO.getValidDt().length()!=4) {
			CardPaymentException.throwException(ResultCode.INVALID_VALID_DT);
		}
		if(!PayUtil.isNumber(cardPaymentReqVO.getCvc()) || cardPaymentReqVO.getCvc().length()!=3) {
			CardPaymentException.throwException(ResultCode.INVALID_CVC);
		}
		if(cardPaymentReqVO.getInstPlanNum()>12) {
			CardPaymentException.throwException(ResultCode.INVALID_INST_PLAN);
		}
		if(cardPaymentReqVO.getPayAmt().compareTo(BigDecimal.valueOf(100))<0 || cardPaymentReqVO.getPayAmt().compareTo(BigDecimal.valueOf(1000000000))>0 ) {
			CardPaymentException.throwException(ResultCode.INVALID_PAY_AMT);
		}
		
		String cardInfo = PayUtil.encryptCardInfo(cardPaymentReqVO);
		
		CardPaymentVO cardPaymentVOOfProcessing = mapper.readCardPaymentByCardInfo(CardPaymentVO.builder().jobCls(JobCls.PAYMENT.getVal())
				.cardInfo(cardInfo).state(State.ONGOING.getVal()));
		
		if(cardPaymentVOOfProcessing!=null) {
			CardPaymentException.throwException(ResultCode.PAYING);
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
		
		CardPaymentVO cardPaymentVO = CardPaymentVO.builder().uid(CodeUtil.genUid())
				 .jobCls(JobCls.PAYMENT.getVal())
				 .cardInfo(cardInfo)
	             .instPlan(PayUtil.refineInstPlan(cardPaymentReqVO.getInstPlanNum()))
	             .payAmt(cardPaymentReqVO.getPayAmt())
	             .vat(cardPaymentReqVO.getVat())
	             .state(State.ONGOING.getVal())
	             .dataCls(DataCls.PAYMENT.getVal())
	             .refUid("")
	             .comment("")
	             .pdate(PayUtil.getPdate())
	             .ptime(PayUtil.getPtime());
		
		mapper.insertCardPayment(cardPaymentVO);
		
		CardPayTxtVO payTxtVO  = this.callPay(cardPaymentReqVO, cardPaymentVO);
		PayUtil.setResult(cardPaymentVO,payTxtVO,isVatCalculated);
		
		/*
		 * 중복결제테스트를 위해 요청 테스트토근 Y 인경우 상태를 진행중 9 로 업데이트한다.
		 */
		if("Y".equals(cardPaymentReqVO.getTestToken())){
			cardPaymentVO.setState(State.ONGOING.getVal());
			cardPaymentVO.setComment("");
		}
		
		mapper.updateCardPayment(cardPaymentVO);
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
		cardPayTxtVO.setTxResultCd("00"); //--카드사통신결과 무조건 정상처리
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
		
		CardPaymentVO cardPaymentVOOfPay = mapper.readCardPayment(cardPaymentReqVO.getUid());
		
		if(cardPaymentVOOfPay==null) {//--결제내역이 없는경우 예외처리
			CardPaymentException.throwException(ResultCode.NO_DATA,String.format(ResultCode.NO_DATA.getMessage(), cardPaymentReqVO.getUid()));
		}else {
			if(!cardPaymentVOOfPay.getJobCls().equals(JobCls.PAYMENT.getVal())){//--처리대상관리번호데이터가 결제내역이 아닌경우 예외처리
				CardPaymentException.throwException(ResultCode.NO_DATA,String.format(ResultCode.NO_DATA.getMessage(), cardPaymentReqVO.getUid()));
			}
		}
		
		if(cardPaymentReqVO.getPayAmt().compareTo(cardPaymentVOOfPay.getPayAmt())>0) {//--취소금액이 결제금액초과하는지 체크
			CardPaymentException.throwException(ResultCode.INVALID_CANCEL_AMT);
		}
		
		CardPaymentVO cardPaymentVOOfTotCanceling = mapper.readCardPaymentByCardInfo(CardPaymentVO.builder().jobCls(JobCls.TOT_CANCEL.getVal())
				.cardInfo(cardPaymentVOOfPay.getCardInfo()).state(State.ONGOING.getVal()));
		
		if(cardPaymentVOOfTotCanceling!=null) {//--전체취소 중복진행체크
			CardPaymentException.throwException(ResultCode.TOT_CANCELING);
		}
		
		CardPaymentVO cardPaymentVOOfPartialCanceling = mapper.readCardPaymentByCardInfo(CardPaymentVO.builder().jobCls(JobCls.PARTIAL_CANCEL.getVal())
				.cardInfo(cardPaymentVOOfPay.getCardInfo()).state(State.ONGOING.getVal()));
		
		if(cardPaymentVOOfPartialCanceling!=null) {//--부분취소 중복진행체크
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
		CardPaymentVO cancelCardPaymentVO = mapper.readCancelCardPayment(cardPaymentReqVO.getUid());
		if(cancelCardPaymentVO==null) {
			if(cardPaymentReqVO.getPayAmt().compareTo(cardPaymentVOOfPay.getPayAmt())==0) {
				jobCls = JobCls.TOT_CANCEL;
				if(cardPaymentReqVO.getVat().compareTo(cardPaymentVOOfPay.getVat())>0) {
					CardPaymentException.throwException(ResultCode.INVALID_PARTIAL_CANCEL_VAT
							,String.format(ResultCode.INVALID_PARTIAL_CANCEL_VAT.getMessage()
									, PayUtil.comma(cardPaymentReqVO.getPayAmt())
									, PayUtil.comma(cardPaymentReqVO.getVat())));
				}else {
					cardPaymentReqVO.setVat(cardPaymentVOOfPay.getVat());
				}
			}else {
				jobCls = JobCls.PARTIAL_CANCEL;
				if(cardPaymentReqVO.getVat().compareTo(cardPaymentVOOfPay.getVat())>0) {
					CardPaymentException.throwException(ResultCode.INVALID_PARTIAL_CANCEL_VAT
							,String.format(ResultCode.INVALID_PARTIAL_CANCEL_VAT.getMessage()
									, PayUtil.comma(cardPaymentReqVO.getPayAmt())
									, PayUtil.comma(cardPaymentReqVO.getVat())));
				}
			}
		}else{
			jobCls = JobCls.PARTIAL_CANCEL;
			BigDecimal remainPayAmt = cardPaymentVOOfPay.getPayAmt().subtract(cancelCardPaymentVO.getPayAmt());
			BigDecimal remainVat = cardPaymentVOOfPay.getVat().subtract(cancelCardPaymentVO.getVat());
			
			if(remainPayAmt.signum()==0) {
				CardPaymentException.throwException(ResultCode.ALEADY_CANCEL);
			}
			
			
			if(cardPaymentReqVO.getPayAmt().compareTo(remainPayAmt)>0) {
				CardPaymentException.throwException(ResultCode.INVALID_PARTIAL_CANCEL_AMT
						,String.format(ResultCode.INVALID_PARTIAL_CANCEL_AMT.getMessage(), PayUtil.comma(cardPaymentReqVO.getPayAmt())));
			}
			
			if(isVatCalculated && cardPaymentReqVO.getVat().compareTo(remainVat)>0) {//--계산된 부가가치세가 잔여 부가가치세보다 큰경우 잔여부가가치세로 세팅
				cardPaymentReqVO.setVat(remainVat);
			}
			
			
			if(cardPaymentReqVO.getVat().compareTo(remainVat)>0) {
				CardPaymentException.throwException(ResultCode.INVALID_PARTIAL_CANCEL_VAT
						,String.format(ResultCode.INVALID_PARTIAL_CANCEL_VAT.getMessage()
								, PayUtil.comma(cardPaymentReqVO.getPayAmt())
								, PayUtil.comma(cardPaymentReqVO.getVat())));
			}
			
			
		}
		
		
		PayUtil.decryptCardInfo(cardPaymentReqVO, cardPaymentVOOfPay.getCardInfo());
		
		CardPaymentVO cardPaymentVO = CardPaymentVO.builder().uid(CodeUtil.genUid())
				 .jobCls(jobCls.getVal())
				 .cardInfo(cardPaymentVOOfPay.getCardInfo())
	             .instPlan(cardPaymentVOOfPay.getInstPlan())
	             .payAmt(cardPaymentReqVO.getPayAmt())
	             .vat(cardPaymentReqVO.getVat())
	             .state(State.ONGOING.getVal())
	             .dataCls(DataCls.CANCEL.getVal())
	             .refUid(cardPaymentReqVO.getUid())
	             .comment("")
	             .pdate(PayUtil.getPdate())
	             .ptime(PayUtil.getPtime());
		
		mapper.insertCardPayment(cardPaymentVO);
		CardPayTxtVO payTxtVO  = this.callPay(cardPaymentReqVO, cardPaymentVO);
		PayUtil.setResult(cardPaymentVO,payTxtVO,isVatCalculated);
		
		/*
		 * 중복결제테스트를 위해 요청 테스트토근 Y 인경우 상태를 진행중 9 로 업데이트한다.
		 */
		if("Y".equals(cardPaymentReqVO.getTestToken())){
			cardPaymentVO.setState(State.ONGOING.getVal());
			cardPaymentVO.setComment("");
		}
		
		mapper.updateCardPayment(cardPaymentVO);
		return CardPaymentResVO.builder()
				.jobCls(jobCls.getVal())
				.payAmt(cardPaymentVO.getPayAmt())
				.vat(cardPaymentVO.getVat())
				.uid(cardPaymentVO.getUid())
				.payTxt(payTxtVO.getPayTxt())
				.resultCd(ResultCode.OK.getCode())
				.resultMsg(cardPaymentVO.getComment());
	}

	@Override
	public CardPaymentResVO find(CardPaymentReqVO cardPaymentReqVO) {
		log.info("cancel...."+cardPaymentReqVO);
		
		if(PayUtil.isEmpty(cardPaymentReqVO.getUid())) {
			CardPaymentException.throwException(ResultCode.NOT_UID);
		}
		
		CardPaymentVO cardPaymentVOOfPay = mapper.readCardPayment(cardPaymentReqVO.getUid());
		
		if(cardPaymentVOOfPay==null) {
			CardPaymentException.throwException(ResultCode.NO_DATA,String.format(ResultCode.NO_DATA.getMessage(), cardPaymentReqVO.getUid()));
		}
		
		PayUtil.decryptCardInfo(cardPaymentReqVO, cardPaymentVOOfPay.getCardInfo());
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
