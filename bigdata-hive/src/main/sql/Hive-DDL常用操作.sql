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
  表DDL操作（Table）
  分区DDL操作（Partition）
 */