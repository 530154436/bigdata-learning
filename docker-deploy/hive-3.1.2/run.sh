#!/bin/bash

HIVE_HOME=/usr/local/hive-3.1.2

echo "[INFO] hive starting..."

# create /user/hive directory on hdfs if not exists
$HADOOP_HOME/bin/hdfs dfs -test -d /user/hive
result=$?
if [ $result -ne 0 ]; then
  # should create hdfs for hive user
  $HADOOP_HOME/bin/hdfs hive:hadoop -mkdir -p /user/hive/warehouse
  $HADOOP_HOME/bin/hdfs hive:hadoop -chown -R hive /user/hive
  $HADOOP_HOME/bin/hdfs hive:hadoop -chown -R hive:hdfs /user/hive/warehouse
  $HADOOP_HOME/bin/hdfs hive:hadoop -chmod 777 /user/hive/warehouse
fi


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

bash -c /dockerentry/run-getent-hosts.sh
bash -c /dockerentry/run-metastore.sh
bash -c /dockerentry/run-hiveserver2.sh

echo "[INFO] Hive所有服务启动成功."
bash
