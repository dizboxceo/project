package kr.dizbox.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;

import kr.dizbox.domain.CardPaymentReqVO;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "file:src/main/webapp/WEB-INF/spring/root-context.xml",
 "file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml" })
@SuppressWarnings("deprecation")
public class CardPaymentControllerTest {

	private Logger log = Logger.getLogger(CardPaymentControllerTest.class);
	
	@Autowired
	private WebApplicationContext ctx;
	
	private MockMvc mockMvc;
	
	private Gson gson;
	
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
		this.gson = new Gson();
    }
	
	
	/**
	 * 결제테스트케이스
	 * @throws Exception
	 */
	@Test
	public void testPay() throws Exception {
		CardPaymentReqVO cardPaymentReqVO = CardPaymentReqVO.builder()
				.cardNo("9445111122223333")
				.validDt("0525")
				.cvc("929")
				.payAmt(BigDecimal.valueOf(100000))
				.inputVat("0")
				;
		String jsonStr = gson.toJson(cardPaymentReqVO);
		log.info(jsonStr);
		
		mockMvc.perform(post("/cardpay")
		       .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
               .content(jsonStr))
		       .andDo(print())
		       .andExpect(status().is(200));
		
	}
	
	/**
	 * 중복처리 테스트 케이스
	 * @throws Exception
	 */
	@Test
	public void testPayDupCheck() throws Exception {
		CardPaymentReqVO cardPaymentReqVO = CardPaymentReqVO.builder()
				.cardNo("8445111122223333")
				.validDt("0525")
				.cvc("929")
				.payAmt(BigDecimal.valueOf(100000))
				.inputVat("0")
				;
		
		String jsonStr = gson.toJson(cardPaymentReqVO);
		log.info(jsonStr);
		
		cardPaymentReqVO.setTestToken("Y");
		mockMvc.perform(post("/cardpay")
		       .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
               .content(jsonStr))
		       .andDo(print())
		       .andExpect(status().is(200));
		
		cardPaymentReqVO.setTestToken("");
		mockMvc.perform(post("/cardpay")
			       .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
	               .content(jsonStr))
			       .andDo(print())
			       .andExpect(status().is(200));
		
	}
	
	/**
	 * 취소 테스트 케이스
	 * @throws Exception
	 */
	@Test
	public void testCancel() throws Exception {
		CardPaymentReqVO cardPaymentReqVO = CardPaymentReqVO.builder()
				.uid("20200613231724656dfa")
				.payAmt(BigDecimal.valueOf(30000))
				.inputVat("0")
				;
		
		String jsonStr = gson.toJson(cardPaymentReqVO);
		log.info(jsonStr);
		
		mockMvc.perform(patch("/cardpay")
		       .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
		       .content(jsonStr))
			   .andDo(print())
		       .andExpect(status().is(200));
		
	}
	
	/**
	 * 취소 중복처리 테스트 케이스
	 * @throws Exception
	 */
	@Test
	public void testCancelDupCheck() throws Exception {
		CardPaymentReqVO cardPaymentReqVO = CardPaymentReqVO.builder()
				.uid("2020061321482757f29e")
				.payAmt(BigDecimal.valueOf(100000))
				.inputVat("0")
				;
		
		String jsonStr = gson.toJson(cardPaymentReqVO);
		log.info(jsonStr);
		
		cardPaymentReqVO.setTestToken("Y");
		mockMvc.perform(patch("/cardpay")
		       .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
		       .content(jsonStr))
			   .andDo(print())
		       .andExpect(status().is(200));
		
		cardPaymentReqVO.setTestToken("");
		mockMvc.perform(patch("/cardpay")
			       .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
			       .content(jsonStr))
				   .andDo(print())
			       .andExpect(status().is(200));
		
	}
	
	/**
	 * 조회 테스트 케이스
	 * @throws Exception
	 */
	@Test
	public void testFind() throws Exception {
		String uid = "2020061321482757f29e";
		mockMvc.perform(get("/cardpay/"+uid).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		       .andDo(print())
		       .andExpect(status().is(200));
	}
	
	
}
