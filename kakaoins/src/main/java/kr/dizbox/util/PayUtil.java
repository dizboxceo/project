package kr.dizbox.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import kr.dizbox.domain.CardPayTxtVO;
import kr.dizbox.domain.CardPaymentReqVO;
import kr.dizbox.domain.CardPaymentVO;
import kr.dizbox.domain.CardTxResult;
import kr.dizbox.domain.DataCls;
import kr.dizbox.domain.ResultCode;
import kr.dizbox.domain.State;

public class PayUtil {
	
	private static final BigDecimal VAT_BASE_AMT = BigDecimal.valueOf(11);
	private static final DecimalFormat DEC_FORMATTER = new DecimalFormat("###,###");

	public static BigDecimal calcVat(BigDecimal payamt) {
		return payamt.divide(VAT_BASE_AMT,0,RoundingMode.HALF_UP);
	}
	
	public static String refineInstPlan(int instPlanNum) {
		return String.format("%02d", instPlanNum);
	}
	
	public static String encryptCardInfo(CardPaymentReqVO cardPaymentReqVO) {
		String cardInfo = null;
		try {
			cardInfo = CodeUtil.encrypt(cardPaymentReqVO.getCardNo().concat("|").concat(cardPaymentReqVO.getValidDt()).concat("|").concat(cardPaymentReqVO.getCvc()));
		} catch (Exception e) {
			CardPaymentException.throwException(ResultCode.FAIL_ENCRYPT);
		}
		return cardInfo;
	}
	
	public static void decryptCardInfo(CardPaymentReqVO cardPaymentReqVO,String cardInfo) {
		String decCardInfoStr = null;
		try {
			decCardInfoStr = CodeUtil.decrypt(cardInfo);
			String[] decCardInfoArr = decCardInfoStr.split("\\|");
			cardPaymentReqVO.setCardNo(decCardInfoArr[0]);
			cardPaymentReqVO.setValidDt(decCardInfoArr[1]);
			cardPaymentReqVO.setCvc(decCardInfoArr[2]);
		} catch (Exception e) {
			CardPaymentException.throwException(ResultCode.FAIL_DECRYPT);
		}
	}
	
	public static String comma(BigDecimal decimal) {
		if(decimal==null || decimal.signum()==0) {
			return "0";
		}
		return DEC_FORMATTER.format(decimal.doubleValue());
	}
	
	public static String genPayTxt(CardPaymentReqVO cardPaymentReqVO,CardPaymentVO cardPaymentVO) {
		StringBuilder paymentTxtBuilder = new StringBuilder();
		paymentTxtBuilder.append(String.format("%-10s", cardPaymentVO.getDataCls()))
		                 .append(cardPaymentVO.getUid())
		                 .append(String.format("%-20s", cardPaymentReqVO.getCardNo()))
		                 .append(cardPaymentVO.getInstPlan())
		                 .append(cardPaymentReqVO.getValidDt())
		                 .append(cardPaymentReqVO.getCvc())
		                 .append(String.format("%10s", cardPaymentVO.getPayAmt().toPlainString()))
		                 .append(String.format("%010d", cardPaymentVO.getVat().intValue()))
		                 .append(String.format("%-20s", cardPaymentVO.getRefUid()==null?"":cardPaymentVO.getRefUid()))
		                 .append(String.format("%-300s",cardPaymentVO.getCardInfo()))
		                 .append(String.format("%-47s", ""))
		                 ;
		String txt = paymentTxtBuilder.toString();
		String payTxt = String.format("%4s", String.valueOf(txt.length())).concat(txt);
		if(payTxt.length()!=450) {
			CardPaymentException.throwException(ResultCode.INVALID_PAY_TXT);
		}
		return payTxt;
	}
	
	public static void setResult(CardPaymentVO cardPaymentVO,CardPayTxtVO cardPayTxtVO,boolean isVatCalculated) {
		if(CardTxResult.isFail(cardPayTxtVO.getTxResultCd())){
			cardPaymentVO.setState(State.TX_FAIL.getVal());
			cardPaymentVO.setComment(ResultCode.TX_FAIL.getMessage());
			return;
		}
		if(DataCls.isPayment(cardPaymentVO.getDataCls())) {
			cardPaymentVO.setState(State.COMPLATE.getVal());
			if(isVatCalculated) {
				cardPaymentVO.setComment(String.format("%s원 결제 성공,부가가치세(%s)자동계산", comma(cardPaymentVO.getPayAmt()),comma(cardPaymentVO.getVat())));
			}else {
				cardPaymentVO.setComment(String.format("%s(%s)원 결제 성공", comma(cardPaymentVO.getPayAmt()),comma(cardPaymentVO.getVat())));
			}
			
		}else if(DataCls.isCancel(cardPaymentVO.getDataCls())) {
			cardPaymentVO.setState(State.COMPLATE.getVal());
			if(isVatCalculated) {
				cardPaymentVO.setComment(String.format("%s취소,남은 부가가치세는(%s)원으로 자동계산되어 성공", comma(cardPaymentVO.getPayAmt()),comma(cardPaymentVO.getVat())));
			}else {
				cardPaymentVO.setComment(String.format("%s(%s)원 취소 성공", comma(cardPaymentVO.getPayAmt()),comma(cardPaymentVO.getVat())));
			}
		}
				
	}
	
	public static boolean isEmpty(String value) {
		return value==null || value.trim().equals("");
	}
	
	public static boolean isNumber(String value) {
		try {
			Double.parseDouble(value);
		}catch(Exception e) {
			return false;
		}
		return true;
	}
	
	public static String getPdate() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	}
	
	public static String getPtime() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
	}
	
}
