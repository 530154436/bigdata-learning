## 一、Hive内置运算符
随着Hive版本的不断发展，在Hive SQL中支持的、内置的运算符也越来越多。可以使用下面的命令查看当下支持的运算符和函数，并且查看其详细的使用方式。
```sql
--显示所有的函数和运算符
show functions;
--查看运算符或者函数的使用说明
describe function +;
--使用extended 可以查看更加详细的使用说明
describe function extended +;
```
<img src="images/hive04_01.png" width="100%" height="100%" alt=""><br>

从Hive 0.13.0开始，select查询语句FROM关键字是可选的（例如SELECT 1+1）。因此可以使用这种方式来练习测试内置的运算符、函数的功能。
除此之外，还可以通过创建一张虚表dual来满足于测试需求。
```sql
--1、创建表dual
create table dual(id string);
--2、加载一个文件dual.txt到dual表中（dual.txt只有一行内容：内容为一个空格）
-- echo " " > dual.txt
-- $HADOOP_HOME/bin/hdfs dfs -put -f /home/hive/dual.txt /user/hive/warehouse/itcast.db/dual
--3、在select查询语句中使用dual表完成运算符、函数功能测试
select 1+1 from dual;
```

### 1.1 关系运算符
关系运算符是二元运算符，执行的是两个操作数的比较运算。每个关系运算符都返回`boolean类型`结果（TRUE或FALSE）。
+ 等值比较: = 、==
+ 不等值比较: <> 、!=
+ 小于比较: <
+ 小于等于比较: <=
+ 大于比较: >
+ 大于等于比较: >=
+ 空值判断: `IS NULL`
+ 非空判断: `IS NOT NULL`
+ LIKE比较: `LIKE`
+ JAVA的LIKE操作: `RLIKE`
+ REGEXP操作: REGEXP
```sql
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
```

### 1.2 算术运算符
算术运算符操作数必须是`数值类型`。 分为一元运算符和二元运算符; 一元运算符,只有一个操作数; 二元运算符有两个操作数,运算符在两个操作数之间。
+ 加法操作: +
+ 减法操作: -
+ 乘法操作: *
+ 除法操作: /
+ 取整操作: `div`
+ 取余操作: `%`
+ 位与操作: `&`
+ 位或操作: `|`
+ 位异或操作: `^`
+ 位取反操作: ~
```sql
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
```

### 1.2 逻辑运算符

+ 与操作: A AND B
+ 或操作: A OR B
+ 非操作: NOT A 、!A
+ 在:A IN (val1, val2, ...)
+ 不在:A NOT IN (val1, val2, ...)
+ 逻辑是否存在: [NOT] EXISTS (subquery)

```sql
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
```

## 二、Hive函数
### 2.1 内置函数
### 2.1 内置函数

## 参考引用
[1] [黑马程序员-Apache Hive 3.0](https://book.itheima.net/course/1269935677353533441/1269937996044476418/1269942232408956930) <br>
[2] [Apache Hive - LanguageManual UDF](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+UDF) <br>
