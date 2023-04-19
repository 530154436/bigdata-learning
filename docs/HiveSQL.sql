--
-- @Title : test_hive_op
-- @Kind  : hiveSQL
--
-- @Author: 郑楚彬
-- @Tips  :
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
SELECT substring("aba", 2, 3)  -- ba
;


---------------------------------------------------------------------------------------------------------------------------
-- 函数原型：string substring_index(string A, string delim, int count)
-- 参数说明：A: 要处理的字符串
--          delim: 分隔符
--          count: 计数
-- 函数作用：如果count是正数，返回从左往右数，第N个分隔符的左边的全部内容;
--          如果count是负数，返回从右往左数，第N个分隔符的右边的全部内容。
SELECT substring_index("a;b;c;d",';', 1)  -- a
UNION ALL
SELECT substring_index("a;b;c;d",';', -1) -- d
UNION ALL
SELECT substring_index("a;b;c;d",';', 3)  -- a;b;c
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


---------------------------------------------------------------------------------------------------------------------------
-- 函数原型：string regexp_extract(string source, string pattern[, bigint occurrence])
-- 参数说明：source：STRING类型。需要搜索的字符串。
--         pattern：STRING类型常量。如果pattern为空串或者pattern中没有指定group，则会报错。
--         occurrence：BIGINT类型常量。必须大于等于0，为其他类型或小于0时会报错，不指定时默认为1，表示返回第一个group。
--         如果occurrence等于0，返回满足整个pattern的子串。
-- 函数作用：将字符串source按照pattern正则表达式的规则拆分，返回第occurrence个group的字符。

SELECT regexp_extract('["电池传送及推送机构"]', '\\"(.*?)\\"', 1)                  -- 电池传送及推送机构
SELECT regexp_extract('["踏梯","作业机械平台总成","作业机械"]', '\\"(.*?)\\"', 1)   -- 踏梯


-- 抽取产业词
SELECT regexp_extract('ESSI:("A102") AND AUTHORITY:(CN)', '\"(.*?)\"');
SELECT regexp_extract('ESSI:("E") AND (AP_PVC:(北京) AND AN_CITY:(北京市))', '\"(.*?)\"');
SELECT regexp_extract('ESSI:("A1") AND (AP_PVC:(天津) AND AN_CITY:(天津市) AND AN_COUNTY:(河东区))', '\"(.*?)\"');

-- 抽取地域
SELECT regexp_extract('ESSI:("E202") AND (AP_PVC:(河北省))', 'AP_PVC.*?\\((.*?)\\)');
SELECT regexp_extract('ESSI:("E") AND (AP_PVC:(北京) AND AN_CITY:(北京市))', 'AN_CITY.*?\\((.*?)\\)');
SELECT regexp_extract('ESSI:("A1") AND (AP_PVC:(天津) AND AN_CITY:(天津市) AND AN_COUNTY:(河东区))', 'AN_COUNTY.*?\\((.*?)\\)');



SELECT concat("10", "0000")
UNION ALL
SELECT concat_ws("-", "10", "0000");


-- 解析json字符串
SELECT get_json_object('{"a":1,"c":"111"}', '$.a') AS a;


-- ########################################################################################################################
-- 日期函数
-- ########################################################################################################################

-- 日期格式转换 yyyy-MM-dd HH:mm:ss ->yyyy-MM-dd'T'HH:mm:ss.SSSXXX
SELECT from_unixtime(unix_timestamp('2023-03-20 20:02:02','yyyy-MM-dd HH:mm:ss'), "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");


---------------------------------------------------------------------------------------------------------------------------

-- 获取当前日期
SELECT current_date();

-- 获取n天前的日期
SELECT date_sub(current_date(), 30);

-- 计算日期差值
SELECT datediff('2023-03-21', '2023-03-20');
SELECT datediff(to_date('2023-03-20 20:02:02'), to_date('2023-03-15 20:02:02'));


-- json
SELECT get_json_object(replace('{"message_id":"e0384168-9055-4a3f-81ea-6b63edb5500f"，"message_name":"欧菲光集团股份有限公司"}', "，", ","), '$.message_name')
;


-- ########################################################################################################################
-- 聚合函数
-- ########################################################################################################################




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

