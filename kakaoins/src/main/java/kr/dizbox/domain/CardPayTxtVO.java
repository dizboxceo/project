package kr.dizbox.domain;

public class CardPayTxtVO {

	private String payTxt;
	private String txResultCd;
	
	public static CardPayTxtVO builder() {
		return new CardPayTxtVO();
	}
	
	public CardPayTxtVO payTxt(String payTxt) {
		this.payTxt = payTxt;
		return this;
	}
	
	public CardPayTxtVO txResultCd(String txResultCd) {
		this.txResultCd = txResultCd;
		return this;
	}
	
	public String getPayTxt() {
		return payTxt;
	}
	public void setPayTxt(String payTxt) {
		this.payTxt = payTxt;
	}
	public String getTxResultCd() {
		return txResultCd;
	}
	public void setTxResultCd(String txResultCd) {
		this.txResultCd = txResultCd;
	}
	
	
	
}
