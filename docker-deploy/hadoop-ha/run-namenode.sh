#!/bin/bash

ZOOKEEPER_HOME=/usr/local/zookeeper-3.5.5
HADOOP_HOME=/usr/local/hadoop-3.1.1

ssh-keygen -q -t rsa -N '' -f ~/.ssh/id_rsa \
    && cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys

sshpass -p "hadoop" ssh-copy-id -i ~/.ssh/id_rsa.pub hadoop@hadoop102 -o StrictHostKeyChecking=no
sshpass -p "hadoop" ssh-copy-id -i ~/.ssh/id_rsa.pub hadoop@hadoop103 -o StrictHostKeyChecking=no

$ZOOKEEPER_HOME/bin/zkServer.sh start

#$HADOOP_HOME/bin/hdfs --daemon start journalnode
$HADOOP_HOME/bin/hdfs namenode -format
#$HADOOP_HOME/bin/hdfs --daemon stop journalnode
$HADOOP_HOME/sbin/start-dfs.sh
$HADOOP_HOME/sbin/start-yarn.sh

bash