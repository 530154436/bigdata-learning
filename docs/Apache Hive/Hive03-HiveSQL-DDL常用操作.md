
## 一、数据库DDL操作（Database|schema）
Hive中DATABASE的概念和RDBMS中类似，我们称之为`数据库`。在Hive中， DATABASE和SCHEMA是可互换的，使用DATABASE或SCHEMA都可以。
```sql
CREATE (DATABASE|SCHEMA) [IF NOT EXISTS] database_name
[COMMENT database_comment]
[LOCATION hdfs_path]
[WITH DBPROPERTIES (property_name=property_value, ...)]
```
（1）COMMENT：数据库的注释说明语句<br>
（2）指定数据库在HDFS存储位置，默认`/user/hive/warehouse`<br>
（3）用于指定一些数据库的属性配置。<br>
**注意**：使用`location`指定路径的时候，最好是一个新创建的空文件夹。

```sql
-- 创建数据库
create database if not exists itcast
comment "this is my first db"
location "/user/hive/warehouse/itcast.db"
with dbproperties ('createdBy'='Edward')
;
```
Desc/Use/ALTER/DROP相关操作
```sql
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
-- DROP (DATABASE|SCHEMA) [IF EXISTS] database_name [RESTRICT|CASCADE];
DROP DATABASE IF EXISTS itcast;
DROP DATABASE IF EXISTS itcast CASCADE;
```

## 二、表DDL操作（Table）
```sql
-- 创建示例表
create table if not exists table_name(id int, name string);
```
### 2.1 Describe table
Hive中的DESCRIBE table语句用于显示Hive中表的元数据信息。
```sql
-- 语法
describe formatted [db_name.]table_name;
describe extended [db_name.]table_name;

-- 示例
describe formatted table_name;
describe extended table_name;
```
如果指定了`EXTENDED`关键字，则它将以Thrift序列化形式显示表的所有元数据。如果指定了FORMATTED关键字，则它将以表格格式显示元数据。<br>
<img src="images/hive03DDL常用操作_01.png" width="100%" height="100%" alt="">

### 2.2 Drop table
DROP TABLE删除该表的元数据和数据。 如果已配置垃圾桶（且未指定PURGE），则该表对应的数据实际上将移动到`.Trash/Current目录`，而元数据完全丢失。<br>
删除EXTERNAL表时，该表中的数据不会从文件系统中删除，只删除元数据。 如果指定了`PURGE`，则表数据不会进入.Trash/Current目录，跳过垃圾桶直接被删除。因此如果DROP失败，则无法挽回该表数据。
```sql
DROP TABLE [IF EXISTS] table_name [PURGE];  -- (Note: PURGE available in Hive 0.14.0 and later)
```

### 2.3 Truncate table
从表中`删除所有行`。可以简单理解为清空表的所有数据但是保留表的元数据结构。如果HDFS启用了垃圾桶，数据将被丢进垃圾桶，否则将被删除。
```sql
TRUNCATE [TABLE] table_name;
```

### 2.4 Alter table



## 参考引用
[1] [黑马程序员-Apache Hive 3.0](https://book.itheima.net/course/1269935677353533441/1269937996044476418/1269942232408956930) <br>
[2] [Apache Hive -Materialized views](https://cwiki.apache.org/confluence/display/Hive/Materialized+views)


