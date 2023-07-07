[TOC]

### Spark简介
Spark最初由美国加州伯克利大学（UCBerkeley）的AMP实验室于2009年开发。
+ 基于`内存计算`的大数据并行计算框架，可用于构建大型的、低延迟的数据分析应用程序。<br>
+ Apache软件基金会最重要的三大分布式计算系统开源项目之一（Hadoop、Spark、Storm）。<br>

具有如下几个主要特点：
+ 运行速度快：使用`DAG执行引擎`以支持循环数据流与内存计算
+ 容易使用：支持使用Scala、Java、Python和R语言进行编程，可以通过Spark Shell进行交互式编程
+ 通用性：Spark提供了完整而强大的技术栈，包括SQL查询、流式计算 、机器学习和图算法组件
+ 运行模式多样：可运行于独立的集群模式中，可运行于Hadoop中，也可运行于Amazon EC2等云环境中<br>
可以访问HDFS、Cassandra、HBase、Hive等多种数据源

相比于Hadoop MapReduce，Spark主要具有如下优点：
+ Spark的计算模式也属于MapReduce，但不局限于Map和Reduce操作
+ 还提供了多种数据集操作类型，编程模型比Hadoop MapReduce更灵活
+ Spark提供了内存计算，可将中间结果放到内存中，对于迭代运算效率更高 
+ Spark基于DAG的任务调度执行机制，要优于Hadoop MapReduce的迭代执行机制
+ 使用Hadoop进行迭代计算非常耗资源
+ Spark将数据载入内存后，之后的迭代计算都可以直接使用内存中的中间结果作运算，`避免了从磁盘中频繁读取数据`

| MapReduce                 | Spark                              |
|:--------------------------|:-----------------------------------|
| 数据存储结构：磁盘HDFS文件系统的split   | 使用内存构建弹性分布式数据集RDD<br>对数据进行运算和cache |
| 编程范式：Map + Reduce         | DAG: `Transformation` + `Action`   |
| 计算中间结果落到磁盘，IO及序列化、反序列化代价大 | 计算中间结果在内存中维护<br>存取速度比磁盘高几个数量级      |
| Task以进程的方式维护，需要数秒时间才能启动任务 | Task以线程的方式维护<br>对于小数据集读取能够达到亚秒级的延迟 |

### Spark生态
在实际应用中，大数据处理主要包括以下三个类型：
+ 复杂的`批量数据处理`：通常时间跨度在数十分钟到数小时之间(MapReduce)
+ 基于`历史数据的交互式查询`：通常时间跨度在数十秒到数分钟之间(Impala)
+ 基于`实时数据流`的数据处理：通常时间跨度在数百毫秒到数秒之间(Storm)

当同时存在以上三种场景时，就需要同时部署三种不同的软件，这样做难免会带来一些问题：
+ 不同场景之间输入输出数据无法做到无缝共享，通常需要进行数据格式的转换
+ 不同的软件需要不同的开发和维护团队，带来了较高的使用成本
+ 比较难以对同一个集群中的各个系统进行统一的资源协调和分配

Spark的设计遵循“一个软件栈满足不同应用场景”的理念，逐渐形成了一套完整的生态系统。
+ 既能够提供内存计算框架，也可以支持SQL即时查询、实时流式计算、机器学习和图计算等
+ Spark可以部署在资源管理器YARN之上，提供一站式的大数据解决方案

因此，Spark所提供的生态系统足以应对上述三种场景，即同时支持批处理、交互式查询和流数据处理。
Spark生态系统已经成为伯克利数据分析软件栈BDAS（Berkeley Data Analytics Stack）的重要组成部分。
Spark的生态系统主要包含了以下组件：
+ `Spark Core`：包含Spark的基本功能；定义了RDD的API、操作以及Action。
+ `Spark SQL`：提供通过Apache Hive的SQL变体Hive查询语言（HiveQL）与Spark进行交互的API。<br>
每个数据库表被当做一个RDD，Spark SQL查询被转换为Spark操作。
+ `Spark Streaming`：对实时数据流进行处理和控制，允许程序能够像普通RDD一样处理实时数据。
+ `Spark MLlib`：一个常用机器学习算法库，算法被实现为对RDD的Spark操作。
+ `Spark GraphX`：控制图、并行图操作和计算的一组算法和工具的集合。

<img src="images/spark_BDAS架构.png" width="50%" height="50%" alt="">

Spark生态系统组件的应用场景

| 应用场景         | 时间跨度    | 其他框架           | Spark生态系统中的组件   |
|:-------------|:--------|:---------------|:----------------|
| 批量数据处理(批处理)  | 小时级     | MapReduce、Hive | Spark           |
| 基于历史数据的交互式查询 | 分钟级 、秒级 | Impala         | Spark SQL       |
| 基于实时数据流的数据处理 | 毫秒、秒级   | Storm          | Spark Streaming |
| 基于历史数据的数据挖掘  | -       | Mahout         | MLlib           |
| 图结构数据的处理     | -       | Pregel         | GraphX          |

### Spark运行架构
#### 基本概念和架构设计
Spark运行架构包括：<br>
<img src="images/spark_运行架构.png" width="50%" height="50%" alt="">

+ Cluster Manager（集群资源管理器）：用于在集群上分配资源的外部服务，如Standalone集群管理器Master、Mesos或者YARN。
+ Worker Node（工作节点）：集群中可以运行应用程序代码的任意一个节点。运行一个或多个Executor进程。
+ Driver Program（驱动器程序）：负责控制一个应用的执行，运行Application的main函数和初始化SparkContext进程。
+ SparkContext（Spark上下文）：负责和资源管理器（Cluster Manager）的通信以及进行资源的申请、任务的分配和监控等。
+ SparkSession（Spark会话）：实质上是SQLContext和HiveContext的组合，内部封装了sparkContext。
+ Executor（执行进程）：运行在工作节点（WorkerNode）的一个JVM`进程`，用于运行计算任务，并在内存或磁盘上保存数据。

<img src="images/spark_application概念.png" width="50%" height="50%" alt="">

+ Application（应用程序）：用户编写的Spark应用程序，由集群上的一个驱动器（driver）和多个作业（Job）组成。
+ Job（作业）：一个Job包含多个RDD及作用于相应RDD上的各种操作，一个Job由多个Stage构成。
+ Stage（步骤）：是Job的基本调度单位，一个Job会分为多组Task，每组Task被称为Stage，也称为TaskSet。<br>
  代表了一组关联的、相互之间没有Shuffle依赖关系的任务组成的任务集。
+ Task（任务）：运行在 Executor 的工作单元，是运行Application的基本单位。

Spark任务调度模块主要包含两大部分：DAGScheduler和TaskScheduler：<br>
<img src="images/spark_任务调度模块.png" width="50%" height="50%" alt="">
+ RDD：Resillient Distributed Dataset（弹性分布式数据集）的简称，提供了一种`高度受限的共享内存模型`。

+ DAG：Directed Acyclic Graph（有向无环图）的简称，反映RDD之间的依赖关系
+ DAGScheduler：将DAG划分为不同的Stage，并以TaskSet的形式把Stage提交给TaskScheduler。
+ TaskScheduler：负责Application中不同job之间的调度，将TaskSet提交给Worker执行并返回结果，在Task执行失败时启动重试机制。

#### Spark运行基本流程
<img src="images/spark_运行流程.png" width="50%" height="50%" alt="">
Spark的基本运行流程如下：<br>
1. 首先为应用构建起基本的运行环境，即由Driver创建一个`SparkContext`，进行资源的申请、任务的分配和监控<br>
2. 资源管理器为Executor分配资源，并启动Executor进程<br>
3. SparkContext根据RDD的依赖关系构建DAG图，DAG图提交给DAGScheduler解析成Stage，然后把一个个TaskSet提交给底层调度器TaskScheduler处理<br>
4. Executor向SparkContext申请Task，Task Scheduler将Task发放给Executor运行，并提供应用程序代码<br>
5. Task在Executor上运行，把执行结果反馈给TaskScheduler，然后反馈给DAGScheduler，运行完毕后写入数据并释放所有资源<br>

总体而言，Spark运行架构具有以下特点：<br>
1. 每个Application都有自己专属的Executor进程，并且该进程在Application运行期间一直驻留。Executor进程以`多线程`的方式运行Task
2. Spark运行过程与资源管理器无关，只要能够获取Executor进程并保持通信即可
3. Task采用了数据本地性和推测执行等优化机制

#### Spark运行原理
### 参考引用
+ [Spark入门：Spark运行架构(Python版)](https://dblab.xmu.edu.cn/blog/1711/)
+ [子雨大数据之Spark入门教程（Scala版）](https://dblab.xmu.edu.cn/blog/924/)
+ [深入理解Spark任务调度](https://zhuanlan.zhihu.com/p/68393078)
+ [Spark 2.2.x 中文文档-集群模式概述](https://spark-reference-doc-cn.readthedocs.io/zh_CN/latest/deploy-guide/cluster-overview.html)
+ [Spark基本概念解析](https://andr-robot.github.io/Spark%E5%9F%BA%E6%9C%AC%E6%A6%82%E5%BF%B5%E8%A7%A3%E6%9E%90/)
+ [spark基本架构及原理](https://zhuanlan.zhihu.com/p/91143069)
+ [Spark原理框架和作业执行流程](https://blog.csdn.net/bocai8058/article/details/83051242)












