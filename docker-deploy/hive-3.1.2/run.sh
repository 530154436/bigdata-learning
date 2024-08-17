#!/bin/bash

echo "[INFO] hive starting..."

# init mysql schema if not created
# 初始化成功会在mysql中创建74张表
if [ ! -f /home/hive/.initSchema ]; then
  echo "[INFO] Initing schema for backend MySQL..."
  $HIVE_HOME/bin/schematool -initSchema -dbType mysql
  touch /home/hive/.initSchema
  echo "[INFO] Finished initing schema for backend MySQL."
fi

# start metastore service
if [ ! -d /var/log/hive ]; then
  sudo mkdir -p /var/log/hive
  sudo chown -R hive:hadoop /var/log/hive
fi

# hive启动后只有一个RunJar进程
echo "[INFO] Starting metastore service..."
nohup $HIVE_HOME/bin/hive --service metastore --hiveconf hive.log.file=hivemetastore.log --hiveconf hive.log.dir=/var/log/hive > /var/log/hive/metastore.out 2> /var/log/hive/metastore.err &
echo "[INFO] Finished starting metastore service."

# 启动 hiveserver2
echo "[INFO] Starting hiveserver2..."
nohup $HIVE_HOME/bin/hive --service hiveserver2 --hiveconf hive.log.file=hiveserver2.log --hiveconf hive.log.dir=/var/log/hive > /var/log/hive/hiveserver2.out 2> /var/log/hive/hiveserver2.err &
echo "[INFO] Finished starting hiveserver2."

bash
