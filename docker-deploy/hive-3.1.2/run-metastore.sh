#!/bin/bash

# 禁用ipv6
#export JAVA_TOOL_OPTIONS="-Djava.net.preferIPv4Stack=true"

# hive启动后只有一个RunJar进程
echo "[INFO] 启动 Metastore 服务..."

# > 将标准输出（stdout）重定向到 hiveserver2.out 文件中。
# 2>&1 将标准错误（stderr）重定向到标准输出，使得错误输出和标准输出都写入 hiveserver2.out 文件中
nohup $HIVE_HOME/bin/hive --service metastore --hiveconf hive.root.logger=INFO,console > hivemetastore.out 2>&1 &
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
