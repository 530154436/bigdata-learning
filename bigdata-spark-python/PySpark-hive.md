
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
