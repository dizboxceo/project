package kr.dizbox.service;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import kr.dizbox.domain.CardPaymentReqVO;
import kr.dizbox.domain.CardPaymentResVO;
import kr.dizbox.util.CardPaymentException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/root-context.xml")
public class CardPaymentServiceTest {

	private Logger log = Logger.getLogger(CardPaymentServiceTest.class);
	
	@Autowired
	private CardPaymentService cardPaymentService;
	
	@Test
	public void testPay() {
		CardPaymentReqVO cardPaymentReqVO = CardPaymentReqVO.builder()
				.cardNo("9445234594831111")
				.validDt("0525")
				.cvc("929")
				.payAmt(BigDecimal.valueOf(100000));
		CardPaymentResVO cardPaymentResVO = cardPaymentService.pay(cardPaymentReqVO);
		log.info(cardPaymentResVO);
	}
	
	@Test
	public void testPayException() {
		try {
			CardPaymentReqVO cardPaymentReqVO = CardPaymentReqVO.builder()
					.cardNo("13333424242")
					.validDt("2222")
					.cvc("111")
					.payAmt(BigDecimal.valueOf(10000000000l));
			CardPaymentResVO cardPaymentResVO = cardPaymentService.pay(cardPaymentReqVO);
			log.info(cardPaymentResVO);
		}catch(CardPaymentException e) {
			log.info(e.getResultCode()+":"+e.getMessage());
		}
	}
	
	@Test
	public void testCancel() {
		CardPaymentReqVO cardPaymentReqVO = CardPaymentReqVO.builder()
				.uid("202006122254207b1f4b")
				.payAmt(BigDecimal.valueOf(100000));
		CardPaymentResVO cardPaymentResVO = cardPaymentService.cancel(cardPaymentReqVO);
		log.info(cardPaymentResVO);
	}
	
	@Test
	public void testCancelException() {
		try {
			CardPaymentReqVO cardPaymentReqVO = CardPaymentReqVO.builder()
					.uid("202006122254207b1f4b")
					.payAmt(BigDecimal.valueOf(100000));
			CardPaymentResVO cardPaymentResVO = cardPaymentService.cancel(cardPaymentReqVO);
			log.info(cardPaymentResVO);
		}catch(CardPaymentException e) {
			log.info(e.getResultCode()+":"+e.getMessage());
		}
	}
	
	@Test
	public void testFind() {
		try {
			CardPaymentReqVO cardPaymentReqVO = CardPaymentReqVO.builder()
					.uid("202006122254207b1f4b");
			CardPaymentResVO cardPaymentResVO = cardPaymentService.find(cardPaymentReqVO);
			log.info(cardPaymentResVO);
		}catch(CardPaymentException e) {
			log.info(e.getResultCode()+":"+e.getMessage());
		}
	}
}
