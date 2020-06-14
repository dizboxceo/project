package kr.dizbox.domain;

public enum ResultCode {

	 OK("00","정상적으로 처리되었습니다.")
	,INVALID_CARD_NO("01","카드번호가 유효하지 않습니다.")
	,INVALID_VALID_DT("02","카드 유효번호가 유효하지 않습니다.")
	,INVALID_CVC("03","카드 CVC정보가 유효하지 않습니다.")
	,INVALID_INST_PLAN("04","할부개월수가 유효하지 않습니다.")
	,INVALID_PAY_AMT("05","결제금액이 유효하지 않습니다.")
	,INVALID_VAT("06","부가가치세가 유효하지 않습니다.")
	,INVALID_CANCEL_AMT("07","취소금액은 결제금액을 초과할 수 없습니다.")
	,NOT_UID("08","관리번호는 필수입력 사항입니다.")
	,NOT_CANCEL_AMT("09","취소금액은 필수입력 사항입니다.")
	,INVALID_PARTIAL_CANCEL_AMT("10","%s원 취소하려 했으나 남은 결제금액보다 커서 실패")
	,INVALID_PARTIAL_CANCEL_VAT("11","%s(%s)원 취소하려 했으나 남은 부가가치세보다 취소요청 부가가치세가 커서 실패")
	,ALEADY_CANCEL("12","이미 결제취소가 완료되었습니다.")
	,NO_DATA("13","관리번호[%s] 의 결제내역이 없습니다.")
	,PAYING("14","카드결제가 이미 진행중입니다.")
	,TOT_CANCELING("15","전체취소가 이미 진행중입니다.")
	,PARTIAL_CANCELING("16","부분취소가 이미 진행중입니다.")
	,INVALID_PAY_TXT("17","전문레이아웃이 유효하지 않습니다.")
	,FAIL_ENCRYPT("18","카드정보 암호화수행중 시스템 오류가 발생했습니다.")
	,FAIL_DECRYPT("19","카드정보 복호화수행중 시스템 오류가 발생했습니다.")
	,TX_FAIL("20","카드사 통신중 오류가 발생하였습니다.")
	;
	
	private String code;
	private String message;
	
	private ResultCode(String code,String message) {
		this.code = code;
		this.message = message;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public String getMessage() {
		return this.message;
	}
}
