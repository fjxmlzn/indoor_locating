create database mydb2;
use mydb2;

create table users(
	id int primary key  AUTO_INCREMENT,
	userName varchar(20),
	userAge tinyint,
	userSex varchar(4)
);



insert into users values(1,'leo',18,'male');

insert into users values(2,'mark',28,'male');

insert into users values(3,'Rose',28,'fem');

select * from users where  userName = 'leo';
select * from users;

