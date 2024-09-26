FROM 15521147129/bigdata:hadoop-3.1.4

ENV FLINK_HOME=/usr/local/flink-1.13.2
USER root

# 安装
COPY CDP7.1.4/flink-1.13.2-bin-scala_2.12.tgz /usr/local/CDP7.1.4/flink-1.13.2-bin-scala_2.12.tgz

COPY flink-1.13.2/install_flink.sh /dockerentry/install_flink.sh
RUN chmod a+x /dockerentry/install_flink.sh
RUN bash /dockerentry/install_flink.sh
RUN rm /usr/local/CDP7.1.4/flink-1.13.2-bin-scala_2.12.tgz

# 启动脚本
COPY flink-1.13.2/run-master.sh /dockerentry/run-master.sh
RUN chmod a+x /dockerentry/run-master.sh

COPY flink-1.13.2/run-workers.sh /dockerentry/run-workers.sh
RUN chmod a+x /dockerentry/run-workers.sh

COPY flink-1.13.2/run-getent-hosts.sh /dockerentry/run-getent-hosts.sh
RUN chmod a+x /dockerentry/run-getent-hosts.sh

# 设置工作目录为 hadoop 用户的home目录
USER hadoop
WORKDIR /home/hadoop
