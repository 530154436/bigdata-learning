<nav>
<a href="#1常用参数">1、常用参数</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-memory-和-memoryoverhead">1.1 memory 和 memoryOverhead</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-网络-心跳相关配置spark-3x">1.2 网络 & 心跳相关配置（Spark 3.x）</a><br/>
<a href="#参考引用">参考引用</a><br/>
</nav>


---

## 1、常用参数

### 1.1 memory 和 memoryOverhead
在 Apache Spark（尤其是运行在 YARN 或 Kubernetes 上时）:
+ `driver-memory` 和 `memoryOverhead` 共同决定了分配给 Driver 的总物理内存。
+ `executor-memory` 和 `memoryOverhead` 共同决定了分配给每个 Executor 容器的总物理内存。

| 参数 | 全称 | 作用 |
|---|---|---|
| `spark.driver.memory` | Driver 堆内内存（JVM heap） | 用于 DAG 调度、UI、广播变量、小规模聚合等 Driver 端操作的 JVM 堆内存 |
| `spark.driver.memoryOverhead` | Driver 堆外内存开销 | 用于 Driver 进程的非堆内存，包括：<br>• Python Driver 进程（PySpark）<br>• JNI / native 库（如压缩、Netty）<br>• Direct memory（RPC、日志、shuffle 客户端）<br>• OS 开销、线程栈等 |
| `spark.executor.memory` | Executor 堆内内存（JVM heap） | 用于 Spark 任务执行、缓存 RDD/DataFrame 等的 JVM 堆内存 |
| `spark.executor.memoryOverhead` | Executor 堆外内存开销 | 用于 **非 JVM 堆内存**，包括：<br>• Python 进程内存（PySpark）<br>• JNI/native 代码（如压缩、shuffle）<br>• Direct memory（Netty、shuffle buffer）<br>• OS page cache 等 |

当 Spark 向 YARN/K8s 申请资源时， 总 Driver 容器内存和单个 Executor 容器的总内存如下：
```text
Total Driver Memory = spark.driver.memory + spark.driver.memoryOverhead
Total Memory = spark.executor.memory + spark.executor.memoryOverhead
```

### 1.2 网络 & 心跳相关配置（Spark 3.x）

| 配置项 | 默认值 | 作用 |
|:-------|:-------|:-----|
| `spark.executor.heartbeatInterval` | `10s` | Executor 定期向 Driver 发送心跳的时间间隔。如果 Driver 在该间隔的 **2 倍** 时间内未收到心跳，将把对应 Executor 标记为失效并重新调度任务。 |
| `spark.network.timeout` | `120s` | Driver 与 Executor 之间 **所有网络交互的全局超时**。如果以下任意配置未显式设置，将继承该值：<br>• `spark.shuffle.io.connectionTimeout`<br>• `spark.rpc.askTimeout`<br>• `spark.rpc.lookupTimeout`<br>• `spark.blockManagerSlaveTimeoutMs` 等 |

## 参考引用
[1] [spark-configuration](https://spark.apache.org/docs/latest/configuration.html)<br>
