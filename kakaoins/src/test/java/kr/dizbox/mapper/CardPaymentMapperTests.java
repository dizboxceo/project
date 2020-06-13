package kr.dizbox.mapper;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import kr.dizbox.domain.CardPaymentVO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/root-context.xml")
public class CardPaymentMapperTests {
	
	private Logger log = Logger.getLogger(CardPaymentMapperTests.class);
	
	@Autowired
	private CardPaymentMapper mapper; 
	
	@Test
	public void testMapper() {
		log.info(mapper);
	}
	
	@Test
	public void testCreateCardPayment() {
		IntStream.range(1, 10).forEach(i->{
			
			mapper.insertCardPayment(CardPaymentVO.builder().uid("uid"+i).jobCls("00").cardInfo("11414klj14jl13kljl31jl31j3l1")
		             .instPlan("").payAmt(BigDecimal.ZERO).payAmt(BigDecimal.ZERO).vat(BigDecimal.ZERO)
		             .state("").dataCls("").refUid("").comment(""));
		});
	}
	
	@Test
	public void testReadCardPayment() {
		String uid = "uid1";
		CardPaymentVO cardPaymentVO = mapper.readCardPayment(uid);
		log.info(cardPaymentVO);
	}
	
}
