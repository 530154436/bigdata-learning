#!/bin/bash

FLINK_HOME=/usr/local/flink-1.13.2

# 加载 /etc/profile 环境变量
source /etc/profile

# 跳过 SSH 主机验证，避免卡在主机验证阶段
export FLINK_SSH_OPTS="-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"

echo "[INFO] Flink JobManager 启动中..."

$HADOOP_HOME/bin/hdfs dfs -mkdir -p /flink/historyserver/
$HADOOP_HOME/bin/hdfs dfs -mkdir -p /flink/jobmanager/

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
