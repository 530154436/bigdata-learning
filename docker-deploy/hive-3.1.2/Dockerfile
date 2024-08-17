FROM 15521147129/bigdata:hadoop-3.1.4

ENV HIVE_HOME=/usr/local/hive-3.1.2
USER root

# MySQL驱动程序
COPY mysql-5.6.37/mysql-connector-java-5.1.43-bin.jar /usr/local/CDP7.1.4/mysql-connector-java-5.1.43-bin.jar

# 安装Hive
COPY CDP7.1.4/apache-hive-3.1.2-bin.tar.gz /usr/local/CDP7.1.4/apache-hive-3.1.2-bin.tar.gz
COPY hive-3.1.2/install_hive.sh /dockerentry/install_hive.sh
RUN chmod a+x /dockerentry/install_hive.sh
RUN bash /dockerentry/install_hive.sh
RUN rm /usr/local/CDP7.1.4/apache-hive-3.1.2-bin.tar.gz

# 新建hive用户
RUN useradd -s /bin/bash -g hadoop -d /home/hive -m hive \
    && echo 'hive:hive' | chpasswd \
    && chown -R hive:hadoop $HIVE_HOME

# 启动脚本
COPY hive-3.1.2/install_hive.sh /dockerentry/run.sh
RUN chmod a+x /dockerentry/run.sh

# 设置工作目录为 hadoop 用户的home目录
USER hadoop
WORKDIR /home/hadoop
