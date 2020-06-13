package kr.dizbox.domain;

import java.math.BigDecimal;

public class CardPaymentReqVO {

	private String testToken;
	private String cardNo;
	private String validDt;
	private String cvc;
	private int instPlanNum;
	private String inputVat;
	private BigDecimal payAmt;
	private BigDecimal vat;
	private String uid;
	
	
	public static CardPaymentReqVO builder() {
		return new CardPaymentReqVO();
	}
	
	public CardPaymentReqVO cardNo(String cardNo) {
		this.cardNo = cardNo;
		return this;
	}
	
	public CardPaymentReqVO cvc(String cvc) {
		this.cvc = cvc;
		return this;
	}
	
	public CardPaymentReqVO instPlanNum(int instPlanNum) {
		this.instPlanNum = instPlanNum;
		return this;
	}
	
	public CardPaymentReqVO inputVat(String inputVat) {
		this.inputVat = inputVat;
		return this;
	}
	
	public CardPaymentReqVO validDt(String validDt) {
		this.validDt = validDt;
		return this;
	}
	
	public CardPaymentReqVO payAmt(BigDecimal payAmt) {
		this.payAmt = payAmt;
		return this;
	}
	
	public CardPaymentReqVO vat(BigDecimal vat) {
		this.vat = vat;
		return this;
	}
	
	public CardPaymentReqVO uid(String uid) {
		this.uid = uid;
		return this;
	}
	
	
	public String getTestToken() {
		return testToken;
	}

	public void setTestToken(String testToken) {
		this.testToken = testToken;
	}
	
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getValidDt() {
		return validDt;
	}
	public void setValidDt(String validDt) {
		this.validDt = validDt;
	}
	public String getCvc() {
		return cvc;
	}
	public void setCvc(String cvc) {
		this.cvc = cvc;
	}
	public int getInstPlanNum() {
		return instPlanNum;
	}
	public void setInstPlanNum(int instPlanNum) {
		this.instPlanNum = instPlanNum;
	}
	public String getInputVat() {
		return inputVat;
	}
	public void setInputVat(String inputVat) {
		this.inputVat = inputVat;
	}

	public BigDecimal getPayAmt() {
		return payAmt;
	}
	public void setPayAmt(BigDecimal payAmt) {
		this.payAmt = payAmt;
	}
	public BigDecimal getVat() {
		return vat;
	}
	public void setVat(BigDecimal vat) {
		this.vat = vat;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}

	
	
	
	
	
}
