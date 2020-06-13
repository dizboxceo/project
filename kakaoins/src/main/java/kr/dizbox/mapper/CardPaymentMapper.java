package kr.dizbox.mapper;

import kr.dizbox.domain.CardPayTxtVO;
import kr.dizbox.domain.CardPaymentVO;

public interface CardPaymentMapper {
	
	public int insertCardPayment(CardPaymentVO cardPaymentVO);
	
	public int updateCardPayment(CardPaymentVO cardPaymentVO);
	
	public int insertPayTxt(CardPayTxtVO cardPayTxtVO);
	
	public CardPaymentVO readCardPayment(String uid);
	
	public CardPaymentVO readCancelCardPayment(String uid);
	
	public CardPaymentVO readCardPaymentByCardInfo(CardPaymentVO cardPaymentVO);
	
	
}
