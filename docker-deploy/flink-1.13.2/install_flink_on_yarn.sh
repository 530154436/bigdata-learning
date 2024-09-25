#!/bin/bash

GZ_FILE="/usr/local/CDP7.1.4/flink-1.13.2-bin-scala_2.12.tgz"
INSTALL_DIR="/usr/local/flink-1.13.2"

USER_GROUP=hadoop
USER_NAME=hadoop

JAVA_HOME=/usr/local/jdk1.8.0_112
HADOOP_HOME="/usr/local/hadoop-3.1.4"

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
	envstr=`sed -n "/FLINK_HOME=/p" /etc/profile`
	if [ ! -z "$envstr" ]; then 
		echo "Flink环境变量已经配置"
		echo "$envstr"
		return
	fi

	# 在/etc/profile中追加（<<）环境变量
	HADOOP_CLASSPATH=`$HADOOP_HOME/bin/hadoop classpath`
	cat >> /etc/profile << EOF
# Flink 1.13.2
export FLINK_HOME=$INSTALL_DIR
export PATH=\$FLINK_HOME/bin:\$PATH

export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
export HADOOP_CLASSPATH=$HADOOP_CLASSPATH

EOF

	# 使环境变量生效
	source /etc/profile
	echo "Flink环境变量配置完成."
	echo "FLINK_HOME=$FLINK_HOME"
}


# ---------------------------------------------------------------------------
# 配置 conf/flink-conf.yaml
# ---------------------------------------------------------------------------
function configure_flink_conf(){
	if [ ! -e ${INSTALL_DIR}/conf/flink-conf.yaml.bkp ]; then
		mv $INSTALL_DIR/conf/flink-conf.yaml $INSTALL_DIR/conf/flink-conf.yaml.bkp
	fi

# 覆盖文件 conf/flink-conf.yaml
cat > $INSTALL_DIR/conf/flink-conf.yaml << EOF
# Common
jobmanager.rpc.address: flink101
jobmanager.rpc.port: 6123
jobmanager.memory.process.size: 1600m
taskmanager.memory.process.size: 1728m
taskmanager.numberOfTaskSlots: 4
parallelism.default: 1

fs.default-scheme: hdfs://hadoop101:9000/
# io.tmp.dirs: hdfs://hadoop101:9000/flink/tmp

# 是否启动web提交
web.submit.enable: true

# HistoryServer
jobmanager.archive.fs.dir: hdfs://hadoop101:9000/flink/completed-jobs/
historyserver.archive.fs.dir: hdfs://hadoop101:9000/flink/completed-jobs/
historyserver.web.address: flink101
historyserver.web.port: 8082

# state
state.backend: filesystem
state.checkpoints.dir: hdfs://hadoop101:9000/flink/checkpoints
state.savepoints.dir: hdfs://hadoop101:9000/flink/savepoints

EOF

	echo "$INSTALL_DIR/conf/flink-conf.yaml 配置完成."
}

# ---------------------------------------------------------------------------
# 配置 conf/workers (slaves)
# ---------------------------------------------------------------------------
function configure_workers(){
	if [ ! -e ${INSTALL_DIR}/conf/workers.bkp ]; then
		mv $INSTALL_DIR/conf/workers $INSTALL_DIR/conf/workers.bkp
	fi

cat > $INSTALL_DIR/conf/workers << EOF
flink101
flink102

EOF

	echo "$INSTALL_DIR/conf/workers 配置完成."
}


# ---------------------------------------------------------------------------
# 配置 masters
# ---------------------------------------------------------------------------
function configure_masters(){
	if [ ! -e ${INSTALL_DIR}/conf/masters.bkp ]; then
		mv $INSTALL_DIR/conf/masters $INSTALL_DIR/conf/masters.bkp
	fi

cat > $INSTALL_DIR/conf/masters << EOF
flink101:8081

EOF

	echo "$INSTALL_DIR/conf/masters 配置完成."
}


# ---------------------------------------------------------------------------
# 主流程
# ---------------------------------------------------------------------------
if [ ! -e ${GZ_FILE} ]; then
	echo "安装包(${GZ_FILE})不存在!"
	return
fi


# 检查是否安装MySQL
if ! command -v flink; then

	# 解压缩
	decompress

	# 配置
	configure_env
	configure_flink_conf
	configure_masters
	configure_workers

	# 修改文件夹的权限
	chown -R $USER_GROUP:$USER_NAME $INSTALL_DIR

	echo "Flink装完成!"
else
	echo "Flink已安装!"
fi


