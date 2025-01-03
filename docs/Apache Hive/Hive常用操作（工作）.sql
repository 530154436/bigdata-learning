/*
    Hive 常用操作总结
    Hive 官方文档 https://cwiki.apache.org/confluence/display/Hive/LanguageManual+UDF
    Hive 版本 CDP Hive 3.1.3000.7.1.6.0-297
    目录：
    1. 字符串函数
        substring
        substring_index
        instr
        regexp_replace
        regexp_extract
        concat
        concat_ws
        get_json_object

    2. 日期函数
        from_unixtime
        unix_timestamp
        current_date
        date_sub
        date_diff

    3. 聚合函数
    
    4. 内嵌表生成函数(UDTF, Built-in Table-Generating Functions)
        explode
    
    5. 窗口函数和分析函数

    6. 性能优化
        公共表表达式 (CTE) 
*/

-- ########################################################################################################################
-- 字符串函数
-- ########################################################################################################################

---------------------------------------------------------------------------------------------------------------------------
-- 函数原型：string substring(string|binary A, int start [, int end])
-- 参数说明：A: 要处理的字符串
--          start: 字符串开始的位置
--          end: 字符串结束的位置(可选)
-- 函数作用：返回从第 start 个开始到结束的子串
SELECT substring("abcd", 1)    -- abcd
UNION ALL
SELECT substring("abcd", 2)    -- bcd
UNION ALL
SELECT substring("320000", 3)  -- 0000
;


---------------------------------------------------------------------------------------------------------------------------
-- 函数原型：string substring_index(string A, string delim, int count)
-- 参数说明：A: 要处理的字符串
--          delim: 分隔符
--          count: 计数
-- 函数作用：如果count是正数，返回从左往右数，第N个分隔符的左边的全部内容;
--          如果count是负数，返回从右往左数，第N个分隔符的右边的全部内容。
SELECT substring_index("aba",';', 1)  -- aba
UNION ALL
SELECT substring_index("a;b;c;d",';', 1)  -- a
UNION ALL
SELECT substring_index("a;b;c;d",';', -3) -- b;c;d
UNION ALL
SELECT substring_index("a;b;c;d",';', 3)  -- a;b;c
UNION ALL
SELECT substring_index(";;;;;;;;;;",';', 1)  -- ""
;


---------------------------------------------------------------------------------------------------------------------------
-- 函数原型：int instr(string str, string substr)
-- 参数说明：str: 源字符
--          substr: 目标字符串
-- 函数作用：返回字符串 substr 在 str 中 首次出现的位置，找不到返回0 。
SELECT instr("abaaa", "a") -- 1
UNION ALL
SELECT instr("abaaa", "b") -- 2
UNION ALL
SELECT instr("abaaa", "c") -- 0
;

SELECT locate("锁芯", "紧紧的锁芯")
SELECT locate("紧紧的锁芯", "锁芯")
;

SELECT instr("123\t456\n235,789", "\n");
SELECT instr("123\t456\n235,789", "\s");

SELECT '"sfdad"';

---------------------------------------------------------------------------------------------------------------------------
-- 函数原型：string regexp_replace(string INITIAL_STRING, string PATTERN, string REPLACEMENT)
-- 参数说明：INITIAL_STRING: 源字符
--          PATTERN: 正则表达式
--          REPLACEMENT: 替换的字符
-- 函数作用：将字符串 INITIAL_STRING 中的符合java正则表达式 PATTERN 的部分替换为 REPLACEMENT。
SELECT regexp_replace('["电池传送及推送机构"]', '[\\"\\[\\]]', '')                  -- 电池传送及推送机构
UNION ALL
SELECT regexp_replace('["踏梯","作业机械平台总成","作业机械"]', '[\\"\\[\\]]', '')   -- 踏梯,作业机械平台总成,作业机械
UNION ALL
SELECT regexp_replace('踏梯;作业机械平台总成', '[\\"\\[\\]]', '')                   -- 电池传送及推送机构
UNION ALL
SELECT regexp_replace('作业机械平台总成', '[\\"\\[\\]]', '')                        -- 作业机械平台总成
UNION ALL
SELECT regexp_replace('广东省,广西壮族自治区,内蒙古自治区,新疆维吾尔自治区,北京市,香港特别行政区', '(市|省|维吾尔自治区|.族自治区|自治区|特别行政区)', '')
UNION ALL
SELECT regexp_replace('香港岛,保定市,果洛藏族自治州,延边朝鲜族自治州,苗栗县,恩施土家族苗族自治州,凉山彝族自治州,德宏傣族景颇族自治州,海北藏族自治州,黔东南苗族侗族自治州,红河哈尼族彝族自治州,临夏回族自治州,巴音郭楞蒙古自治州,喀什地区,大理白族自治州,阿拉善盟,黔西南布依族苗族自治州,怒江傈僳族自治州,湘西土家族苗族自治州,阿坝藏族羌族自治州,文山壮族苗族自治州,海西蒙古族藏族自治州,伊犁哈萨克自治州,直辖级县',
'(市|县|(哈萨克|蒙古族藏族|壮族苗族|藏族羌族|土家族苗族|傈僳族|布依族苗族|哈尼族彝族|苗族侗族|土家族苗族|傣族景颇族|朝鲜族|彝族|土家族|藏族|苗族|傣族|回族|蒙古|白族)自治州|自治州|地区|区|盟)',
'')
;

--- 去除两端的逗号
SELECT regexp_replace(regexp_replace(',123,456,235,789,', '^([,]+)', ''), '([,]+)$', '')
;

--- 去除特殊符号
SELECT regexp_replace('123\t456  \n235,7""89', '([\\s\\n\\t"]+)', '')
;


SELECT regexp_replace('123\t456\n\n23\r5,78"9改造\\x07郯城县城造郯', '([\\n\\r"]+)|(\\\\x([0-9A-Fa-f]{2}))', '')
;
-- 1,3-二取代脲类与硫脲类衍生物;1,3-二取代脲类与硫脲类衍生物应用
SELECT regexp_replace(replace('["1,3-二取代脲类与硫脲类衍生物","1,3-二取代脲类与硫脲类衍生物应用"]', '","', ';'), '[\\"\\[\\]]', '')



SELECT concat_ws(";", spalit('900e53ad2c6485ac348bead384d0b107;e72aa855041294f70eb035be690afe80;370492b653fbf27d40dc4f20dabc5beb', ";"), spalit('900e53ad2c6485ac348bead384d0b107;e72aa855041294f70eb035be690afe80;370492b653fbf27d40dc4f20dabc5beb', ";"))
;


SELECT str_to_map("J00000000,C00000000") AS a
;
SELECT str_to_map("J00000000:1,C00000000:1") AS a
;

SELECT MAP(split('J00000000,C00000000,J00000000', ','), split('1,1,1', ','));
SELECT split("0000000000;12140212MB0083985A;6e60cf40-3ba3-11eb-9bd5-00163e0ca5c5", ";")[1]
, split("0000000000;12140212MB0083985A;6e60cf40-3ba3-11eb-9bd5-00163e0ca5c5", ";")[2]
;

SELECT
split("2012-03-27;a319b1c84dfb93e72480f23649ab3f12;昌民 CM", ";")[0]
, split("2012-03-27;a319b1c84dfb93e72480f23649ab3f12;昌民 CM", ";")[1]
, split("2012-03-27;a319b1c84dfb93e72480f23649ab3f12;昌民 CM", ";")[2]
;


---------------------------------------------------------------------------------------------------------------------------
-- 函数原型：string regexp_extract(string source, string pattern[, bigint occurrence])
-- 参数说明：source：STRING类型。需要搜索的字符串。
--         pattern：STRING类型常量。如果pattern为空串或者pattern中没有指定group，则会报错。
--         occurrence：BIGINT类型常量。必须大于等于0，为其他类型或小于0时会报错，不指定时默认为1，表示返回第一个group。
--         如果occurrence等于0，返回满足整个pattern的子串。
-- 函数作用：将字符串source按照pattern正则表达式的规则拆分，返回第occurrence个group的字符。

SELECT regexp_extract('["电池传送及推送机构"]', '\\"(.*?)\\"', 1)                  -- 电池传送及推送机构
SELECT regexp_extract('["踏梯","作业机械平台总成","作业机械"]', '\\"(.*?)\\"', 1)   -- 踏梯
SELECT regexp_extract('["宽体卡车","防护机构"]', '\\"(.*?)\\"', 1);  
SELECT regexp_extract('["1,3-二取代脲类与硫脲类衍生物","1,3-二取代脲类与硫脲类衍生物应用"]', '\\"(.*?)\\"', 1)


-- 抽取产业词
SELECT regexp_extract('ESSI:("A102") AND AUTHORITY:(CN)', '\"(.*?)\"');
SELECT regexp_extract('ESSI:("E") AND (AP_PVC:(北京) AND AN_CITY:(北京市))', '\"(.*?)\"');
SELECT regexp_extract('ESSI:("A1") AND (AP_PVC:(天津) AND AN_CITY:(天津市) AND AN_COUNTY:(河东区))', '\"(.*?)\"');

-- 抽取地域
SELECT regexp_extract('ESSI:("E202") AND (AP_PVC:(河北省))', 'AP_PVC.*?\\((.*?)\\)');
SELECT regexp_extract('ESSI:("E") AND (AP_PVC:(北京) AND AN_CITY:(北京市))', 'AN_CITY.*?\\((.*?)\\)');
SELECT regexp_extract('ESSI:("A1") AND (AP_PVC:(天津) AND AN_CITY:(天津市) AND AN_COUNTY:(河东区))', 'AN_COUNTY.*?\\((.*?)\\)');

SELECT length(regexp_extract('123\t456\n235,789', '(\\n)'));
SELECT length(regexp_extract('123\t456\n235,789', '(\\s)'));


SELECT concat("10", "0000")
UNION ALL
SELECT concat_ws("-", "10", "0000");


-- 解析json字符串
SELECT get_json_object('{"appuserid":"2b8d6787d3874080b3691adc3c7793c7","company_name":"广州市铭汉科技股份有限公司","col3":"3,2","size":2,"recall_path":"上位词召回"}', '$.company_name') AS a;
SELECT get_json_object(replace('{"message_id":"AR092681A1"，"message_name":"METODO"}', "，", ","), '$.message_name') AS a;
-- ["513654552264708096","ISO/IEC27001信息安全管理体系"]
SELECT get_json_object('[["513654552264708096", "ISO/IEC27001信息安全管理体系"], ["467351043605270528", "SA8000社会责任管理体系认证"]]', '$[0]');


select lpad('20', 8, '0');
select concat_ws("::", lpad('20', 8, '0'), "AAA", "BBB", "20");
select regexp_replace(concat_ws("::", lpad('20', 8, '0'), "AAA", "BBB", "20"), "^\\d+::", "");
select regexp_replace(regexp_replace("00121::adfasd;00058::123sdf", "((^\\d{5})|(;\\d{5}))::", ";"), "^;", "");


SELECT get_json_object(`json`,'$.website') AS website, get_json_object(`json`,'$.name') AS `name` from (
SELECT
explode(
split(
regexp_replace(
regexp_replace(
'[{"website":"www.baidu.com","name":"百度"},{"website":"google.com","name":"谷歌"}]'
, '\\[|\\]'
,''
)
,'\\}\\,\\{'
,'\\}\\;\\{'
)
,'\\;'
)
) as `json`
) test;


-- ########################################################################################################################
-- 日期函数
-- ########################################################################################################################

-- 日期格式转换 yyyy-MM-dd HH:mm:ss ->yyyy-MM-dd'T'HH:mm:ss.SSSXXX
SELECT from_unixtime(unix_timestamp('2023-03-20 20:02:02','yyyy-MM-dd HH:mm:ss'), "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");


SELECT CAST(date_format("2023-03-20 20:02:02", "yyyy-MM-dd") AS STRING)
SELECT CAST(date_format("2023-03-20 20:02:02.0", "yyyy-MM-dd HH:mm:ss") AS STRING)
---------------------------------------------------------------------------------------------------------------------------

-- 获取当前日期
SELECT current_date();

-- 获取n天前的日期
SELECT date_sub(current_date(), 30);
SELECT date_sub("2023-06-07", 30);

-- 获取n天后的日期
SELECT date_add("2023-06-07", 30);
-- 报错：DATE_ADD() only takes TINYINT/SMALLINT/INT types as second argument, got INTERVAL_YEAR_MONTH
-- SELECT date_add("2023-06-07", INTERVAL 6 MONTH);


-- 计算日期差值
SELECT datediff('2023-03-21', '2023-03-20');
SELECT datediff(to_date('2023-03-20 20:02:02'), to_date('2023-03-15 20:02:02'));

-- 计算日期之间的月份差
SELECT ceil(months_between(end_date, start_date)) AS num_months
FROM (
select
'2020-01-01' as start_date,
'2020-03-05' as end_date
) t
;

-- json
SELECT get_json_object(replace('{"message_id":"e0384168-9055-4a3f-81ea-6b63edb5500f"，"message_name":"欧菲光集团股份有限公司"}', "，", ","), '$.message_name')
;

-- 获取年份、月份
SELECT year("2023-06-07");
SELECT month("2023-06-07");
SELECT CONCAT(YEAR("2023-06-07"), '-', LPAD(MONTH("2023-06-07"), 2, '0'));

-- 衰减系数
SELECT exp(-ln(2) / 3 * 0);


-- 列出两个日期之间的所有日期
select
start_date,
end_date,
date_add(start_date, pos) as mid_date,
pos
from(
select
'2020-01-01' as start_date,
'2020-03-05' as end_date
) tmp
lateral view posexplode(split(space(datediff(end_date, start_date)), '')) t as pos, val
;


-- 列出两个月份之间的所有月份
select
start_date,
end_date,
mid_month
from (
select
start_date,
end_date,
CONCAT(YEAR(start_date), '-', LPAD(MONTH(start_date), 2, '0')) AS start_month,
CONCAT(YEAR(end_date), '-', LPAD(MONTH(end_date), 2, '0')) AS end_month,
CONCAT(YEAR(add_months(start_date, pos)), '-', LPAD(MONTH(add_months(start_date, pos)), 2, '0')) as mid_month,
pos
from(
select
'2020-01-30' as start_date,
'2023-05-01' as end_date
) tmp
lateral view posexplode(split(space(CAST(ceil(months_between(end_date, start_date)) AS INT)), '')) t as pos, val
) t
WHERE start_month <= mid_month AND mid_month <= end_month
;





-- ########################################################################################################################
-- 聚合函数
-- ########################################################################################################################
DROP TABLE IF EXISTS website_pv_infogroup_1;
CREATE TABLE IF NOT EXISTS website_pv_infogroup_1(
cookieid STRING COMMENT 'cookieid'
, create_time STRING COMMENT '创建时间'
, pv BIGINT COMMENT 'page view'
)
COMMENT '测试示例-网站pv计算'
STORED AS ORC
TBLPROPERTIES ('external.table.purge' = 'TRUE', 'transactional'='false')
;
INSERT INTO website_pv_infogroup_1 VALUES
('cookieid1', '2023-01-01', null)
,('cookieid1', '2023-01-02', 4)
,('cookieid1', '2023-01-03', 4)
,('cookieid1', '2023-01-04', null)
,('cookieid1', '2023-01-05', null)
,('cookieid1', '2023-01-06', null)
,('cookieid1', '2023-01-07', 5)
,('cookieid2', '2023-01-01', 7)
,('cookieid2', '2023-01-02', 9)
;

SELECT
cookieid
, collect_list(pv) AS pvs
FROM website_pv_infogroup_1
GROUP BY cookieid
;


SELECT round(10.222454, 3);
SELECT round(0.210, 3);


-- 百分位[0.0,0.0,11.0,100.0,250.0,500.0,2000.0,9272.0,144603.0]
SELECT percentile(CAST(reg_capi AS BIGINT), array(0, 0.01, 0.05, 0.1, 0.2,0.25,0.5,0.75,0.95))
FROM tmp_v_algo_rs_ent_feature
;

-- ########################################################################################################################
-- 内嵌表生成函数(UDTF, Built-in Table-Generating Functions)
-- ########################################################################################################################

/*
explode() 炸裂函数
函数原型：T regexp_extract([ARRAY<T>, MAP<Tkey,Tvalue>] a)
参数说明：接收一个 array 或 map 类型的数据作为输入，然后将 array 或 map 里面的元素按照每行的形式输出。可以配合 LATERAL VIEW 一起使用。
函数作用：一行转多行

    Lateral view（侧视图）与UDTF函数一起使用:
    1）UDTF对每个输入行产生0或者多个输出行(拆分成多行); 不加lateral view的UDTF只能提取单个字段拆分,并不能塞回原来数据表中.
    2）Lateral view首先在基表的每个输入行应用UDTF，然后连接结果输出行与输入行组成拥有指定表别名的虚拟表。即将拆分的单个字段数据与原始表数据关联上。
    Syntax：
        lateralView: LATERAL VIEW udtf(expression) tableAlias AS columnAlias (',' columnAlias)*
        fromClause: FROM baseTable (lateralView)*

官方文档：https://cwiki.apache.org/confluence/display/Hive/LanguageManual+UDF#LanguageManualUDF-Built-inTable-GeneratingFunctions(UDTF)
*/
-- 输入array
select explode(array('A','B','C'));
select explode(split('A,B,C,D', ','));
-- 输入Map
select explode(map('A',10,'B',20,'C',30)) as (`key`, `value`);
-- Lateral view与UDTF函数一起使用
select
id, col
from (
select 'A,B,C,D' as chars, 1 as id
) t
lateral view explode(split(chars, ',')) tf as col;



-- ########################################################################################################################
-- 窗口函数(Windowing Functions)和窗口排序函数(Analytics Functions)
-- ########################################################################################################################
/*
窗口函数( Window functions)也叫做开窗函数、OLAP函数
1) 最大特点是:输入值是从SELECT语句的结果集中的行或多行的“窗口”中获取的。
2) 如果函数具有OVER子句，则它是窗口函数。
3) 窗口函数可以简单地解释为类似于聚合函数的计算函数，但是通过GROUP BY子句组合的常规聚合会隐藏正在聚合的各个行，最终输出一行。
4) 窗口函数聚合后还可以访问当中的各个行，并且可以将这些行中的某些属性添加到结果集中。

语法：
Function(arg1,..,argn) OVER([PARTITION BY <...>][ORDER BY <...>][<window expression>])

其中Function(arg1,..,argn)可以是下面分类中的任意一个
- 聚合函数:比如sum max avg count min等
- 排序函数:比如rank row number警
- 分析函数:比如lead lag first_value等

[PARTITION BY <..>]：分组
- 类似于group by 用于指定分组，每个分组你可以把它叫做窗口
- 如果没有PARTITTON BY那整张表的所有行就是一组

[ORDER BY <...>]：排序
- 用于指定每个分组内的数据排序规则 支持ASC、DESC

[<window expression>]：窗口表达式
- 用于指定每个窗口中操作的数据范围默认是窗口中所有N，提供了控制行范围的能力，
- 关键字是rows between，包括下面这几个选项
  preceding: 往前
  following: 往后
  current row :当前行
  unbounded: 边界
  unbounded preceding: 表示从前面的起点
  unbounded following :表示到后面的终点

窗口排序函数–row_number家族
适合TopNotch业务分析
- rank()，在每个分组中，为每行分配一个从1开始的序列号，考虑重复，挤占后续位置；
- dense_rank()，为每行分配一个从1开始的序列号，考虑重复，不挤占后续位置；
- row_number(),为每行分配一个从1开始的唯一序列号，递增，不考虑重复。

https://cwiki.apache.org/confluence/display/Hive/LanguageManual+WindowingAndAnalytics
https://blog.csdn.net/weixin_43629813/article/details/129847325
*/
DROP TABLE IF EXISTS website_pv_infogroup;
CREATE TABLE IF NOT EXISTS website_pv_infogroup(
cookieid STRING COMMENT 'cookieid'
, create_time STRING COMMENT '创建时间'
, pv BIGINT COMMENT 'page view'
)
COMMENT '测试示例-网站pv计算'
STORED AS ORC
TBLPROPERTIES ('external.table.purge' = 'TRUE', 'transactional'='false')
;
INSERT INTO website_pv_infogroup VALUES
('cookieid1', '2023-01-01', 1)
,('cookieid1', '2023-01-02', 4)
,('cookieid1', '2023-01-03', 4)
,('cookieid1', '2023-01-04', 2)
,('cookieid1', '2023-01-05', 3)
,('cookieid1', '2023-01-06', 7)
,('cookieid1', '2023-01-07', 5)
,('cookieid2', '2023-01-01', 7)
,('cookieid2', '2023-01-02', 9)
;

/*
求出每个用户总pv数 sum+group by
sum(…) over( )对表所有行求和
sum(…) over( order by …) 连续累积求和,默认从第一行到当前行
sum(…) over( partition by…) 同组内所行求和
*/
-- 按cookieid分组求和
select cookieid, sum(pv) as total_pv from website_pv_infogroup group by cookieid;
select cookieid, sum(pv) over(partition by cookieid) as total_pv from website_pv_infogroup;  -- 每行都保留

-- 求出每个用户截止到当天，累积的总pV数
select cookieid, create_time, sum(pv) over(partition by cookieid order by create_time) as total_pv from website_pv_infogroup;  -- 在每个分组内，连续累积求和

/*
求出每天uv数 count+group by
count(…) over( )对表所有行求和
count(…) over( partition by…) 同组内所行求和
*/
-- 按create_time分组求count
select create_time, count(distinct cookieid) as total_uv from website_pv_infogroup group by create_time;
select create_time, count(distinct cookieid) over(partition by create_time) as total_uv from website_pv_infogroup;  -- 每行都保留

/*
窗口表达式
*/
-- 向前3行至当前行累加
select cookieid,create_time,pv,sum(pv) over(partition by cookieid order by create_time rows between 3 preceding and current row) as pv4 from website_pv_infogroup;
-- 向前3行 向后1行累加
select cookieid,create_time,pv,sum(pv) over(partition by cookieid order by create_time rows between 3 preceding and 1 following) as pv5 from website_pv_infogroup;
-- 当前行至最后一行累加
select cookieid,create_time,pv,sum(pv) over(partition by cookieid order by create_time rows between current row and unbounded following) as pv6 from website_pv_infogroup;
-- 第一行到当前行累加: 为什么不是所有行？版本问题？
select cookieid,create_time,pv,sum(pv) over(partition by cookieid order by create_time rows between unbounded preceding and unbounded following) as pv7 from website_pv_infogroup;

/*
row_number 每天pv数最多的用户排序
*/
select
cookieid
, create_time
, pv
, rank() over(partition by cookieid order by pv desc) as `rank`
, dense_rank()  over(partition by cookieid order by pv desc) as `dense_rank`
, row_number() over(partition by cookieid order by pv desc) as `row_number`
from website_pv_infogroup
;



-- ########################################################################################################################
-- 性能调优
-- ########################################################################################################################

---------------------------------------------------------------------------------------------------------------------------
/*
1. CTE(Common Table Expression)
   公共表表达式 (CTE) 是从 WITH 子句中指定的简单查询派生的临时结果集，它紧跟在 SELECT 或 INSERT 关键字之前。CTE 仅在单个语句的执行范围内定义。
   在 Hive SELECT、INSERT、 CREATE TABLE AS SELECT或CREATE VIEW AS SELECT语句中可以使用一个或多个 CTE 。

作用：
相当于视图，定义了一个SQL片段，每次使用时候可以将该定义的SQL片段拿出来再被使用，该SQL片段可以理解为一个变量，主要用途简化SQL，让SQL更简洁，替换子查询，方便定位问题。

语法：
WITH temp_name AS (select statement)

规则：
1) 子查询块中不支持 WITH 子句；
2) 视图、CTAS(Create Table As Select)和 INSERT 语句支持 CTE；
3) 不支持递归查询。

优化：
默认情况下，如果使用CTE后被多次使用，则CTE子句就会被执行多次，若需要用HIVE CTE进行优化，则需要通过参数调优，即：
hive.optimize.cte.materialize.threshold
该参数默认值为：-1，表示不开启物化，当开启（大于等于0），比如设置为2，表示如果WITH…AS语句被引用2次及以上时，
会把WITH…AS语句生成的table物化，从而做到WITH…AS语句只执行一次，来提高效率。在默认情况下，可以通过explain来查看执行计划。
*/

SET hive.optimize.cte.materialize.threshold = 2
;
WITH t0 AS (SELECT rand() AS c0),
t1 AS (SELECT c0, rand() AS c FROM t0),
t2 AS (SELECT c0, rand() AS c FROM t0)
SELECT * FROM t1   -- c0 0.5134221478450147
union all
SELECT * FROM t2   -- c0 0.5134221478450147
;



---------------------------------------------------------------------------------------------------------------------------

/*
csv 文件导入混合云开发和管控平台hive表操作步骤
1st  先将csv文件转为 utf-8 编码格式，解决导入后编码乱码问题
2nd  将csv 文件拖拽到 文件管理 路径下
3rd  将 文件路径下刚拖入的csv文件导入 14 号机器：进入14号机器dos界面，执行命令
hdfs dfs -put -f /tmp/zhengchubin/syn_data/file_name.csv /dtb-dev/102/dev_Algo_Recommend/blacklist
4th  再开发环境创建一个hive表：注意表的设置：示例如下：详见如下建表语句
特别注意：
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTfile
5th  将3rd导入14号的的 csv文件导入开发环境, 数据开发平台执行load操作：
load data inpath '/dtb-dev/102/dev_Algo_Recommend/blacklist/file_name.csv' overwrite into table  algo_recommend_dev.table_name_list;
6th  进入14号机器dos界面，执行命令：切换到测试环境
kinit -kt /tmp/qzdsf_test.keytab  qzdsf_test
beeline -u "jdbc:hive2://vip-address.prd.xxxxxxxx.com:10099/;principal=hive/_HOST@xxxxxxxx.COM"
7th  在测试环境建表，执行4th中的建表语句：
user algo_recommend_test;
8th  把dev环境的hive表数据同步到test环境：
insert overwrite table algo_recommend_test.table_name_list select * from algo_recommend_dev.table_name_list;
9th  执行命令：切换到生产环境
kinit -kt /tmp/qzdsf_prd.keytab  qzdsf_prd
beeline -u "jdbc:hive2://vip-address.prd.xxxxxxxx.com:10099/;principal=hive/_HOST@xxxxxxxx.COM"
10th 在生产环境建表，执行4th中的建表语句：
use algo_recommend;
11th  把dev环境的hive表数据同步到prd环境：
insert overwrite table algo_recommend.table_name_list select * from algo_recommend_dev.table_name_list;
*/

-- 4th 建表示例
DROP TABLE IF EXISTS table_name_list;
CREATE EXTERNAL TABLE IF not EXISTS `table_name_list`(
`word` string COMMENT '黑名单词',
`type` string COMMENT '词类型 1:技术词 2:产品词 3:关键词 4:功效词 '
)
COMMENT '专利词黑名单'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTfile
TBLPROPERTIES ('external.table.purge' = 'TRUE')
;

-- 5th 导入 csv 到dev hive 示例：
load data inpath '/dtb-dev/102/dev_Algo_Recommend/blacklist/file_name.csv' overwrite into table  algo_recommend_dev.table_name_list;
-- 6th
kinit -kt /tmp/qzdsf_test.keytab  qzdsf_test
beeline -u "jdbc:hive2://vip-address.prd.xxxxxxxx.com:10099/;principal=hive/_HOST@xxxxxxxx.COM"
-- 7th test建表示例
use algo_recommend_test;
DROP TABLE IF EXISTS table_name_list;
CREATE EXTERNAL TABLE IF not EXISTS `table_name_list`(
`word` string COMMENT '黑名单词',
`type` string COMMENT '词类型 1:技术词 2:产品词 3:关键词 4:功效词 '
)
COMMENT '专利词黑名单'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTfile
TBLPROPERTIES ('external.table.purge' = 'TRUE')
;
-- 8th 导入数据到test
insert overwrite table algo_recommend_test.table_name_list select * from algo_recommend_dev.table_name_list;
-- 9th 切换生产环境
kinit -kt /tmp/qzdsf_prd.keytab  qzdsf_prd
beeline -u "jdbc:hive2://vip-address.prd.xxxxxxxx.com:10099/;principal=hive/_HOST@xxxxxxxx.COM"
-- 10th prd建表
user algo_recommend;
DROP TABLE IF EXISTS table_name_list;
CREATE EXTERNAL TABLE IF not EXISTS `table_name_list`(
`word` string COMMENT '黑名单词',
`type` string COMMENT '词类型 1:技术词 2:产品词 3:关键词 4:功效词 '
)
COMMENT '专利词黑名单'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTfile
TBLPROPERTIES ('external.table.purge' = 'TRUE')
;
-- 11th 导入数据到prd
insert overwrite table algo_recommend.table_name_list select * from algo_recommend_dev.table_name_list;

---------------------------------------------------------------------------------------------------------------------------






















