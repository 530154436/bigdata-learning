[TOC]
### 基于Docker搭建大数据集群
+ 查看系统内核版本（Debian 11）：cat /proc/version
```
Linux version 6.6.32-linuxkit (root@buildkitsandbox) 
(gcc (Alpine 13.2.1_git20240309) 13.2.1 20240309, GNU ld (GNU Binutils) 2.42) 
1 SMP Thu Jun 13 14:13:01 UTC 2024
```
+ Hadoop大数据平台安装包<br>
  各组件版本信息参考 [Cloudera CDP7.1.4](https://docs.cloudera.com/cdp-private-cloud-base/7.1.4/runtime-release-notes/topics/rt-pvc-runtime-component-versions.html)

| 名称     | 版本      | 软件包名及下载地址                                                                                                                                                                        | 安装目录                    |
|--------|---------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------|
| Java   | 1.8u112 | [jdk-8u112-linux-x64.tar.gz](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html)                                                                     | /usr/local/jdk1.8.0_112 |
| MySQL  | 5.6.37  | [mysql-5.6.37-linux-glibc2.12-x86_64.tar.gz](https://dev.mysql.com/downloads/mysql/5.6.html)<br>[mysql-connector-java-5.1.43-bin.jar](https://downloads.mysql.com/archives/c-j/) | /usr/local/mysql-5.6.37 |
| Hadoop | 3.1.1   | [hadoop-3.1.4.tar.gz](https://archive.apache.org/dist/hadoop/common/hadoop-3.1.4/hadoop-3.1.4.tar.gz)                                                                            | /usr/local/hadoop-3.1.4 |
| Hive   | 3.1.2   | [apache-hive-3.1.2-bin.tar.gz](https://archive.apache.org/dist/hive/)                                                                                                            | /usr/local/hive-3.1.2   |

+ `运行的服务列表`

| 容器名称      | 对应镜像                                     | 进程                                            |
|:----------|:-----------------------------------------|:----------------------------------------------|
| Hadoop101 | 15521147129/bigdata:hadoop-3.1.4         | NameNode、DataNode、ResourceManager、NodeManager |
| Hadoop102 | 15521147129/bigdata:hadoop-3.1.4         | DataNode、NodeManager                          |
| Hadoop103 | 15521147129/bigdata:hadoop-3.1.4         | SecondaryNameNode、DataNode                    |
| mysql     | 15521147129/bigdata:mysql-5.6.37         | mysqld、mysqld_safe                            |
| hive      | 15521147129/bigdata:hive-3.1.2           | RunJar                                        |

### 编译与执行Scala
+ Windows
```shell
cd \Users\chubin.zheng\JavaProjects\bigdata-learnning
# 编译
scalac org.zcb.common.baseline\ch0_HelloWorld
# 执行
scala org.zcb.common.baseline.ch4_1_类
java -classpath %SCALA_HOME%/lib/scala-library.jar;. org.zcb.common.baseline.ch0_HelloWorld
```
+ Macos
```shell
cd /Users/chubin.zheng/Documents/JavaProjects/bigdata-learnning
# 编译
scalac org.zcb.common.baseline.ch0_HelloWorld
# 执行
scala org.zcb.common.baseline.ch4_1_类
java -classpath .:$SCALA_HOME/lib/scala-library.jar org.zcb.common.baseline.ch0_HelloWorld
```

### 编译与执行Java+Scala
+ Maven Java+Scala
```shell
# 打包
mvn clean scala:compile compile package
# 执行
cd target
java -cp bigdata-learnning-1.0.jar;%SCALA_HOME%/lib/scala-library.jar org.zcb.common.baseline.ch0_HelloWorld
```

### HiveUDF
docs/HiveUDF.md

### 参考
[大数据之Spark入门教程（Scala版）](http://dblab.xmu.edu.cn/blog/spark/)

