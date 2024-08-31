## 一、Select基础查询
### 1.1 概述
#### 1.1.1 语法树
```sql
[WITH CommonTableExpression (, CommonTableExpression)*] 
SELECT [ALL | DISTINCT] select_expr, select_expr, ...
  FROM table_reference
  [WHERE where_condition]
  [GROUP BY col_list]
  [ORDER BY col_list]
  [CLUSTER BY col_list
    | [DISTRIBUTE BY col_list] [SORT BY col_list]
  ]
 [LIMIT [offset,] rows]
```
（1）`table_reference`：查询的输入，可以是普通物理表、视图、join查询结果或子查询结果。<br>
（2）`表名`和`列名`不区分大小写。<br>

#### 1.1.2 案例：美国Covid-19新冠
《us-covid19-counties.dat》记录了2021-01-28美国各个县累计新冠确诊病例数和累计死亡病例数。 在Hive中创建表，并加载该文件到表中：
```sql
-- 创建普通表t_usa_covid19
drop table t_usa_covid19;
CREATE TABLE t_usa_covid19
(
    count_date string,
    county     string,
    state      string,
    fips       int,
    cases      int,
    deaths     int
)
row format delimited
    fields terminated by ",";

-- 创建一张分区表 基于count_date日期,state州进行分区
CREATE TABLE t_usa_covid19_p
(
    county string,
    fips   int,
    cases  int,
    deaths int
)
partitioned by (count_date string, state string)
row format delimited
    fields terminated by ",";

-- 将源数据load加载到t_usa_covid19表对应的路径下
load data local inpath '/home/hive/us-covid19-counties.dat' into table t_usa_covid19;

-- 使用动态分区插入将数据导入t_usa_covid19_p中
set hive.exec.dynamic.partition.mode = nonstrict;
insert into table t_usa_covid19_p partition (count_date, state)
select county,fips,cases,deaths,count_date,state from t_usa_covid19;
```

### 1.2 select_expr
每个`select_expr`表示要检索的列。必须至少有一个 select_expr。
```sql
--查询所有字段或者指定字段
select * from t_usa_covid19_p;
select county, cases, deaths from t_usa_covid19_p;

--查询匹配正则表达式的所有字段
SET hive.support.quoted.identifiers = none; --带反引号的名称被解释为正则表达式
select `^c.*` from t_usa_covid19_p;

--查询当前数据库
select current_database(); --省去from关键字

--查询使用函数 3245
select count(county) from t_usa_covid19_p;
```

### 1.3 ALL 、DISTINCT
`ALL`和`DISTINCT`选项指定是否应返回重复的行。如果没有给出这些选项，则默认值为ALL（返回所有匹配的行）。DISTINCT指定从结果集中删除重复的行。
```sql
--返回所有匹配的行
select state from t_usa_covid19_p;
--相当于
select all state from t_usa_covid19_p;

--返回所有匹配的行 去除重复的结果
select distinct state from t_usa_covid19_p;

--多个字段distinct 整体去重
select distinct county,state from t_usa_covid19_p;
```

### 1.4 WHERE
`WHERE条件`是一个布尔表达式。在WHERE表达式中，可以使用Hive支持的任何函数和运算符，但`聚合函数除外`。
从Hive 0.13开始，WHERE子句支持某些类型的子查询。
```sql
select * from t_usa_covid19_p where state ="California" and deaths > 1000;
select * from t_usa_covid19_p where 1 > 2;  -- 1 > 2 返回false
select * from t_usa_covid19_p where 1 = 1;  -- 1 = 1 返回true

--where条件中使用函数 找出州名字母超过10个
select * from t_usa_covid19_p where length(state) >10 ;

--WHERE子句支持子查询
SELECT *
FROM t_usa_covid19_p
WHERE state IN (
    -- 一般是其他的表
    SELECT state FROM t_usa_covid19_p WHERE length(state) >10
);

--where条件中不能使用聚合函数
--报错 SemanticException:Not yet supported place for UDAF 'sum'
SELECT state, sum(deaths)
FROM t_usa_covid19_p
WHERE sum(deaths) >100 GROUP BY state;
```
> `那么为什么不能在where子句中使用聚合函数呢？`<br>
因为聚合函数要使用它的前提是结果集已经确定，而where子句还处于“确定”结果集的过程中，因而不能使用聚合函数。

### 1.5 分区查询、分区裁剪
通常，SELECT查询会执行全表扫描。如果使用`PARTITIONED BY`子句创建分区表时，可以通过指定分区查询来减少全表扫描，这个过程称为分区裁剪。
`分区裁剪`是指在对分区表进行查询时，系统会检查`WHERE`子句或`JOIN`中的`ON`子句是否对分区字段进行了过滤。如果存在这些过滤条件，查询将只访问符合条件的分区，从而裁剪掉不必要访问的分区，提高查询效率。
```sql
--找出来自加州，累计死亡人数大于1000的县 state字段就是分区字段 进行分区裁剪 避免全表扫描
select * from t_usa_covid19_p where state ="California" and deaths > 1000;

--多分区裁剪
select * from t_usa_covid19_p where count_date = "2021-01-28" and state ="California" and deaths > 1000;
```

### 1.6 GROUP BY
`GROUP BY` 语句用于结合聚合函数，根据一个或多个列对结果集进行分组。<br>
注意：出现在GROUP BY中`select_expr`的字段：要么是GROUP BY分组的字段；要么是被聚合函数应用的字段。`避免出现一个字段多个值的歧义`。
```sql
--根据state州进行分组
--SemanticException:Expression not in GROUP BY key 'deaths'
--deaths不是分组字段 报错，state是分组字段 可以直接出现在select_expr中
select state,deaths
from t_usa_covid19_p where count_date = "2021-01-28" group by state;

--被聚合函数应用
select state,count(deaths)
from t_usa_covid19_p where count_date = "2021-01-28" group by state;
```

### 1.7 HAVING
在SQL中增加`HAVING`子句原因是，`WHERE关键字无法与聚合函数一起使用`。
HAVING子句可以让我们筛选分组后的各组数据,并且可以在Having中使用聚合函数，因为此时where、group by已经执行结束，结果集已经确定。
```sql
--统计死亡病例数大于10000的州
--where语句中不能使用聚合函数 语法报错
select state, sum(deaths)
from t_usa_covid19_p
where count_date = "2021-01-28" and sum(deaths) >10000 group by state;

--先where分组前过滤（此处是分区裁剪），再进行group by分组（含聚合）， 分组后每个分组结果集确定，再使用having过滤
select state, sum(deaths) AS cnts
from t_usa_covid19_p
where count_date = "2021-01-28"
group by state
having sum(deaths) > 10000;

--这样写更好 即在group by的时候聚合函数已经作用得出结果 having直接引用结果过滤 不需要再单独计算一次了
select state,sum(deaths) as cnts
from t_usa_covid19_p
where count_date = "2021-01-28"
group by state
having cnts > 10000;
```
`having与where的区别`:
+ having是在`分组后`对数据进行过滤
+ where是在`分组前`对数据进行过滤
+ having后面可以使用聚合函数
+ where后面不可以使用聚合

### 1.8 LIMIT
LIMIT子句可用于约束SELECT语句返回的行数。<br>
LIMIT接受一个或两个数字参数，这两个参数都必须是`非负整数常量`。
+ 第一个参数指定要返回的第一行的偏移量，第二个参数指定要返回的最大行数。
+ 当给出单个参数时，它代表最大行数，并且偏移量默认为0。
```sql
--返回结果集的前5条
select *
from t_usa_covid19_p
where count_date = "2021-01-28" and state ="California"
limit 5;

--返回结果集从第1行开始 共3行
select *
from t_usa_covid19_p
where count_date = "2021-01-28" and state ="California"
limit 2,3; --注意 第一个参数偏移量是从0开始的: => 返回第3-5条
```

## 二、Select高阶查询

### 2.1 ORDER BY、CLUSTER BY、DISTRIBUTE BY + SORT BY

#### 2.1.1 ORDER BY
Hive SQL中的`ORDER BY`语法类似于SQL语言中的ORDER BY语法。会对输出的结果进行`全局排序`，因此底层使用MapReduce引擎执行的时候，只会有一个ReduceTask执行。
也因此，如果输出的行数太大，会导致需要很长的时间才能完成全局排序。
`默认排序顺序为升序`（ASC），也可以指定为DESC降序。

```sql
--根据字段进行排序
--强烈建议将LIMIT与ORDER BY一起使用，避免数据集行数过大。
--当hive.mapred.mode设置为strict严格模式时，使用不带LIMIT的ORDER BY时会引发异常。
select *
from t_usa_covid19_p
where count_date = "2021-01-28" and state ="California"
order by deaths
limit 100; --默认asc null first

select *
from t_usa_covid19_p
where count_date = "2021-01-28" and state ="California"
order by deaths desc
limit 100; --指定desc null last
```

#### 2.1.2 CLUSTER BY
Hive SQL中的`CLUSTER BY`语法可以指定根据后面的字段将数据分组，每组内再根据某个字段正序排序（不允许指定排序规则），即根据同一个字段，分且排序。
```sql
--不指定reduce task个数
--日志显示：Number of reduce tasks not specified. Estimated from input data size: 1
select * from t_usa_covid19 cluster by county;

--手动设置reduce task个数
set mapreduce.job.reduces = 2;
select * from t_usa_covid19 cluster by county;
```
<img src="images/hive03_dql_2_1_01.png" width="100%" height="100%" alt=""><br>

默认情况下，ReduceTask的个数由Hive在编译期间自己决定。<br>

#### 2.1.3 DISTRIBUTE BY + SORT BY
CLUSTER BY的功能是分且排序（同一个字段），那么`DISTRIBUTE BY` + `SORT BY`就相当于把cluster by的功能一分为二：
+ DISTRIBUTE BY负责分
+ SORT BY负责分组内排序，并且可以是不同的字段。
+ 如果DISTRIBUTE BY + SORT BY的字段一样，可以得出下列结论： CLUSTER BY = DISTRIBUTE BY + SORT BY（字段一样）
```sql
--根据区分为两个部分，每个分组内根据死亡数的倒序排序。
select * from t_usa_covid19 distribute by county sort by deaths desc;

--下面两个语句执行结果一样
select * from t_usa_covid19 distribute by county sort by county;
select * from t_usa_covid19 cluster by county;
```
<img src="images/hive03_dql_2_1_02.png" width="100%" height="100%" alt=""><br>

#### 2.1.4 总结
1、`order by`会对输入做全局排序，因此只有一个reducer，会导致当输入规模较大时，需要较长的计算时间。<br>
2、`Cluster by`(字段) 除了具有Distribute by的功能外，还会对该字段进行排序。<br>
3、`distribute by`(字段)根据指定字段将数据分到不同的reducer，分发算法是hash散列。<br>
4、`sort by`不是全局排序，其在数据进入reducer前完成排序。<br>
+ 如果用sort by进行排序，并且设置mapred.reduce.tasks>1，则`sort by只保证每个reducer的输出有序，不保证全局有序`。
+ 如果distribute和sort的字段是同一个时，此时，`cluster by = distribute by + sort by`

<img src="images/hive03_dql_2_1_03.png" width="100%" height="100%" alt=""><br>

### 1.11 Union联合查询
### 1.12 Subqueries子查询
### 1.13 where子句中子查询
### Join连接查询


## 参考引用
[1] [黑马程序员-Apache Hive 3.0](https://book.itheima.net/course/1269935677353533441/1269937996044476418/1269942232408956930) <br>
[2] [Apache Hive - LanguageManual Select](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+Select) <br>
[3] [Hive SQL 语句的执行顺序](https://gairuo.com/p/hive-sql-execution-order) <br>
