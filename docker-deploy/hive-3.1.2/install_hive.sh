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

GZ_FILE="/usr/local/CDP7.1.4/apache-hive-3.1.2-bin.tar.gz"
INSTALL_DIR="/usr/local/hive-3.1.2"

USER_NAME=hive
USER_GROUP=hadoop
HADOOP_HOME=/usr/local/hadoop-3.1.4

# MySQL相关
MS_CONNECTOR="/usr/local/CDP7.1.4/mysql-connector-java-5.1.43-bin.jar"
MS_HOST=mysql
MS_CONNECTION_USER_NAME=hive
MS_CONNECTION_PASSWORD=123456

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

# 覆盖文件 conf/hive-site.xml
cat > $INSTALL_DIR/conf/hive-site.xml << EOF
<configuration>
    <!-- 存储元数据mysql相关配置 -->
    <property>
        <name>javax.jdo.option.ConnectionURL</name>
        <value> jdbc:mysql://$MS_HOST:3306/hive?createDatabaseIfNotExist=true&amp;useSSL=false&amp;useUnicode=true&amp;characterEncoding=UTF-8</value>
    </property>

    <property>
        <name>javax.jdo.option.ConnectionDriverName</name>
        <value>com.mysql.jdbc.Driver</value>
    </property>

    <property>
        <name>javax.jdo.option.ConnectionUserName</name>
        <value>$MS_CONNECTION_USER_NAME</value>
    </property>

    <property>
        <name>javax.jdo.option.ConnectionPassword</name>
        <value>$MS_CONNECTION_PASSWORD</value>
    </property>

    <!-- H2S运行绑定host/port -->
    <property>
        <name>hive.server2.thrift.bind.host</name>
        <value>hive</value>
    </property>
    <property>
        <name>hive.server2.thrift.port</name>
        <value>10000</value>
    </property>
    <property>
        <name>hive.server2.enable.doAs</name>
        <value>true</value>
    </property>

    <!-- H2S web ui-->
    <property>
        <name>hive.server2.webui.host</name>
        <value>hive</value>
    </property>
    <property>
        <name>hive.server2.webui.port</name>
        <value>10002</value>
    </property>

    <!-- 远程模式部署 metastore 服务地址 -->
    <property>
        <name>hive.metastore.uris</name>
        <value>thrift://hive:9083</value>
    </property>

    <!-- 关闭元数据存储授权  -->
    <property>
        <name>hive.metastore.event.db.notification.api.auth</name>
        <value>false</value>
    </property>

    <!-- 关闭元数据存储版本的验证 -->
    <property>
        <name>hive.metastore.schema.verification</name>
        <value>false</value>
    </property>
</configuration>
EOF

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

	# 修改文件夹的权限
	chown -R $USER_NAME:$USER_GROUP $INSTALL_DIR

	# 解决日志库冲突
	sudo rm $INSTALL_DIR/lib/log4j-slf4j-impl-2.10.0.jar

	echo "Hive安装完成!"
else
	echo "Hive已安装!"
fi

hive --version


