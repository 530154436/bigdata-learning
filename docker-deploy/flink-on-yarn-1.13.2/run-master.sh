#!/bin/bash

source /etc/profile
FLINK_HOME=/usr/local/flink-1.13.2
HADOOP_HOME=/usr/local/hadoop-3.1.4
HADOOP_NN_DIR=$BASE_DIR/hadoop_data/hadoop/namenode

#----------------------------------------------------------------------
# 启动Hadoop
#----------------------------------------------------------------------
echo "[INFO] Hadoop 启动中..."

if [ "`ls -A $HADOOP_NN_DIR`" == "" ]; then
  echo "Formatting namenode, name directory: $HADOOP_NN_DIR"
  #$HADOOP_HOME/bin/hdfs --daemon start journalnode
  $HADOOP_HOME/bin/hdfs namenode -format
  #$HADOOP_HOME/bin/hdfs --daemon stop journalnode
fi

$HADOOP_HOME/sbin/start-dfs.sh
$HADOOP_HOME/sbin/start-yarn.sh
# 启动日志服务
$HADOOP_HOME/bin/mapred --daemon start historyserver

#----------------------------------------------------------------------
# 启动Flink
#----------------------------------------------------------------------
# 跳过 SSH 主机验证，避免卡在主机验证阶段
export FLINK_SSH_OPTS="-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"

echo "[INFO] Flink JobManager 启动中..."

$HADOOP_HOME/bin/hdfs dfs -mkdir -p /flink/completed-jobs
$HADOOP_HOME/bin/hdfs dfs -mkdir -p /flink/checkpoints
$HADOOP_HOME/bin/hdfs dfs -mkdir -p /flink/savepoints

# 配置主机名-ip映射关系
bash -c /dockerentry/run-getent-hosts.sh

# 启动集群
$FLINK_HOME/bin/start-cluster.sh
# 单独启动
#$FLINK_HOME/bin/jobmanager.sh start cluster
#$FLINK_HOME/bin/taskmanager.sh start

# 启动历史服务器
$FLINK_HOME/bin/historyserver.sh start

echo "[INFO] Flink JobManager启动成功."
bash
