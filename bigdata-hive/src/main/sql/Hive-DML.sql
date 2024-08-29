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



