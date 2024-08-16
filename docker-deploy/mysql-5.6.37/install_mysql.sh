#!/bin/bash
# source install_mysql.sh

GZ_FILE="/usr/local/CDP7.1.4/mysql-5.6.37-linux-glibc2.12-x86_64.tar.gz"
INSTALL_DIR="/usr/local/mysql-5.6.37"  # 默认路径,若修改会影响后续操作(server)

USER_GROUP=mysql
USER_NAME=mysql


# ---------------------------------------------------------------------------
# 解压缩
# ---------------------------------------------------------------------------
function decompress(){
	if [ -e ${INSTALL_DIR} ]; then
		echo "安装目录(${INSTALL_DIR})已存在."
		# rm -r ${INSTALL_DIR}
		return
	fi

	# 解压缩
	mkdir ${INSTALL_DIR}
	tar -zxvf ${GZ_FILE} -C ${INSTALL_DIR}

	# 获取解压后的目录名称
	TEMP=`ls ${INSTALL_DIR}`
	
	# 将解压后目录的所有内容移动到安装目录
	mv ${INSTALL_DIR}/${TEMP}/* ${INSTALL_DIR}
	sudo rm -r ${INSTALL_DIR}/${TEMP}
	echo "${GZ_FILE} 解压缩完成"
	echo "安装目录: ${INSTALL_DIR}"
}


# ---------------------------------------------------------------------------
# 配置环境变量
# ---------------------------------------------------------------------------
configue_env(){

	# 判断环境变量是否存在
	envstr=`sed -n "/MYSQL_HOME=/p" /etc/profile`
	if [ ! -z "$envstr" ]; then 
		echo "MySQL环境变量已经配置"
		echo "$envstr"
		return
	fi

	# 在/etc/profile中追加（<<）环境变量
	cat >> /etc/profile << EOF
# MySQL 5.6.37
export MYSQL_HOME=$INSTALL_DIR
export PATH=\$PATH:\$MYSQL_HOME/bin

EOF

	# 使环境变量生效
	source /etc/profile
	echo "MySQL环境变量配置完成."
	echo "MYSQL_HOME=$MYSQL_HOME"
}


# ---------------------------------------------------------------------------
# 配置用户组和用户
# ---------------------------------------------------------------------------
create_mysql_user(){

	# 判断用户组是否创建
	groupstr=`sed -n "/$USER_GROUP/p" /etc/group`
	if [ -z "$groupstr" ]; then
		groupadd $USER_GROUP
	    echo "用户组($USER_GROUP)创建成功."
	else
		echo "用户组($USER_GROUP)已存在."
	fi

	# 判断用户是否创建
	userstr=`sed -n "/$USER_NAME/p" /etc/passwd`
	if [ -z "$userstr" ]; then
		# -r：创建一个系统用户
		# -s：手工指定用户的登录 Shell，默认是 /bin/bash
		useradd -r -g mysql -s /bin/false mysql
	    echo "用户($USER_NAME)创建成功."
	else
		echo "用户($USER_NAME)已存在."
	fi
}

# ---------------------------------------------------------------------------
# 配置MySQL配置文件
# ---------------------------------------------------------------------------
configue_my_cnf(){
	sudo cp ${INSTALL_DIR}/support-files/my-default.cnf /etc/my.cnf

	# 追加（<<）环境变量
	cat >> /etc/my.cnf << EOF
[mysqld]
user=mysql
basedir=$INSTALL_DIR
datadir=$INSTALL_DIR/data
bind-address = 0.0.0.0

EOF

	# 使环境变量生效
	echo "/etc/my.cnf 配置完成."
}

# ---------------------------------------------------------------------------
# 主流程
# ---------------------------------------------------------------------------
if [ ! -e ${GZ_FILE} ]; then
	echo "安装包(${GZ_FILE})不存在!"
	return
fi


# 检查是否安装MySQL
if ! command -v mysql; then

	# 解压缩
	decompress

	# 配置MySQL环境变量
	configue_env

	# 新建MySQL用户
	create_mysql_user

	chown -R ${USER_GROUP}:${USER_NAME} ${INSTALL_DIR}

	# mysql_install_db 初始化mysql的data目录、并创建系统表
	${INSTALL_DIR}/scripts/mysql_install_db --user=${USER_NAME} --basedir=${INSTALL_DIR} --datadir=${INSTALL_DIR}/data
	
	chown -R root ${INSTALL_DIR}

	# 修改当前 data 目录拥有者为 mysql 用户
	chown -R ${USER_NAME} ${INSTALL_DIR}/data

	# 设定MySQL配置文件
	configue_my_cnf

else
	echo "MySQL已安装!"
fi

