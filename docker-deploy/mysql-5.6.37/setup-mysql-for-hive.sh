#!/bin/bash

MYSQL_HOME=/usr/local/mysql-5.6.37

# 启动 mysql
${MYSQL_HOME}/support-files/mysql.server start -user=mysql

## 使用mysqladmin工具进行密码修改
#${MYSQL_HOME}/bin/mysqladmin -uroot -p123456 password "123456"
${MYSQL_HOME}/bin/mysql -uroot -p123456 << EOF
	/*创建数据库*/
	CREATE database hive;
	GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
  FLUSH PRIVILEGES;
	/*设置编码*/
	set character_set_client = utf8;
	set character_set_server = utf8;
	set character_set_connection = utf8;
	set character_set_database = utf8;
	set character_set_results = utf8;
	set collation_connection = utf8_general_ci;
	set collation_database = utf8_general_ci;
	set collation_server = utf8_general_ci;
	/*创建用户*/
	CREATE USER 'hive'@'%' IDENTIFIED BY '123456';
	grant all on hive.* to hive@'%';
  grant all on hive.* to hive@'localhost';
  grant all on hive.* to hive@'hive';
  flush privileges;
/*注意:EOF要靠边*/
EOF

# 查看当前用户
# SELECT USER(), CURRENT_USER();
# 查询用户权限
# SELECT user, host FROM mysql.user

# 关闭服务
#${MYSQL_HOME}/support-files/mysql.server stop

echo "启动成功."

bash
