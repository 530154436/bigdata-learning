#!/bin/bash

HADOOP_HOME=/usr/local/hadoop-3.1.1

BASE_DIR=/home/hadoop_files
DATA_DIR=$BASE_DIR/hadoop_data
HADOOP_NN_DIR=$DATA_DIR/hadoop/namenode

if [ "`ls -A $HADOOP_NN_DIR`" == "" ]; then
  echo "Formatting namenode, name directory: $HADOOP_NN_DIR"
  #$HADOOP_HOME/bin/hdfs --daemon start journalnode
  $HADOOP_HOME/bin/hdfs namenode -format
  #$HADOOP_HOME/bin/hdfs --daemon stop journalnode
fi

$HADOOP_HOME/sbin/start-dfs.sh
$HADOOP_HOME/sbin/start-yarn.sh
$HADOOP_HOME/sbin/mr-jobhistory-daemon.sh start historyserver

echo "启动成功."
# 防止容器启动后退出
bash
