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
create table t_all_hero(
   id int,
   name string,
   hp_max int,
   mp_max int,
   attack_max int,
   defense_max int,
   attack_range string,
   hero_main string,
   hero_assist string
)
row format delimited
    fields terminated by "\t";

select * from t_all_hero;

select count(*) from t_all_hero where hero_main="archer" and hp_max >6000;


create table t_all_hero_part(
    id int,
    name string,
    hp_max int,
    mp_max int,
    attack_max int,
    defense_max int,
    attack_range string,
    hero_main string,
    hero_assist string
)
partitioned by (hero string)
row format delimited
    fields terminated by "\t";

load data local inpath '/home/hive/honor_of_kings/hero/archer.txt' into table t_all_hero_part partition(role='sheshou');
load data local inpath '/home/hive/honor_of_kings/hero/assassin.txt' into table t_all_hero_part partition(role='cike');
load data local inpath '/home/hive/honor_of_kings/hero/mage.txt' into table t_all_hero_part partition(role='fashi');
load data local inpath '/home/hive/honor_of_kings/hero/support.txt' into table t_all_hero_part partition(role='fuzhu');
load data local inpath '/home/hive/honor_of_kings/hero/tank.txt' into table t_all_hero_part partition(role='tanke');
load data local inpath '/home/hive/honor_of_kings/hero/warrior.txt' into table t_all_hero_part partition(role='zhanshi');

show partitions t_all_hero_part;