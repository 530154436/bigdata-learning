-- 切换数据库
use itcast;

/**
  案例：美国Covid-19新冠
 */
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
set hive.exec.dynamic.partition=true;
set hive.exec.dynamic.partition.mode=nonstrict;

-- [08S01][2] Error while processing statement: FAILED: Execution Error, return code 2 from org.apache.hadoop.hive.ql.exec.mr.MapRedTask
-- => yarn is running beyond physical memory limits，调整配置后正常
insert overwrite table t_usa_covid19_p partition (count_date, state)
select county,fips,cases,deaths,count_date,state from t_usa_covid19;


/**
  select查询-select_expr
 */
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


/**
  select查询-ALL 、DISTINCT
 */
--返回所有匹配的行
select state from t_usa_covid19_p;
--相当于
select all state from t_usa_covid19_p;

--返回所有匹配的行 去除重复的结果
select distinct state from t_usa_covid19_p;

--多个字段distinct 整体去重
select distinct county,state from t_usa_covid19_p;


/**
  select查询-WHERE
 */
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


/**
  select查询-WHERE
 */
--找出来自加州，累计死亡人数大于1000的县 state字段就是分区字段 进行分区裁剪 避免全表扫描
select * from t_usa_covid19_p where state ="California" and deaths > 1000;

--多分区裁剪
select * from t_usa_covid19_p where count_date = "2021-01-28" and state ="California" and deaths > 1000;


/**
  select查询-GROUP BY
 */
--根据state州进行分组
--SemanticException:Expression not in GROUP BY key 'deaths'
--deaths不是分组字段 报错，state是分组字段 可以直接出现在select_expr中
select state,deaths
from t_usa_covid19_p where count_date = "2021-01-28" group by state;

--被聚合函数应用
select state,count(deaths)
from t_usa_covid19_p where count_date = "2021-01-28" group by state;


/**
  select查询-having
 */
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


/**
  select查询-limit
 */
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

/**
  select查询-ORDER BY
 */

--根据字段进行排序
--强烈建议将LIMIT与ORDER BY一起使用，避免数据集行数过大。
--当hive.mapred.mode设置为strict严格模式时，使用不带LIMIT的ORDER BY时会引发异常。
select *
from t_usa_covid19_p
where count_date = "2021-01-28" and state ="California"
order by deaths
limit 10; --默认asc null first

select *
from t_usa_covid19_p
where count_date = "2021-01-28" and state ="California"
order by deaths desc
limit 10; --指定desc null last


/**
  select查询-cluster by
*/

--不指定reduce task个数
--日志显示：Number of reduce tasks not specified. Estimated from input data size: 1
select * from t_usa_covid19 cluster by county;

--手动设置reduce task个数
set mapreduce.job.reduces = 2;
select * from t_usa_covid19 cluster by county;


/**
  select查询-DISTRIBUTE BY + SORT BY
*/
--根据区分为两个部分，每个分组内根据死亡数的倒序排序。
select * from t_usa_covid19 distribute by county sort by deaths desc;

--下面两个语句执行结果一样
select * from t_usa_covid19 distribute by county sort by county;
select * from t_usa_covid19 cluster by county;













