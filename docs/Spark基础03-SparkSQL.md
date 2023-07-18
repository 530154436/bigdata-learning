[TOC]
### Spark SQL简介
#### Spark SQL发展历程
`Shark`即Hive on Spark，Shark在HiveQL方面重用了Hive中HiveQL的解析、逻辑执行计划翻译、执行计划优化等逻辑。<br>
可以近似认为仅将物理执行计划从MapReduce作业替换成了Spark作业，通过Hive的HiveQL解析，把HiveQL翻译成Spark上的RDD操作。

<img src="images/sparkSQL_shark_hive.png" width="70%" height="70%" alt="">

Hive中SQL查询的MapReduce作业转化过程：<br>
<img src="images/sparkSQL_Hive中SQL查询的MapReduce作业转化过程.png" width="30%" height="30%" align="center">

Shark的出现，使得SQL-on-Hadoop的性能比Hive有了10-100倍的提高。<br>
Shark的设计导致了两个问题：
1. 是执行计划优化`完全依赖于Hive`，不方便添加新的优化策略
2. 因为Spark是`线程级并行`，而MapReduce是进程级并行，因此，Spark在兼容Hive的实现上存在线程安全问题，导致Shark不得不使用另外一套独立维护的打了补丁的Hive源码分支

2014年6月1日Shark项目和Spark SQL项目的主持人Reynold Xin宣布：停止对Shark的开发，团队将所有资源放在Spark SQL项目上。<br>
至此，Shark的发展画上了句话，但也因此发展出两个直线：Spark SQL和Hive on Spark。
+ `Spark SQL`作为Spark生态的一员继续发展，而不再受限于Hive，只是兼容Hive
+ `Hive on Spark`是一个Hive的发展计划，该计划将Spark作为Hive的底层引擎之一，即Hive将不再受限于一个引擎，可以采用Map-Reduce、Tez、Spark等引擎

#### Spark SQL架构
Spark SQL增加了DataFrame（即带有Schema信息的RDD），使用户可以在Spark SQL中执行SQL语句。<br>
数据既可以来自RDD，也可以是Hive、HDFS、Cassandra等外部数据源，还可以是JSON格式的数据。<br>
Spark SQL目前支持Scala、Java、Python三种语言，支持SQL-2003规范。<br>
<img src="images/sparkSQL_支持的数据格式和编程语言.png" width="50%" height="50%" align="center">

`为什么推出Spark SQL？`
+ 关系数据库已经很流行
+ 关系数据库在大数据时代已经不能满足要求：<br>
  1）用户需要从不同数据源执行各种操作，包括结构化、半结构化和非结构化数据<br>
  2）用户需要执行高级分析，比如机器学习和图像处理
+ 在实际大数据应用中，经常需要融合关系查询和复杂分析算法（比如机器学习或图像处理），但是，缺少这样的系统

Spark SQL填补了这个鸿沟：
+ 首先，可以提供DataFrame API，可以对内部和外部各种数据源执行各种关系型操作
+ 其次，可以支持大数据中的大量数据源和数据分析算法

Spark SQL可以融合：传统关系数据库的结构化数据管理能力和机器学习算法的数据处理能力

Spark SQL的特点如下：
1. 容易整合（集成）。Spark SQL可以将SQL查询和Spark程序无缝集成，允许我们使用SQL或熟悉的DataFrame API在Spark程序中查询结构化数据。
2. 统一的数据访问方式。Spark SQL可以以相同方式连接到任何数据源，DataFrame和SQL提供了访问各种数据源的方法，包括Hive、JSON和JDBC。
3. 兼容Hive。Spark SQL支持HiveQL语法以及Hive SerDes和UDF（用户自定义函数），允许我们访问现有的Hive仓库。
4. 标准的数据库连接。Spark SQL支持JDBC或ODBC连接。

### 结构化数据DataFrame
DataFrame的推出，让Spark具备了处理大规模结构化数据的能力，不仅比原有的RDD转化方式更加简单易用，而且获得了更高的计算性能。<br>
Spark能够轻松实现从MySQL到DataFrame的转化，并且支持SQL查询。<br>
+ RDD是分布式的 `Java对象的集合`，但是，对象内部结构对于RDD而言却是不可知的
+ DataFrame是一种`以RDD为基础的分布式数据集`，提供了详细的结构信息

<img src="images/sparkSQL_DataFrame与RDD的区别.png" width="50%" height="50%" align="center">

DataFrame的优点：
+ 可以在Spark组件间获得更好的性能和更优的空间效率。
+ DataFrame的突出优点是表达能力强、简洁、易组合、风格一致。

#### DataFrame的创建和保存
##### Parquet
Parquet是Spark的默认数据源，很多大数据处理框架和平台都支持Parquet格式，它是一种开源的列式存储文件格式，提供多种I/O优化措施。<br>
比如压缩，以节省存储空间，支持快速访问数据列；存储Parquet文件的目录中包含了`_SUCCESS文件`和很多像`part-XXXXX`这样的压缩文件。<br>
```scala
// 将DataFrame保存为Parquet文件
df.write.format("parquet").mode("overwrite").option("compression","snappy").save(s"file://${path}")
// 读取Parquet文件
val df_read = sparkSession.read.parquet(s"file://${path}")
```

##### JSON
JSON（JavaScript Object Notation）是一种常见的数据格式，与XML相比，JSON的可读性更强，更容易解析。<br>
JSON有两种表示格式，即`单行模式`和`多行模式`，这两种模式Spark都支持。<br>
```scala
// 从JSON文件创建DataFrame
val df = sparkSession.read.format("json").load(path.toString)
// 将DataFrame保存为JSON文件
df.write.format("json").mode("overwrite").save(s"file://${otherPath}")
```

##### CSV
CSV是一种将所有的数据字段用逗号隔开的文本文件格式，在这些用逗号隔开的字段中，每行表示一条记录。<br>
CSV文件已经和普通的文本文件一样被广泛使用。<br>
```scala
// 从CSV文件创建DataFrame
val df = sparkSession.read.format("csv")
  .schema(schema)               // 用于设置每行数据的模式，也就是每行记录包含哪些字段，每个字段是什么数据类型
  .option("header","true")      // 用于表明这个CSV文件是否包含表头
  .option("sep", ",")           // 用于表明这个CSV文件中字段之间使用的分割符是分号，默认使用逗号作为分隔符
  .load(path.toString)
// 将DataFrame保存为JSON文件
df.write.format("json").mode("overwrite").save(s"file://${otherPath}")
```

##### 文本文件
```scala
// 从文本文件创建DataFrame
val df = sparkSession.read.format("text").load(path.toString)
// 把DataFrame保存成文本文件
df.write.format("text").saveAsTable(s"file://${otherPath}")
```

### 参考引用
+ [子雨大数据之Spark入门教程（Scala版）](https://dblab.xmu.edu.cn/blog/924/)












