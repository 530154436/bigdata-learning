#!/bin/bash
# 
# 官方文档
# https://hive.apache.org/
# https://cwiki.apache.org/confluence/display/Hive//GettingStarted
# 
# 安装
# source install_hbase.sh
# 
# 启动/关闭(cluster1)
# 
# 
# Webui:
# http://cluster1:/

GZ_FILE="/usr/local/CDP7.1.4/apache-hive-3.1.2-bin.编译.tar.gz"
MS_CONNECTOR="/usr/local/CDP7.1.4/mysql-connector-java-5.1.43-bin.jar"
INSTALL_DIR="/usr/local/hive-3.1.2"

USER_GROUP=hadoop
USER_NAME=hadoop
HADOOP_HOME=/usr/local/hadoop-3.1.4

BASE_DIR=/home/hadoop_files
DATA_DIR=$BASE_DIR/hadoop_data
LOGS_DIR=$BASE_DIR/hadoop_logs
TEMP_DIR=$BASE_DIR/hadoop_tmp

# 配置文件相关
mysql_host=mysql
mysql_connection_user_name=hive
mysql_connection_password=123456

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
	envstr=`sed -n "/HIVE_HOME=/p" /etc/profile`
	if [ ! -z "$envstr" ]; then 
		echo "Hive环境变量已经配置"
		echo "$envstr"
		return
	fi

	# 在/etc/profile中追加（<<）环境变量
	cat >> /etc/profile << EOF
# Hive 3.1.3
export HIVE_HOME=$INSTALL_DIR
export PATH=\$HIVE_HOME/bin:\$PATH

EOF

	# 使环境变量生效
	source /etc/profile
	echo "Hive环境变量配置完成."
	echo "HIVE_HOME=$HIVE_HOME"
}


# ---------------------------------------------------------------------------
# 配置 conf/hive-site.xml
# ---------------------------------------------------------------------------
function configure_hive_site_xml(){

	# 替换非法字符: &#
	sed -i "s/&#//g" $INSTALL_DIR/conf/hive-default.xml.template

	# 修改 xml 的属性值
	xmlstarlet ed \
		-u 'configuration/property[name = "javax.jdo.option.ConnectionURL"]/value' -v "jdbc:mysql://$mysql_host:3306/hive" \
		-u 'configuration/property[name = "javax.jdo.option.ConnectionDriverName"]/value' -v "com.mysql.jdbc.Driver" \
		-u 'configuration/property[name = "javax.jdo.option.ConnectionPassword"]/value' -v "$mysql_connection_password" \
		-u 'configuration/property[name = "javax.jdo.option.ConnectionUserName"]/value' -v "$mysql_connection_user_name" \
		-u 'configuration/property[name = "hive.exec.local.scratchdir"]/value' -v "$TEMP_DIR/hive/iotmp" \
		-u 'configuration/property[name = "hive.downloaded.resources.dir"]/value' -v "$TEMP_DIR/hive/iotmp" \
		-u 'configuration/property[name = "hive.querylog.location"]/value' -v "$LOGS_DIR/hive/querylog" \
		-u 'configuration/property[name = "hive.metastore.schema.verification"]/value' -v "false" \
		$INSTALL_DIR/conf/hive-default.xml.template > $INSTALL_DIR/conf/hive-site.xml

	echo "$INSTALL_DIR/conf/hive-site.xml 配置完成."
}


# ---------------------------------------------------------------------------
# 配置 conf/hive-env.sh
# ---------------------------------------------------------------------------
function configure_hive_env(){
	if [ ! -e $INSTALL_DIR/conf/hive-env.sh ]; then
		mv $INSTALL_DIR/conf/hive-env.sh.template $INSTALL_DIR/conf/hive-env.sh
	fi

# 覆盖文件
cat > $INSTALL_DIR/conf/hive-env.sh << EOF
HADOOP_HOME=$HADOOP_HOME
export HIVE_HOME=$INSTALL_DIR

# Hive Configuration Directory can be controlled by:
export HIVE_CONF_DIR=$INSTALL_DIR/conf

EOF

	echo "$INSTALL_DIR/conf/hive-env.sh 配置完成."
}


# ---------------------------------------------------------------------------
# 配置 conf/log4j.properties 
# 解决大量打印日志的问题
# ---------------------------------------------------------------------------
function configure_log4j_properties(){

# 覆盖文件
cat > $INSTALL_DIR/conf/log4j.properties << EOF
log4j.rootLogger=WARN, CA
log4j.appender.CA=org.apache.log4j.ConsoleAppender
log4j.appender.CA.layout=org.apache.log4j.PatternLayout
log4j.appender.CA.layout.ConversionPattern=%-4r [%t] %-5p %c %x - %m%n

EOF

	echo "$INSTALL_DIR/conf/log4j.properties 配置完成."
}

# ---------------------------------------------------------------------------
# 主流程
# ---------------------------------------------------------------------------
if [ ! -e ${GZ_FILE} ]; then
	echo "安装包(${GZ_FILE})不存在!"
	return
fi


# 检查是否安装MySQL
if ! command -v hive --version ; then

	# 解压缩
	decompress

	# 配置环境变量
	configure_env

	# 配置文件
	configure_hive_site_xml
	configure_hive_env
	configure_log4j_properties

	# 设置依赖包
	cp $MS_CONNECTOR $INSTALL_DIR/lib/

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
	if [ ! -e $TEMP_DIR/hive ]; then
    	mkdir -p $TEMP_DIR/hive
  	fi
  	if [ ! -e $TEMP_DIR/hive/iotmp ]; then
    	mkdir -p $TEMP_DIR/hive/iotmp
  	fi
  	if [ ! -e $LOGS_DIR/hive ]; then
    	mkdir -p $LOGS_DIR/hive
  	fi
  	if [ ! -e $LOGS_DIR/hive/querylog ]; then
    	mkdir -p $LOGS_DIR/hive/querylog
  	fi

	# 修改文件夹的权限
	chown -R $USER_GROUP:$USER_NAME $BASE_DIR
	chown -R $USER_GROUP:$USER_NAME $INSTALL_DIR

	# 解决日志库冲突
	sudo rm /usr/local/apache-hive-3.1.2-bin/lib/log4j-slf4j-impl-2.10.0.jar

	echo "Hive安装完成!"
else
	echo "Hive已安装!"
fi

hive --version


