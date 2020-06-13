package kr.dizbox.domain;

public enum JobCls {
	PAYMENT("00"),TOT_CANCEL("01"),PARTIAL_CANCEL("02");
	private String val;
	private JobCls(String val) {
		this.val = val;
	}
	
	public String getVal() {
		return val;
	}
	
	public static boolean isPayment(String val) {
		return PAYMENT.val.equals(val);
	}
	
	public static boolean isTotCancel(String val) {
		return TOT_CANCEL.val.equals(val);
	}
	
	public static boolean isPartialCancel(String val) {
		return PARTIAL_CANCEL.val.equals(val);
	}
}
