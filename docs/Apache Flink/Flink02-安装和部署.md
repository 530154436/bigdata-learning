<nav>
<a href="#一flink-安装">一、Flink 安装</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-flink-安装方式">1.1 Flink 安装方式</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-flink集群安装与配置">1.2 Flink集群安装与配置</a><br/>
<a href="#二flink-部署模式">二、Flink 部署模式</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-会话模式session-mode">2.1 会话模式（Session Mode）</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#122-单作业模式-per-job-mode">1.2.2 单作业模式 Per-Job Mode</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#123-应用模式-application-mode">1.2.3 应用模式 Application Mode</a><br/>
<a href="#三flink-部署模式flink-on-yarn">三、Flink 部署模式（Flink on Yarn）</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#31-会话模式部署">3.1 会话模式部署</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#311-启动集群">3.1.1 启动集群</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#312-提交作业">3.1.2 提交作业</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#32-单作业模式部署">3.2 单作业模式部署</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#33-应用模式部署">3.3 应用模式部署</a><br/>
<a href="#四遇到的问题">四、遇到的问题</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#1hadoop-is-not-in-the-classpathdependencies">1）Hadoop is not in the classpath/dependencies</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#2javaioioexception-cannot-instantiate-file-system-for-uri-hdfsflinkcompleted-jobs">2）java.io.IOException: Cannot instantiate file system for URI: hdfs://flink/completed-jobs</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#3file-does-not-exist-tmpapplication_xxx">3）File does not exist: /tmp/application_xxx</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#五参考引用">五、参考引用</a><br/>
</nav>

## 一、Flink 安装

### 1.1 Flink 安装方式
Flink 的安装方式有多种，具体取决于用户的环境和需求。以下是几种常见的安装方式：

+ `Local`（单机模式）：<br>
  在一个JVM进程中，通过线程模拟出各个Flink角色来得到Flink环境，直接解压安装包就可使用。

+ `Standalone`（独立模式）：<br>
  各个Flink组件都是独立运行的一个`JVM进程`，不依赖任何外部的资源管理平台。<br>
  如果资源不足，或者出现故障，没有自动扩展或重分配资源的保证，必须手动处理。

+ Yarn（`Flink on Yarn`）：<br>
  各个Flink组件，均运行在多个YARN的容器内，其整体上是一个YARN的任务。

详细部署信息参考：[基于Docker搭建大数据集群](https://github.com/530154436/bigdata-learning/blob/main/docker-deploy/README.md)

### 1.2 Flink集群安装与配置
+ 下载解压
```
wget https://archive.apache.org/dist/flink/flink-1.13.2/flink-1.13.2-bin-scala_2.12.tgz
tar -xvf flink-1.13.2-bin-scala_2.11.tgz -C /usr/local/
```
+ 配置环境变量
```
export HADOOP_CLASSPATH=`$HADOOP_HOME/bin/hadoop classpath`
export FLINK_HOME=/usr/local/flink
```
+ 配置集群文件-$FLINK_HOME/conf/flink-conf.yaml
```
# Common
jobmanager.rpc.address: flink101
jobmanager.rpc.port: 6123
jobmanager.memory.process.size: 1600m   # JobManager进程可使用到的全部内存，默认1600MB。
taskmanager.memory.process.size: 1728m  # TaskManager进程可使用到的全部内存，默认1600MB。
taskmanager.numberOfTaskSlots: 4        # 每个TaskManager能够分配的slots数量，默认为1，一般由CPU数量决定。
parallelism.default: 1                  # Flink任务执行的默认并行度
# fs.default-scheme: hdfs://hadoop101:9000/
# io.tmp.dirs: /tmp

# 是否启动web提交
web.submit.enable: true

# HistoryServer
jobmanager.archive.fs.dir: hdfs://hadoop101:9000/flink/completed-jobs/
historyserver.archive.fs.dir: hdfs://hadoop101:9000/flink/completed-jobs/
historyserver.web.address: flink101
historyserver.web.port: 8082

# state
state.backend: filesystem
state.checkpoints.dir: hdfs://hadoop101:9000/flink/checkpoints
state.savepoints.dir: hdfs://hadoop101:9000/flink/savepoints
```
+ 配置集群文件-$FLINK_HOME/conf/masters
```
# JobManager节点
flink101:8081
```
+ 配置集群文件-$FLINK_HOME/conf/workers
```
# TaskManager节点
flink101
flink102
```
+ 启动集群
```shell
# 启动集群
$FLINK_HOME/bin/start-cluster.sh

# 启动历史服务器
$FLINK_HOME/bin/historyserver.sh start
```
+ 查看进程：
```shell
hadoop@flink101: $JAVA_HOME/bin/jps
1217 Jps
777 TaskManagerRunner
490 StandaloneSessionClusterEntrypoint
1003 HistoryServer

hadoop@flink102:~$ $JAVA_HOME/bin/jps
391 Jps
282 TaskManagerRunner
```
## 二、Flink 部署模式

不同应用场景对集群资源分配和占用的方式有不同的需求。 Flink为各种场景提供了不同的部署模式，主要有以下3种：
+ 会话模式(Session Mode)
+ 单作业模式(Per-Job Mode)
+ 应用模式(Application Mode)

它们的区别主要在于：集群的生命周期和资源的分配方式，以及应用的main方法到底在哪里执行——客户端(Client)还是JobManager。

### 2.1 会话模式（Session Mode）
会话模式 (Session Mode) 是指`先启动一个集群，保持一个会话并且确定所有的资源`，然后向集群提交作业，所有提交的作业会竞争集群中的资源，从而会出现资源不足作业执行失败的情况。<br>

<img src="images/flink02/deploy_session_mode_v2.png" width="80%" height="80%" alt=""><br>

**优点**: `资源共享`: 多个作业可以共用同一个Flink集群，节省资源；`快速作业提交`: 集群已启动，作业提交后可立即运行，减少作业启动时间。<br>
**缺点**: `资源竞争`: 不同作业之间可能争夺资源，导致性能不稳定；`管理复杂度`: 多个作业共用集群，资源管理和隔离相对复杂。<br>
**应用场景**：适合需要运行多个轻量级作业（单个规模小、执行时间短）的场景。<br>

### 1.2.2 单作业模式 Per-Job Mode
单作业模式 (Per-Job Mode) 是指`为每一个提交的作业启动一个集群`，由客户端运行应用程序，然后启动集群，作业被提交给 JobManager，进而分发给 TaskManager 执行`。
作业作业完成后，集群就会关闭，所有资源也会释放。每个作业都有它自己的JobManager管理，占用独享的资源，即使发生故障，它的TaskManager宕机也不会影响其他作业。
单作业模式在生产环境运行更加稳定，所以是实际应用的首选模式。
单作业模式一般需要借助一些资源管理框架来启动集群，比如 YARN、Kubernetes。<br>

<img src="images/flink02/deploy_per_job_mode_v2.png" width="80%" height="80%" alt=""><br>

**优点**: `资源隔离`: 每个作业独占资源，避免了资源争夺问题；`故障隔离`: 一个作业崩溃不会影响其他作业，可靠性高。<br>
**缺点**: `启动时间长`: 每次提交作业都需要启动一个新的Flink集群，导致启动时间长；`资源浪费`: 对于小作业，独立启动集群可能会浪费资源。<br>
**应用场景**：适合需要运行独立的大型作业或对稳定性要求较高的关键任务。<br>

### 1.2.3 应用模式 Application Mode
应用模式 (Application Mode) 是指为`每一个提交的应用单独启动一个JobManager，也就是创建一个集群`。这个 JobManager 只为执行这一个应用而存在，执行结束之后 JobManager 也就关闭了。<br>

<img src="images/flink02/deploy_app_mode_v2.png" width="80%" height="80%" alt=""><br>

应用模式与单作业模式，都是提交作业之后才创建集群；单作业模式是通过`客户端`来提交的，客户端解析出的每个作业都对应一个集群；而在应用模式下，是直接由`JobManager`解析和执行应用程序的，并且即使应用包含了多个作业，也只创建一个集群。 

**优点**: `简化管理`: 不需要管理多个作业提交，Flink集群与应用逻辑紧密结合，简化部署和管理；`资源独立`：每个应用独立运行，避免了资源争夺和故障传播问题。<br>
**缺点**: `资源利用率低`:和单作业模式一样，对于资源要求不高的应用，可能存在资源浪费问题；`启动开销`: 每个应用都需要启动一个独立的Flink集群，启动时间较长。<br>
**应用场景**：适合运行完整的应用程序生命周期，尤其是在作业逻辑复杂或需要长时间运行的应用场景。

> Session 和 per-job 模式都是先将对应的 jar 在 client 进行解析，然后提交到 JobManager。
生成 JobGraph 的过程很消耗 CPU 资源，而且在多个 JAR 进行排队提交的时候，有可能会造成阻塞（排队等待解析和上传 dependency JAR）。
dependency jar 也可能很大，造成 cluster 和 client 之间的网络堵塞。
所以就有一种想法——为什么不把这些交给 JobManager？<br>

## 三、Flink 部署模式（Flink on Yarn）
Flink可以基于Yarn来运行任务，Yarn作为资源提供方，可以根据Flink任务资源需求动态的启动TaskManager来提供资源。 Flink在YARN上部署的过程如下：
1. 客户端将Flink应用提交给YARN的ResourceManager，YARN的ResourceManager会向YARN的NodeManager申请容器。<br>
2. 在这些容器上，Flink会部署JobManager和TaskManager的实例，从而启动集群。<br>
3. Flink会根据运行在JobManger上的作业所需要的slots数量动态分配TaskManager资源。<br>

> 为什么使用Flink on Yarn? <br>
> 1.Yarn的资源可以按需使用，提高集群的资源利用率<br>
> 2.Yarn的任务有优先级，根据优先级运行作业<br>
> 3.基于Yarn调度系统，能够自动化地处理各个角色的Failover(容错)<br>
（1）JobManager进程和TaskManager进程都由Yarn NodeManager 监控<br>
（2）如果JobManager进程异常退出，则Yarn ResourceManager会重新调度JobManager到其他机器<br>
（3）如果TaskManager进程异常退出，JobManager会收到消息并重新向Yarn ResourceManager 申请资源，重新启动TaskManager<br>

**前置工作**： socket模拟产生数据流（在hadoop103）
```shell
nc -l -p 7777
```

### 3.1 会话模式部署
YARN的会话模需要首先申请一个`YARN会话`(YARN session)来启动Flink集群。 具体步骤如下所示。<br>
YARN Session实际上是一个YARN的Application，并且有唯一的Application ID。

<img src="images/flink02/flink_on_yarn_session_mode.png" width="80%" height="80%" alt=""><br>

#### 3.1.1 启动集群
(1) 启动Hadoop集群，包括HDFS和YARN。<br>
(2) 执行脚本命令向YARN集群申请资源，开启一个YARN会话，启动Flink集群。
```shell
# 启动集群
$FLINK_HOME/bin/yarn-session.sh -nm test -d
# -d：分离模式，如果你不想让Flink YARN客户端一直在前台运行，可以使用这个参数，即使关掉当前对话窗口，YARN session也可以在后台运行。
# -jm(--jobManagerMemory)：配置JobManager所需内存，默认单位为MB。
# -nm(--name)：配置在YARN UI界面上显示的任务名。
# -qu(--queue)：指定YARN队列名。
# -tm(--taskManager)：配置每个TaskManager所用内存。

# 恢复会话窗口
$FLINK_HOME/bin/yarn-session.sh -id application_1727322223306_0004

# 关闭yar任务（关闭会话）
$HADOOP_HOME/bin/yarn application -kill application_1727322223306_0004
```
YARN Session启动后会生成JobManager的地址（http://hadoop103:45995）及一个YARN application ID（application_1727322223306_0004）。<br>

<img src="images/flink02/flink_on_yarn_session_mode_01.png" width="100%" height="100%" alt=""><br>
<img src="images/flink02/flink_on_yarn_session_mode_02.png" width="100%" height="100%" alt=""><br>

#### 3.1.2 提交作业
(1) 通过Web UI提交作业。<br>
(2) 通过命令行提交作业。<br>
① 构建jar包并上传到flink101服务器。<br>
② 执行以下命令，将该任务提交到已经开启的YARN Session中运行。<br>
```shell
$FLINK_HOME/bin/flink run -c org.zcb.flink.baseline.ch02_03_StreamUnbounded /tmp/target/bigdata-flink-scala-1.0-shaded.jar --jobmanager hadoop103:45995
# -c,--class <classname>  具有程序入口的类, 即该类下有 main() 方法, 只需要指定类名即可
# -m,--jobmanager <arg>   指定JobManager的地址
```
③ 任务提交成功后，可在YARN的Web UI界面查看运行情况。<br>
<img src="images/flink02/flink_on_yarn_session_mode_03.png" width="100%" height="100%" alt=""><br>

### 3.2 单作业模式部署
在YARN环境中，因为有了外部平台作资源调度，所以可以直接向YARN提交一个单独的作业，从而启动一个Flink集群。<br>
<img src="images/flink02/flink_on_yarn_per_job_mode.png" width="80%" height="80%" alt=""><br>

(1) 执行命令提交作业。
```shell
$FLINK_HOME/bin/flink run -t yarn-per-job -c org.zcb.flink.baseline.ch02_03_StreamUnbounded /tmp/target/bigdata-flink-scala-1.0-shaded.jar
# -c,--class <classname>  具有程序入口的类, 即该类下有 main() 方法, 只需要指定类名即可
# -m,--jobmanager <arg>   指定JobManager的地址
# -t,--target <arg>	设置应用程序的部署目标，相当于 execution.target 配置选项
#    run action 可以使用: “remote”, “local”, “kubernetes-session”, “yarn-per-job”, “yarn-session”
#    run-application action 可以使用: “kubernetes-application”, “yarn-application”.
```
(2) 在YARN的ResourceManager界面查看执行情况。<br>
<img src="images/flink02/flink_on_yarn_per_job_mode_01.png" width="100%" height="100%" alt=""><br>
<img src="images/flink02/flink_on_yarn_per_job_mode_02.png" width="100%" height="100%" alt=""><br>

(3) 可以使用命令行查看或取消作业，命令如下：<br>
```shell
$FLINK_HOME/bin/flink list -t yarn-per-job -Dyarn.application.id=application_1727322223306_0008
$FLINK_HOME/bin/flink cancel -t yarn-per-job -Dyarn.application.id=application_1727322223306_0008 6275f85e2354bc0e5698d7ffddb6d158
```
<img src="images/flink02/flink_on_yarn_per_job_mode_03.png" width="100%" height="100%" alt=""><br>
<img src="images/flink02/flink_on_yarn_per_job_mode_04.png" width="100%" height="100%" alt=""><br>

### 3.3 应用模式部署
与单作业模式类似，直接执行flink run-application命令即可。<br>
(1) 执行命令提交作业。<br>
```shell
source /etc/profile

# 本地jar包提交
$FLINK_HOME/bin/flink run-application -t yarn-application -c org.zcb.flink.baseline.ch02_03_StreamUnbounded /tmp/target/bigdata-flink-scala-1.0-shaded.jar

# hdfs提交
# 这种方式下jar可以预先上传到HDFS，而不需要单独发送到集群，这就使得作业提交更加轻量了。
$HADOOP_HOME/bin/hdfs dfs -mkdir /tmp/target
$HADOOP_HOME/bin/hdfs dfs -put -f /tmp/target/bigdata-flink-scala-1.0-shaded.jar /tmp/target/
$FLINK_HOME/bin/flink run-application -t yarn-application -c org.zcb.flink.baseline.ch02_03_StreamUnbounded hdfs://tmp/target/bigdata-flink-scala-1.0-shaded.jar
```
<img src="images/flink02/flink_on_yarn_application_mode_01.png" width="100%" height="100%" alt=""><br>
<img src="images/flink02/flink_on_yarn_application_mode_02.png" width="100%" height="100%" alt=""><br>

(2) 在命令行中查看或取消作业。
```shell
$FLINK_HOME/bin/flink list -t yarn-application -Dyarn.application.id=application_1727322223306_0010
$FLINK_HOME/bin/flink cancel -t yarn-application -Dyarn.application.id=application_1727322223306_0010 0af24ab3aacb40ddd414b636c57fde10
```

## 四、遇到的问题
### 1）Hadoop is not in the classpath/dependencies
```
Caused by: org.apache.flink.core.fs.UnsupportedFileSystemSchemeException:
Could not find a file system implementation for scheme 'hdfs'. 
The scheme is not directly supported by Flink and no Hadoop file system to support this scheme could be loaded. 
```
原因： 在 Flink 1.11.0 版本之后,增加了很多重要新特性,其中就包括增加了对 Hadoop3.0.0 以及更高版本 Hadoop 的支持,不再提供“flink-shaded-hadoop-*”
jar 包,而是通过配置环境变量完成与 YARN 集群的对接。　在将 Flink 任务部署至 YARN 集群之前,需要确认集群是否安装有 Hadoop,保证 Hadoop
版本至少在 2.2 以上,并且集群中安装有 HDFS 服务。<br>

解决方案：配置环境变量,增加环境变量配置如下:
```
sudo vim /etc/profile
HADOOP_HOME=/usr/local/hadoop-3.1.4
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
export HADOOP_CONF_DIR=${HADOOP_HOME}/etc/hadoop
export HADOOP_CLASSPATH=`hadoop classpath`  
```
其中，`hadoop classpath`是一句shell命令，用于获取配置的Hadoop类路径

### 2）java.io.IOException: Cannot instantiate file system for URI: hdfs://flink/completed-jobs
原因：hdfs路径配置不正确。<br>
解决方案：修改 `conf/flink-conf.yaml`配置信息
```
jobmanager.archive.fs.dir: hdfs://hadoop101:9000/flink/completed-jobs/
historyserver.archive.fs.dir: hdfs://hadoop101:9000/flink/completed-jobs/
```

### 3）File does not exist: /tmp/application_xxx
Flink on Yarn 启动回话模式：
```shell
$FLINK_HOME/bin/yarn-session.sh -nm test
```
报错：
```
org.apache.flink.client.deployment.ClusterDeploymentException: Couldn't deploy Yarn session cluster
at org.apache.flink.yarn.YarnClusterDescriptor.deploySessionCluster(YarnClusterDescriptor.java:382)
....
Caused by: java.io.FileNotFoundException: File does not exist: /tmp/application_1594196612035_0008-flink-conf.yaml3951184480005887817.tmp
at org.apache.hadoop.hdfs.DistributedFileSystem$27.doCall(DistributedFileSystem.java:1444)
...
```
原因：Flink配置不正确。<br>
解决方案：注释`conf/flink-conf.yaml`的这两项配置信息`fs.default-scheme`、`io.tmp.dirs`
```
# fs.default-scheme: hdfs://hadoop101:9000/
# io.tmp.dirs: /tmp
```

### 五、参考引用
[1] [剑指大数据——flink学习精要（scala版）](https://weread.qq.com/web/reader/c3f32e90813ab8449g01292dkc9f326d018c9f0f895fb5e4)<br>
[2] [Docker下安装zookeeper（单机 & 集群）](https://www.cnblogs.com/LUA123/p/11428113.html)<br>
[3] [Flink -3- 一文详解安装部署以及使用和调优（standalone 模式 | yarn 模式）](https://blog.csdn.net/qq_41694906/article/details/140610459)<br>
[4] [一文详解Flink on Yarn的三种部署方式及使用说明](https://www.bilibili.com/read/cv23629566/)<br>
[5] [林子雨编著《Flink编程基础（Scala版）》教材官网](https://dblab.xmu.edu.cn/post/flink/)<br>
[6] [Flink部署模式剖析](https://timzhouyes.github.io/2021/04/14/Flink-%E9%83%A8%E7%BD%B2%E6%A8%A1%E5%BC%8F%E5%89%96%E6%9E%90/)<br>