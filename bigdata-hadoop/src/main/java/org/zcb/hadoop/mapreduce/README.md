本地环境运行 MapReduce：
1. 运行时需配置参数<br>
```shell
org.zcb.hadoop.hdfs://cluster1:9000/test/test-in org.zcb.hadoop.hdfs://cluster1:9000/test/test-out
```
2. 需修改配置文件core-site.xml（因为存在用户读写权限问题）<br>
```xml
<property>
    <name>hadoop.tmp.dir</name>
    <value>/tmp</value>
</property>
```