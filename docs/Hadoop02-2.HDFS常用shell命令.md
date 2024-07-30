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

# 查看集群状态
$HADOOP_HOME/bin/hdfs dfsadmin -report

# 查看目录
$HADOOP_HOME/bin/hdfs dfs -ls /
$HADOOP_HOME/bin/hdfs dfs -ls -R /a # 查看目录下的所有文件
# 创建文件夹
$HADOOP_HOME/bin/hdfs dfs -mkdir /a
$HADOOP_HOME/bin/hdfs dfs -mkdir -p /a/b/c

# 上传文件
bash
echo "hahahaha" > test.txt
$HADOOP_HOME/bin/hdfs dfs -copyFromLocal test.txt /a
$HADOOP_HOME/bin/hdfs dfs -copyFromLocal test.txt /a/b
$HADOOP_HOME/bin/hdfs dfs -copyFromLocal -f test.txt /a/b  # 覆盖

# 下载文件
$HADOOP_HOME/bin/hdfs dfs -get /a/b/test.txt ~/newtest.txt
$HADOOP_HOME/bin/hdfs dfs -getmerge /a/test.txt /a/b/test.txt ~/2words.txt  # 合并下载

# 从HDFS一个路径拷贝到HDFS另一个路径
$HADOOP_HOME/bin/hdfs dfs -cp /a/test.txt /
# 在HDFS目录中移动文件
$HADOOP_HOME/bin/hdfs dfs -mv /a/test.txt /a/b/c

# 删除文件或目录
$HADOOP_HOME/bin/hdfs dfs -rm /a/b/c/test.txt
$HADOOP_HOME/bin/hdfs dfs -rmdir /a/b/c/
$HADOOP_HOME/bin/hdfs dfs -rm -r /a/b/  # 强制删除

# 追加文件
$HADOOP_HOME/bin/hdfs dfs -appendToFile ~/test.txt /a/test.txt

# 查看文件
$HADOOP_HOME/bin/hdfs dfs -cat /a/test.txt
```
### HDFS练习

### 参考
[Hadoop学习之路（七）Hadoop集群shell常用命令](https://www.cnblogs.com/qingyunzong/p/8527595.html)