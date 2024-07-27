#!/bin/bash

ZOOKEEPER_HOME=/usr/local/zookeeper-3.5.5
HADOOP_HOME=/usr/local/hadoop-3.1.1

$ZOOKEEPER_HOME/bin/zkServer.sh start
#$HADOOP_HOME/sbin/start-dfs.sh
#$HADOOP_HOME/sbin/start-yarn.sh

echo "启动成功."

bash


