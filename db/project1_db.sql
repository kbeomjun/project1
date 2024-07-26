drop database if exists atm;
create database if not exists atm;

use atm;
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
	`ac_num`	char(9) primary key	NOT NULL,
	`ac_pw`	char(4)	NOT NULL,
	`ac_name`	varchar(30)	NOT NULL,
	`ac_balance`	int	NOT NULL default 0
);

DROP TABLE IF EXISTS `detail`;
CREATE TABLE `detail` (
	`dt_num`	int primary key auto_increment	NOT NULL,
	`dt_detail`	varchar(40)	NOT NULL,
	`dt_money`	int	NOT NULL,
	`dt_date`	datetime	NOT NULL default current_timestamp,
	`dt_balance`	int	NOT NULL,
	`dt_ac_num`	char(9)	NOT NULL
);

ALTER TABLE `detail` ADD CONSTRAINT `FK_account_TO_detail_1` FOREIGN KEY (
	`dt_ac_num`
)
REFERENCES `account` (
	`ac_num`
);