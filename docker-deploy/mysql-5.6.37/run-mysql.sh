#!/bin/bash

MYSQL_HOME=/usr/local/mysql-5.6.37

# 启动 MySQL
${MYSQL_HOME}/bin/mysqld_safe --user=mysql &

# 创建hive用户、hive数据库


echo "启动成功."
bash
