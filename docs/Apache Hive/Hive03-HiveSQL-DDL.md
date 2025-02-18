<nav>
<a href="#一hive-ddl建库语法">一、Hive DDL建库语法</a><br/>
<a href="#二hive-ddl建表基础">二、Hive DDL建表基础</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-原生数据类型案例">2.1 原生数据类型案例</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-复杂数据类型案例">2.2 复杂数据类型案例</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-默认分隔符案例">2.3 默认分隔符案例</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#24-指定存储路径">2.4 指定存储路径</a><br/>
<a href="#三hive-ddl建表高阶">三、Hive DDL建表高阶</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#31-内部表外部表">3.1 内部表、外部表</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#32-分区表">3.2 分区表</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#321-分区表数据加载-静态分区">3.2.1 分区表数据加载-静态分区</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#322-分区表数据加载-动态分区">3.2.2 分区表数据加载-动态分区</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#323-多重分区表">3.2.3 多重分区表</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#324-分区表使用总结">3.2.4 分区表使用总结</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#33-分桶表">3.3 分桶表</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#331-分桶表的概念">3.3.1 分桶表的概念</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#332-分桶表的创建加载数据">3.3.2 分桶表的创建、加载数据</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#333-分桶表的使用好处">3.3.3 分桶表的使用好处</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#34-hive-transactional-tables事务表">3.4 Hive Transactional Tables事务表</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#341-概述">3.4.1 概述</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#342-创建和使用事务表">3.4.2 创建和使用事务表</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#35-hive视图view">3.5 Hive视图（View）</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#351-概述">3.5.1 概述</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#352-view相关语法">3.5.2 View相关语法</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#353-view的好处">3.5.3 View的好处</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#36-hive30新特性物化视图-materialized-views">3.6 Hive3.0新特性：物化视图 materialized views</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#361-概述">3.6.1 概述</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#362-物化视图语法">3.6.2 物化视图语法</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#363-案例物化视图查询重写">3.6.3 案例：物化视图查询重写</a><br/>
<a href="#参考引用">参考引用</a><br/>
</nav>

## 一、Hive DDL建库语法
```sql
-- 创建数据库
create database if not exists itheima;
show databases;

-- 切换数据库
use itheima;
```
## 二、Hive DDL建表基础
### 2.1 原生数据类型案例
文件[archer.txt](../../data/hive/honor_of_kings/hero/archer.txt)中记录了手游《王者荣耀》射手的相关信息，其中字段之间分隔符为制表符\t,要求在Hive中建表映射成功该文件。<br>
```
1	后羿	5986	1784	396	336	remotely	archer
2	马可波罗	5584	200	362	344	remotely	archer
```
字段之间的分隔符是制表符，需要使用row format语法进行指定。
```sql
create table t_archer(
    id int comment "ID",
    name string comment "英雄名称",
    hp_max int comment "最大生命",
    mp_max int comment "最大法力",
    attack_max int comment "最高物攻",
    defense_max int comment "最大物防",
    attack_range string comment "攻击范围",
    role_main string comment "主要定位",
    role_assist string comment "次要定位"
)
row format delimited
fields terminated by "\t"
;

show tables;
```
建表成功之后，在Hive的默认存储路径下就生成了表对应的文件夹，把`archer.txt`文件上传到对应的表文件夹下。
```
$HADOOP_HOME/bin/hdfs dfs -put /home/hive/honor_of_kings/hero/archer.txt /user/hive/warehouse/itheima.db/t_archer

select * from t_archer;
```
<img src="images/hive03_2_1_01.png" width="100%" height="100%" alt="">

### 2.2 复杂数据类型案例
文件[hot_hero_skin_price.txt](../../data/hive/honor_of_kings/hot_hero_skin_price.txt)中记录了手游《王者荣耀》热门英雄的相关皮肤价格信息，内容如下,要求在Hive中建表映射成功该文件。
```
1,孙悟空,53,西部大镖客:288-大圣娶亲:888-全息碎片:0-至尊宝:888-地狱火:1688
2,鲁班七号,54,木偶奇遇记:288-福禄兄弟:288-黑桃队长:60-电玩小子:2288-星空梦想:0
```
字段：id、name（英雄名称）、win_rate（胜率）、skin_price（皮肤及价格）<br>
分析：前3个字段原生数据类型、最后一个字段复杂类型map。需要指定字段之间分隔符、集合元素之间分隔符、map kv之间分隔符。
```sql
create table t_hot_hero_skin_price(
    id int,
    name string,
    win_rate int,
    skin_price map<string,int>
)
row format delimited
fields terminated by ','            -- 指定字段之间的分隔符
collection items terminated by '-'  -- 指定集合元素之间的分隔符
map keys terminated by ':'          -- 指定map元素kv之间的分隔符
```
建表成功后，把`hot_hero_skin_price.txt`文件上传到对应的表文件夹下，并执行查询操作
```
$HADOOP_HOME/bin/hdfs dfs -put /home/hive/honor_of_kings/hot_hero_skin_price.txt /user/hive/warehouse/itheima.db/t_hot_hero_skin_price

select * from t_hot_hero_skin_price;
```
<img src="images/hive03_2_2_01.png" width="100%" height="100%" alt="">

### 2.3 默认分隔符案例
文件[team_ace_player.txt](../../data/hive/honor_of_kings/team_ace_player.txt)中记录了手游《王者荣耀》主要战队内最受欢迎的王牌选手信息，内容如下,要求在Hive中建表映射成功该文件。
```
1成都AG超玩会一诺
2重庆QGhappyHurt
```
字段：id、team_name（战队名称）、ace_player_name（王牌选手名字）<br>
分析：数据都是原生数据类型，且字段之间分隔符是\001，因此在建表的时候可以省去row format语句，因为hive默认的分隔符就是\001。
```
create table if not exists t_team_ace_player(
    id int,
    team_name string,
    ace_player_name string
);
```
建表成功后，把`team_ace_player.txt`文件上传到对应的表文件夹下，并执行查询操作
```
$HADOOP_HOME/bin/hdfs dfs -put /home/hive/honor_of_kings/team_ace_player.txt /user/hive/warehouse/itheima.db/t_team_ace_player

select * from t_team_ace_player;
```
<img src="images/hive03_2_3_01.png" width="100%" height="100%" alt="">

### 2.4 指定存储路径
在Hive建表的时候，可以通过location语法来更改数据在HDFS上的存储路径，使得建表加载数据更加灵活方便。
```sql
-- 语法：LOCATION '<hdfs_location>'。
drop table if exists t_team_ace_player;
create table if not exists t_team_ace_player(
    id int,
    team_name string,
    ace_player_name string
) location "/data"
;
select * from t_team_ace_player;
```

## 三、Hive DDL建表高阶
### 3.1 内部表、外部表
`内部表`（Internal table）也称为被Hive拥有和管理的托管表（Managed table）。 默认情况下创建的表就是内部表，Hive拥有该表的结构和文件。`当删除内部表时，它会删除数据以及表的元数据`。
```sql
drop table if exists student;
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

-- $HADOOP_HOME/bin/hdfs dfs -put /home/hive/honor_of_kings/students.txt /data/student
select  * from student;
drop table student;
```
<img src="images/hive03_3_1_01.png" width="100%" height="100%" alt=""><br>

`外部表`（External table）中的数据不是Hive拥有或管理的，只管理表元数据的生命周期。要创建一个外部表，需要使用`EXTERNAL`语法关键字。
删除外部表只会删除元数据，而不会删除实际数据。在Hive外部仍然可以访问实际数据。 而且外部表更为方便的是可以搭配`location`语法指定数据的路径。
```sql
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
location "/data/student_ext";;

desc formatted student_ext;

-- $HADOOP_HOME/bin/hdfs dfs -put /home/hive/honor_of_kings/students.txt /data/student_ext
select  * from student_ext;
drop table student_ext;
```
<img src="images/hive03_3_1_02.png" width="100%" height="100%" alt=""><br>

内部表与外部表的区别：

|          | **内部表、托管表**                                     | **外部表**             |
|----------|-------------------------------------------------|---------------------|
| 创建方式     | 默认情况下                                           | 使用外部(External)语法关键字 |
| Hive管理范围 | 元数据、表数据                                         | 元数据                 |
| 删除表结果    | 删除元数据，删除HDFS上文件数据                               | 只会删除元数据             |
| 操作       | 支持ARCHIVE, UNARCHIVE,TRUNCATE,MERGE,CONCATENATE | 不支持                 |
| 事务       | 支持ACID/事务性                                      | 不支持                 |
| 缓存       | 支持结果缓存                                          | 不支持                 |

### 3.2 分区表
现有6份数据文件，分别记录了《王者荣耀》中6种位置的英雄相关信息。现要求通过建立一张表t_all_hero，把6份文件同时映射加载。
```sql
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

-- cd /home/hive/honor_of_kings/hero/
-- $HADOOP_HOME/bin/hdfs dfs -put archer.txt assassin.txt mage.txt support.txt tank.txt warrior.txt /user/hive/warehouse/itheima.db/t_all_hero
```
<img src="images/hive03_3_2_01.png" width="100%" height="100%" alt=""><br><br>


现要求查询role_main主要定位是射手并且hp_max最大生命大于6000的有几个，如何优化可以加快查询，减少全表扫描呢？
```sql
-- 2m31s
select count(*) from t_all_hero where role_main="archer" and hp_max >6000;
```
<img src="images/hive03_3_2_02.png" width="100%" height="100%" alt=""><br><br>

针对《王者荣耀》英雄数据，重新创建一张分区表t_all_hero_part，以role角色作为分区字段。<br>
`注意`：分区字段不能是表中已经存在的字段，因为分区字段最终也会以虚拟字段的形式显示在表结构上。

#### 3.2.1 分区表数据加载-静态分区
```sql
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
```
`静态分区`指的是分区的字段值是由用户在加载数据的时候手动指定。 语法如下：
```sql
load data [local] inpath ' ' into table tablename partition(分区字段='分区值'...);

-- 载入数据
load data local inpath '/home/hive/honor_of_kings/hero/archer.txt' into table t_all_hero_part partition(role='archer');
load data local inpath '/home/hive/honor_of_kings/hero/assassin.txt' into table t_all_hero_part partition(role='assassin');
load data local inpath '/home/hive/honor_of_kings/hero/mage.txt' into table t_all_hero_part partition(role='mage');
load data local inpath '/home/hive/honor_of_kings/hero/support.txt' into table t_all_hero_part partition(role='support');
load data local inpath '/home/hive/honor_of_kings/hero/tank.txt' into table t_all_hero_part partition(role='tank');
load data local inpath '/home/hive/honor_of_kings/hero/warrior.txt' into table t_all_hero_part partition(role='warrior');
```

<img src="images/hive03_3_2_1_01.png" width="100%" height="100%" alt=""><br>

重新执行统计SQL：耗时仅`32s`
```sql
-- 32s
select count(*) from t_all_hero_part where role="archer" and hp_max >6000;
```
<img src="images/hive03_3_2_1_02.png" width="100%" height="100%" alt=""><br>


#### 3.2.2 分区表数据加载-动态分区
动态分区是指分区的字段值是基于查询结果自动推断。核心语法就是insert+select。启用hive动态分区，需要在hive会话中设置两个参数：
```sql
-- 开启动态分区功能
set hive.exec.dynamic.partition=true;
-- 指定动态分区的模式：分为`nonstick非严格模式`和`strict严格模式`；strict严格模式要求至少有一个分区为静态分区。
set hive.exec.dynamic.partition.mode=nonstrict;
```
创建一张新的分区表t_all_hero_part_dynamic：
```sql
create table t_all_hero_part_dynamic(
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
```
执行动态分区插入:
```sql
-- 46s
insert into table t_all_hero_part_dynamic partition(role)
select tmp.*,tmp.role_main from t_all_hero tmp;
```
动态分区插入时，分区值是根据查询返回字段位置自动推断的。<br>

<img src="images/hive03_3_2_2_01.png" width="100%" height="100%" alt=""><br>

#### 3.2.3 多重分区表

通过建表语句中关于分区的相关语法可以发现，Hive支持多个分区字段：
```sql
PARTITIONED BY (partition1 data_type, partition2 data_type,….)

```
多重分区下，分区之间是一种递进关系，可以理解为在前一个分区的基础上继续分区。从HDFS的角度来看就是文件夹下继续划分子文件夹。
```sql
--单分区表，按省份分区
create table t_user_province (id int, name string,age int) partitioned by (province string);

--双分区表，按省份和市分区
create table t_user_province_city (id int, name string,age int) partitioned by (province string, city string);

--三分区表，按省份、市、县分区
create table t_user_province_city_county (id int, name string,age int) partitioned by (province string, city string,county string);
```

#### 3.2.4 分区表使用总结

分区的概念：
+ 提供了一种将 Hive 表数据分离为多个文件/目录的方法。
+ `不同分区对应着不同的文件夹`，同一分区的数据存储在同一个文件夹下。
+ 只需根据分区值找到对应的文件夹，扫描该分区下的文件，避免全表数据扫描。

分区表使用重点：
1. 建表时选择合适的分区字段： 根据业务场景设置合适的分区字段，比如日期、地域、类别等。
2. 查询时使用分区过滤：查询时尽量使用 `WHERE` 子句进行分区过滤，避免全表扫描，查询指定分区的数据。

分区表注意事项：
1. 可选优化手段：分区表不是建表的必要语法规则，`只是一种优化手段`。
2. **分区字段不能重复**：分区字段不能是表中已有的字段，`不能重复定义`。
3. **虚拟字段**：分区字段是虚拟字段，其数据并不存储在底层文件中。
4. **分区字段值的确定**：分区字段值可以由用户手动指定（静态分区）或根据查询结果自动推断（动态分区）。
5. 多重分区支持：Hive 支持多重分区，即在分区的基础上可以继续分区，以达到更细粒度的划分。


### 3.3 分桶表
#### 3.3.1 分桶表的概念
`分桶表`也叫做桶表，源自建表语法中`bucket`单词。是一种用于优化查询而设计的表类型，该功能可以让数据分解为若干个部分易于管理。
在分桶时，需要指定根据哪个字段将数据分为几桶（几个部分）。默认规则是：
```
Bucket number = hash_function(bucketing_column) mod num_buckets
如果是int类型，hash_function(int) == int;
如果是其他类型，比如bigint、string或者复杂数据类型，将传入该类型派生的某个数字，比如hashcode值：hash_function(hashcode)
```

需要注意的是，`分桶的字段必须是表中已经存在的字段`。 分桶表建表语句如下：
```sql
CREATE [EXTERNAL] TABLE [db_name.]table_name
[(col_name data_type, ...)]
CLUSTERED BY (col_name) INTO N BUCKETS;
-- 其中CLUSTERED BY (col_name)表示根据哪个字段进行分；
-- INTO N BUCKETS表示分为几桶（也就是几个部分）。
```
#### 3.3.2 分桶表的创建、加载数据
现有美国2021-1-28号，各个县county的新冠疫情累计案例信息，包括确诊病例和死亡病例，数据格式如下所示：
```
count_date（统计日期）,county（县）,state（州）,fips（县编码code）,cases（累计确诊病例）,deaths（累计死亡病例）
2021-01-28,Juneau City and Borough,Alaska,02110,1108,3
2021-01-28,Kenai Peninsula Borough,Alaska,02122,3866,18
2021-01-28,Ketchikan Gateway Borough,Alaska,02130,272,1
```
根据state州把数据分为5桶，在创建分桶表时，还可以指定分桶内的数据`排序规则`，建表语句如下：
```sql
--根据state州分为5桶 每个桶内根据cases确诊病例数倒序排序
CREATE TABLE IF NOT EXISTS t_usa_covid19_bucket
(
    count_date string,
    county     string,
    state      string,
    fips       int,
    cases      int,
    deaths     int
)
CLUSTERED BY (state) sorted by (cases DESC) INTO 5 BUCKETS;
```
数据加载：
```
-- 第1步：创建普通表并把源数据加载到普通hive表中
CREATE TABLE t_usa_covid19(
    count_date string,
    county string,
    state string,
    fips int,
    cases int,
    deaths int
);
$HADOOP_HOME/bin/hdfs dfs -put /home/hive/us-covid19-counties.dat /user/hive/warehouse/itheima.db/t_usa_covid19

-- 第2步：使用insert+select语法将数据加载到分桶表中，3245条数据耗时：1m51s
INSERT INTO t_usa_covid19_bucket SELECT * FROM t_usa_covid19;
```
HDFS上查看t_usa_covid19_bucket底层数据结构可以发现，数据被分为了5个部分。<br>
<img src="images/hive03_3_3_2_01.png" width="100%" height="100%" alt=""><br>

#### 3.3.3 分桶表的使用好处
和非分桶表相比，分桶表的使用好处有以下几点：
1. 基于分桶字段查询时，`减少全表扫描`<br>
    ```sql
    select * from t_usa_covid19_bucket where state="New York";
    ```
2. `JOIN`时可以提高MR程序效率，减少笛卡尔积数量<br>
   对于JOIN操作两个表有一个相同的列，如果对这两个表都进行了分桶操作。那么将保存相同列值的桶进行JOIN操作即可，大大较少JOIN的数据量。<br>
   <img src="images/hive03_3_3_3_01.png" width="80%" height="80%" alt=""><br>

3. 分桶表数据进行抽样<br>
   当数据量特别大时，对全体数据进行处理存在困难时，抽样可以从被抽取的数据中估计和推断出整体的特性，是科学实验、质量检验、社会调查普遍采用的一种经济有效的工作和研究方法。


### 3.4 Hive Transactional Tables事务表

#### 3.4.1 概述

Hive 最初不支持事务，其核心目标是将结构化数据映射为表进行 SQL 分析处理。由于数据通常存储于 HDFS，而 HDFS 不支持随机修改文件，因此早期 Hive 不具备 `UPDATE` 和 `DELETE` 操作，也午所谓事务支持。<br>

为了应对以下场景，Hive 从 0.14 版本开始引入 ACID 事务：

+ **流式数据传输**  
   使用如 Apache Flume 或 Apache Kafka 之类的工具，可以将数据流式传输到 Hadoop 集群中。<br>
   虽然这些工具能够以每秒数百行的速度写入数据，但 Hive 只能在一定时间间隔内（如 15 分钟或一小时）添加分区。<br>
   频繁添加分区会导致表中出现大量分区（小文件），并造成查询效率下降。<br>
   通过引入事务，Hive 能够在流式数据写入时为读者提供一致的数据视图，同时避免生成过多小文件给 NameNode 带来压力。<br>

+ **缓慢变化的维度表**  
   在星型模式的数据仓库中，维度表（如商店信息）可能随着时间发生变化。Hive 的事务功能允许在需要时`更新`或`插入`新记录，支持对缓慢变化的数据进行精确处理。

+ **数据修正**  
   有时发现收集的数据不正确，需要更正。
   事务使 Hive 能够处理数据的修正需求，支持 `INSERT`、`UPDATE` 和 `DELETE` 操作。

Hive事务表局限性：
+ 尚不支持BEGIN，COMMIT和ROLLBACK，所有语言操作都是自动提交的。
+ 仅支持`ORC文件格式`（STORED AS ORC）。
+ 默认情况下事务配置为关闭，需要`配置参数开启`使用。
+ 表必须是`分桶表`（Bucketed）才可以使用事务功能。
  表参数transactional必须为true；
  外部表不能成为ACID表，不允许从非ACID会话读取/写入ACID表。

#### 3.4.2 创建和使用事务表
```sql
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
clustered by (id) into 2 buckets stored as orc TBLPROPERTIES('transactional'='true');

--3、针对事务表进行insert update delete操作（效率很低）
insert into trans_student (id, name, age) values (1,"allen",18);  -- 2 m 3 s
update trans_student set age = 20 where id = 1;                   -- 29 s 426 ms
delete from trans_student where id = 1;                           -- 1 m 14 s
select * from trans_student;


select * from trans_student;
```
`DELETE` 操作实际上没有删除数据，而是增加了标志位。<br>
<img src="images/hive03_3_4_2_01.png" width="100%" height="100%" alt=""><br>


### 3.5 Hive视图（View）
#### 3.5.1 概述
Hive中的视图（view）是一种`虚拟表`，**只保存定义，不实际存储数据**。<br>
通常从真实的物理表查询中创建生成视图，也可以从已经存在的视图上创建新视图。<br>
创建视图时，将冻结视图的架构，如果删除或更改基础表，则视图将失败，并且视图不能存储、操作数据，只能查询。<br>
视图是用来简化操作的，不缓冲记录，也没有提高查询性能。<br>

#### 3.5.2 View相关语法
```sql
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
```
#### 3.5.3 View的好处
1、将真实表中特定的列数据提供给用户，**保护数据隐私**
```sql
--通过视图来限制数据访问可以用来保护信息不被随意查询:
create table userinfo(firstname string, lastname string, ssn string, password string);
create view safer_user_info as select firstname, lastname from userinfo;
```

2、降低查询的复杂度，**优化查询语句**
```sql
--使用视图优化嵌套查询
from (
    select * from people join cart on(cart.pepople_id = people.id) where firstname = 'join'
)a select a.lastname where a.id = 3;

--把嵌套子查询变成一个视图
create view shorter_join as
select * from people join cart on (cart.pepople_id = people.id) where firstname = 'join';
--基于视图查询
select lastname from shorter_join where id = 3;
```

### 3.6 Hive3.0新特性：物化视图 materialized views
#### 3.6.1 概述
`物化视图`（Materialized View）是一个包括查询结果的数据库对像，可以用于`预先计算并保存表连接或聚集等耗时较多的操作的结果`。
在执行查询时，就可以避免进行这些耗时的操作，从而快速的得到结果。<br>

Hive 3.0引入了物化视图，并实现了基于`Apache Calcite`的`查询自动重写功能`。
用户可选择将物化视图存储在Hive或通过自定义存储处理器存储在其他系统（如Druid）。
Hive 3.0弃用了索引的语法支持，推荐使用`物化视图`和`列式存储文件格式`来加快查询的速度。<br>

物化视图与普通视图的**主要区别**在于：
+ 视图是虚拟的、逻辑存在的，只有定义没有存储数据，目的是简化查询复杂度；
+ 物化视图是真实的、物理存在的，存储预计算数据，用于提升查询性能。

#### 3.6.2 物化视图语法
```sql
CREATE MATERIALIZED VIEW [IF NOT EXISTS] [db_name.]materialized_view_name
    [DISABLE REWRITE]
    [COMMENT materialized_view_comment]
    [PARTITIONED ON (col_name, ...)]
    [CLUSTERED ON (col_name, ...) | DISTRIBUTED ON (col_name, ...) SORTED ON (col_name, ...)]
    [
    [ROW FORMAT row_format]
    [STORED AS file_format]
    | STORED BY 'storage.handler.class.name' [WITH SERDEPROPERTIES (...)]
  ]
  [LOCATION hdfs_path]
  [TBLPROPERTIES (property_name=property_value, ...)]
AS SELECT ...;
```
语法说明：<br>
（1）物化视图创建后，select查询执行数据自动落地，"自动"也即在query的执行期间，任何用户对该物化视图是不可见的<br>
（2）默认该物化视图可被用于`查询优化器optimizer查询重写`（在物化视图创建期间可以通过DISABLE REWRITE参数设置禁止使用）<br>
（3）SerDe和storage format非强制参数，可以用户配置，默认可用hive.materializedview.serde、 hive.materializedview.fileformat<br>
（4）物化视图可以使用custom storage handlers`存储在外部系统`（如druid）例如：
```sql
CREATE MATERIALIZED VIEW druid_wiki_mv
STORED AS 'org.apache.hadoop.hive.druid.DruidStorageHandler'
AS
SELECT __time, page, user, c_added, c_removed FROM src;
```

目前支持物化视图的drop和show操作，后续会增加其他操作：
```sql
-- Drops a materialized view
DROP MATERIALIZED VIEW [db_name.]materialized_view_name;
-- Shows materialized views (with optional filters)
SHOW MATERIALIZED VIEWS [IN database_name];
-- Shows information about a specific materialized view
DESCRIBE [EXTENDED | FORMATTED] [db_name.]materialized_view_name;
```

当数据源变更（新数据插入inserted、数据修改modified），物化视图也需要更新以保持数据一致性，目前需要用户主动触发rebuild:
```sql
ALTER MATERIALIZED VIEW [db_name.]materialized_view_name REBUILD;
```

`基于物化视图的查询重写`：物化视图创建后即可用于相关查询的加速，用户提交查询query，若该query经过重写后可命中已建视图，则被重写命中相关已建视图实现查询加速。
```sql
-- 是否重写查询使用物化视图可以通过全局参数控制，默认为true：
SET hive.materializedview.rewriting=true;
-- 用户可选择性的失能物化视图的重写：
ALTER MATERIALIZED VIEW [db_name.]materialized_view_name ENABLE|DISABLE REWRITE;
```

#### 3.6.3 案例：物化视图查询重写
1、新建普通表（`student`）、事务表（`student_trans`）
```sql
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
```

2、导入数据到`student`、`student_trans`中
```sql
-- $HADOOP_HOME/bin/hdfs dfs -put /home/hive/students.txt /user/hive/warehouse/itheima.db/student
select * from student;

insert overwrite table student_trans select sno,sname,ssex,sage,sdept from student;
select * from student_trans;
```
<img src="images/hive03_3_6_3_01.png" width="100%" height="100%" alt=""><br>


3、对`student_trans`建立聚合物化视图
```sql
CREATE MATERIALIZED VIEW student_trans_agg
AS
SELECT sdept, count(*) as sdept_cnt from student_trans group by sdept;

--可以发现当下的数据库中有了一个物化视图
show materialized views;
SELECT * FROM student_trans_agg;
```

4、查询原始表`student_trans`，转换成了对物化视图的查询，提高了查询效率
```sql
--由于会命中物化视图，重写query查询物化视图，查询速度会加快（没有启动MR，只是普通的table scan）
SELECT sdept, count(*) as sdept_cnt from student_trans group by sdept;

--查询执行计划可以发现：查询被自动重写为TableScan alias: itcast.student_trans_agg
explain SELECT sdept, count(*) as sdept_cnt from student_trans group by sdept;
```
<img src="images/hive03_3_6_3_02.png" width="100%" height="100%" alt=""><br>


## 参考引用
[1] [黑马程序员-Apache Hive 3.0](https://book.itheima.net/course/1269935677353533441/1269937996044476418/1269942232408956930) <br>
[2] [Apache Hive -Materialized views](https://cwiki.apache.org/confluence/display/Hive/Materialized+views)


