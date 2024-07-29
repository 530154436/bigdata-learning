#!/bin/bash

ZOOKEEPER_HOME=/usr/local/zookeeper-3.5.5
HADOOP_HOME=/usr/local/hadoop-3.1.1

BASE_DIR=/home/hadoop_files
DATA_DIR=$BASE_DIR/hadoop_data
HADOOP_NN_DIR=$DATA_DIR/hadoop/namenode

$ZOOKEEPER_HOME/bin/zkServer.sh start
#$ZOOKEEPER_HOME/bin/zkServer.sh status

if [ "`ls -A $HADOOP_NN_DIR`" == "" ]; then
  echo "Formatting namenode, name directory: $HADOOP_NN_DIR"
  #$HADOOP_HOME/bin/hdfs --daemon start journalnode
  $HADOOP_HOME/bin/hdfs namenode -format
  #$HADOOP_HOME/bin/hdfs --daemon stop journalnode
fi

$HADOOP_HOME/sbin/start-dfs.sh
$HADOOP_HOME/sbin/start-yarn.sh

echo "启动成功."
