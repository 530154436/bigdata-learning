#!/bin/bash

# 定义主机名数组
HOSTNAMES=("hadoop101" "hadoop102" "hadoop103", "mysql")

# 遍历每个主机名
for HOSTNAME in "${HOSTNAMES[@]}"; do
    # 使用 getent hosts 获取主机名的 IP 地址
    IP_ADDRESS=$(getent hosts $HOSTNAME | awk '{ print $1 }')

    # 检查是否成功解析到 IP 地址
    if [ -z "$IP_ADDRESS" ]; then
        echo "无法解析 $HOSTNAME 的 IP 地址，跳过..."
        continue
    else
        echo "$HOSTNAME 的 IP 地址为: $IP_ADDRESS"
    fi

    # 检查 /etc/hosts 文件中是否已经存在主机名映射
    if grep -q "$HOSTNAME" /etc/hosts; then
        echo "$HOSTNAME 已经存在于 /etc/hosts 文件中"
    else
        # 追加主机名和 IP 映射到 /etc/hosts
        echo "$IP_ADDRESS    $HOSTNAME" | sudo tee -a /etc/hosts
        echo "已将 $HOSTNAME 添加到 /etc/hosts"
    fi
done