#!/bin/bash

GZ_FILE="/usr/local/CDP7.1.4/jdk-8u112-linux-x64.tar.gz"
INSTALL_DIR=/usr/local/jdk1.8.0_112


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
	echo "解压安装包: ${GZ_FILE}"
	echo "安装目录: ${INSTALL_DIR}"
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
	envstr=`sed -n "/JAVA_HOME=/p" /etc/profile`
	if [ ! -z "$envstr" ]; then 
		echo "Java环境变量已经配置"
		echo "$envstr"
		return
	fi

	# 在/etc/profile中追加（<<）环境变量
	cat >> /etc/profile << EOF
# JDK 1.8
export JAVA_HOME=/usr/local/jdk1.8.0_112
export CLASSPATH=\$CLASSPATH:\$JAVA_HOME/lib:\$JAVA_HOME/jre/lib
export PATH=\$PATH:\$JAVA_HOME/bin:\$JAVA_HOME/jre/bin

EOF

	# 使环境变量生效
	source /etc/profile
	echo "Java环境变量配置完成."
	echo "JAVA_HOME=$JAVA_HOME"
}


# 主流程
# ---------------------------------------------------------------------------

if [ ! -e ${GZ_FILE} ]; then
	echo "安装包(${GZ_FILE})不存在!"
	return
fi


# 检查JDK是否安装
if ! command -v java ; then

	# 卸载 openjdk
	sudo yum -y remove java-1.8.0-openjdk-headless-1.8.0.161-2.b14.el7.x86_64
	sudo yum -y remove copy-jdk-configs-3.3-2.el7.noarch

	decompress
	configure_env

	echo "Java安装完成!"
else
	echo "Java已安装!"
fi

# 查看版本，验证安装是否成功
java -version
javac -version

