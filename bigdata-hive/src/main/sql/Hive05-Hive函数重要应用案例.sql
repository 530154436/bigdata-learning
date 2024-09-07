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


