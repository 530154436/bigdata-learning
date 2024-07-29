[TOC]
### 基于Docker搭建大数据集群
| 容器名称            | 对应镜像                                   | 进程                              |
|:-------------------|:-----------------------------------------|:---------------------------------|
| Hadoop101          | 15521147129/bigdata:hadoop-3.1.1         |NameNode、DataNode、ResourceManager、NodeManager |
| Hadoop102          | 15521147129/bigdata:hadoop-3.1.1         |DataNode、NodeManager|
| Hadoop103          | 15521147129/bigdata:hadoop-3.1.1         |SecondaryNameNode、DataNode |

### 编译与执行Scala
+ Windows
```python
cd \Users\chubin.zheng\JavaProjects\bigdata-learnning
# 编译
scalac org.zcb.common.baseline\ch0_HelloWorld
# 执行
scala org.zcb.common.baseline.ch4_1_类
java -classpath %SCALA_HOME%/lib/scala-library.jar;. org.zcb.common.baseline.ch0_HelloWorld
```
+ Macos
```python
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

