FROM 15521147129/bigdata:debian-base

# passwordless with sudo for hadoop user
RUN echo "hadoop ALL=NOPASSWD: ALL" > "/etc/sudoers.d/hadoop-docker"

WORKDIR /home/hadoop
ENV JAVA_HOME=/usr/local/jdk1.8.0_112
ENV SCALA_HOME=/usr/local/scala-2.11.12
ENV ZOOKEEPER_HOME=/usr/local/zookeeper-3.5.5
ENV HADOOP_HOME=/usr/local/hadoop-3.1.1

# 新建 hadoop组 和 hadoop用户
RUN groupadd hadoop \
    && useradd -s /bin/bash -g hadoop -d /home/hadoop -m hadoop \
    && echo 'hadoop:hadoop' | chpasswd

# 安装Zookeeper
COPY CDP7.1.4/apache-zookeeper-3.5.5-bin.tar.gz /usr/local/CDP7.1.4/apache-zookeeper-3.5.5-bin.tar.gz
COPY CDP7.1.4/install_zookeeper.sh /dockerentry/install_zookeeper.sh
RUN chmod a+x /dockerentry/install_zookeeper.sh
RUN bash /dockerentry/install_zookeeper.sh
RUN rm /usr/local/CDP7.1.4/apache-zookeeper-3.5.5-bin.tar.gz

# 安装Hadoop
COPY CDP7.1.4/hadoop-3.1.1.tar.gz /usr/local/CDP7.1.4/hadoop-3.1.1.tar.gz
COPY CDP7.1.4/install_hadoop.sh /dockerentry/install_hadoop.sh
RUN chmod a+x /dockerentry/install_hadoop.sh
RUN bash /dockerentry/install_hadoop.sh
RUN rm /usr/local/CDP7.1.4/hadoop-3.1.1.tar.gz

# start script
COPY hadoop-ha/run-namenode.sh /dockerentry/run-namenode.sh
RUN chown hadoop /dockerentry/run-namenode.sh
COPY hadoop-ha/run-datanode.sh /dockerentry/run-datanode.sh
RUN chown hadoop /dockerentry/run-datanode.sh

# 创建 .ssh 目录并生成 SSH 密钥（以 root 用户运行）
RUN mkdir -p /home/hadoop/.ssh \
    && ssh-keygen -q -t rsa -N '' -f /home/hadoop/.ssh/id_rsa \
    && cat /home/hadoop/.ssh/id_rsa.pub >> /home/hadoop/.ssh/authorized_keys \
    && chown -R hadoop:hadoop /home/hadoop/.ssh \
    && chmod 700 /home/hadoop/.ssh \
    && chmod 600 /home/hadoop/.ssh/authorized_keys

# 切换到 hadoop 用户
USER hadoop
