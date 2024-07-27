#!/bin/bash
# source install_zookeeper.sh
# 
# 官方文档
# https://zookeeper.apache.org/doc/r3.5.5/zookeeperStarted.html
# 
# 安装
# source install_zookeeper.sh
# 
# 启动 zk
# zkServer.sh start
# 
# 查看 zookeeper 状态
# zkServer.sh status
# 
# 关闭 zookeeper 的命令
# zkServer.sh stop

GZ_FILE="/usr/local/CDP7.1.4/apache-zookeeper-3.5.5-bin.tar.gz"
INSTALL_DIR="/usr/local/zookeeper-3.5.5"

USER_GROUP=hadoop
USER_NAME=hadoop

BASE_DIR=/home/hadoop_files
DATA_DIR=$BASE_DIR/hadoop_data
LOGS_DIR=$BASE_DIR/hadoop_logs


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
function configue_env(){

	# 判断环境变量是否存在
	envstr=`sed -n "/ZOOKEEPER_HOME=/p" /etc/profile`
	if [ ! -z "$envstr" ]; then 
		echo "ZooKeeper环境变量已经配置"
		echo "$envstr"
		return
	fi

	# 在/etc/profile中追加（<<）环境变量
	cat >> /etc/profile << EOF
# ZooKeeper 3.5.5
export ZOOKEEPER_HOME=$INSTALL_DIR
export PATH=\$PATH:\$ZOOKEEPER_HOME/bin

EOF

	# 使环境变量生效
	source /etc/profile
	echo "ZooKeeper环境变量配置完成"
	echo "ZOOKEEPER_HOME=$ZOOKEEPER_HOME"
}


# ---------------------------------------------------------------------------
# 配置zoo.cfg
# ---------------------------------------------------------------------------
configue_zoo_cfg(){
	sudo cp ${INSTALL_DIR}/conf/zoo_sample.cfg ${INSTALL_DIR}/conf/zoo.cfg

	# 覆盖
	cat > ${INSTALL_DIR}/conf/zoo.cfg << EOF

# 客户端心跳时间(毫秒)
tickTime=2000

# 允许心跳间隔的最大时间
initLimit=10

# 同步时限
syncLimit=5

# 数据存储目录
dataDir=$DATA_DIR/zookeeper

# 数据日志存储目录 
dataLogDir=$LOGS_DIR/zookeeper/dataLog

# 端口号
clientPort=2181

# 集群节点和服务端口配置
# 3.4.*及以前的配置
# server.1=hadoop101:2888:3888
# server.2=hadoop102:2888:3888
# server.3=hadoop103:2888:3888

# 3.5.*的配置（用分号拼接客户端端口号）
server.1=hadoop101:2888:3888;2181
server.2=hadoop102:2888:3888;2181
server.3=hadoop103:2888:3888;2181

EOF

	# 使环境变量生效
	echo "${INSTALL_DIR}/conf/zoo.cfg 配置完成."
}


# ---------------------------------------------------------------------------
# 配置 log4j.properties
# ---------------------------------------------------------------------------
function configure_log4j_properties(){
	# sed -n "/zookeeper.root.logger=.*/p" log4j.properties 
	# sed s: 替换
	if [ ! -e $INSTALL_DIR/conf/log4j.properties.bkp ]; then
		mv $INSTALL_DIR/conf/log4j.properties $INSTALL_DIR/conf/log4j.properties.bkp
	fi

	sed "s/zookeeper.root.logger=.*/zookeeper.root.logger=INFO, ROLLINGFILE/g" $INSTALL_DIR/conf/log4j.properties.bkp | \
	sed "s/log4j.appender.ROLLINGFILE=.*/log4j.appender.ROLLINGFILE=org.apache.log4j.DailyRollingFileAppender/g" > $INSTALL_DIR/conf/log4j.properties


	echo "$INSTALL_DIR/conf/log4j.properties 配置完成."
}


# ---------------------------------------------------------------------------
# 配置 zkEnv.sh
# ---------------------------------------------------------------------------
function configure_zkEnv_sh(){
	if [ ! -e $INSTALL_DIR/bin/zkEnv.sh.bkp ]; then
		mv $INSTALL_DIR/bin/zkEnv.sh $INSTALL_DIR/bin/zkEnv.sh.bkp
	fi
	sed 's#ZOO_LOG_DIR=.*#ZOO_LOG_DIR='$LOGS_DIR'/zookeeper/logs#g' $INSTALL_DIR/bin/zkEnv.sh.bkp | \
	sed 's#ZOO_LOG4J_PROP=.*#ZOO_LOG4J_PROP="INFO,ROLLINGFILE"#g' > $INSTALL_DIR/bin/zkEnv.sh
	chmod +x $INSTALL_DIR/bin/zkEnv.sh

	echo "$INSTALL_DIR/bin/zkEnv.sh 配置完成."
}


# ---------------------------------------------------------------------------
# 配置 zkServer.sh
# ---------------------------------------------------------------------------
function configure_zkServer_sh(){
	# sed -n "/myid=.*/p" /usr/local/zookeeper-3.5.5/bin/zkServer.sh
	if [ ! -e $INSTALL_DIR/bin/zkServer.sh.bkp ]; then
		mv $INSTALL_DIR/bin/zkServer.sh $INSTALL_DIR/bin/zkServer.sh.bkp
	fi
	sed 's#myid=`.*`#myid=`cat "'$DATA_DIR'/zookeeper/myid"`#g' $INSTALL_DIR/bin/zkServer.sh.bkp > $INSTALL_DIR/bin/zkServer.sh
	chmod +x $INSTALL_DIR/bin/zkServer.sh

	echo "$INSTALL_DIR/bin/zkServer.sh 配置完成."
}


# ---------------------------------------------------------------------------
# 主流程
# ---------------------------------------------------------------------------
if [ ! -e ${GZ_FILE} ]; then
	echo "安装包(${GZ_FILE})不存在!"
	return
fi


# 检查是否安装MySQL
if ! command -v zkServer.sh status; then

	# 解压缩
	decompress

	# 配置环境变量
	configue_env

	# 配置文件
	configue_zoo_cfg
	configure_log4j_properties
	configure_zkEnv_sh
	configure_zkServer_sh

	# 创建 zookeeper 的数据存储目录和日志存储目录
	if [ ! -e $BASE_DIR ]; then
		mkdir -p $BASE_DIR
	fi
	if [ ! -e $DATA_DIR ]; then
		mkdir -p $DATA_DIR
	fi
	if [ ! -e $LOGS_DIR ]; then
		mkdir -p $LOGS_DIR
	fi
	if [ ! -e $DATA_DIR/zookeeper ]; then
		mkdir -p $DATA_DIR/zookeeper
	fi
	if [ ! -e $LOGS_DIR/zookeeper/dataLog ]; then
		mkdir -p $LOGS_DIR/zookeeper/dataLog
	fi
	if [ ! -e $LOGS_DIR/zookeeper/logs ]; then
		mkdir -p $LOGS_DIR/zookeeper/logs
	fi
	# 在 hadoop101 号服务器的 data 目录中创建一个文件 myid，输入内容为 1
	# myid 应与 zoo.cfg 中的集群节点相匹配
	# cat /home/hadoop_files/hadoop_data/zookeeper/myid
	hostname=`cat /etc/hostname`
	myid=${hostname//cluster/}
	echo "主机名为:$hostname, myid=$myid"
	echo $myid >> $DATA_DIR/zookeeper/myid

	# 修改文件夹的权限
	chown -R $USER_GROUP:$USER_NAME $BASE_DIR
	chown -R $USER_GROUP:$USER_NAME $INSTALL_DIR

	echo "ZooKeeper安装完成!"
else
	echo "ZooKeeper已安装!"
fi


