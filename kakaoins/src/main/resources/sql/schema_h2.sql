--drop table if exists card_payment;
--drop table if exists card_pay_txt;

create table if not exists card_payment(
	uid varchar(20) not null,
	job_cls char(2) not null,
	card_info varchar(300) not null,
	inst_plan char(2) not null,
	pay_amt decimal(10) not null,
	vat decimal(10) not null,
	state char(1) not null,
	data_cls varchar(10) not null,
	ref_uid varchar(20) not null,
	comment varchar(100) not null,
	pdate char(8) not null,
	ptime char(6) not null,
	primary key(uid)
);

create table if not exists card_pay_txt(
	pay_txt char(450) not null primary key
);
