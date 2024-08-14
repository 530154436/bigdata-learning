#!/bin/bash

GZ_FILE="/usr/local/CDP7.1.4/scala-2.11.12.tgz"
INSTALL_DIR=/usr/local/scala-2.11.12


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
function configure_env(){

	# 判断环境变量是否存在
	envstr=`sed -n "/SCALA_HOME=/p" /etc/profile`
	if [ ! -z "$envstr" ]; then 
		echo "Scala环境变量已经配置"
		echo "$envstr"
		return
	fi

	# 在/etc/profile中追加（<<）环境变量
	cat >> /etc/profile << EOF
# Scala 2.11.12
export SCALA_HOME=/usr/local/scala-2.11.12
export PATH=\$PATH:\$SCALA_HOME/bin

EOF

	# 使环境变量生效
	source /etc/profile
	echo "Scala环境变量配置完成."
	echo "SCALA_HOME=$SCALA_HOME"
}


# 主流程
# ---------------------------------------------------------------------------
if [ ! -e ${GZ_FILE} ]; then
	echo "安装包(${GZ_FILE})不存在!"
	return
fi


# 检查JDK是否安装
if ! command -v scala ; then

	decompress
	configure_env

	echo "Scala安装完成!"
else
	echo "Scala已安装!"
fi

# 查看版本，验证安装是否成功
scala -version

