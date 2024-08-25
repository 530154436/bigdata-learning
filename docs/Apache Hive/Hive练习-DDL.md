
## 一、Hive DDL建库语法
```sql
-- 创建数据库
create database if not exists itheima;
show databases;

-- 切换数据库
use itheima;
```
## 二、Hive DDL-建表语法
### 2.1 原生数据类型案例
文件[archer.txt](honor_of_kings/hero/archer.txt)中记录了手游《王者荣耀》射手的相关信息，其中字段之间分隔符为制表符\t,要求在Hive中建表映射成功该文件。<br>
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
<img src="images/hive练习1_1.png" width="100%" height="100%" alt="">

### 2.2 复杂数据类型案例
文件[hot_hero_skin_price.txt](honor_of_kings/hot_hero_skin_price.txt)中记录了手游《王者荣耀》热门英雄的相关皮肤价格信息，内容如下,要求在Hive中建表映射成功该文件。
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
<img src="images/hive练习1_2.png" width="100%" height="100%" alt="">

### 2.3 默认分隔符案例
文件[team_ace_player.txt](honor_of_kings/team_ace_player.txt)中记录了手游《王者荣耀》主要战队内最受欢迎的王牌选手信息，内容如下,要求在Hive中建表映射成功该文件。
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
<img src="images/hive练习1_3.png" width="100%" height="100%" alt="">

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

## 三、Hive DDL-内部表、外部表
### 3.1 内部表、外部表


`内部表`（Internal table）也称为被Hive拥有和管理的托管表（Managed table）。 默认情况下创建的表就是内部表，Hive拥有该表的结构和文件。`当删除内部表时，它会删除数据以及表的元数据`。
```sql
drop table if exists student;
create table if not exists student (
    num  int,
    name string,
    sex  string,
    age  int,
    dept string
)
row format delimited
    fields terminated by ','
location "/data";

desc formatted student;

-- $HADOOP_HOME/bin/hdfs dfs -put /home/hive/honor_of_kings/students.txt /data/student
select  * from student;
drop table student;
```
<img src="images/hive练习2_1.png" width="100%" height="100%" alt=""><br>

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
location "/data";;

desc formatted student_ext;

-- $HADOOP_HOME/bin/hdfs dfs -put /home/hive/honor_of_kings/students.txt /data/student_ext
select  * from student_ext;
drop table student_ext;
```
<img src="images/hive练习2_2.png" width="100%" height="100%" alt=""><br>

内部表与外部表的区别：

|          | **内部表、托管表**                                     | **外部表**             |
|----------|-------------------------------------------------|---------------------|
| 创建方式     | 默认情况下                                           | 使用外部(External)语法关键字 |
| Hive管理范围 | 元数据、表数据                                         | 元数据                 |
| 删除表结果    | 删除元数据，删除HDFS上文件数据                               | 只会删除元数据             |
| 操作       | 支持ARCHIVE, UNARCHIVE,TRUNCATE,MERGE,CONCATENATE | 不支持                 |
| 事务       | 支持ACID/事务性                                      | 不支持                 |
| 缓存       | 支持结果缓存                                          | 不支持                 |















