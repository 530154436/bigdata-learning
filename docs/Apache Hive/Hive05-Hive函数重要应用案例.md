
## 一、多字节分隔符
### 1.1 应用场景
Hive中默认使用单字节分隔符来加载文本数据，例如逗号、制表符、空格等等，默认的分隔符为`\001`。根据不同文件的不同分隔符，可以通过在创建表时使用
`row format delimited fields terminated by ‘单字节分隔符’ `来指定文件中的分割符，确保正确将表中的每一列与文件中的每一列实现一一对应的关系。
在实际工作中，我们遇到的数据往往不是非常规范化的数据，例如我们会遇到以下的两种情况
+ 情况一：每一行数据的分隔符是`多字节分隔符`，例如：”||”、“--”等<br>
  <img src="images/hive05_01.png" width="80%" height="80%" alt=""><br>

+ 数据的字段中包含了分隔符，如下图中每列的分隔符为空格，但是数据中包含了分隔符，时间字段中也有空格<br>
  <img src="images/hive05_02.png" width="100%" height="100%" alt=""><br>

### 1.2 问题
基于上述的两种特殊数据，如果使用正常的加载数据的方式将数据加载到表中，就会出以下两种错误。
#### 情况一：加载数据的分隔符为多字节分隔符
```sql
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
```
<img src="images/hive05_03.png" width="100%" height="100%" alt=""><br>

+ 问题：数据发生了错位，没有正确的加载每一列的数据
+ 原因：**Hive中默认只支持单字节分隔符，无法识别多字节分隔符**

#### 情况二：数据中包含了分隔符
```sql
drop table if exists apachelog;
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
```
<img src="images/hive05_04.png" width="100%" height="100%" alt=""><br>

+ 问题：时间字段被切分成了两个字段，后面所有的字段出现了错位
+ 原因：时间数据中包含了分隔符，导致Hive认为这是两个字段，但实际业务需求中为一个字段


### 1.3 解决方案
