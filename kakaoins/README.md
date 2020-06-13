1.개발프레임워크
--스프링
--mybatis

2.테이블설계
-- 카드결제 메인테이블
create table if not exists card_payment(
	uid varchar(20) not null,          --관리번호
	job_cls char(2) not null,          --작업구분 00:결제 01:전체취소 02:부분취소
	card_info varchar(300) not null,   --암호화된 카드정보
	inst_plan char(2) not null,        --할부개월수
	pay_amt decimal(10) not null,      --결제/취소금액
	vat decimal(10) not null,          --결제/취소부가가치세
	state char(1) not null,            --처리상테 0:완료 9:진행중
	data_cls varchar(10) not null,     --데이터구분 PAYMENT:결제 CANCEL:취소
	ref_uid varchar(20) not null,      --취소시 결제관리번호
	comment varchar(100) not null,     --처리내용코멘트
	pdate char(8) not null,            --처리일자
	ptime char(6) not null,            --처리시간
	primary key(uid)
);

--카드사 전송데이터
create table if not exists card_pay_txt(
	pay_txt char(450) not null primary key --카드사전송전문
);

3.문제해결 전략
--결제/취소시 동일카드번호 중복트랜 호출방지
  --카드사전송전 카드결제 메인테이블에 진행중 상태로 저장
  --카드사전송전 카드결제 메인테이블에 진행중 데이터를 조회하여 예외처리
--카드사 전송요청이 성공이 아닌경우
  --카드사 전송요청이 성공인경우 카드결제 메인테이블 상태를 완료로 업데이트 처리
  --카드사 전송요청 실패시 예외처리

4.빌드 및 실행방법
--메이븐 업데이트 수행
--단위테스트 를 통한 테스트 수행
  --kr.dizbox.controller.CardPaymentControllerTest 의 테스트 메소드를 수행하여 콘솔로그로 결과확인
  --시나리오
    testPay 메소드 수행 (input 변경후 수행가능)
       -- 수행결과 콘솔로그 확인
    testPayDupCheck 메소드 수행 (input 변경후 수행가능)
       --중복수행테스트를 위해 input객체에 testToken 필드를 추가하였으며 첫번호출시 "Y" 세팅하면 처리상태가 9:진행중 데이터로 생성되고
          두번째 호출시 중복처리 예외처리됨을 확인
       --수행결과 콘솔로그 확인
    testCancel 메소드 수행 (*위의 testPay의 콘솔로그수행결과의 관리번호를 input에 기재하여 수행 )
       -- 수행결과 콘솔로그 확인
    testPayDupCheck 메소드 수행(*위의 testPay의 콘솔로그수행결과의 관리번호를 input에 기재하여 수행 )
       --중복수행테스트를 위해 input객체에 testToken 필드를 추가하였으며 첫번호출시 "Y" 세팅하면 처리상태가 9:진행중 데이터로 생성되고
          두번째 호출시 중복처리 예외처리됨을 확인
       --수행결과 콘솔로그 확인   
    testFind 메소드 수행(*위의 testPay의 콘솔로그수행결과의 관리번호를 input에 기재하여 수행 )
       --수행결과 콘솔로그 확인      