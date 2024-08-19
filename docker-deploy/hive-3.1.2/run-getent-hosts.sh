#!/bin/bash

# 定义主机名数组
HOSTNAMES=("hadoop101" "hadoop102" "hadoop103" "mysql")

# 遍历每个主机名
for HOSTNAME in "${HOSTNAMES[@]}"; do
    # 使用 getent hosts 获取主机名的 IP 地址
    IP_ADDRESS=$(getent hosts $HOSTNAME | awk -v host="$HOSTNAME" '$2 == host { print $1 }')

    # 检查是否成功解析到 IP 地址
    if [ -z "$IP_ADDRESS" ]; then
        echo "无法解析 $HOSTNAME 的 IP 地址，跳过..."
        continue
    fi

    echo "$HOSTNAME 的 IP 地址为: $IP_ADDRESS"

    # 检查 /etc/hosts 是否已经存在这个 IP 地址和主机名的映射
    if grep -q "$HOSTNAME" /etc/hosts; then
        echo "$HOSTNAME 已经存在于 /etc/hosts 文件中"
    else
        # 添加 IP 和主机名到 /etc/hosts
        echo "$IP_ADDRESS    $HOSTNAME" | sudo tee -a /etc/hosts > /dev/null
        echo "已将 $HOSTNAME 添加到 /etc/hosts"
    fi
done