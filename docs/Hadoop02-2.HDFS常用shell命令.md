### HDFS常用shell命令
```shell
HADOOP_HOME=/usr/local/hadoop-3.1.1

# 启动HDFS、YARN、JobHistory
$HADOOP_HOME/sbin/start-dfs.sh
$HADOOP_HOME/sbin/start-yarn.sh
$HADOOP_HOME/sbin/mr-jobhistory-daemon.sh start historyserver
# 停止HDFS、YARN、JobHistory
$HADOOP_HOME/sbin/stop-dfs.sh
$HADOOP_HOME/sbin/stop-yarn.sh
$HADOOP_HOME/sbin/mr-jobhistory-daemon.sh stop historyserver

# 查看目录
$HADOOP_HOME/bin/hdfs dfs -ls /
$HADOOP_HOME/bin/hdfs dfs -ls -R /a # 查看目录下的所有文件
# 创建文件夹
$HADOOP_HOME/bin/hdfs dfs -mkdir /a
$HADOOP_HOME/bin/hdfs dfs -mkdir -p /a/b/c

# 上传文件
bash
echo "hahahaha" > test.txt
$HADOOP_HOME/bin/hdfs dfs -copyFromLocal test.txt /a/b
$HADOOP_HOME/bin/hdfs dfs -copyFromLocal -f test.txt /a/b  # 覆盖

# 下载文件
$HADOOP_HOME/bin/hdfs dfs -get /a/b/test.txt ~/newtest.txt

```
### HDFS练习

### 参考
[Hadoop学习之路（七）Hadoop集群shell常用命令](https://www.cnblogs.com/qingyunzong/p/8527595.html)