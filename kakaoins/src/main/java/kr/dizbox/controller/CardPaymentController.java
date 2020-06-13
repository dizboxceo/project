package kr.dizbox.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.dizbox.domain.CardPaymentReqVO;
import kr.dizbox.domain.CardPaymentResVO;
import kr.dizbox.service.CardPaymentService;
import kr.dizbox.util.CardPaymentException;

@RequestMapping("/cardpay")
@RestController
@SuppressWarnings("deprecation")
public class CardPaymentController {
	
	private Logger log = Logger.getLogger(CardPaymentController.class);

	@Autowired
	private CardPaymentService service;
	
	@PostMapping(consumes = "application/json", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<CardPaymentResVO> pay(@RequestBody CardPaymentReqVO cardPaymentReqVO){
		log.info("CardPaymentReqVO:"+cardPaymentReqVO);
		CardPaymentResVO cardPaymentResVO = null;
		try {
			cardPaymentResVO = service.pay(cardPaymentReqVO);
		}catch(CardPaymentException e) {
			cardPaymentResVO = CardPaymentResVO.builder().resultCd(e.getResultCode()).resultMsg(e.getMessage());
		}
		return new ResponseEntity<CardPaymentResVO>(cardPaymentResVO,HttpStatus.OK);
	}
	
	@DeleteMapping(consumes = "application/json", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<CardPaymentResVO> cancel(@RequestBody CardPaymentReqVO cardPaymentReqVO){
		log.info("CardPaymentReqVO:"+cardPaymentReqVO);
		CardPaymentResVO cardPaymentResVO = null;
		try {
			cardPaymentResVO = service.cancel(cardPaymentReqVO);
		}catch(CardPaymentException e) {
			cardPaymentResVO = CardPaymentResVO.builder().resultCd(e.getResultCode()).resultMsg(e.getMessage());
		}
		return new ResponseEntity<CardPaymentResVO>(cardPaymentResVO,HttpStatus.OK);
	}
	
	@GetMapping(value = "/{uid}",produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
	public ResponseEntity<CardPaymentResVO> find(@PathVariable("uid") String uid){
		log.info("uid:"+uid);
		CardPaymentResVO cardPaymentResVO = null;
		try {
			cardPaymentResVO = service.find(CardPaymentReqVO.builder().uid(uid));
		}catch(CardPaymentException e) {
			cardPaymentResVO = CardPaymentResVO.builder().resultCd(e.getResultCode()).resultMsg(e.getMessage());
		}
		return new ResponseEntity<CardPaymentResVO>(cardPaymentResVO,HttpStatus.OK);
	}
}
