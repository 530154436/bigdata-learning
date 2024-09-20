#!/bin/bash

FLINK_HOME=/usr/local/flink-1.13.2

# 跳过 SSH 主机验证，避免卡在主机验证阶段
export FLINK_SSH_OPTS="-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"

echo "[INFO] Flink JobManager 启动中..."

# 陪孩子主机名-ip
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
