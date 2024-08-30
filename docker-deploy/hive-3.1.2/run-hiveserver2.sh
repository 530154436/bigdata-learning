#!/bin/bash

# 禁用ipv6
#export JAVA_TOOL_OPTIONS="-Djava.net.preferIPv4Stack=true"
export HADOOP_HEAPSIZE=2048 # Setting for HiveServer2 and Client


# 启动 HiveServer2（可能需要手动启动）
timeout=600
counter=0
hiveserver_port=10000  # 默认端口是 10000
echo "[INFO] HiveServer2 服务启动中，检测 $hiveserver_port 端口......"

# > 将标准输出（stdout）重定向到 hiveserver2.out 文件中。
# 2>&1 将标准错误（stderr）重定向到标准输出，使得错误输出和标准输出都写入 hiveserver2.out 文件中
nohup $HIVE_HOME/bin/hive --service hiveserver2 --hiveconf hive.root.logger=INFO,console > hiveserver2.out 2>&1 &
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