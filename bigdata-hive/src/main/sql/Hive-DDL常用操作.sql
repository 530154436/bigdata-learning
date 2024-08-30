/**
  数据库DDL操作（Database|schema）
 */
-- 创建数据库
create database if not exists itcast
comment "this is my first db"
location "/user/hive/warehouse/itcast.db"
with dbproperties ('createdBy'='Edward')
;

-- 显示Hive中数据库的名称、注释及其在文件系统上的位置等信息。
desc database itcast;
desc database extended itcast;

-- 切换数据库: 用于选择特定的数据库,切换当前会话使用哪一个数据库进行操作。
use itcast;

-- Hive中的ALTER DATABASE语句用于更改与Hive中的数据库关联的元数据。
--更改数据库属性
ALTER DATABASE itcast SET DBPROPERTIES ("createDate"="20240827");

--更改数据库所有者
ALTER DATABASE itcast SET OWNER USER zhengchubin;

--更改数据库位置：元数据修改生效了，但hdfs位置没改变
ALTER DATABASE itcast SET LOCATION "hdfs:///data/itcast.db";

-- Hive中的DROP DATABASE语句用于删除（删除）数据库。
-- 默认行为是RESTRICT，这意味着仅在数据库为空时才删除它。要删除带有表的数据库，我们可以使用CASCADE。
DROP DATABASE IF EXISTS itcast;
DROP DATABASE IF EXISTS itcast CASCADE;


/**
  表DDL操作（Table）
 */
create table if not exists table_name(
    id         int,
    name       string
)
;

-- 显示Hive中表的元数据信息
describe formatted table_name;
describe extended table_name;

-- 清空表
TRUNCATE TABLE table_name;

--1、更改表名
ALTER TABLE table_name RENAME TO new_table_name;
ALTER TABLE new_table_name RENAME TO table_name;

--2、更改表属性
-- ALTER TABLE table_name SET TBLPROPERTIES (property_name = property_value, ... );
ALTER TABLE table_name SET TBLPROPERTIES ("createdBy" = "Edward");

--更改表注释
ALTER TABLE table_name SET TBLPROPERTIES ('comment' = "new comment for student table");

--3、更改表的文件存储格式 该操作仅更改表元数据。现有数据的任何转换都必须在Hive之外进行。
-- ALTER TABLE table_name  SET FILEFORMAT file_format;
ALTER TABLE table_name  SET FILEFORMAT ORC;

--4、更改表的存储位置路径
ALTER TABLE table_name SET LOCATION "/data/table_name";

--5、添加/替换列
--使用ADD COLUMNS，您可以将新列添加到现有列的末尾但在分区列之前。
--REPLACE COLUMNS 将删除所有现有列，并添加新的列集。
-- ALTER TABLE table_name ADD|REPLACE COLUMNS (col_name data_type,...);
ALTER TABLE table_name ADD COLUMNS (age INT);


/**
  分区DDL操作（Partition）
 */

--1、增加分区
drop table if exists table_name;
create table if not exists table_name(id int) PARTITIONED BY (dt string);
--一次添加一个分区
ALTER TABLE table_name ADD PARTITION (dt='20170101') location '/user/hadoop/warehouse/table_name/dt=20170101';
show partitions table_name;

drop table if exists table_name;
create table if not exists table_name(id int) PARTITIONED BY (dt string, country string);
-- 一次添加多个分区
ALTER TABLE table_name ADD PARTITION (dt='20170101', country='us') location '/user/hadoop/warehouse/table_name/dt=20170101/country=us';
show partitions table_name;


--2、重命名分区
-- 语法
ALTER TABLE table_name PARTITION partition_spec RENAME TO PARTITION partition_spec;
-- 示例
ALTER TABLE table_name PARTITION (dt='20170101', country='us') RENAME TO PARTITION (dt='20080809', country='cn');


--3、删除分区
ALTER TABLE table_name DROP IF EXISTS PARTITION (dt='20080809', country='cn');
ALTER TABLE table_name DROP IF EXISTS PARTITION (dt='20080809', country='cn') PURGE; --直接删除数据 不进垃圾桶
show partitions table_name;

--4、修复分区

use itheima;
-- ① 创建一张分区表，直接使用HDFS命令在表文件夹下创建分区文件夹并上传数据，此时在Hive中查询是无法显示表数据的，因为metastore中没有记录，使用`MSCK ADD PARTITIONS`进行修复。
--Step1：创建分区表
create table t_all_hero_part_msck
(
    id           int,
    name         string,
    hp_max       int,
    mp_max       int,
    attack_max   int,
    defense_max  int,
    attack_range string,
    role_main    string,
    role_assist  string
) partitioned by (role string)
row format delimited
    fields terminated by "\t";

--Step2：在linux上，使用HDFS命令创建分区文件夹
-- $HADOOP_HOME/bin/hdfs dfs -mkdir -p /user/hive/warehouse/itheima.db/t_all_hero_part_msck/role=sheshou
-- $HADOOP_HOME/bin/hdfs dfs -mkdir -p /user/hive/warehouse/itheima.db/t_all_hero_part_msck/role=tanke

--Step3：把数据文件上传到对应的分区文件夹下
-- $HADOOP_HOME/bin/hdfs dfs -put /home/hive/honor_of_kings/hero/archer.txt /user/hive/warehouse/itheima.db/t_all_hero_part_msck/role=sheshou
-- $HADOOP_HOME/bin/hdfs dfs -put /home/hive/honor_of_kings/hero/tank.txt /user/hive/warehouse/itheima.db/t_all_hero_part_msck/role=tanke

--Step4：查询表 可以发现没有数据
select * from t_all_hero_part_msck;
--Step5：使用MSCK命令进行修复
--add partitions可以不写 因为默认就是增加分区
MSCK repair table t_all_hero_part_msck add partitions;

-- ② 针对分区表，直接使用HDFS命令删除分区文件夹，此时在Hive中查询显示分区还在，因为metastore中还没有被删除，使用`MSCK DROP PARTITIONS`进行修复。
--Step1：直接使用HDFS命令删除分区表的某一个分区文件夹
-- $HADOOP_HOME/bin/hdfs dfs -rm -r /user/hive/warehouse/itheima.db/t_all_hero_part_msck/role=sheshou

--Step2：查询发现还有分区信息
--因为元数据信息没有删除
show partitions t_all_hero_part_msck;

--Step3：使用MSCK命令进行修复
MSCK repair table t_all_hero_part_msck drop partitions;
show partitions t_all_hero_part_msck;


--5、修改分区
--更改分区文件存储格式
ALTER TABLE table_name PARTITION (dt='2008-08-09') SET FILEFORMAT file_format;
--更改分区位置
ALTER TABLE table_name PARTITION (dt='2008-08-09') SET LOCATION "new location";


/**
  Hive Show显示语法
 */
--1、显示所有数据库 SCHEMAS和DATABASES的用法 功能一样
show databases;
show schemas;

--2、显示当前数据库所有表/视图/物化视图/分区/索引
show tables;
show tables IN itheima; --指定某个数据库

--3、显示当前数据库下所有视图
Show Views;
SHOW VIEWS 'tmp_v_*'; -- show all views that start with "test_"
SHOW VIEWS FROM itheima; -- show views from database test1
SHOW VIEWS IN itheima;

--4、显示当前数据库下所有物化视图
SHOW MATERIALIZED VIEWS IN itheima;

--5、显示表分区信息，分区按字母顺序列出，不是分区表执行该语句会报错
show partitions table_name;

--6、显示表/分区的扩展信息
SHOW TABLE EXTENDED IN itheima LIKE table_name;
show table extended like student;

--7、显示表的属性信息
SHOW TBLPROPERTIES table_name;
show tblproperties student;

--8、显示表、视图的创建语句
SHOW CREATE TABLE itheima.t_usa_covid19_bucket;
show create table student;

--9、显示表中的所有列，包括分区列。
SHOW COLUMNS IN t_usa_covid19_bucket IN itheima;
show columns  in student;

--10、显示当前支持的所有自定义和内置的函数
show functions;

--11、Describe desc
--查看表信息
desc extended table_name;
--查看表信息（格式化美观）
desc formatted table_name;
--查看数据库相关信息
describe database itheima;