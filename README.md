[TOC]
### 编译与执行Scala
+ Windows
```python
cd \Users\chubin.zheng\JavaProjects\bigdata-learnning
# 编译
scalac baseline\ch0_HelloWorld
# 执行
scala baseline.ch4_1_类
java -classpath %SCALA_HOME%/lib/scala-library.jar;. baseline.ch0_HelloWorld
```
+ Macos
```python
cd /Users/chubin.zheng/Documents/JavaProjects/bigdata-learnning
# 编译
scalac baseline.ch0_HelloWorld
# 执行
scala baseline.ch4_1_类
java -classpath .:$SCALA_HOME/lib/scala-library.jar baseline.ch0_HelloWorld
```

### 编译与执行Java+Scala
+ Maven Java+Scala
```shell
# 打包
mvn clean scala:compile compile package
# 执行
cd target
java -cp bigdata-learnning-1.0.jar;%SCALA_HOME%/lib/scala-library.jar baseline.ch0_HelloWorld
```

### 参考
[大数据之Spark入门教程（Scala版）](http://dblab.xmu.edu.cn/blog/spark/)

