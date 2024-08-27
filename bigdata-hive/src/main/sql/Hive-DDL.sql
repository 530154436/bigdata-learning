-- 创建数据库
create database if not exists itheima;
show databases;

-- 切换数据库
use itheima;

/**
  文件archer.txt中记录了手游《王者荣耀》射手的相关信息，内容如下所示，其中字段之间分隔符为制表符\t,要求在Hive中建表映射成功该文件。
 */
create table t_archer(
    id int comment "ID",
    name string comment "英雄名称",
    hp_max int comment "最大生命",
    mp_max int comment "最大法力",
    attack_max int comment "最高物攻",
    defense_max int comment "最大物防",
    attack_range string comment "攻击范围",
    hero_main string comment "主要定位",
    hero_assist string comment "次要定位"
)
row format delimited
fields terminated by "\t"
;

show tables;

-- 上传文件到hdfs
-- $HADOOP_HOME/bin/hdfs dfs -put /home/hive/honor_of_kings/hero/hero/archer.txt /user/hive/warehouse/itheima.db/t_archer

select * from t_archer;

/**
  文件hot_hero_skin_price.txt中记录了手游《王者荣耀》热门英雄的相关皮肤价格信息，内容如下,要求在Hive中建表映射成功该文件。
 */
create table if not exists t_hot_hero_skin_price(
    id int,
    name string,
    win_rate int,
    skin_price map<string,int>
)
row format delimited
fields terminated by ','            -- 指定字段之间的分隔符
collection items terminated by '-'  -- 指定集合元素之间的分隔符
map keys terminated by ':'          -- 指定map元素kv之间的分隔符
;

select * from t_hot_hero_skin_price;


/**
  文件[team_ace_player.txt](honor_of_kings/team_ace_player.txt)中记录了手游《王者荣耀》主要战队内最受欢迎的王牌选手信息，内容如下,要求在Hive中建表映射成功该文件。
 */
create table if not exists t_team_ace_player(
    id int,
    team_name string,
    ace_player_name string
)
;

select * from t_team_ace_player;

/**
  location语法来更改数据在HDFS上的存储路径
 */

drop table if exists t_team_ace_player;
create table if not exists t_team_ace_player(
    id int,
    team_name string,
    ace_player_name string
) location "/data"
;
select * from t_team_ace_player;

desc formatted t_team_ace_player;


/**
  内部表、外部表
 */
drop table student;
create table student
(
    num  int,
    name string,
    sex  string,
    age  int,
    dept string
)
row format delimited
    fields terminated by ','
location "/data/student";
desc formatted student;
select  * from student;
drop table student;

show tables;


drop table if exists student_ext;
create external table if not exists student_ext (
    num  int,
    name string,
    sex  string,
    age  int,
    dept string
)
row format delimited
    fields terminated by ','
location "/data/student_ext";
select  * from student_ext;
drop table student_ext;


/**
  分区表
  现要求查询hero_main主要定位是射手并且hp_max最大生命大于6000的有几个，如何优化可以加快查询，减少全表扫描呢？
 */
drop table t_all_hero;
create table t_all_hero(
   id int,
   name string,
   hp_max int,
   mp_max int,
   attack_max int,
   defense_max int,
   attack_range string,
   role_main string,
   role_assist string
)
row format delimited
    fields terminated by "\t";

select * from t_all_hero;

-- 2m31s
select count(*) from t_all_hero where role_main="archer" and hp_max >6000;


// 静态分区
drop table t_all_hero_part;
create table t_all_hero_part(
    id int,
    name string,
    hp_max int,
    mp_max int,
    attack_max int,
    defense_max int,
    attack_range string,
    role_main string,
    role_assist string
)
partitioned by (role string)
row format delimited
    fields terminated by "\t";


desc formatted t_all_hero_part;

load data local inpath '/home/hive/honor_of_kings/hero/archer.txt' into table t_all_hero_part partition(role='archer');
load data local inpath '/home/hive/honor_of_kings/hero/assassin.txt' into table t_all_hero_part partition(role='assassin');
load data local inpath '/home/hive/honor_of_kings/hero/mage.txt' into table t_all_hero_part partition(role='mage');
load data local inpath '/home/hive/honor_of_kings/hero/support.txt' into table t_all_hero_part partition(role='support');
load data local inpath '/home/hive/honor_of_kings/hero/tank.txt' into table t_all_hero_part partition(role='tank');
load data local inpath '/home/hive/honor_of_kings/hero/warrior.txt' into table t_all_hero_part partition(role='warrior');

show partitions t_all_hero_part;

-- 32s
select count(*) from t_all_hero_part where role="archer" and hp_max >6000;


// 动态分区
drop table if exists t_all_hero_part_dynamic;
create table if not exists t_all_hero_part_dynamic(
    id int,
    name string,
    hp_max int,
    mp_max int,
    attack_max int,
    defense_max int,
    attack_range string,
    role_main string,
    role_assist string
)
partitioned by (role string)
row format delimited
    fields terminated by "\t";

set hive.exec.dynamic.partition=true;
set hive.exec.dynamic.partition.mode=nonstrict;

-- 46s
insert into table t_all_hero_part_dynamic partition(role)
select tmp.*,tmp.role_main from t_all_hero tmp;


// 多分区
--单分区表，按省份分区
create table t_user_province (id int, name string,age int) partitioned by (province string);
desc formatted t_user_province;

--双分区表，按省份和市分区
create table t_user_province_city (id int, name string,age int) partitioned by (province string, city string);
desc formatted t_user_province_city;

--三分区表，按省份、市、县分区
create table t_user_province_city_county (id int, name string,age int) partitioned by (province string, city string,county string);
desc formatted t_user_province_city_county;



/**
  分桶表
 */
-- 创建普通表
DROP TABLE IF EXISTS t_usa_covid19;
CREATE TABLE IF NOT EXISTS t_usa_covid19(
    count_date string,
    county string,
    state string,
    fips int,
    cases int,
    deaths int
)
row format delimited
fields terminated by ",";
SELECT * FROM t_usa_covid19;

-- 创建分桶表
DROP TABLE IF EXISTS t_usa_covid19_bucket;
CREATE TABLE IF NOT EXISTS t_usa_covid19_bucket(
    count_date string,
    county string,
    state string,
    fips int,
    cases int,
    deaths int
)
CLUSTERED BY(state) sorted by(cases DESC) INTO 5 BUCKETS;

-- $HADOOP_HOME/bin/hdfs dfs -put /home/hive/us-covid19-counties.dat /user/hive/warehouse/itheima.db/t_usa_covid19

--使用insert+select语法将数据加载到分桶表中，3245条数据耗时：1m51s
INSERT INTO t_usa_covid19_bucket SELECT * FROM t_usa_covid19;

select * from t_usa_covid19_bucket where state="New York";
select * from t_usa_covid19 where state="New York";


/**
  事务表
 */
--1、开启事务配置（可以使用set设置当前session生效 也可以配置在hive-site.xml中）
set hive.support.concurrency = true; --Hive是否支持并发
set hive.enforce.bucketing = true; --从Hive2.0开始不再需要  是否开启分桶功能
set hive.exec.dynamic.partition.mode = nonstrict; --动态分区模式  非严格
set hive.txn.manager = org.apache.hadoop.hive.ql.lockmgr.DbTxnManager; --
set hive.compactor.initiator.on = true; --是否在Metastore实例上运行启动线程和清理线程
set hive.compactor.worker.threads = 1; --在此metastore实例上运行多少个压缩程序工作线程。

--2、创建Hive事务表
create table trans_student(
    id int,
    name String,
    age int
)
clustered by (id) into 2 buckets
stored as orc
TBLPROPERTIES('transactional'='true');

--3、针对事务表进行insert update delete操作（效率很低）
insert into trans_student (id, name, age) values (1,"allen",18);  -- 2 m 3 s
update trans_student set age = 20 where id = 1;                   -- 29 s 426 ms
delete from trans_student where id = 1;                           -- 1 m 14 s
select * from trans_student;


/**
  Hive视图View
 */
SELECT * FROM itheima.t_usa_covid19;

--1、创建视图和查询
CREATE VIEW tmp_v_usa_covid19 AS SELECT count_date, county,state,deaths FROM t_usa_covid19 LIMIT 5;
SELECT * FROM tmp_v_usa_covid19;

--从已有的视图中创建视图
CREATE VIEW tmp_v_v_usa_covid19 AS SELECT * FROM tmp_v_usa_covid19 LIMIT 2;
SELECT * FROM tmp_v_v_usa_covid19;

--2、显示当前已有的视图
SHOW VIEWS;

--能否插入数据到视图中呢？
--不行 报错  SemanticException:A view cannot be used as target table for LOAD or INSERT
INSERT INTO tmp_v_v_usa_covid19 SELECT count_date, county,state,deaths FROM t_usa_covid19 ;

--3、查看视图定义
SHOW CREATE TABLE tmp_v_usa_covid19;

--4、更改视图属性
ALTER VIEW tmp_v_usa_covid19 set TBLPROPERTIES ('comment' = '这货是个视图');

--5、更改视图定义
ALTER VIEW tmp_v_v_usa_covid19 AS select county,deaths from t_usa_covid19 limit 2;
SELECT * FROM tmp_v_v_usa_covid19;

--6、删除视图
DROP VIEW tmp_v_v_usa_covid19;


/**
  案例：物化视图查询重写
 */
--1、新建一张事务表 student_trans
set hive.support.concurrency = true; --Hive是否支持并发
set hive.enforce.bucketing = true; --从Hive2.0开始不再需要  是否开启分桶功能
set hive.exec.dynamic.partition.mode = nonstrict; --动态分区模式  非严格
set hive.txn.manager = org.apache.hadoop.hive.ql.lockmgr.DbTxnManager; --
set hive.compactor.initiator.on = true; --是否在Metastore实例上运行启动线程和清理线程
set hive.compactor.worker.threads = 1; --在此metastore实例上运行多少个压缩程序工作线程。

drop table if exists student;
create table if not exists student
(
    sno  int,
    sname string,
    ssex  string,
    sage  int,
    sdept string
)
row format delimited
    fields terminated by ',';

create table if not exists  student_trans(
    sno  int,
    sname string,
    ssex  string,
    sage  int,
    sdept string
)
clustered by (sno) into 2 buckets
stored as orc
TBLPROPERTIES ('transactional' = 'true');

--2、导入数据到student_trans中
-- $HADOOP_HOME/bin/hdfs dfs -put /home/hive/students.txt /user/hive/warehouse/itheima.db/student
select * from student;

insert overwrite table student_trans select sno,sname,ssex,sage,sdept from student;
select * from student_trans;

--3、对student_trans建立聚合物化视图
CREATE MATERIALIZED VIEW student_trans_agg
AS
SELECT sdept, count(*) as sdept_cnt from student_trans group by sdept;

--注意 这里当执行CREATE MATERIALIZED VIEW，会启动一个MR对物化视图进行构建
--可以发现当下的数据库中有了一个物化视图
show materialized views;
SELECT * FROM student_trans_agg;

--4、对原始表student_trans查询
--由于会命中物化视图，重写query查询物化视图，查询速度会加快（没有启动MR，只是普通的table scan）
SELECT sdept, count(*) as sdept_cnt from student_trans group by sdept;

--5、查询执行计划可以发现 查询被自动重写为TableScan alias: itcast.student_trans_agg
--转换成了对物化视图的查询  提高了查询效率
explain SELECT sdept, count(*) as sdept_cnt from student_trans group by sdept;





