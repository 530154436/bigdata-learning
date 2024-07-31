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
$HADOOP_HOME/bin/hdfs dfs -put test.txt /a
$HADOOP_HOME/bin/hdfs dfs -put -f test.txt /a

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
### JAVA API

### 遇到的问题
#### 一、xxx could only be written to 0 of the 1 minReplication nodes,There are 3
在使用 Docker 搭建 Hadoop 集群时，遇到以下问题：
```
File /idea/warn.log could only be written to 0 of the 1 minReplication nodes. 
There are 3 datanode(s) running and 3 node(s) are excluded in this operation.
Excluding datanode DatanodeInfoWithStorage[xxx:9866,DS-2520925f-7afd-4f12-89e6-09bee8d8297b,DISK]
Exception in createBlockOutputStream blk_1073741861_1038
```
`分析原因`：<br>
Docker 环境下，文件上传和下载报错，而创建和删除文件夹正常，表明是 DataNode 出现了问题。<br>
NameNode 管理文件信息，创建和删除文件夹属于它的职责，故 NameNode 没有问题。<br>
DataNode 负责具体保存文件数据，DataNode 的问题导致无法上传和下载文件。<br>
Docker 集群外部通信基于端口映射，只有 NameNode 的 8020 端口进行了映射，而 DataNode 的 9866 端口没有映射，因此无法找到 DataNode。

`解决方案`：<br>
1、配置容器主机名访问 DataNode
```
Configuration conf = new Configuration();
conf.set("dfs.client.use.datanode.hostname", "true");
```
2、将所有容器的主机名映射到服务器主机域名，修改`/etc/hosts`文件，添加如下内容：
```
127.0.0.1 hadoop001
127.0.0.1 hadoop002
127.0.0.1 hadoop003
```
3、设置能写入数据的 DataNode 的端口映射动态为 Docker 容器添加端口映射，即修改`docker-compose.yml`文件：
```
services:
  hadoop101:
    ports:
      - 9866:9866

  hadoop102:
    ports:
      - 19866:9866

  hadoop103:
    ports:
      - 29866:9866
```

### 参考
[Hadoop学习之路（七）Hadoop集群shell常用命令](https://www.cnblogs.com/qingyunzong/p/8527595.html)
[【Docker x Hadoop x Java API】](https://juejin.cn/post/7102339801805750285)