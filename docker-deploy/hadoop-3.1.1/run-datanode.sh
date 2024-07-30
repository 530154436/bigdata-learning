#!/bin/bash

HADOOP_HOME=/usr/local/hadoop-3.1.1

# 等待300秒的空循环(不然镜像拉起来后会退出)
end=$((SECONDS+300))
while [ $SECONDS -lt $end ]; do
    :  # 空操作，什么也不做
done

echo "启动成功."
bash
