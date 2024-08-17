#!/bin/bash
# source install_hadoop.sh
# 
# 官方文档
# https://hadoop.apache.org/docs/r3.1.1/index.html
# https://hadoop.apache.org/docs/r3.1.1/hadoop-project-dist/hadoop-common/ClusterSetup.html
# https://hadoop.org.cn/docs/hadoop-project-dist/hadoop-common/ClusterSetup.html
# 
# 默认配置
# https://hadoop.apache.org/docs/r3.1.1/hadoop-project-dist/hadoop-common/core-default.xml
# https://hadoop.apache.org/docs/r3.1.1/hadoop-project-dist/hadoop-hdfs/hdfs-default.xml
# https://hadoop.apache.org/docs/r3.1.1/hadoop-mapreduce-client/hadoop-mapreduce-client-core/mapred-default.xml
# https://hadoop.apache.org/docs/r3.1.1/hadoop-yarn/hadoop-yarn-common/yarn-default.xml
# 
# 安装
# source install_hadoop.sh
# hdfs version
# 
# Webui:
# Yarn http://hadoop101:8088/
# Hdfs http://hadoop101:9870/
################################### 初始化（hadoop3）###################################
# 1. 格式化 HDFS(在 hadoop101 上执行)
# hdfs namenode -format
# 
# 2. 开启 NaneNode 和 DataNode 进程
# hdfs --daemon start|stop namenode
# hdfs --daemon start|stop datanode
# 
# 启动多个节点：start-dfs.sh
# 
# 3. 开启 YARN
# yarn --daemon start|stop resourcemanager
# yarn --daemon start|stop nodemanager
# yarn --daemon start|stop proxyserver
# 
# 启动多个管理器：start-yarn.sh
# 
################################### 初始化（hadoop3）###################################
################################### 不同点（hadoop2）###################################
# hdfs.site.xml
  # 默认端口变了
  # <property>
  #   <name>dfs.namenode.http.address</name>
  #   <name>dfs.namenode.http-address</name>
  #   <value>hadoop101:9870</value> 
  # </property>
  # <property>
  #   <name>dfs.secondary.http.address</name>
  #   <name>dfs.namenode.secondary.http-address</name>
  #   <value>hadoop101:50090</value> 
  # </property>
  # <property> 
  #   <name>dfs.webhdfs.enabled</name> 
  #   <name>dfs.webhdfs.rest-csrf.enabled</name>
  #   <value>true</value>
  # </property>

 # slaves
  # slaves => workers
################################### 不同点（hadoop2）###################################


GZ_FILE="/usr/local/CDP7.1.4/hadoop-3.1.4.tar.gz"
INSTALL_DIR="/usr/local/hadoop-3.1.4"

USER_GROUP=hadoop
USER_NAME=hadoop

BASE_DIR=/home/hadoop_files
DATA_DIR=$BASE_DIR/hadoop_data
LOGS_DIR=$BASE_DIR/hadoop_logs
TEMP_DIR=$BASE_DIR/hadoop_tmp

JAVA_HOME=/usr/local/jdk1.8.0_112

# 配置文件相关
yarn_web=hadoop102:8888


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
	envstr=`sed -n "/HADOOP_HOME=/p" /etc/profile`
	if [ ! -z "$envstr" ]; then 
		echo "Hadoop环境变量已经配置"
		echo "$envstr"
		return
	fi

	# 在/etc/profile中追加（<<）环境变量
	cat >> /etc/profile << EOF
# Hadoop 3.1.1
export HADOOP_HOME=$INSTALL_DIR
export LD_LIBRARY_PATH=\$HADOOP_HOME/lib/native
export PATH=\$HADOOP_HOME/bin:\$HADOOP_HOME/sbin:\$PATH

EOF

	# 使环境变量生效
	source /etc/profile
	echo "Hadoop环境变量配置完成."
	echo "HADOOP_HOME=$HADOOP_HOME"
}


# ---------------------------------------------------------------------------
# 配置 etc/hadoop/hadoop-env.sh 
# ---------------------------------------------------------------------------
function configure_hadoop_env(){
	if [ ! -e ${INSTALL_DIR}/etc/hadoop/hadoop-env.sh.bkp ]; then
		mv $INSTALL_DIR/etc/hadoop/hadoop-env.sh $INSTALL_DIR/etc/hadoop/hadoop-env.sh.bkp
	fi
	# sed 's|.*export HADOOP_PID_DIR=.*|export HADOOP_PID_DIR='$BASE_DIR'|g' $INSTALL_DIR/etc/hadoop/hadoop-env.sh.bkp | \
	# sed 's|.*export JAVA_HOME=.*|export JAVA_HOME='$JAVA_HOME'|g' > $INSTALL_DIR/etc/hadoop/hadoop-env.sh

# 覆盖文件 etc/hadoop/hadoop-env.sh
cat > $INSTALL_DIR/etc/hadoop/hadoop-env.sh << EOF
export HADOOP_OS_TYPE=\${HADOOP_OS_TYPE:-\$(uname -s)}

case \${HADOOP_OS_TYPE} in
  Darwin*)
    export HADOOP_OPTS="\${HADOOP_OPTS} -Djava.security.krb5.realm= "
    export HADOOP_OPTS="\${HADOOP_OPTS} -Djava.security.krb5.kdc= "
    export HADOOP_OPTS="\${HADOOP_OPTS} -Djava.security.krb5.conf= "
  ;;
esac

export HADOOP_PID_DIR=$BASE_DIR
export JAVA_HOME=$JAVA_HOME

EOF

	echo "$INSTALL_DIR/etc/hadoop/hadoop-env.sh 配置完成."
}


# ---------------------------------------------------------------------------
# 配置 etc/hadoop/mapred-env.sh
# ---------------------------------------------------------------------------
function configure_mapred_env(){
	if [ ! -e $INSTALL_DIR/etc/hadoop/mapred-env.sh.bkp ]; then
		mv $INSTALL_DIR/etc/hadoop/mapred-env.sh $INSTALL_DIR/etc/hadoop/mapred-env.sh.bkp
	fi

# 覆盖文件 etc/hadoop/mapred-env.sh 
cat > $INSTALL_DIR/etc/hadoop/mapred-env.sh << EOF
export HADOOP_MAPRED_PID_DIR=$BASE_DIR

EOF

	echo "$INSTALL_DIR/etc/hadoop/mapred-env.sh 配置完成."
}


# ---------------------------------------------------------------------------
# 配置 core-site.xml
# ---------------------------------------------------------------------------
function configure_core_site(){
	if [ ! -e ${INSTALL_DIR}/etc/hadoop/core-site.xml.bkp ]; then
		mv $INSTALL_DIR/etc/hadoop/core-site.xml $INSTALL_DIR/etc/hadoop/core-site.xml.bkp
	fi

# 覆盖文件 etc/hadoop/core-site.xml
# cat > test.txt << EOF
cat > $INSTALL_DIR/etc/hadoop/core-site.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>

<configuration>

  <!-- 设置主节点 --> 
  <property>
    <name>fs.defaultFS</name>
    <value>hdfs://hadoop101:9000</value> 
  </property>

  <!-- 指定缓存文件存储的路径 --> 
  <property>
    <name>hadoop.tmp.dir</name>
    <value>$BASE_DIR/hadoop_tmp/hadoop/data/tmp</value> 
  </property>

  <!-- 配置 hdfs 文件被永久删除前保留的时间(单位:分钟)，默认值为 0 表明垃圾回收站功能关闭 --> 
  <property>
    <name>fs.trash.interval</name>
    <value>1440</value> 
  </property>

  <!-- 指定 zookeeper 地址，配置 HA 时需要 -->

</configuration>


EOF

	echo "$INSTALL_DIR/etc/hadoop/core-site.xml 配置完成."
}



# ---------------------------------------------------------------------------
# 配置 hdfs-site.xml 
# ---------------------------------------------------------------------------
function configure_hdfs_site(){
	if [ ! -e ${INSTALL_DIR}/etc/hadoop/hdfs-site.xml.bkp ]; then
		mv $INSTALL_DIR/etc/hadoop/hdfs-site.xml $INSTALL_DIR/etc/hadoop/hdfs-site.xml.bkp
	fi

# 覆盖文件 etc/hadoop/hdfs-site.xml
# cat > test.txt << EOF
cat > $INSTALL_DIR/etc/hadoop/hdfs-site.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>

<configuration>

  <!-- 指定 hdfs 元数据存储的路径 -->
  <property>
    <name>dfs.namenode.name.dir</name>
    <value>$DATA_DIR/hadoop/namenode</value> 
  </property>

  <!-- 指定 hdfs 数据存储的路径 -->
  <property>
    <name>dfs.datanode.data.dir</name>
    <value>$DATA_DIR/hadoop/datanode</value> 
  </property>

  <!-- name node web ui address -->
  <property>
    <name>dfs.namenode.http-address</name>
    <value>hadoop101:9870</value>
  </property>

  <!-- 2nn web ui address -->
  <property>
    <name>dfs.namenode.secondary.http-address</name>
    <value>hadoop103:9868</value>
  </property>

  <!-- 数据备份的个数 --> 
  <property>
    <name>dfs.replication</name>
    <value>3</value> 
  </property>

  <!-- 关闭权限验证 -->
  <property>
    <name>dfs.permissions.enabled</name>
    <value>false</value> 
  </property>

  <!-- 开启 WebHDFS 功能(基于 REST 的接口服务) -->
  <property>
    <name>dfs.webhdfs.rest-csrf.enabled</name>
    <value>true</value> 
  </property>

  <!-- 数据节点配置 -->
  <property>
    <name>dfs.datanode.address</name>
    <value>0000:9866</value>
  </property>
  <property>
    <name>dfs.datanode.http.address</name>
    <value>0000:9864</value>
  </property>
  <property>
    <name>dfs.datanode.ipc.address</name>
    <value>0000:9867</value>
  </property>
  <property>
    <name>dfs.client.use.datanode.hostname</name>
    <value>true</value>
  </property>
</configuration>


EOF

	echo "$INSTALL_DIR/etc/hadoop/hdfs-site.xml 配置完成."
}



# ---------------------------------------------------------------------------
# 配置 mapred-site.xml 
# ---------------------------------------------------------------------------
function configure_mapred_site(){
	if [ ! -e ${INSTALL_DIR}/etc/hadoop/mapred-site.xml.bkp ]; then
		mv $INSTALL_DIR/etc/hadoop/mapred-site.xml $INSTALL_DIR/etc/hadoop/mapred-site.xml.bkp
	fi

# 覆盖文件 etc/hadoop/mapred-site.xml
cat > $INSTALL_DIR/etc/hadoop/mapred-site.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>

<configuration>

  <!-- 指定 MapReduce 计算框架使用 YARN -->
  <property>
    <name>mapreduce.framework.name</name>
    <value>yarn</value> 
  </property>

  <!-- 指定 jobhistory server 的 rpc 地址 -->
  <property>
    <name>mapreduce.jobhistory.address</name>
    <value>hadoop101:10020</value> 
  </property>

  <!-- 指定 jobhistory server 的 http 地址 -->
  <property>
    <name>mapreduce.jobhistory.webapp.address</name>
    <value>hadoop101:19888</value> 
  </property>

  <property>
    <name>yarn.app.mapreduce.am.env</name>
    <value>HADOOP_MAPRED_HOME=$INSTALL_DIR</value>
  </property>

  <property>
    <name>mapreduce.map.env</name>
    <value>HADOOP_MAPRED_HOME=$INSTALL_DIR</value>
  </property>

  <property>
    <name>mapreduce.reduce.env</name>
    <value>HADOOP_MAPRED_HOME=$INSTALL_DIR</value>
  </property>

</configuration>

EOF

	echo "$INSTALL_DIR/etc/hadoop/mapred-site.xml 配置完成."
}


# ---------------------------------------------------------------------------
# 配置 yarn-site.xml
# ---------------------------------------------------------------------------
function configure_yarn_site(){
	if [ ! -e ${INSTALL_DIR}/etc/hadoop/yarn-site.xml.bkp ]; then
		mv $INSTALL_DIR/etc/hadoop/yarn-site.xml $INSTALL_DIR/etc/hadoop/yarn-site.xml.bkp
	fi

# 覆盖文件 etc/hadoop/yarn-site.xml
cat > $INSTALL_DIR/etc/hadoop/yarn-site.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>

<configuration>
  
  <!-- NodeManager 上运行的附属服务，需配置成 mapreduce_shuffle 才可运行 MapReduce 程序 -->
  <property>
    <name>yarn.nodemanager.aux-services</name>
    <value>mapreduce_shuffle</value> 
  </property>

  <!-- 配置 Web Application Proxy 安全代理(防止 yarn 被攻击) -->
  <property>
    <name>yarn.web-proxy.address</name>
    <value>$yarn_web</value> 
  </property>

  <!-- 开启日志 -->
  <property>
    <name>yarn.log-aggregation-enable</name>
    <value>true</value> 
  </property>

  <!-- 配置日志删除时间为 7 天，-1 为禁用，单位为秒 -->
  <property>
    <name>yarn.log-aggregation.retain-seconds</name>
    <value>604800</value> 
  </property>

  <!-- 修改日志目录 -->
  <property>
    <name>yarn.nodemanager.remote-app-log-dir</name>
    <value>$LOGS_DIR/yarn</value> 
  </property>

  <property>
    <name>yarn.resourcemanager.address</name>
    <value>hadoop101:8032</value> 
  </property>

  <property>
    <name>yarn.resourcemanager.scheduler.address</name>
    <value>hadoop101:8030</value> 
  </property>

  <property>
    <name>yarn.resourcemanager.resource-tracker.address</name>
    <value>hadoop101:8031</value> 
  </property>

  <!-- 配置 nodemanager 可用的资源内存 -->
  <property>
    <name>yarn.nodemanager.resource.memory-mb</name>
    <value>20480</value>
  </property>

  <!-- 配置 nodemanager 可用的资源 CPU -->
  <property>
    <name>yarn.nodemanager.resource.cpu-vcores</name>
    <value>24</value>
  </property>

</configuration>

EOF

	echo "$INSTALL_DIR/etc/hadoop/yarn-site.xml 配置完成."
}


# ---------------------------------------------------------------------------
# 配置 workers (slaves)
# ---------------------------------------------------------------------------
function configure_workers(){
	if [ ! -e ${INSTALL_DIR}/etc/hadoop/workers.bkp ]; then
		mv $INSTALL_DIR/etc/hadoop/workers $INSTALL_DIR/etc/hadoop/workers.bkp
	fi

cat > $INSTALL_DIR/etc/hadoop/workers << EOF
hadoop101
hadoop102
hadoop103

EOF

	echo "$INSTALL_DIR/etc/hadoop/workers 配置完成."
}


# ---------------------------------------------------------------------------
# 主流程
# ---------------------------------------------------------------------------
if [ ! -e ${GZ_FILE} ]; then
	echo "安装包(${GZ_FILE})不存在!"
	return
fi


# 检查是否安装MySQL
if ! command -v hdfs; then

	# 解压缩
	decompress

	# 配置环境变量
	configure_env
	configure_hadoop_env
	configure_mapred_env

	# 配置文件
	configure_core_site
	configure_hdfs_site
	configure_mapred_site
	configure_yarn_site
	configure_workers

	# 创建 Hadoop 的数据存储目录和日志存储目录
	if [ ! -e $BASE_DIR ]; then
		mkdir -p $BASE_DIR
	fi
	if [ ! -e $DATA_DIR ]; then
		mkdir -p $DATA_DIR
	fi
	if [ ! -e $LOGS_DIR ]; then
		mkdir -p $LOGS_DIR
	fi
  if [ ! -e $TEMP_DIR ]; then
    mkdir -p $TEMP_DIR
  fi
	if [ ! -e $DATA_DIR/hadoop ]; then
		mkdir -p $DATA_DIR/hadoop
	fi
	if [ ! -e $DATA_DIR/hadoop/namenode ]; then
		mkdir -p $DATA_DIR/hadoop/namenode
	fi
	if [ ! -e $DATA_DIR/hadoop/datanode ]; then
		mkdir -p $DATA_DIR/hadoop/datanode
	fi
	if [ ! -e $DATA_DIR/hadoop/data ]; then
		mkdir -p $DATA_DIR/hadoop/data
	fi
	if [ ! -e $DATA_DIR/hadoop/data/tmp ]; then
		mkdir -p $DATA_DIR/hadoop/data/tmp
	fi
	if [ ! -e $LOGS_DIR/yarn ]; then
		mkdir -p $LOGS_DIR/yarn
	fi

	# 修改文件夹的权限
	chown -R $USER_GROUP:$USER_NAME $BASE_DIR
	chown -R $USER_GROUP:$USER_NAME $INSTALL_DIR

	echo "Hadoop安装完成!"
else
	echo "Hadoop已安装!"
fi


