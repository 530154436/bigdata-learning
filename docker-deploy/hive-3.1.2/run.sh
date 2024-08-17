#!/bin/bash

echo "[INFO] hive starting..."

# 通过标志文件来判断 Hive 是否已经初始化。
if [ ! -f /home/hive/.initSchema ]; then
  echo "[INFO] 正在初始化 Hive数据库..."
  $HIVE_HOME/bin/schematool -initSchema -dbType mysql
  touch /home/hive/.initSchema
  echo "[INFO] Hive数据库初始化成功."
else
  echo "[INFO] Hive数据库已经初始化。"
fi

# start metastore service
if [ ! -d /var/log/hive ]; then
  sudo mkdir -p /var/log/hive
  sudo chown -R hive:hadoop /var/log/hive
fi

# hive启动后只有一个RunJar进程
echo "[INFO] 启动 Metastore 服务..."
nohup $HIVE_HOME/bin/hive --service metastore --hiveconf hive.log.file=hivemetastore.log --hiveconf hive.log.dir=/var/log/hive > /var/log/hive/metastore.out 2> /var/log/hive/metastore.err &
echo "[INFO] Metastore 服务启动成功."

# 启动 HiveServer2
echo "[INFO] 启动 HiveServer2 服务..."
nohup $HIVE_HOME/bin/hive --service hiveserver2 --hiveconf hive.log.file=hiveserver2.log --hiveconf hive.log.dir=/var/log/hive > /var/log/hive/hiveserver2.out 2> /var/log/hive/hiveserver2.err &
echo "[INFO] HiveServer2 服务启动成功."

echo "[INFO] Hive启动成功."
bash
