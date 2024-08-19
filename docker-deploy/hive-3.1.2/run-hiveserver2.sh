#!/bin/bash

# 禁用ipv6
#export JAVA_TOOL_OPTIONS="-Djava.net.preferIPv4Stack=true"

# 启动 HiveServer2（可能需要手动启动）
timeout=600
counter=0
hiveserver_port=10000  # 默认端口是 10000
echo "[INFO] HiveServer2 服务启动中，检测 $hiveserver_port 端口......"
nohup $HIVE_HOME/bin/hive --service hiveserver2 --hiveconf hive.root.logger=DEBUG,console --hiveconf hive.log.file=hiveserver2.log --hiveconf hive.log.dir=/var/log/hive > /var/log/hive/hiveserver2.out &
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