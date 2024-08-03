<nav>
<a href="#一hdfs常用shell命令">一、HDFS常用shell命令</a><br/>
<a href="#二hdfs-java-api">二、HDFS JAVA API</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-判断目录或文件是否存在">2.1 判断目录或文件是否存在</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-创建目录">2.2 创建目录</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-列出指定目录下的文件以及块的信息">2.3 列出指定目录下的文件以及块的信息</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#24-重命名目录或文件">2.4 重命名目录或文件</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#25-删除文件或目录">2.5 删除文件或目录</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#26-上传文件">2.6 上传文件</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#27-写文件">2.7 写文件</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#28-追加文件内容">2.8 追加文件内容</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#29-下载文件">2.9 下载文件</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#210-读文件">2.10 读文件</a><br/>
<a href="#三遇到的问题">三、遇到的问题</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#31-xxx-could-only-be-written-to-0-of-the-1-minreplication-nodesthere-are-3">3.1 xxx could only be written to 0 of the 1 minReplication nodes,There are 3</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#32-failed-to-replace-a-bad-datanode-on-the-existing-pipeline-due-to-no-more-good-datanodes">3.2 Failed to replace a bad datanode on the existing pipeline due to no more good datanodes</a><br/>
<a href="#参考引用">参考引用</a><br/>
</nav>

### 一、HDFS常用shell命令
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
### 二、HDFS JAVA API
详见 bigdata-hadoop/src/main/java/org/zcb/hdfs/HdfsUtil.java
#### 2.1 判断目录或文件是否存在
```
public boolean exists(String path) throws IOException {
    return fs.exists(new Path(path));
}
```
#### 2.2 创建目录
```
public boolean mkdirs(String path) throws IOException {
    boolean flag = false;
    if(!exists(path)) {
        System.out.println("目录创建成功：" + path);
        flag = fs.mkdirs(new Path(path));
    } else {
        System.out.println("目录已存在：" + path);
    }
    return flag;
}
```
#### 2.3 列出指定目录下的文件以及块的信息
```
public void listFiles(String path, boolean recursive){
    RemoteIterator<LocatedFileStatus> iterator = null;
    try {
        iterator = fs.listFiles(new Path(path), recursive);
        while (iterator.hasNext()){
            LocatedFileStatus fileStatus = iterator.next();
            // 文件的block信息
            BlockLocation[] blockLocations = fileStatus.getBlockLocations();
            System.out.printf("文件路径: %s, 块的数量: %s.\n", fileStatus.getPath(), blockLocations.length);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

#### 2.4 重命名目录或文件
```
public boolean rename(String src, String dst) throws IOException {
    if(!exists(src) || exists(dst)){
        System.out.printf("源文件不存在或目标文件存在: %s, %s\n", src, dst);
        return false;
    }
    boolean flag = fs.rename(new Path(src), new Path(dst));
    System.out.printf("文件命名程刚成功: %s => %s\n", src, dst);
    return flag;
}
```

#### 2.5 删除文件或目录
```
public boolean delete(String path, boolean recursive) throws IOException {
    if(!exists(path)){
        System.out.printf("文件不存在: %s\n", path);
        return false;
    }
    boolean flag = fs.delete(new Path(path), recursive);
    System.out.printf("文件删除成功: %s.\n", path);
    return flag;
}
```

#### 2.6 上传文件
```
public boolean upload(boolean delSrc, boolean overwrite, String src, String dst) throws IOException {
    fs.copyFromLocalFile(delSrc, overwrite, new Path(src), new Path(dst));
    System.out.printf("文件上传成功: %s => %s.\n", src, dst);
    return true;
}
public boolean upload(boolean overwrite, String src, String dst) throws IOException {
    return upload(false, overwrite, src, dst);
}
public boolean upload(String src, String dst) throws IOException {
    return upload(true, src, dst);
}
```

#### 2.7 写文件
通过调用FileSystem实例的create方法获取写文件的输出流。通常获得输出流之后，可以直接对这个输出流进行写入操作，将内容写入HDFS的指定文件中。写完文件后，需要调用close方法关闭输出流。
```
public boolean create(String dst, String content) throws IOException{
    FSDataOutputStream out = null;
    try {
        out = fs.create(new Path(dst));
        out.write(content.getBytes());
        out.hsync();
        System.out.printf("文件内容写入成功: %s => %s.\n", dst, content);
        return true;
    } finally {
        assert out != null;
        out.close();
    }
}
```

#### 2.8 追加文件内容
对于已经在HDFS中存在的文件，可以追加指定的内容，以增量的形式在该文件现有内容的后面追加。通过调用FileSystem实例的append方法获取追加写入的输出流。然后使用该输出流将待追加内容添加到HDFS的指定文件后面。追加完指定的内容后，需要调用close方法关闭输出流。
```
public boolean append(String dst, String content) throws IOException{
    FSDataOutputStream out = null;
    try {
        out = fs.append(new Path(dst));
        out.write(content.getBytes());
        out.hsync();
        System.out.printf("文件内容追加成功: %s => %s.\n", dst, content);
        return true;
    } finally {
        assert out != null;
        out.close();
    }
}
```

#### 2.9 下载文件
```
public boolean download(boolean delSrc, String src, String dst, boolean useRaw) throws IOException {
    Path srcPath = new Path(src);
    if(!fs.exists(srcPath)){
        System.out.printf("文件不存在: %s\n", src);
        return false;
    }
    fs.copyToLocalFile(delSrc, new Path(src), new Path(dst), useRaw);
    System.out.printf("文件下载成功: %s => %s.\n", src, dst);
    return true;
}
public boolean download(String src, String dst) throws IOException {
    return download(false, src, dst, true);
}
```


#### 2.10 读文件
获取HDFS上某个指定文件的内容。通过调用FileSystem实例的open方法获取读取文件的输入流。然后使用该输入流读取HDFS的指定文件的内容。读完文件后，需要调用close方法关闭输入流。
```
public String read(String path) throws IOException {
    if(!exists(path)){
        return null;
    }
    FSDataInputStream in = null;
    BufferedReader reader = null;
    StringBuilder strBuilder = new StringBuilder();
    try {
        in = fs.open(new Path(path));
        reader = new BufferedReader(new InputStreamReader(in));
        String sTempOneLine;
        // write file
        while ((sTempOneLine = reader.readLine()) != null) {
            strBuilder.append(sTempOneLine);
        }
        return strBuilder.toString();
    } finally {
        // make sure the streams are closed finally.
        IOUtils.closeStream(reader);
        IOUtils.closeStream(in);
    }
}
```

### 三、遇到的问题
#### 3.1 xxx could only be written to 0 of the 1 minReplication nodes,There are 3
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

#### 3.2 Failed to replace a bad datanode on the existing pipeline due to no more good datanodes
在使用Java api操作HDFS的`追加文件内容(append)`时，遇到以下错误：
```
java.io.IOException: Failed to replace a bad datanode on the existing pipeline due to no more good datanodes being available to try. (Nodes: current=[DatanodeInfoWithStorage[34.2.31.31:50010,DS-8234bb39-0fd4-49be-98ba-32080bc24fa9,DISK], DatanodeInfoWithStorage[34.2.31.33:50010,DS-b4758979-52a2-4238-99f0-1b5ec45a7e25,DISK]], original=[DatanodeInfoWithStorage[34.2.31.31:50010,DS-8234bb39-0fd4-49be-98ba-32080bc24fa9,DISK], DatanodeInfoWithStorage[34.2.31.33:50010,DS-b4758979-52a2-4238-99f0-1b5ec45a7e25,DISK]]). The current failed datanode replacement policy is DEFAULT, and a client may configure this via 'dfs.client.block.write.replace-datanode-on-failure.policy' in its configuration.
        at org.apache.hadoop.hdfs.DFSOutputStream$DataStreamer.findNewDatanode(DFSOutputStream.java:1036)
        at org.apache.hadoop.hdfs.DFSOutputStream$DataStreamer.addDatanode2ExistingPipeline(DFSOutputStream.java:1110)
        at org.apache.hadoop.hdfs.DFSOutputStream$DataStreamer.setupPipelineForAppendOrRecovery(DFSOutputStream.java:1268)
        at org.apache.hadoop.hdfs.DFSOutputStream$DataStreamer.processDatanodeError(DFSOutputStream.java:993)
        at org.apache.hadoop.hdfs.DFSOutputStream$DataStreamer.run(DFSOutputStream.java:500)
```
由于DataNode写入策略的问题，数据写入失败。<br>
`解决方案`：
```
# 在写入pipeline中如果有DataNode或网络故障，DFSClient会尝试从pipeline中移除失败的DataNode，并继续尝试使用剩下的DataNodes进行写入。
conf.set("dfs.client.block.write.replace-datanode-on-failure.enable", "true");

ALWAYS：总是添加一个新的DataNode。
NEVER：永远不添加新的DataNode。
DEFAULT：副本数为r，DataNode数为n，当r >= 3或floor(r/2) >= n且r > n时，添加一个新的DataNode，并且块是hflushed/appended。
conf.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
```

通过以上配置，确保在DataNode写入失败时不会自动添加新的DataNode，从而避免在小规模集群中可能出现的异常高的pipeline错误。

### 参考引用
[Hadoop学习之路（七）Hadoop集群shell常用命令](https://www.cnblogs.com/qingyunzong/p/8527595.html)
[【Docker x Hadoop x Java API】](https://juejin.cn/post/7102339801805750285)
[开源大数据平台 E-MapReduce-HDFS-开发指南](https://help.aliyun.com/zh/emr/emr-on-ecs/user-guide/developer-guide-2?spm=a2c4g.11186623.0.0.52691274lB3YoN)