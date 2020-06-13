package kr.dizbox.domain;

public enum State {
	COMPLATE("0"),ONGOING("9");
	private String val;
	private State(String val) {
		this.val = val;
	}
	
	public String getVal() {
		return val;
	}
	
	public static boolean isComplate(String val) {
		return COMPLATE.val.equals(val);
	}
	
	public static boolean isOngoing(String val) {
		return ONGOING.val.equals(val);
	}
	
}
