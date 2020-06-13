package kr.dizbox.service;

import kr.dizbox.domain.CardPaymentReqVO;
import kr.dizbox.domain.CardPaymentResVO;

public interface CardPaymentService {
	
	public CardPaymentResVO pay(CardPaymentReqVO cardPaymentReqVO);
	
	public CardPaymentResVO cancel(CardPaymentReqVO cardPaymentReqVO);
	
	public CardPaymentResVO find(CardPaymentReqVO cardPaymentReqVO);
	
	
}
