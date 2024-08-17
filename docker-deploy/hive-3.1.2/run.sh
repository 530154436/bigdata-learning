#!/bin/bash

HIVE_HOME=/usr/local/hive-3.1.2

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

# 设置超时时间（例如，60秒）
timeout=600
counter=0

metastore_port=9083  # 默认端口是 9083
echo "[INFO] Metastore 服务启动中，检测 $metastore_port 端口..."

# 使用 nc 工具检测端口，并根据返回的状态码判断
#hive@hive:~$ nc -zv localhost 9083
#Connection to localhost (::1) 9083 port [tcp/*] succeeded!
while true; do
    nc -zv localhost $metastore_port
    status=$?
    if [ $status -eq 0 ]; then
        break
    fi
    sleep 2
    counter=$((counter + 2))
    if [ $counter -ge $timeout ]; then
        echo "[ERROR] Metastore 服务启动失败."
        exit 1
    fi
done
echo "[INFO] Metastore 服务启动成功."

# 启动 HiveServer2（可能需要手动启动）
timeout=600
counter=0
hiveserver_port=10002  # 默认端口是 9083
echo "[INFO] HiveServer2 服务启动中，检测 $hiveserver_port 端口......"
nohup $HIVE_HOME/bin/hive --service hiveserver2 --hiveconf hive.log.file=hiveserver2.log --hiveconf hive.log.dir=/var/log/hive > /var/log/hive/hiveserver2.out 2> /var/log/hive/hiveserver2.err &
while true; do
    nc -zv localhost $hiveserver_port
    status=$?
    if [ $status -eq 0 ]; then
        break
    fi
    sleep 2
    counter=$((counter + 2))
    if [ $counter -ge $timeout ]; then
        echo "[ERROR] HiveServer2 服务启动失败."
        exit 1
    fi
done
echo "[INFO] HiveServer2 服务启动成功."

echo "[INFO] Hive所有服务启动成功."
bash
