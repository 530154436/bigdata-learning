-- 切换数据库
use itcast;

--显示所有的函数和运算符
show functions;
--查看运算符或者函数的使用说明
describe function +;  -- a + b - Returns a+b
--使用extended 可以查看更加详细的使用说明
describe function extended +;

--1、创建表dual
create table dual(id string);
--2、加载一个文件dual.txt到dual表中（dual.txt只有一行内容：内容为一个空格）
-- echo " " > dual.txt
-- $HADOOP_HOME/bin/hdfs dfs -put -f /home/hive/dual.txt /user/hive/warehouse/itcast.db/dual
--3、在select查询语句中使用dual表完成运算符、函数功能测试
select 1+1 from dual;


/**
  Hive内置运算符-关系运算符
 */
--空值判断
select 'itcast' is null;            -- false
select 'itcast' is not null;        -- true

--like比较： _表示任意单个字符 %表示任意数量字符
--否定比较： NOT A like B
select 'itcast' like 'it_';         -- false
select 'itcast' like 'it%';         -- true
select 'itcast' like 'hadoo_';      -- false

--rlike：确定字符串是否匹配正则表达式，是REGEXP_LIKE()的同义词。
select 'itcast' rlike '^i.*t$';     -- true
select '123456' rlike '^\\d+$';     -- true
select '123456aa' rlike '^\\d+$';   -- false

--regexp：功能与rlike相同 用于判断字符串是否匹配正则表达式
select 'itcast' regexp '^i.*t$';    -- true


/**
  Hive内置运算符-算术运算符
 */
--取整操作: div  给出将A除以B所得的整数部分。例如17 div 3得出5。
select 17 div 3;    -- 5

--取余操作: %  也叫做取模  A除以B所得的余数部分
select 17 % 3;  -- 5

--位与操作: &  A和B按位进行与操作的结果。 与表示两个都为1则结果为1
select 4 & 8;  -- 4转换二进制：0100、8转换二进制：1000 => 0
select 6 & 4;  -- 4转换二进制：0100、6转换二进制：0110 => 4

--位或操作: |  A和B按位进行或操作的结果  或表示有一个为1则结果为1
select 4 | 8;  -- 4转换二进制：0100、8转换二进制：1000 => 12
select 6 | 4;  -- 4转换二进制：0100、6转换二进制：0110 => 6

--位异或操作: ^ A和B按位进行异或操作的结果 异或表示两个不同则结果为1
select 4 ^ 8;  -- 4转换二进制：0100、8转换二进制：1000 => 12
select 6 ^ 4;  -- 4转换二进制：0100、6转换二进制：0110 => 2


/**
Hive内置运算符-逻辑运算符
*/
--与操作: A AND B   如果A和B均为TRUE，则为TRUE，否则为FALSE。如果A或B为NULL，则为NULL。
select 3>1 and 2>1; -- true
--或操作: A OR B   如果A或B或两者均为TRUE，则为TRUE，否则为FALSE。
select 3>1 or 2!=2; -- true
--非操作: NOT A 、!A   如果A为FALSE，则为TRUE；如果A为NULL，则为NULL。否则为FALSE。
select not 2>1; -- false
select !2=1;    -- true
--在:A IN (val1, val2, ...)  如果A等于任何值，则为TRUE。
select 1 from dual where 11 in(11,22,33);
--不在:A NOT IN (val1, val2, ...) 如果A不等于任何值，则为TRUE
select 1 from dual where 11 not in(22,33,44);

/**
Hive内置运算符-复杂数据类型运算符
*/
SELECT arr[0] AS A, arr[1] AS B
FROM (
    SELECT split("A;B;C", ";") AS arr
    FROM dual
) t;


SELECT `map`['John'] AS A, `map`['Jane'] AS B
FROM (
    SELECT map('John', 'Manager', 'Jane', 'Analyst') AS `map`
    FROM dual
) t;

SELECT ns.location AS A, ns.founded
FROM (
     SELECT
         named_struct('location', 'New York', 'founded', 1999, 'employees', 500) AS ns
     FROM dual
 ) t;

--查看函数的使用说明
describe function year;
--使用extended可以查看更加详细的使用说明
describe function extended year;


/**
  Hive内置函数-字符串
*/
--字符串长度函数：length(str | binary)
select length("angelababy");

--字符串反转函数：reverse
select reverse("angelababy");

--字符串连接函数：concat(str1, str2, ... strN)
select concat("angela","baby");

--带分隔符字符串连接函数：concat_ws(separator, [string | array(string)]+)
select concat_ws('.', 'www', array('itcast', 'cn'));

--字符串截取函数：substr(str, pos[, len]) 或者  substring(str, pos[, len])
select substr("angelababy",-2);     --pos是从1开始的索引，如果为负数则倒着数：by
select substr("angelababy", 2, 2);  -- ng

--字符串转大写函数：upper,ucase
select upper("angelababy");
select ucase("angelababy");

--字符串转小写函数：lower,lcase
select lower("ANGELABABY");
select lcase("ANGELABABY");

--去空格函数：trim 去除左右两边的空格
select trim(" angelababy ");

--左边去空格函数：ltrim
select ltrim(" angelababy ");

--右边去空格函数：rtrim
select rtrim(" angelababy ");

--正则表达式替换函数：regexp_replace(str, regexp, rep)
select regexp_replace('100-200', '(\\d+)', 'num');

--正则表达式解析函数：regexp_extract(str, regexp[, idx]) 提取正则匹配到的指定组内容
select regexp_extract('100-200', '(\\d+)-(\\d+)', 2);

--URL解析函数：parse_url 注意要想一次解析出多个 可以使用parse_url_tuple这个UDTF函数
select parse_url('http://www.itcast.cn/path/p1.php?query=1', 'HOST');

--json解析函数：get_json_object
WITH t AS (
    SELECT '{"store":{"bicycle":{"price":19.95,"color":"red"}},' ||
           '"email":["amy@only_for_json_udf_test.net"],' ||
           '"owner":"amy"}' AS json
)
SELECT get_json_object(t.json, '$.owner') FROM t                -- amy
UNION ALL
SELECT get_json_object(t.json, '$.email[0]') FROM t             -- amy@only_for_json_udf_test.net
UNION ALL
SELECT get_json_object(t.json, '$.store.bicycle.price') FROM t  -- 19.95
;
SELECT get_json_object('[["123", "管理体系"]]', '$[0][0]');       -- 123


--空格字符串函数：space(n) 返回指定个数空格
select space(4);

--重复字符串函数：repeat(str, n) 重复str字符串n次
select repeat("angela",2);

--首字符ascii函数：ascii
select ascii("angela");  --a对应ASCII 97

--左补足函数：lpad
select lpad('hi', 5, '??');  --???hi
select lpad('hi', 1, '??');  --h

--右补足函数：rpad
select rpad('hi', 5, '??');  --hi???
select rpad('hi', 1, '??');  --h

--分割字符串函数: split(str, regex)
select split('apache hive', '\\s+');

--集合查找函数: find_in_set(str,str_array)
select find_in_set('b','abc,b,ab,c,def');
describe function extended find_in_set;


/**
  Hive内置函数-日期函数
 */
--获取当前日期: current_date
select current_date();  -- 2024-09-02

--获取当前时间戳: current_timestamp
--同一查询中对current_timestamp的所有调用均返回相同的值。
select current_timestamp();  -- 2024-09-02 20:44:58.714000000

--时间戳函数
select unix_timestamp();                                        -- 获取当前UNIX时间戳：1725281120
select unix_timestamp("2024-09-02 12:45:20");                   -- 日期转UNIX时间戳：1725281120
select unix_timestamp('20240902 12:45:20','yyyyMMdd HH:mm:ss'); -- 指定格式日期转UNIX时间戳：1725281120

--UNIX时间戳转日期函数: from_unixtime
select from_unixtime(1725281120);                -- 2024-09-02 12:45:20
select from_unixtime(0, 'yyyy-MM-dd HH:mm:ss');  -- 1970-01-01 00:00:00

--抽取日期函数: to_date
select to_date('2009-07-30 04:17:52');

--日期转年、月、日、天、小时、分钟、秒、周:
select year('2009-07-30 04:17:52');
select month('2009-07-30 04:17:52');
select day('2009-07-30 04:17:52');
select hour('2009-07-30 04:17:52');
select minute('2009-07-30 04:17:52');
select second('2009-07-30 04:17:52');
select weekofyear('2009-07-30 04:17:52');

--日期比较函数: datediff  日期格式要求'yyyy-MM-dd HH:mm:ss' or 'yyyy-MM-dd'
select datediff('2012-12-08','2012-05-09');

--日期增加函数: date_add
select date_add('2012-02-28',10);

--日期减少函数: date_sub
select date_sub('2012-01-1',10);


/**
  Hive内置函数-数学函数
 */
--取整函数: round  返回double类型的整数值部分 （遵循四舍五入）
select round(3.1415926);    -- 3
select round(3.1415926,4);  -- 3.1416 指定精度取整函数: round(double a, int d) 返回指定精度d的double类型

--向下取整函数: floor
select floor(3.1415926);    -- 3
select floor(-3.1415926);   -- -4

--向上取整函数: ceil
select ceil(3.1415926);     -- 4
select ceil(-3.1415926);    -- -3

--取随机数函数: rand 每次执行都不一样 返回一个0到1范围内的随机数
select rand();
select rand(2); --指定种子取随机数函数: rand(int seed) 得到一个稳定的随机数序列

--二进制函数:  bin(BIGINT a)
select bin(18);

--进制转换函数: conv(BIGINT num, int from_base, int to_base)
select conv(17, 10, 16);    -- 0x11

--绝对值函数: abs
select abs(-3.9);   -- 3.9


/**
  Hive内置函数-集合函数（Collection Functions）
 */
--集合元素size函数: size(Map<K.V>) size(Array<T>)
select size(array(11,22,33));                               -- 3
select size(map("id",10086,"name","zhangsan","age",18));    -- 3

--取map集合keys函数: map_keys(Map<K.V>)
select map_keys(map("id",10086,"name","zhangsan","age",18));    -- ["id","name","age"]

--取map集合values函数: map_values(Map<K.V>)
select map_values(map("id",10086,"name","zhangsan","age",18));  -- ["10086","zhangsan","18"]

--判断数组是否包含指定元素: array_contains(Array<T>, value)
select array_contains(array(11,22,33),11);  -- true
select array_contains(array(11,22,33),66);  -- false

--数组排序函数:sort_array(Array<T>)
select sort_array(array(12,2,32));  -- [2,12,32]


/**
  Hive内置函数-条件函数
 */
--if条件判断: if(boolean testCondition, T valueTrue, T valueFalseOrNull)
select if(1=2, 100, 200);   -- 200
select `name`, if(sex ='男', 'M', 'W') from itheima.student limit 3;

--空判断函数: isnull( a )
select isnull("allen"); -- false
select isnull(null);    -- true

--非空判断函数: isnotnull ( a )
select isnotnull("allen");  -- true
select isnotnull(null);     -- false

--空值转换函数: nvl(T value, T default_value)
select nvl("allen","itcast");   -- allen
select nvl(null,"itcast");      -- itcast

--非空查找函数: COALESCE(T v1, T v2, ...)
--返回参数中的第一个非空值；如果所有值都为NULL，那么返回NULL
select COALESCE(null,11,22,33);     -- 11
select COALESCE(null,null,null,33); -- 33
select COALESCE(null,null,null);    -- null

--条件转换函数:
-- CASE a WHEN b THEN c [WHEN d THEN e]* [ELSE f] END
select case 100 when 50 then 'tom' when 100 then 'mary' else 'tim' end;     -- mary
select
    `name`
    , case `sex`
        when '男' then 'man'
        when '女' then 'women'
        else 'unknow'
    end AS sex_en
from itheima.student;

--nullif( a, b ):
-- 如果a = b，则返回NULL；否则返回NULL。否则返回一个
select nullif(11, 11);  -- null
select nullif(11, 12);  -- 11

--assert_true(condition)：
-- 如果'condition'不为真，则引发异常，否则返回null
SELECT assert_true(11 >= 0);    -- NULL
SELECT assert_true(-1 >= 0);    -- HiveException: ASSERT_TRUE(): assertion failed.


/**
  Hive内置函数-类型转换函数
 */
select cast(12.14 as bigint);   -- 12
select cast(12.14 as string);   -- 12.14


/**
  Hive内置函数-数据脱敏函数
 */
--mask
--将查询回的数据，大写字母转换为X，小写字母转换为x，数字转换为n。
select mask("abc123DEF");               -- xxxnnnXXX
select mask("abc123DEF",'-','.','^');   -- ...^^^---

--mask_first_n(string str[, int n]
--对前n个进行脱敏替换
select mask_first_n("abc123DEF", 4);    -- xxxn23DEF

--mask_last_n(string str[, int n])
select mask_last_n("abc123DEF",4);      -- abc12nXXX

--mask_show_first_n(string str[, int n])
--除了前n个字符，其余进行掩码处理
select mask_show_first_n("abc123DEF", 4);   -- abc1nnXXX

--mask_show_last_n(string str[, int n])
select mask_show_last_n("abc123DEF", 4);    -- xxxnn3DEF

--mask_hash(string|char|varchar str)
--返回字符串的hash编码。
select mask_hash("abc123DEF");  -- 86fedeec79b2020...


/**
  Hive内置函数-其他杂项函数
 */

--hive调用java方法: java_method(class, method[, arg1[, arg2..]])
select java_method("java.lang.Math", "max", 11, 22);    -- 22

--反射函数: reflect(class, method[, arg1[, arg2..]])
select reflect("java.lang.Math","max", 11, 22);   -- 22

--取哈希值函数:hash
select hash("allen");   -- 92905994

--current_user()、logged_in_user()、current_database()、version()
--SHA-1加密: sha1(string/binary)
select sha1("allen");   -- a4aed34f4966dc8688b8e67046bf8b276626e284

--SHA-2家族算法加密：sha2(string/binary, int)  (SHA-224, SHA-256, SHA-384, SHA-512)
select sha2("allen",224);   -- 792eef8d0e63...
select sha2("allen",512);   -- 43ecb6c48548...

--crc32加密:
select crc32("allen");  -- 3771531426

--MD5加密: md5(string/binary)
select md5("allen");    -- a34c3d45b6...


/**
  Hive内置函数-内置聚合函数（基础、增强）
 */
-- 测试案例
drop table student;
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
;
LOAD DATA LOCAL INPATH '/home/hive/students.txt' INTO TABLE student;
SELECT * FROM student;


select count(*) as cnt1, count(1) as cnt2 from itheima.student; --两个一样
select `sex`, count(*) as cnt from student group by sex;
select
    count(*) as cnt
    , count(distinct dept) AS dept_distict
    , avg(age) as age_avg
    , min(age) as age_min
    , max(age) as age_max
    , sum(age) as age_sum
from student;

--聚合参数不支持嵌套聚合函数
select avg(count(*))  from student; --  Not yet supported place for UDAF 'count'

--聚合参数针对null的处理方式
select max(null), min(null), count(null);   -- null null 0
select sum(null), avg(null);                -- 这两个不支持null UDFArgumentTypeException

--场景5：聚合操作时针对null的处理，可以使用coalesce函数解决
select
    sum(coalesce(val1, 0))            -- 3
    , sum(coalesce(val1, 0) + val2)   -- 10
from (
    select 1 AS val1, 2 AS val2
    union all
    select null AS val1, 2 AS val2
    union all
    select 2 AS val1, 3 AS val2
) t;

-- 聚集所有的性别
select collect_set(sex) from student;   -- ["男","女"]
select collect_list(sex) from student;  -- ["男","女","女"...]

-- 计算百分位
-- [17.0,17.21,18.0,18.0,18.0,18.0,19.0,20.0,21.95]
SELECT percentile(CAST(age AS BIGINT), array(0, 0.01, 0.05, 0.1, 0.2,0.25,0.5,0.75,0.95))
FROM  student;

-- 18.571428571428573
SELECT percentile_approx(CAST(age AS BIGINT), 0.5, 10000)
FROM  student;

------GROUPING SETS---------------
--grouping_id表示这一组结果属于哪个分组集合，
--根据grouping sets中的分组条件sex、dept，1代表sex、2代表dept
SELECT
     sex
     , dept
     , COUNT(DISTINCT num)  AS nums
     , GROUPING__ID
FROM student
GROUP BY sex, dept
GROUPING SETS (sex, dept, (sex, dept))
ORDER BY GROUPING__ID;

-- <=等价于=>
SELECT sex, NULL, COUNT(DISTINCT num) AS nums,1 AS GROUPING__ID FROM student GROUP BY sex
UNION ALL
SELECT NULL as sex, dept, COUNT(DISTINCT num) AS nums,2 AS GROUPING__ID FROM student GROUP BY dept
UNION ALL
SELECT sex, dept, COUNT(DISTINCT num) AS nums, 3 AS GROUPING__ID FROM student GROUP BY sex, dept;

------cube---------------
SELECT
    sex,
    dept,
    COUNT(DISTINCT num) AS nums,
    GROUPING__ID
FROM student
GROUP BY sex, dept
WITH CUBE
ORDER BY GROUPING__ID;

-- <=等价于=>
SELECT NULL, NULL, COUNT(DISTINCT num) AS nums, 0 AS GROUPING__ID FROM student
UNION ALL
SELECT sex, NULL, COUNT(DISTINCT num) AS nums,1 AS GROUPING__ID FROM student GROUP BY sex
UNION ALL
SELECT NULL, dept, COUNT(DISTINCT num) AS nums,2 AS GROUPING__ID FROM student GROUP BY dept
UNION ALL
SELECT sex, dept, COUNT(DISTINCT num) AS nums,3 AS GROUPING__ID FROM student GROUP BY sex, dept;

--rollup-------------
--比如，以sex维度进行层级聚合：
SELECT
    sex,
    dept,
    COUNT(DISTINCT num) AS nums,
    GROUPING__ID
FROM student
GROUP BY sex, dept
WITH ROLLUP
ORDER BY GROUPING__ID;

-- <=等价于=>
SELECT NULL, NULL, COUNT(DISTINCT num) AS nums, 0 AS GROUPING__ID FROM student
UNION ALL
SELECT sex, NULL, COUNT(DISTINCT num) AS nums,1 AS GROUPING__ID FROM student GROUP BY sex
UNION ALL
SELECT sex, dept, COUNT(DISTINCT num) AS nums,3 AS GROUPING__ID FROM student GROUP BY sex, dept;


/**
  Hive内置函数-内置表生成函数
 */
--explode-------------
-- 输入array
select explode(array('A','B','C'));
select explode(split('A,B,C,D', ','));

-- 输入Map
select explode(map('A',10,'B',20,'C',30)) as (`key`, `value`);

-- 报错：UDTF's are not supported outside the SELECT clause, nor nested in expressions
select id, explode(split(chars, ','))
from (select 'A,B,C,D' as chars, 1 as id) t;

-- Lateral view与UDTF函数一起使用
select
    t.id
    , tf.`col`
from (select 'A,B,C,D' as chars, 1 as id) t
lateral view explode(split(chars, ',')) tf as `col`;

--posexplode-------------

-- 输入array
select posexplode(array('A','B','C'));
select posexplode(array('A','B','C')) as (pos, val);

-- Lateral view与UDTF函数一起使用
select t.id, tf.*
from (select 0 AS id) t
lateral view posexplode(array('A','B','C')) tf as pos,val;


--json_tuple-------------

-- 输入json字符串
WITH t AS (
    SELECT '{"store":{"bicycle":{"price":19.95,"color":"red"}},' ||
           '"email":["amy@only_for_json_udf_test.net"],' ||
           '"owner":"amy"}' AS json
)
SELECT json_tuple(t.json, 'store', 'owner', 'email') FROM t
;

-- Lateral view与UDTF函数一起使用
SELECT t.*, store, owner, email
FROM (SELECT '{"store":{"bicycle":{"price":19.95,"color":"red"}},' ||
             '"email":["amy@only_for_json_udf_test.net"],' ||
             '"owner":"amy"}' AS json) t
LATERAL VIEW json_tuple(t.json, 'store', 'owner', 'email') b as store, owner, email;
;


/**
  窗口函数
 */
---建表并且加载数据
create table website_pv_info(
    cookieid   string   comment "cookieid",
    createtime string   comment "访问时间",
    pv         int      comment "pv数（页面浏览数）"
)
COMMENT "website_pv_info.txt"
row format delimited
    fields terminated by ',';

create table website_url_info(
    cookieid   string   comment "cookieid",
    createtime string   comment "访问时间",
    url        string   comment "访问页面"
)
COMMENT "website_url_info.txt"
row format delimited
    fields terminated by ',';

load data local inpath '/home/hive/data/website_pv_info.txt' into table website_pv_info;
load data local inpath '/home/hive/data/website_url_info.txt' into table website_url_info;

select * from website_pv_info;
select * from website_url_info;

-----窗口聚合函数的使用-----------

--需求：求出网站总的pv数、所有用户所有访问加起来
select cookieid,createtime,pv,sum(pv) over() as pv_total
from website_pv_info;

--需求：求出每个用户总pv数
select cookieid,createtime,pv,sum(pv) over(partition by cookieid) as pv_total
from website_pv_info;

--需求：求出每个用户截止到当天，累积的总pv数
select cookieid,createtime,pv,sum(pv) over(partition by cookieid order by createtime) as pv_total
from website_pv_info;

--需求：求出每个用户最近3天pv之和（包含当天在内）
-- 指定的窗口包括当前行、当前行-1、当前行-2，总共3行。
select cookieid,createtime,pv
       , sum(pv) over(partition by cookieid order by createtime rows between 2 preceding and current row) as pv_total
from website_pv_info;


-----窗口表达式-----------
--第一行到当前行
select cookieid,createtime,pv,
       sum(pv) over(partition by cookieid order by createtime rows between unbounded preceding and current row) as pv2
from website_pv_info;

--向前3行至当前行
select cookieid,createtime,pv,
       sum(pv) over(partition by cookieid order by createtime rows between 3 preceding and current row) as pv4
from website_pv_info;

--向前3行 向后1行
select cookieid,createtime,pv,
       sum(pv) over(partition by cookieid order by createtime rows between 3 preceding and 1 following) as pv5
from website_pv_info;

--当前行至最后一行
select cookieid,createtime,pv,
       sum(pv) over(partition by cookieid order by createtime rows between current row and unbounded following) as pv6
from website_pv_info;

--第一行到最后一行 也就是分组内的所有行
select cookieid,createtime,pv,
       sum(pv) over(partition by cookieid order by createtime rows between unbounded preceding  and unbounded following) as pv6
from website_pv_info;


-----窗口排序函数-----------
SELECT
    cookieid,
    createtime,
    pv,
    RANK() OVER(PARTITION BY cookieid ORDER BY pv desc) AS `RANK`,
    DENSE_RANK() OVER(PARTITION BY cookieid ORDER BY pv desc) AS `DENSE_RANK`,
    ROW_NUMBER() OVER(PARTITION BY cookieid ORDER BY pv DESC) AS `ROW_NUMBER`,

    --需求：统计每个用户pv数最多的前3分之1天。
    --理解：将数据根据cookieid分 根据pv倒序排序 排序之后分为3个部分 取第一部分
    NTILE(3) OVER (PARTITION BY cookieid ORDER BY pv DESC) AS `NTILE`
FROM website_pv_info
WHERE cookieid = 'cookie1';


-----------窗口分析函数----------
--LAG
SELECT cookieid,
       createtime,
       url,
       ROW_NUMBER() OVER (PARTITION BY cookieid ORDER BY createtime)                               AS rn,
       LAG(createtime, 1, '1970-01-01 00:00:00') OVER (PARTITION BY cookieid ORDER BY createtime)  AS LAG1,
       LAG(createtime, 2) OVER (PARTITION BY cookieid ORDER BY createtime)                         AS LAG2,

       LEAD(createtime, 1, '1970-01-01 00:00:00') OVER (PARTITION BY cookieid ORDER BY createtime) AS LEAD1,
       LEAD(createtime, 2) OVER (PARTITION BY cookieid ORDER BY createtime)                        AS LEAD2,

       FIRST_VALUE(url) OVER (PARTITION BY cookieid ORDER BY createtime)                           AS FIRST_VALUE,
       LAST_VALUE(url) OVER (PARTITION BY cookieid ORDER BY createtime)                            AS LAST_VALUE
FROM website_url_info;


-----------抽样函数----------
--需求：随机抽取2个学生的情况进行查看
SELECT * FROM website_url_info SORT BY rand() LIMIT 2;
SELECT * FROM website_url_info DISTRIBUTE BY rand() SORT BY rand() LIMIT 2;

--使用order by+rand也可以实现同样的效果 但是效率不高 23s
SELECT * FROM website_url_info ORDER BY rand() LIMIT 2;


---block抽样
--根据行数抽样
SELECT * FROM website_url_info TABLESAMPLE(1 ROWS);
--根据数据大小百分比抽样
SELECT * FROM website_url_info TABLESAMPLE(50 PERCENT);
--根据数据大小抽样
--支持数据单位 b/B, k/K, m/M, g/G
SELECT * FROM website_url_info TABLESAMPLE(1k);


---bucket table抽样
--根据整行数据进行抽样
SELECT * FROM website_url_info TABLESAMPLE(BUCKET 1 OUT OF 2 ON rand());

--根据分桶字段进行抽样 效率更高
describe formatted website_url_info;
SELECT * FROM website_url_info TABLESAMPLE(BUCKET 1 OUT OF 2 ON `createtime`);


