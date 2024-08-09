## 一、MapReduce概述
Hadoop MapReduce 是一个强大的分布式计算框架，允许用户编写并行处理大规模数据集的应用程序。框架负责任务调度、监控和失败重试，确保作业在大规模集群中可靠、高效地运行。
MapReduce 作业通过将输入数据集分割为独立的小块，这些块由 `map` 以并行的方式处理，框架对 `map` 的输出进行排序，然后输入到 `reduce` 中。MapReduce 框架专门用于 `<key，value>` 键值对处理，它将作业的输入视为一组 `<key，value>` 对，并生成一组 `<key，value>` 对作为输出。输入和输出的 `key` 和 `value` 都必须实现[Writable](http://hadoop.apache.org/docs/stable/api/org/apache/hadoop/io/Writable.html) 接口。

```
(input) <k1, v1> -> map -> <k2, v2> -> combine -> <k2, v2> -> reduce -> <k3, v3> (output)
```


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
[1] [分布式计算框架——MapReduce](https://github.com/heibaiying/BigData-Notes/blob/master/notes/Hadoop-MapReduce.md) <br>
[2] [Apache Hadoop Map/Reduce教程](https://hadoop.apache.org/docs/r1.0.4/cn/mapred_tutorial.html) <br>
[3] [Hadoop MapReduce 保姆级吐血宝典，学习与工作必读此文！](https://juejin.cn/post/7022839585021362184)