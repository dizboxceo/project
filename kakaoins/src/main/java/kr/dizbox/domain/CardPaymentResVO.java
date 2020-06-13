package kr.dizbox.domain;

import java.math.BigDecimal;

public class CardPaymentResVO {
	
	private String uid;
	private String resultCd;
	private String resultMsg;
	private String comment;
	private String jobCls;
	private String dataCls;
	private String cardNo;
	private String validDt;
	private String cvc;
	private int instPlanNum;
	private boolean isVatCalculated;
	private BigDecimal payAmt;
	private BigDecimal vat;
	private String payTxt;
	
	
	public static CardPaymentResVO builder() {
		return new CardPaymentResVO();
	}
	
	public CardPaymentResVO uid(String uid) {
		this.uid = uid;
		return this;
	}
	
	public CardPaymentResVO resultCd(String resultCd) {
		this.resultCd = resultCd;
		return this;
	}
	
	public CardPaymentResVO resultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
		return this;
	}
	
	public CardPaymentResVO comment(String comment) {
		this.comment = comment;
		return this;
	}
	
	public CardPaymentResVO jobCls(String jobCls) {
		this.jobCls = jobCls;
		return this;
	}
	
	public CardPaymentResVO dataCls(String dataCls) {
		this.dataCls = dataCls;
		return this;
	}
	
	public CardPaymentResVO cardNo(String cardNo) {
		this.cardNo = cardNo;
		return this;
	}
	
	public CardPaymentResVO validDt(String validDt) {
		this.validDt = validDt;
		return this;
	}
	
	public CardPaymentResVO cvc(String cvc) {
		this.cvc = cvc;
		return this;
	}
	
	public CardPaymentResVO instPlanNum(int instPlanNum) {
		this.instPlanNum = instPlanNum;
		return this;
	}
	
	public CardPaymentResVO isVatCalculated(boolean isVatCalculated) {
		this.isVatCalculated = isVatCalculated;
		return this;
	}
	
	public CardPaymentResVO payAmt(BigDecimal payAmt) {
		this.payAmt = payAmt;
		return this;
	}
	
	public CardPaymentResVO vat(BigDecimal vat) {
		this.vat = vat;
		return this;
	}
	
	public CardPaymentResVO payTxt(String payTxt) {
		this.payTxt = payTxt;
		return this;
	}
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getResultCd() {
		return resultCd;
	}
	public void setResultCd(String resultCd) {
		this.resultCd = resultCd;
	}
	public String getResultMsg() {
		return resultMsg;
	}
	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getJobCls() {
		return jobCls;
	}
	public void setJobCls(String jobCls) {
		this.jobCls = jobCls;
	}
	public String getDataCls() {
		return dataCls;
	}
	public void setDataCls(String dataCls) {
		this.dataCls = dataCls;
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
	public boolean getIsVatCalculated() {
		return isVatCalculated;
	}
	public void setVatCalculated(boolean isVatCalculated) {
		this.isVatCalculated = isVatCalculated;
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
	public String getPayTxt() {
		return payTxt;
	}
	public void setPayTxt(String payTxt) {
		this.payTxt = payTxt;
	}
	
	
}
