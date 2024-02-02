[TOC]
### 流计算概述
#### 流数据
`流数据`，即数据以**大量**、**快速**、**时变**的流形式持续到达。<br>
+ 实例：PM2.5检测、电子商务网站用户点击流

流数据具有如下特征：
1. 数据快速持续到达，潜在大小也许是无穷无尽的
2. 数据来源众多，格式复杂
3. 数据量大，但是不关注存储，一旦经过处理，要么被丢弃，要么被归档存储
4. 注重数据的整体价值，不过分关注个别数据
5. 数据顺序颠倒，或者不完整，**系统无法控制将要处理的新到达的数据元素的顺序**

#### 批量计算和实时计算
<img src="images/spark/sparkStreaming_数据的两种处理模型.png" width="300" height="250" align="center"><br>
批量计算：充裕时间处理静态数据，如Hadoop。

1. 流数据不适合采用批量计算，因为流数据不适合用传统的关系模型建模。
2. 流数据必须采用`实时计算`，响应时间为`秒级`
3. 数据量少时，不是问题，但是，在大数据时代，数据格式复杂、来源众多、数据量巨大，对实时计算提出了很大的挑战。

流计算：实时获取来自不同数据源的海量数据，经过实时分析处理，获得有价值的信息
+ 基本理念：即`数据的价值随着时间的流逝而降低`，如用户点击流。 因此，当事件出现时就应该立即进行处理，而不是缓存起来进行批量处理。
+ 对于一个流计算系统来说，它应达到如下需求：<br>
（1）高性能：处理大数据的基本要求，如每秒处理几十万条数据<br>
（2）海量式：支持TB级甚至是PB级的数据规模<br>
（3）实时性：保证较低的延迟时间，达到秒级别，甚至是毫秒级别<br>
（4）分布式：支持大数据的基本架构，必须能够平滑扩展<br>
（5）易用性：能够快速进行开发和部署<br>
（6）可靠性：能可靠地处理流数据<br>
+ 较为常见的是开源流计算框架，代表如下：<br>
（1）Twitter Storm：免费、开源的分布式实时计算系统，可简单、高效、可靠地处理大量的流数据 <br>
（2）Yahoo! S4（Simple Scalable Streaming System）：开源流计算平台，是通用的、分布式的、可扩展的、分区容错的、可插拔的流式系统

#### 流计算处理流程
流计算的处理流程一般包含三个阶段：数据实时采集、数据实时计算、实时查询服务。<br>
`数据实时采集`阶段通常采集多个数据源的海量数据，需要保证实时性、低延迟与稳定可靠。<br>
+ 以日志数据为例，由于分布式集群的广泛应用，数据分散存储在不同的机器上，因此需要实时汇总来自不同机器上的日志数据
+ 目前有许多互联网公司发布的开源分布式日志采集系统均可满足每秒数百MB的数据采集和传输需求，如：<br>
（1）Facebook的Scribe<br>
（2）LinkedIn的`Kafka`<br>
（3）淘宝的Time Tunnel<br>
（4）基于Hadoop的Chukwa和Flume<br>
  
`数据实时计算`阶段对采集的数据进行实时的分析和计算，并反馈实时结果。<br>
+ 经流处理系统处理后的数据，可视情况进行存储，以便之后再进行分析计算。
+ 在时效性要求较高的场景中，处理之后的数据也可以直接丢弃。

`实时查询服务`：经由流计算框架得出的结果可供用户进行实时查询、展示或储存。
+ 传统的数据处理流程，用户需要主动发出查询才能获得想要的结果。而在流处理流程中，实时查询服务可以不断更新结果，并将用户所需的结果实时推送给用户。
+ 虽然通过对传统的数据处理系统进行定时查询，也可以实现不断地更新结果和结果推送，但通过这样的方式获取的结果，仍然是根据过去某一时刻的数据得到的结果，与实时结果有着本质的区别。

可见，流处理系统与传统的数据处理系统有如下`不同`：<br>
1. 流处理系统处理的是实时的数据，而传统的数据处理系统处理的是预先存储好的静态数据
2. 用户通过流处理系统获取的是实时结果，而通过传统的数据处理系统，获取的是过去某一时刻的结果
3. 流处理系统无需用户主动发出查询，实时查询服务可以主动将实时结果推送给用户

### SparkStreaming
Spark Streaming可整合多种输入数据源，如Kafka、Flume、HDFS，甚至是普通的TCP套接字。经处理后的数据可存储至文件系统、数据库，或显示在仪表盘里。<br>
基本原理：将实时输入数据流以`时间片`（秒级）为单位进行拆分，然后经Spark引擎以`类似批处理的方式处理每个时间片数据`。<br>
<img src="images/spark/sparkStreaming_执行流程.png" width="50%" height="50%" align="center"><br>

#### DStream概述
Spark Streaming最主要的抽象是`DStream`（Discretized Stream，离散化数据流），表示连续不断的数据流。
- 在内部实现上，Spark Streaming的输入数据按照时间片（如1秒）分成一段一段
- 每一段数据转换为Spark中的RDD，这些分段就是Dstream，并且对DStream的操作都最终转变为对相应的RDD的操作

<img src="images/spark/sparkStreaming_DStream操作示意图.png" width="50%" height="50%" align="center"><br>

完整WordCount示例<br>
<img src="images/spark/sparkStreaming_WordCount.png" width="50%" height="50%" align="center"><br>

#### 工作机制
在Spark Streaming中，会有一个组件`Receiver`，作为一个长期运行的task跑在一个Executor上。
每个Receiver都会负责一个`input DStream`（比如从文件中读取数据的文件流，比如套接字流，或者从Kafka中读取的一个输入流等等）。
Spark Streaming通过input DStream与外部数据源进行连接，读取相关数据。<br>
<img src="images/spark/sparkStreaming_架构.png" width="50%" height="50%" align="center"><br>


- 为了更好的协调数据接收速率与资源处理能力，1.5版本开始 Spark Streaming 可以动态控制数据接收速率来适配集群数据处理能力。
- 背压机制（即Spark Streaming Backpressure）: 根据 JobScheduler 反馈作业的执行信息来`动态调整 Receiver 数据接收率`。
- 通过属性spark.streaming.backpressure.enabled来控制是否启用backpressure机制，默认值false，即不启用。

#### Spark Streaming与Storm的对比
Spark Streaming和Storm最大的区别在于，Spark Streaming`无法实现毫秒级的流计算`，而Storm可以实现毫秒级响应。<br>
Spark Streaming构建在Spark上，一方面是因为Spark的低延迟执行引擎（100ms+）可以用于实时计算，另一方面，相比于Storm，RDD数据集更容易做高效的容错处理<br>
Spark Streaming采用的小批量处理的方式使得它可以同时兼容批量和实时数据处理的逻辑和算法，因此，方便了一些需要历史数据和实时数据联合分析的特定应用场合<br>
采用Spark架构具有如下优点：
- 实现一键式安装和配置、线程级别的任务监控和告警；
- 降低硬件集群、软件维护、任务监控和应用开发的难度；
- 便于做成统一的硬件、计算平台资源池。

### DStream操作
#### 输入源
创建StreamingContext对象
```markdown
import org.apache.spark._
import org.apache.spark.streaming._
val conf = new SparkConf().setAppName("TestDStream").setMaster("local[2]")
val ssc = new StreamingContext(conf, Seconds(1))
```

示例程序:
- [文件流(DStream)](https://github.com/530154436/bigdata-learning/blob/main/src/main/scala/spark/streaming/ch01_1_%E6%96%87%E4%BB%B6%E6%B5%81.scala)
- [文件流(DStream)](https://github.com/530154436/bigdata-learning/blob/main/src/main/scala/spark/streaming/ch01_2_%E5%A5%97%E6%8E%A5%E5%AD%97%E6%B5%81.scala)
- [RDD队列流(DStream)](https://github.com/530154436/bigdata-learning/blob/main/src/main/scala/spark/streaming/ch01_3_RDD%E9%98%9F%E5%88%97%E6%B5%81.scala)

遇到的问题:
```markdown
1. only one SparkContext may be running in this JVM (see SPARK-2243)
  => 创建StreamingContext时，已经存在一个SparkContext实例，从而导致错误。
2. windows下nc命令无效
   下载netcat(https://eternallybored.org/misc/netcat/netcat-win32-1.12.zip)
   解压，将nc.exe拷贝到C:\Windows下。
   nc -l -p 9999
```
#### 转换操作
DStream转换操作包括无状态转换和有状态转换。
- `无状态转换`：每个批次的处理不依赖于之前批次的数据。
- `有状态转换`：当前批次的处理需要使用之前批次的数据或者中间结果。

##### DStream无状态转换操作
- map(func) ：对源DStream的每个元素，采用func函数进行转换，得到一个新的DStream
- flatMap(func)： 与map相似，但是每个输入项可用被映射为0个或者多个输出项
- filter(func)： 返回一个新的DStream，仅包含源DStream中满足函数func的项
- repartition(numPartitions)： 通过创建更多或者更少的分区改变DStream的并行程度
- reduce(func)：利用函数func聚集源DStream中每个RDD的元素，返回一个包含单元素RDDs的新DStream
- count()：统计源DStream中每个RDD的元素数量
- union(otherStream)： 返回一个新的DStream，包含源DStream和其他DStream的元素
- countByValue()：应用于元素类型为K的DStream上，返回一个（K，V）键值对类型的新DStream，每个键的值是在原DStream的每个RDD中的出现次数
- reduceByKey(func, [numTasks])：当在一个由(K,V)键值对组成的DStream上执行该操作时，返回一个新的由(K,V)键值对组成的DStream，每一个key的值均由给定的recuce函数（func）聚集起来
- join(otherStream, [numTasks])：当应用于两个DStream（一个包含（K,V）键值对,一个包含(K,W)键值对），返回一个包含(K, (V, W))键值对的新Dstream
- cogroup(otherStream, [numTasks])：当应用于两个DStream（一个包含（K,V）键值对,一个包含(K,W)键值对），返回一个包含(K, Seq[V], Seq[W])的元组
- `transform`(func)：通过对源DStream的每个RDD应用RDD-to-RDD函数，创建一个新的DStream。支持在新的DStream中做任何RDD操作

##### DStream有状态转换操作
对于DStream有状态转换操作而言，当前批次的处理`需要使用之前批次的数据`或者中间结果。<br>
有状态转换包括`基于滑动窗口的转换`和`追踪状态变化`(updateStateByKey)的转换。<br>
**滑动窗口转换操作**
1. 事先设定一个`滑动窗口的长度`（也就是窗口的持续时间）
2. 设定滑动窗口的时间间隔（每隔多长时间执行一次计算），让窗口按照`指定时间间隔`在源DStream上滑动
3. 每次窗口停放的位置上，都会有一部分Dstream（或者一部分RDD）被框入窗口内，形成一个小段的Dstream，可以启动对这个小段DStream的计算

<img src="images/spark/sparkStreaming_滑动窗口转换操作.png" width="50%" height="50%" align="center"><br>


一些窗口转换操作的含义：
- window(windowLength, slideInterval) 基于源DStream产生的窗口化的批数据，计算得到一个新的Dstream
- countByWindow(windowLength, slideInterval) 返回流中元素的一个滑动窗口数
- reduceByWindow(func, windowLength, slideInterval) 返回一个单元素流。<br>
  利用函数func聚集滑动时间间隔的流的元素创建这个单元素流。函数func必须满足结合律，从而可以支持并行计算
- reduceByKeyAndWindow(func, windowLength, slideInterval, [numTasks]) 
  应用到一个(K,V)键值对组成的DStream上时，会返回一个由(K,V)键值对组成的新的DStream。<br>
  每一个key的值均由给定的reduce函数(func函数)进行聚合计算。<br>
  注意：在默认情况下，这个算子利用了Spark默认的并发任务数去分组。可以通过numTasks参数的设置来指定不同的任务数<br>

#### 输出操作


### 参考引用
+ [子雨大数据之Spark入门教程（Scala版）](https://dblab.xmu.edu.cn/blog/924/)












