
镜像准备
```shell
docker pull 15521147129/bigdata:hadoop-3.1.4
docker pull 15521147129/bigdata:mysql-5.6.37
docker pull 15521147129/bigdata:hive-3.1.2

docker compose -f hadoop-3.1.4/docker-compose.yml up -d 
docker compose -f mysql-5.6.37/docker-compose.yml up -d
docker compose -f hive-3.1.2/docker-compose.yml up -d
```


本地docker运行镜像后需配置以下项才能通过spark连接。

+ hosts
```
127.0.0.1	hadoop101
127.0.0.1	hadoop102
127.0.0.1	hadoop103
127.0.0.1	mysql
127.0.0.1	hive
```
+ ${PythonEnv}\Lib\site-packages\pyspark目录新建conf目录后添加以下文件
```
[core-site.xml](conf/core-site.xml)
[hdfs-site.xml](conf/hdfs-site.xml)
[hive-site.xml](conf/hive-site.xml)
[mapred-site.xml](conf/mapred-site.xml)
[yarn-site.xml](conf/yarn-site.xml)
```

+ 配置hive-site.xml和数帆保持一致
```xml
<property>
    <name>hive.metastore.warehouse.dir</name>
    <value>/user/hive/warehouse/hive_db</value>
</property>
<property>
    <name>hive.metastore.warehouse.external.dir</name>
    <value>/user/hive/warehouse/hive_db</value>
</property>
```