#!/bin/bash

FLINK_HOME=/usr/local/flink-1.13.2

# 加载 /etc/profile 环境变量
source /etc/profile

echo "[INFO] 启动中..."

# 配置主机名-ip映射关系
bash -c /dockerentry/run-getent-hosts.sh

# 启动 hdfs 和 yarn
bash -c /dockerentry/run-namenode.sh

# 启动 flink
bash -c /dockerentry/run-master.sh

echo "[INFO] 启动成功."
bash
