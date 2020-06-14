package kr.dizbox.domain;

public enum CardTxResult {
	SUCCESS("00"),FAIL("01");
	private String val;
	private CardTxResult(String val) {
		this.val = val;
	}
	
	public String getVal() {
		return val;
	}
	
	public static boolean isSuccess(String val) {
		return SUCCESS.val.equals(val);
	}
	
	public static boolean isFail(String val) {
		return FAIL.val.equals(val);
	}
	
}
