#!/bin/bash

ZOOKEEPER_HOME=/usr/local/zookeeper-3.5.5
HADOOP_HOME=/usr/local/hadoop-3.1.1

ssh-keygen -q -t rsa -N '' -f ~/.ssh/id_rsa \
    && cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys

$ZOOKEEPER_HOME/bin/zkServer.sh start
$HADOOP_HOME/sbin/start-dfs.sh
$HADOOP_HOME/sbin/start-yarn.sh


