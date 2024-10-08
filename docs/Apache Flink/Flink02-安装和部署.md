## 一、Flink 安装

### 1.1 Flink 安装方式
Flink 的安装方式有多种，具体取决于用户的环境和需求。以下是几种常见的安装方式：

+ `Local`（单机模式）：<br>
  最简单的启动方式。直接解压安装包就可以使用，不用进行任何配置；一般用来做一些简单的测试。

+ `Standalone`（独立模式）：<br>
  所需要的所有Flink组件，都只是操作系统上运行的一个`JVM进程`，独立运行，不依赖任何外部的资源管理平台。<br>
  如果资源不足，或者出现故障，没有自动扩展或重分配资源的保证，必须手动处理。所以独立模式一般只用在开发测试或作业非常少的场景下。<br>

+ Yarn（`Flink on Yarn`）：<br>
  客户端将Flink应用提交给YARN的ResourceManager，YARN的ResourceManager会向YARN的NodeManager申请容器。<br>
  在这些容器上，Flink会部署JobManager和TaskManager的实例，从而启动集群。<br>
  Flink会根据运行在JobManger上的作业所需要的slots数量动态分配TaskManager资源。<br>

详细部署信息参考：

### 1.2 Flink集群配置文件

修改集群配置(1)进入conf目录下，修改flink-conf.yaml文件，修改jobmanager.rpc.address参数为hadoop102，如下所示：[插图]这就指定了hadoop102节点服务器为JobManager节点。(2)修改workers文件，将另外两台节点服务器添加为本Flink集群的TaskManager节点，具体修改如下：[插图]这样就指定了hadoop103和hadoop104为TaskManager节点。

(2)修改workers文件，将另外两台节点服务器添加为本Flink集群的TaskManager节点，具体修改如下：[插图]这样就指定了hadoop103和hadoop104为TaskManager节点。

(3)另外，在flink-conf.yaml文件中还可以对集群中的JobManager和TaskManager组件进行优化配置，主要配置项如下所示。

• jobmanager.memory.process.size：对JobManager进程可使用到的全部内存进行配置，包括JVM元空间和其他开销，默认为1600MB，可以根据集群规模进行适当调整。

• taskmanager.memory.process.size：对TaskManager进程可使用到的全部内存进行配置，包括JVM元空间和其他开销，默认为1600MB，可以根据集群规模进行适当调整。

• taskmanager.numberOfTaskSlots：对每个TaskManager能够分配的slots数量进行配置，默认为1，可根据TaskManager所在的机器能够提供给Flink的CPU数量决定。所谓slots就是TaskManager中具体运行一个任务所分配的计算资源。

• parallelism.default：Flink任务执行的默认并行度配置，优先级低于代码中进行的并行度配置和任务提交时使用参数进行的并行度配置。关于slots和并行度的概念，我们会在下一章做详细讲解。


## 二、Flink 部署模式
一些应用场景对集群资源分配和占用的方式，可能会有特定的需求。 Flink为各种场景提供了不同的部署模式，主要有以下3种：
+ 会话模式(Session Mode)
+ 单作业模式(Per-Job Mode)
+ 应用模式(Application Mode)

它们的区别主要在于：集群的生命周期和资源的分配方式，以及应用的main方法到底在哪里执行——客户端(Client)还是JobManager。

### 2.1 会话模式（Session Mode）
会话模式 (Session Mode) 是指`先启动一个集群，保持一个会话并且确定所有的资源`，然后向集群提交作业，所有提交的作业会竞争集群中的资源，从而会出现资源不足作业执行失败的情况。

<img src="images/flink02/deploy_session_mode.png" width="40%" height="40%" alt=""><br>

**优点**: `资源共享`: 多个作业可以共用同一个Flink集群，节省资源；`快速作业提交`: 集群已启动，作业提交后可立即运行，减少作业启动时间。<br>
**缺点**: `资源竞争`: 不同作业之间可能争夺资源，导致性能不稳定；`管理复杂度`: 多个作业共用集群，资源管理和隔离相对复杂。<br>
**应用场景**：适合需要运行多个轻量级作业（单个规模小、执行时间短）的场景。<br>

### 1.2.2 单作业模式 Per-Job Mode
单作业模式 (Per-Job Mode) 是指`为每一个提交的作业启动一个集群`，由客户端运行应用程序，然后启动集群，作业被提交给 JobManager，进而分发给 TaskManager 执行`。
作业作业完成后，集群就会关闭，所有资源也会释放。每个作业都有它自己的JobManager管理，占用独享的资源，即使发生故障，它的TaskManager宕机也不会影响其他作业。
单作业模式在生产环境运行更加稳定，所以是实际应用的首选模式。
单作业模式一般需要借助一些资源管理框架来启动集群，比如 YARN、Kubernetes。

<img src="images/flink02/deploy_per_job_mode.png" width="40%" height="40%" alt=""><br>

**优点**: `资源隔离`: 每个作业独占资源，避免了资源争夺问题；`故障隔离`: 一个作业崩溃不会影响其他作业，可靠性高。<br>
**缺点**: `启动时间长`: 每次提交作业都需要启动一个新的Flink集群，导致启动时间长；`资源浪费`: 对于小作业，独立启动集群可能会浪费资源。<br>
**应用场景**：适合需要运行独立的大型作业或对稳定性要求较高的关键任务。<br>

### 1.2.3 应用模式 Application Mode
应用模式 (Application Mode) 是指为`每一个提交的应用单独启动一个 JobManager，也就是创建一个集群`。这个 JobManager 只为执行这一个应用而存在，执行结束之后 JobManager 也就关闭了。

<img src="images/flink02/deploy_app_mode.png" width="35%" height="35%" alt=""><br>

应用模式与单作业模式，都是提交作业之后才创建集群；单作业模式是通过`客户端`来提交的，客户端解析出的每个作业都对应一个集群；而在应用模式下，是直接由`JobManager`执行应用程序的，并且即使应用包含了多个作业，也只创建一个集群。


**优点**: `简化管理`: 不需要管理多个作业提交，Flink集群与应用逻辑紧密结合，简化部署和管理；`资源独立`：每个应用独立运行，避免了资源争夺和故障传播问题。<br>
**缺点**: `资源利用率低`:和单作业模式一样，对于资源要求不高的应用，可能存在资源浪费问题；`启动开销`: 每个应用都需要启动一个独立的Flink集群，启动时间较长。<br>
**应用场景**：适合运行完整的应用程序生命周期，尤其是在作业逻辑复杂或需要长时间运行的应用场景。


3：常见部署模式组合
Standalone + 会话模式
Standalone + 应用模式
Yarn + 会话模式
Yarn + 单作业模式
Yarn + 应用模式

### 三、参考引用
[2] [Docker下安装zookeeper（单机 & 集群）](https://www.cnblogs.com/LUA123/p/11428113.html)<br>
[3] [Flink -3- 一文详解安装部署以及使用和调优（standalone 模式 | yarn 模式）](https://blog.csdn.net/qq_41694906/article/details/140610459?spm=1001.2101.3001.6650.3&utm_medium=distribute.pc_relevant.none-task-blog-2~default~YuanLiJiHua~Position-3-140610459-blog-108770855.235%5Ev43%5Epc_blog_bottom_relevance_base4&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2~default~YuanLiJiHua~Position-3-140610459-blog-108770855.235%5Ev43%5Epc_blog_bottom_relevance_base4&utm_relevant_index=4)
