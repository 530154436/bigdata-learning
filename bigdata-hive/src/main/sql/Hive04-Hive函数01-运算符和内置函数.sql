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
  Hive内置函数-
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
