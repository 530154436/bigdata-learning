# Hadoop伪分布式部署
+ 环境：苹果 Mac M2(巨坑)
+ WEB-UI：[Hadoop](http://localhost:50070)、[Yarn](http://localhost:8088/cluster)
+ 环境变量
```shell
sudo vim ~/.zshrc

# /usr/libexec/java_home -V
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_211.jdk/Contents/Home
export SCALA_HOME=/Users/chubin.zheng/Documents/software/scala-2.11.12
export PATH=$SCALA_HOME/bin:$PATH

export HADOOP_HOME=/Users/chubin.zheng/Documents/software/hadoop-2.8.5
export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin

export SPARK_HOME=/Users/chubin.zheng/Documents/software/spark-2.4.5
export PATH=$PATH:$SPARK_HOME/sbin:$SPARK_HOME/bin

# 免密登录
# 共享->远程登录：允许远程用户对磁盘进行完全访问
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys

# 初始化（格式化）
hdfs namenode -format

# 启动
start-dfs.sh
start-yarn.sh
```

#### 出现的问题
+ 执行start-dfs.sh后出现 Operation not permitted
```shell
localhost: bash: /Users/chubin.zheng/Documents/software/hadoop-2.8.5/sbin/hadoop-daemon.sh: Operation not permitted
localhost: bash: /Users/chubin.zheng/Documents/software/hadoop-2.8.5/sbin/hadoop-daemon.sh: Operation not permitted

# 解决方案
sshd-keygen-wrapper in the Setting -> Security & Privacy -> Privacy -> Full Disk Access. I solved by above.
```
+ 执行spark-shell后出现 Can't assign requested address: Service
```shell
java.net.BindException: Can't assign requested address: Service 'sparkDriver' failed after 16 retries (on a random free port)!

${SPARK_HOME}/bin/load-spark-env.sh 添加这行：export SPARK_LOCAL_IP="127.0.0.1"
```

#### 参考
[厦门大学-Hadoop安装教程_单机/伪分布式配置_Hadoop2.6.0(2.7.1)/Ubuntu14.04(16.04)](https://dblab.xmu.edu.cn/blog/7/)
[厦门大学-Spark2.1.0入门：Spark的安装和使用](https://dblab.xmu.edu.cn/blog/1307/)
