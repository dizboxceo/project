package kr.dizbox.domain;

import java.math.BigDecimal;

public class CardPaymentVO {

	private String uid;
	private String jobCls;
	private String cardInfo;
	private String instPlan;
	private BigDecimal payAmt;
	private BigDecimal vat;
	private String state;
	private String dataCls;
	private String refUid;
	private String comment;
	private String pdate;
	private String ptime;
	
	public static CardPaymentVO builder() {
		return new CardPaymentVO();
	}
	
	public CardPaymentVO uid(String uid) {
		this.uid = uid;
		return this;
	}
	
	public CardPaymentVO jobCls(String jobCls) {
		this.jobCls = jobCls;
		return this;
	}
	
	public CardPaymentVO cardInfo(String cardInfo) {
		this.cardInfo = cardInfo;
		return this;
	}
	
	public CardPaymentVO instPlan(String instPlan) {
		this.instPlan = instPlan;
		return this;
	}
	
	public CardPaymentVO payAmt(BigDecimal payAmt) {
		this.payAmt = payAmt;
		return this;
	}
	
	public CardPaymentVO vat(BigDecimal vat) {
		this.vat = vat;
		return this;
	}
	
	public CardPaymentVO state(String state) {
		this.state = state;
		return this;
	}
	
	public CardPaymentVO dataCls(String dataCls) {
		this.dataCls = dataCls;
		return this;
	}
	
	public CardPaymentVO refUid(String refUid) {
		this.refUid = refUid;
		return this;
	}
	
	public CardPaymentVO comment(String comment) {
		this.comment = comment;
		return this;
	}
	
	public CardPaymentVO pdate(String pdate) {
		this.pdate = pdate;
		return this;
	}
	
	public CardPaymentVO ptime(String ptime) {
		this.ptime = ptime;
		return this;
	}
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getJobCls() {
		return jobCls;
	}
	public void setJobCls(String jobCls) {
		this.jobCls = jobCls;
	}
	public String getCardInfo() {
		return cardInfo;
	}
	public void setCardInfo(String cardInfo) {
		this.cardInfo = cardInfo;
	}
	public String getInstPlan() {
		return instPlan;
	}
	public void setInstPlan(String instPlan) {
		this.instPlan = instPlan;
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
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getDataCls() {
		return dataCls;
	}
	public void setDataCls(String dataCls) {
		this.dataCls = dataCls;
	}
	public String getRefUid() {
		return refUid;
	}
	public void setRefUid(String refUid) {
		this.refUid = refUid;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getPdate() {
		return pdate;
	}
	public void setPdate(String pdate) {
		this.pdate = pdate;
	}
	public String getPtime() {
		return ptime;
	}
	public void setPtime(String ptime) {
		this.ptime = ptime;
	}
	
	
	
}
