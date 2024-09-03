### HDFS 常见问题

#### 为什么块的大小不能设置太小，也不能设置太大？ 像Linux的文件系统默认块大小4KB，为什么HDFS中块大小是64MB或128MB？
#### 为什么不适合低延时访问呢？
#### 为什么不适合存储小文件呢？
#### 为什么不支持并发写入，不支持随机修改？

#### yarn is running beyond physical memory limits
在运行HiveSQL时遇到了错误，排查yarn日志如下：
```
Container [pid=3730,containerID=container_1725006692302_0009_01_000003] is running 1723304448B beyond the 'VIRTUAL' memory limit.
Current usage: 348.4 MB of 1 GB physical memory used; 3.7 GB of 2.1 GB virtual memory used. Killing container.
```
解决方案：<br>
1、检查`yarn-site.xml` job内存限制，并增加`yarn.scheduler.minimum-allocation-mb`内存上限。
```xml
<configuration>
    <!-- 容器job内存限制 -->
    <property>
        <name>yarn.scheduler.maximum-allocation-mb</name>
        <value>8192</value>
    </property>
    <!-- 是否对容器强制执行虚拟内存限制 -->
    <property>
        <name>yarn.nodemanager.vmem-check-enabled</name>
        <value>false</value>
    </property>
    <!-- 为容器设置内存限制时虚拟内存与物理内存之间的比率 -->
    <property>
        <name>yarn.nodemanager.vmem-pmem-ratio</name>
        <value>4</value>
    </property>
</configuration>

```
2、重启yarn
```shell
$HADOOP_HOME/sbin/stop-yarn.sh
$HADOOP_HOME/sbin/start-yarn.sh
```