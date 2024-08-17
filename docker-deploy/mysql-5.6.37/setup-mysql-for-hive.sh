#!/bin/bash

MYSQL_HOME=/usr/local/mysql-5.6.37
# MySQL 连接信息
MYSQL_HOST=localhost
MYSQL_USER="root"
MYSQL_PASSWORD="123456"

# 启动 mysql
${MYSQL_HOME}/support-files/mysql.server start -user=mysql

# 等待 MySQL 服务启动并可连接
while ! ${MYSQL_HOME}/bin/mysqladmin ping -h"$MYSQL_HOST" --silent; do
    echo "等待 MySQL 启动..."
    sleep 2
done
echo "MySQL 启动完成，准备连接。"

## 使用mysqladmin工具进行密码修改
#${MYSQL_HOME}/bin/mysqladmin -uroot -p123456 password "123456"

# 检查 Hive 数据库是否已经存在
DB_EXISTS=$(echo "SHOW DATABASES LIKE 'hive';" | ${MYSQL_HOME}/bin/mysql -h${MYSQL_HOST} -u${MYSQL_USER} -p${MYSQL_PASSWORD} | grep hive)

# 如果 Hive 数据库不存在，执行数据库创建和配置
if [ -z "$DB_EXISTS" ]; then
  echo "Hive 数据库不存在，正在创建..."
  ${MYSQL_HOME}/bin/mysql -h${MYSQL_HOST} -u${MYSQL_USER} -p${MYSQL_PASSWORD} <<EOF
    /* 创建 Hive 数据库 */
    CREATE DATABASE hive;
    GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
    FLUSH PRIVILEGES;
    /* 设置编码 */
    SET character_set_client = utf8;
    SET character_set_server = utf8;
    SET character_set_connection = utf8;
    SET character_set_database = utf8;
    SET character_set_results = utf8;
    SET collation_connection = utf8_general_ci;
    SET collation_database = utf8_general_ci;
    SET collation_server = utf8_general_ci;
    /* 创建用户并授予权限 */
    CREATE USER 'hive'@'%' IDENTIFIED BY '123456';
    GRANT ALL ON hive.* TO 'hive'@'%';
    GRANT ALL ON hive.* TO 'hive'@'localhost';
    GRANT ALL ON hive.* TO 'hive'@'hive';
    FLUSH PRIVILEGES;
EOF
  echo "Hive 数据库创建成功。"
else
  echo "Hive 数据库已存在，跳过创建。"
fi

# 查看当前用户
# SELECT USER(), CURRENT_USER();
# 查询用户权限
# SELECT user, host FROM mysql.user

# 关闭服务
#${MYSQL_HOME}/support-files/mysql.server stop

echo "启动成功."

bash
