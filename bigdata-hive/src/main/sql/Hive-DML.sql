-- 创建数据库
create database if not exists itheima;
show databases;

-- 切换数据库
use itheima;

/**
  Load加载数据
 */
--step1:建表
--建表student_local 用于演示从本地加载数据
drop table if exists student_local;
create table student_local(
    num  int,
    name string,
    sex  string,
    age  int,
    dept string
)
row format delimited
    fields terminated by ',';

--建表student_HDFS  用于演示从HDFS加载数据
create table student_hdfs(
    num  int,
    name string,
    sex  string,
    age  int,
    dept string
)
row format delimited
    fields terminated by ',';

--建表student_HDFS_p 用于演示从HDFS加载数据到分区表
create table student_hdfs_p(
    num  int,
    name string,
    sex  string,
    age  int,
    dept string
)
partitioned by(country string)
row format delimited
    fields terminated by ',';


-- 登录beeline客户端：$HIVE_HOME/bin/beeline -u "jdbc:hive2://hive:10000" -n hive

--step2:加载数据
-- 从本地加载数据  数据位于HS2（node1）本地文件系统  本质是hadoop fs -put上传操作
-- 多次上传同名文件不会覆盖，而是添加后缀：xxx_copy_1.txt、xxx_copy_2.txt..
LOAD DATA LOCAL INPATH 'file:///home/hive/students.txt' INTO TABLE student_local;
LOAD DATA LOCAL INPATH '/home/hive/students.txt' INTO TABLE student_local;
LOAD DATA LOCAL INPATH './students.txt' INTO TABLE student_local;  -- `pwd`: /home/hive/
-- 添加Overwrite关键字则覆盖
LOAD DATA LOCAL INPATH 'file:///home/hive/students.txt' OVERWRITE INTO TABLE student_local;


--从HDFS加载数据  数据位于HDFS文件系统根目录下  本质是hadoop fs -mv 移动操作
-- $HADOOP_HOME/bin/hdfs dfs -put students.txt /data/
LOAD DATA INPATH '/data/students.txt' INTO TABLE student_HDFS;


----从HDFS加载数据到分区表中并制定分区  数据位于HDFS文件系统根目录下
LOAD DATA INPATH '/data/students.txt' INTO TABLE student_HDFS_p partition(country ="CHN");


-- Hive3.0 Load新特性
drop table if exists student_hdfs_p2;
create table if not exists student_hdfs_p2(
    num  int,
    name string,
    sex  string,
    age  int
)
partitioned by(dept string)
row format delimited
    fields terminated by ',';

-- 动态分区：insert as select
-- $HADOOP_HOME/bin/hdfs dfs -put students.txt /data/
LOAD DATA INPATH '/data/students.txt' INTO TABLE student_hdfs_p2;

SHOW partitions student_hdfs_p2;
SELECT * FROM student_hdfs_p2;


/**
  DML-Insert插入数据
 */

--创建一张源表student
drop table if exists student;
create table student(
    num  int,
    name string,
    sex  string,
    age  int,
    dept string
)
row format delimited
    fields terminated by ',';
load data local inpath '/home/hive/students.txt' into table student;

--insert+select
create table student_from_insert(num int, name string);
insert into table student_from_insert
select num, name from student;

select * from student_from_insert;

-- multiple inserts
drop table student_insert1;
drop table student_insert2;

--创建两张新表
create table student_insert1(num int);
create table student_insert2(name string);
--多重插入
from student
insert overwrite table student_insert1
select num
insert overwrite table student_insert2
select name
;

select * from student_insert1;
select * from student_insert2;

-- dynamic partition insert
--1、首先设置动态分区模式为非严格模式 默认已经开启了动态分区功能
set hive.exec.dynamic.partition = true;
set hive.exec.dynamic.partition.mode = nonstrict;

--2、创建分区表 以sdept作为分区字段
--注意：分区字段名不能和表中的字段名重复。
drop table if exists student_partition;
create table student_partition(num int, name string, sex string, age int) partitioned by(dept string);

--3、执行动态分区插入操作
insert into table student_partition partition(dept)
select num,name,sex,age,dept from student;

-- insert + directory（导出数据）
--1、导出查询结果到HDFS指定目录下
insert overwrite directory '/data/hive_export/e1.txt' select * from student;

--2、导出时指定分隔符和文件存储格式
insert overwrite directory '/data/hive_export/e2.txt'
    row format delimited fields terminated by ','
    stored as textfile
select * from student;

--3、导出数据到本地文件系统指定目录下
insert overwrite local directory '/root/hive_export/e1.txt' select * from student;












