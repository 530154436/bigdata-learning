## 一、Select查询
### 语法树
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

### 案例：美国Covid-19新冠
《us-covid19-counties.dat》记录了2021-01-28美国各个县累计新冠确诊病例数和累计死亡病例数。 在Hive中创建表，并加载该文件到表中：
```dsl
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

### 1.1 select_expr
每个`select_expr`表示要检索的列。必须至少有一个 select_expr。

### 1.2 ALL 、DISTINCT
### 1.3 WHERE
### 1.4 分区查询、分区裁剪
### 1.5 HAVING
### 1.6 LIMIT
### 1.7 ORDER BY
### 1.8 CLUSTER BY
### 1.9 DISTRIBUTE BY +SORT BY
### 1.10 Union联合查询
### 1.11 Subqueries子查询
### 1.12 where子句中子查询
### 1.13 HiveSQL查询执行顺序

## 二、Join连接查询
