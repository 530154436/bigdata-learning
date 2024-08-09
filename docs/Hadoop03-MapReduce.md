## 一、MapReduce概述
### 1.1 MapReduce核心思想

MapReduce的核心思想是“`分而治之`”，即将一个复杂的问题分解为多个小规模的部分，再逐一解决，并汇总各部分的结果形成最终答案。
MapReduce作为一种分布式计算模型，它主要用于解决海量数据的计算问题。使用MapReduce操作海量数据时，每个MapReduce程序被初始化为一个工作任务`Job`，每个工作任务可以分为`Map`和`Reduce`两个阶段：

+ Map阶段：负责将任务分解，即把复杂的任务分解成若干个“简单的任务”来并行处理，但前提是这些任务没有必然的依赖关系，可以单独执行任务。
+ Reduce阶段：负责将任务合并，即把Map阶段的结果进行全局汇总。

即使用户不懂分布式计算框架的内部运行机制，但只要能用Map和Reduce思想描述清楚要处理的问题，就能轻松地在Hadoop集群上实现分布式计算功能。

### 1.2 MapReduce编程模型

MapReduce编程模型借鉴了函数式程序设计语言的设计思想，其程序实现过程是通过map()和reduce()函数来完成的。从数据格式上来看，map()函数接收的数据格式是键值对，产生的输出结果也是键值对形式，reduce()函数会将map()函数输出的键值对作为输入，把相同key值的value进行汇总，输出新的键值对。 简易数据流模型的相关说明，具体如下：
```
(input) <k1, v1> -> map -> <k2, v2> -> group -> <K2，{V2，…}> -> reduce -> <k3, v3> (output)
```
1. 将原始数据处理成键值对<K1，V1>形式。
2. 将解析后的键值对<K1，V1>传给map()函数，map()函数会根据映射规则，将键值对<K1，V1>映射为一系列中间结果形式的键值对<K2，V2>。
3. 将中间形式的键值对<K2，V2>形成<K2，{V2，…}>形式传给reduce()函数
4. reduce()函数把具有相同key的value合并在一起，产生新的键值对<K3，V3>，此时的键值对<K3，V3>就是最终输出的结果。

输入和输出的 `key` 和 `value` 都必须实现[Writable](http://hadoop.apache.org/docs/stable/api/org/apache/hadoop/io/Writable.html) 接口。


## 二、MapReduce工作原理
### 2.1 MapReduce工作过程


```
$HADOOP_HOME/bin/hdfs dfs -cat /wordcount/output/WordCountApp/*
Flink   6
HBase   4
Hadoop  4
Hive    3
Kafka   5
Spark   5
```




## 参考引用
[1] [黑马程序员教程-MapReduce分布式计算框架](https://book.itheima.net/course/1269935677353533441/1269937996044476418/1270615533565255681) <br>
[2] [分布式计算框架——MapReduce](https://github.com/heibaiying/BigData-Notes/blob/master/notes/Hadoop-MapReduce.md) <br>
[3] [Apache Hadoop Map/Reduce教程](https://hadoop.apache.org/docs/r1.0.4/cn/mapred_tutorial.html) <br>
[4] [Hadoop MapReduce 保姆级吐血宝典，学习与工作必读此文！](https://juejin.cn/post/7022839585021362184)