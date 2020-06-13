package kr.dizbox.util;

import kr.dizbox.domain.ResultCode;

public class CardPaymentException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private ResultCode resultCode;
	
	private String localMessage;
	
	private CardPaymentException(ResultCode resultCode) {
		this.resultCode = resultCode;
	}
	
	private CardPaymentException(ResultCode resultCode,String localMessage) {
		this.resultCode = resultCode;
		this.localMessage = localMessage;
	}
	
	public static void throwException(ResultCode resultCode) {
		throw new CardPaymentException(resultCode);
	}
	
	public static void throwException(ResultCode resultCode,String localMessage) {
		throw new CardPaymentException(resultCode,localMessage);
	}
	
	public String getResultCode() {
		return this.resultCode.getCode();
	}
	
	public String getMessage() {
		return this.localMessage==null?this.resultCode.getMessage():this.localMessage;
	}

}
