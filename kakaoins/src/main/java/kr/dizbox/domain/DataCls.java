package kr.dizbox.domain;

public enum DataCls {
	PAYMENT("PAYMENT"),CANCEL("CANCEL");
	private String val;
	private DataCls(String val) {
		this.val = val;
	}
	
	public String getVal() {
		return val;
	}
	
	public static boolean isPayment(String val) {
		return PAYMENT.val.equals(val);
	}
	
	public static boolean isCancel(String val) {
		return CANCEL.val.equals(val);
	}
}
