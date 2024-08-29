## 一、Load加载数据

### 1.1 Load语法
在将数据load加载到表中时，`Hive不会进行任何转换`。 加载操作是将数据文件移动到与Hive表对应的位置的纯复制/移动操作。语法如下：
```sql
LOAD DATA [LOCAL] INPATH 'filepath' [OVERWRITE] INTO TABLE tablename 
    [PARTITION (partcol1=val1, partcol2=val2 ...)]
    [INPUTFORMAT 'inputformat' SERDE 'serde'] (3.0 or later)
```
+ `filepath`：表示的待移动数据的路径，可以引用一个文件或目录。<br>
   相对路径：project/data1<br>
   绝对路径：/user/hive/project/data1<br>
   具有schema的完整URI：hdfs://namenode:9000/user/hive/project/data1<br>
+  `LOCAL`：load命令将在本地文件系统中查找文件路径。<br>
   如果指定了相对路径，它将相对于用户的当前工作目录进行解释。<br>
   用户也可以为本地文件指定完整的URI-例如：file:///user/hive/project/data1。<br>
   **注意**：如果对`HiveServer2服务`运行此命令，这里的本地文件系统指的是Hiveserver2服务所在机器的本地Linux文件系统。
+  `OVERWRITE`：目标表（或者分区）中的内容会被删除，然后再将 filepath 指向的文件/目录中的内容添加到表/分区中。 

### 1.2 案例：load加载数据到Hive表
建表，为后续操作做准备：
```sql
--建表student_local 用于演示从本地加载数据
drop table if exists student_local;
create table student_local(num  int, name string, sex  string, age  int, dept string) row format delimited fields terminated by ',';

--建表student_HDFS  用于演示从HDFS加载数据
create table student_hdfs(num  int, name string, sex  string, age  int, dept string) row format delimited fields terminated by ',';

--建表student_HDFS_p 用于演示从HDFS加载数据到分区表
create table student_hdfs_p(num  int, name string, sex  string, age  int, dept string) partitioned by(country string) row format delimited fields terminated by ',';
```
(1) `从本地加载数据`：数据位于HS2本地文件系统，本质是`hadoop fs -put`上传操作
```sql
-- 多次上传同名文件不会覆盖，而是添加后缀：xxx_copy_1.txt、xxx_copy_2.txt..
LOAD DATA LOCAL INPATH 'file:///home/hive/students.txt' INTO TABLE student_local;
LOAD DATA LOCAL INPATH '/home/hive/students.txt' INTO TABLE student_local;
LOAD DATA LOCAL INPATH './students.txt' INTO TABLE student_local;  -- `pwd`: /home/hive/

-- 添加Overwrite关键字则覆盖
LOAD DATA LOCAL INPATH 'file:///home/hive/students.txt' OVERWRITE INTO TABLE student_local;
```
<img src="images/hive03_dml_1_2_01.png" width="100%" height="100%" alt=""><br>

(2) `从HDFS加载数据`：数据位于HDFS文件系统根目录下，本质是`hadoop fs -mv`移动操作（移动后文件位置发生变更）
```sql
-- $HADOOP_HOME/bin/hdfs dfs -put students.txt /data/
LOAD DATA INPATH '/data/students.txt' INTO TABLE student_hdfs;
-- $HADOOP_HOME/bin/hdfs dfs -put students.txt /data/
LOAD DATA INPATH '/data/students.txt' INTO TABLE student_HDFS_p partition(country ="CHN");
```
<img src="images/hive03_dml_1_2_02.png" width="100%" height="100%" alt=""><br>

### 1.3 Hive3.0 Load新特性
Hive 3.0及更高版本中，除了移动复制操作之外，还支持其他加载操作，因为Hive在内部在某些场合下会将加载重写为`INSERT AS SELECT`。
比如，如果表具有分区，则load命令没有指定分区，则将load转换为INSERT AS SELECT，并假定最后一组列为分区列。如果文件不符合预期的架构，它将引发错误。
```sql
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
```
本来加载的时候没有指定分区，语句是报错的，但是文件的格式符合表的结构，最后一个是分区字段，则此时会将load语句转换成为`insert as select`语句。<br>
在Hive3.0中，还支持使用inputformat、SerDe指定任何Hive输入格式，例如文本，ORC等。
<img src="images/hive03_dml_1_3_01.png" width="100%" height="100%" alt=""><br>






