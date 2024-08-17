FROM 15521147129/bigdata:hadoop-3.1.4

ENV HIVE_HOME=/usr/local/hive-3.1.2

# 安装Hive
COPY CDP7.1.4/apache-hive-3.1.2-bin.tar.gz /usr/local/CDP7.1.4/apache-hive-3.1.2-bin.tar.gz
COPY hive-3.1.2/install_hive.sh /dockerentry/install_hive.sh
RUN chmod a+x /dockerentry/install_hive.sh
RUN bash /dockerentry/install_hive.sh
RUN rm /usr/local/CDP7.1.4/apache-hive-3.1.2-bin.tar.gz

# 设置工作目录为 hadoop 用户的home目录
WORKDIR /home/hadoop
