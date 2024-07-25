drop database if exists atm;
create database if not exists atm;

use atm;
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
	`ac_num`	char(9)	NOT NULL primary key,
	`ac_pw`	char(4)	NULL,
	`ac_name`	varchar(30)	NULL,
	`ac_balance`	int	NULL
);

DROP TABLE IF EXISTS `detail`;
CREATE TABLE `detail` (
	`dt_num`	int	NOT NULL primary key auto_increment,
	`dt_detail`	varchar(200)	NULL,
	`dt_money`	int	NULL,
	`dt_date`	date	NULL,
	`dt_ac_num`	char(9)	NOT NULL
);

ALTER TABLE `detail` ADD CONSTRAINT `FK_account_TO_detail_1` FOREIGN KEY (
	`dt_ac_num`
)
REFERENCES `account` (
	`ac_num`
);