-- 切换数据库
use itcast;

/**
  一、多字节分隔符
 */

-- 情况一：加载数据的分隔符为多字节分隔符
drop table if exists singer;
create table singer(
    id       string,--歌手id
    name     string,--歌手名称
    country  string,--国家
    province string,--省份
    gender   string,--性别
    works    string--作品
)
row format delimited fields terminated by '||';

load data local inpath '/home/hive/data/cases/case01/test01.txt' into table singer;
select * from singer;

-- 情况二：数据中包含了分隔符
drop table if exists apachelog;
--创建表
create table apachelog(
    ip     string, --IP地址
    stime  string, --时间
    mothed string, --请求方式
    url    string, --请求地址
    policy string, --请求协议
    stat   string, --请求状态
    body   string  --字节大小
)
row format delimited fields terminated by ' ';

load data local inpath '/home/hive/data/cases/case01/apache_web_access.log' into table apachelog;
select * from apachelog;


------------------------解决方案一：替换分隔符------------------------
-- 重新创建Hive表并导入数据
drop table if exists singer;
create table if not exists singer(
    id       string,--歌手id
    name     string,--歌手名称
    country  string,--国家
    province string,--省份
    gender   string,--性别
    works    string--作品
)
row format delimited fields terminated by '|';

load data inpath '/data/output/changeSplit/part-m-00000' into table singer;
select * from singer;


------------------------解决方案二：RegexSerDe正则加载------------------------
-- ---------------情况1 RegexSerDe解决多字节分隔符
drop table if exists singer;
create table if not exists singer(
    id       string,--歌手id
    name     string,--歌手名称
    country  string,--国家
    province string,--省份
    gender   string,--性别
    works    string--作品
)
--指定使用RegexSerde加载数据
ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.RegexSerDe'
--指定正则表达式
WITH SERDEPROPERTIES ("input.regex" = "([0-9]*)\\|\\|([^}]*)\\|\\|([^}]*)\\|\\|([^}]*)\\|\\|([^}]*)\\|\\|([^}]*)");

load data local inpath '/home/hive/data/cases/case01/test01.txt' into table singer;
select * from singer;

---------------情况2 RegexSerDe解决数据中包含分割符
drop table if exists apachelog;
create table if not exists apachelog(
    ip     string, --IP地址
    stime  string, --时间
    mothed string, --请求方式
    url    string, --请求地址
    policy string, --请求协议
    stat   string, --请求状态
    body   string  --字节大小
)
--指定使用RegexSerde加载数据
ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.RegexSerDe'
--指定正则表达式
WITH SERDEPROPERTIES (
    "input.regex" = "([^ ]*) ([^}]*) ([^ ]*) ([^ ]*) ([^ ]*) ([0-9]*) ([^ ]*)"
);

load data local inpath '/home/hive/data/cases/case01/apache_web_access.log' into table apachelog;
select * from apachelog;


------------------------解决方案三：自定义InputFormat------------------------
add jar /tmp/jars/bigdata-hadoop-1.0-shaded.jar;

drop table if exists singer;
create table if not exists singer(
    id       string,--歌手id
    name     string,--歌手名称
    country  string,--国家
    province string,--省份
    gender   string,--性别
    works    string--作品
)
--指定使用分隔符为|
row format delimited fields terminated by '|'
stored as
--指定使用自定义的类实现解析
    inputformat 'org.zcb.mr.UserInputFormat'
    outputformat 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat';

load data local inpath '/home/hive/data/cases/case01/test01.txt' into table singer;
select * from singer;


/**
  二、URL解析函数及侧视图
 */

create table tb_url(
    id  int,
    url string
) row format delimited fields terminated by '\t';

load data local inpath '/home/hive/data/cases/case02/url.txt' into table tb_url;
select * from tb_url;

select id,
       url,
       parse_url(url, "HOST")  as host,
       parse_url(url, "PATH")  as path,
       parse_url(url, "QUERY") as query
from tb_url;

-- SemanticException 3:59 AS clause has an invalid number of aliases. Error encountered near token 'path'
select id,
       url,
       parse_url_tuple(url,"HOST","PATH","QUERY") as (host,path,query)
from tb_url;
select parse_url_tuple(url,"HOST","PATH","QUERY") as (host,path,query) from tb_url;


select a.id       as id,
       b.host     as host,
       b.path     as path,
       c.protocol as protocol,
       c.query    as query
from tb_url a
lateral view parse_url_tuple(url, "HOST", "PATH") b as host, path
lateral view parse_url_tuple(url, "PROTOCOL", "QUERY") c as protocol, query;


/**
  三、行列转换应用与实现
 */
------------------------3.2 行转列：多行转多列------------------------
create table row2col1(
    col1 string,
    col2 string,
    col3 int
) row format delimited fields terminated by '\t';
load data local inpath '/home/hive/data/cases/case03/r2c1.txt' into table row2col1;
select * from row2col1;

SELECT
    col1 as col1
    , max(case col2 when "c" then col3 else 0 end) AS c
    , max(case col2 when "d" then col3 else 0 end) AS c
    , max(case col2 when "e" then col3 else 0 end) AS c
FROM row2col1
GROUP BY col1
;

------------------------3.3 行转列：多行转单列------------------------
create table if not exists row2col2(
    col1 string,
    col2 string,
    col3 int
) row format delimited fields terminated by '\t';
load data local inpath '/home/hive/data/cases/case03/r2c2.txt' into table row2col2;
select * from row2col2;

SELECT col1, col2, concat_ws(",", collect_list(cast(col3 as string)))
FROM row2col2
GROUP BY col1, col2
;

------------------------3.4 列转行：多列转多行------------------------
drop table if exists col2row1;
create table if not exists col2row1(
    col1 string,
    c int,
    d int,
    e int
) row format delimited fields terminated by '\t';
load data local inpath '/home/hive/data/cases/case03/c2r1.txt' into table col2row1;
select * from col2row1;

SELECT col1, "c" AS col2, `c` AS col3 FROM col2row1
UNION
SELECT col1, "d" AS col2, `d` AS col3 FROM col2row1
UNION
SELECT col1, "e" AS col2, `e` AS col3 FROM col2row1
;

------------------------3.5 列转行：单列转多行------------------------
drop table if exists col2row2;
create table if not exists col2row2(
    col1 string,
    col2 string,
    col3 string
) row format delimited fields terminated by '\t';
load data local inpath '/home/hive/data/cases/case03/c2r2.txt' into table col2row2;
select * from col2row2;

select col1, col2, lv.col31 AS col3
from col2row2
lateral view explode(split(col3, ",")) lv as col31
;


/**
  四、JSON数据处理
 */
------------------------4.1 get_json_object------------------------
create table tb_json_test1(
    json string
);
load data local inpath '/home/hive/data/cases/case04/device.json' into table tb_json_test1;
select * from tb_json_test1;

select
    --获取设备名称
    get_json_object(json, "$.device")     as device,
    --获取设备类型
    get_json_object(json, "$.deviceType") as deviceType,
    --获取设备信号强度
    get_json_object(json, "$.signal")     as signal,
    --获取时间
    get_json_object(json, "$.time")       as stime
from tb_json_test1;

------------------------4.2 json_tuple------------------------
-- 单独使用
select json_tuple(json, "device", "deviceType", "signal", "time") as (device, deviceType, signal, stime)
from tb_json_test1;

-- 搭配侧视图
select json,device,deviceType,signal,stime
from tb_json_test1
lateral view json_tuple(json,"device","deviceType","signal","time") b as device,deviceType,signal,stime;

------------------------4.3 JSONSerde------------------------
create table tb_json_test2(
    device     string,
    deviceType string,
    signal     double,
    `time`     string
)
ROW FORMAT SERDE 'org.apache.hive.hcatalog.data.JsonSerDe'
STORED AS TEXTFILE;

load data local inpath '/home/hive/data/cases/case04/device.json' into table tb_json_test2;
select * from tb_json_test2;


/**
  五、窗口函数应用实例
 */
------------------------5.1 连续登陆用户-----------------------
create table tb_login(
    userid    string,
    logintime string
) row format delimited fields terminated by '\t';

load data local inpath '/home/hive/data/cases/case05/login.log' into table tb_login;
select * from tb_login;

-- 统计连续2天登录
with t as (
    select userid,
           logintime,
           date_add(logintime, 1) as nextday,
           -- 用于统计窗口内基于当前行数据向下偏移取第n行值
           lead(logintime, 1) over (partition by userid order by logintime) as nextlogin
    from tb_login
)
-- select * from t
select distinct userid from t where nextday=nextlogin
;

------------------------5.2 级联累加求和-----------------------
create table tb_money(
    userid string,
    mth    string,
    money  int
) row format delimited fields terminated by '\t';
load data local inpath '/home/hive/data/cases/case05/money.tsv' into table tb_money;
select * from tb_money;

-- 1、统计得到每个用户每个月的消费总金额
drop table if exists tb_money_mth;
create table if not exists tb_money_mth
as
select userid, mth, sum(money) as money_mth
from tb_money
group by userid, mth
;
select * from tb_money_mth;

-- 2、统计每个用户每个月累计总金额
select
    userid,
    mth,
    money_mth,
    sum(money_mth) over(partition by userid order by mth) as total_money
from tb_money_mth
;

------------------------5.3 分组TopN-----------------------
create table tb_emp(
    empno     string,
    ename     string,
    job       string,
    managerid string,
    hiredate  string,
    salary    double,
    bonus     double,
    deptno    string
) row format delimited fields terminated by '\t';

load data local inpath '/home/hive/data/cases/case05/emp.txt' into table tb_emp;
select empno,ename,salary,deptno from tb_emp;

-- 统计查询每个部门薪资最高的前两名员工的薪水
with t as (
    select empno,
           ename,
           salary,
           deptno,
           row_number() over (partition by deptno order by salary desc) as `rank`
    from tb_emp
)
select * from t where `rank` <= 2
;
