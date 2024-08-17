FROM 15521147129/bigdata:debian-base

RUN echo 'root:root' | chpasswd

ENV JAVA_HOME=/usr/local/jdk1.8.0_112
ENV HADOOP_HOME=/usr/local/hadoop-3.1.4

# 新建 hadoop组 和 hadoop用户
RUN groupadd hadoop \
    && useradd -s /bin/bash -g hadoop -d /home/hadoop -m hadoop \
    && echo 'hadoop:hadoop' | chpasswd

# 添加 #includedir /etc/sudoers.d 确保所有在该目录中的配置文件都被自动加载。
# passwordless with sudo for hadoop user
RUN sed -i '1i #includedir /etc/sudoers.d' /etc/sudoers
RUN echo "hadoop ALL=NOPASSWD: ALL" > "/etc/sudoers.d/hadoop-docker"

# 安装Java
COPY CDP7.1.4/jdk-8u112-linux-x64.tar.gz /usr/local/CDP7.1.4/jdk-8u112-linux-x64.tar.gz
COPY base/install_jdk.sh /dockerentry/install_jdk.sh
RUN chmod a+x /dockerentry/install_jdk.sh
RUN bash /dockerentry/install_jdk.sh
RUN rm /usr/local/CDP7.1.4/jdk-8u112-linux-x64.tar.gz

# 安装Hadoop
COPY CDP7.1.4/hadoop-3.1.4.tar.gz /usr/local/CDP7.1.4/hadoop-3.1.4.tar.gz
COPY hadoop-3.1.4/install_hadoop.sh /dockerentry/install_hadoop.sh
RUN chmod a+x /dockerentry/install_hadoop.sh
RUN bash /dockerentry/install_hadoop.sh
RUN rm /usr/local/CDP7.1.4/hadoop-3.1.4.tar.gz

# start script
COPY hadoop-3.1.4/run-namenode.sh /dockerentry/run-namenode.sh
RUN chmod a+x /dockerentry/run-namenode.sh
RUN chown hadoop:hadoop /dockerentry/run-namenode.sh
COPY hadoop-3.1.4/run-datanode.sh /dockerentry/run-datanode.sh
RUN chmod a+x /dockerentry/run-datanode.sh
RUN chown hadoop:hadoop /dockerentry/run-datanode.sh

# 切换到 root 用户以创建 .ssh 目录并设置权限
USER root
RUN mkdir -p /home/hadoop/.ssh \
    && chown -R hadoop:hadoop /home/hadoop

# 切换到 hadoop 用户以生成 SSH 密钥
USER hadoop
RUN ssh-keygen -q -t rsa -N '' -f /home/hadoop/.ssh/id_rsa \
    && cat /home/hadoop/.ssh/id_rsa.pub >> /home/hadoop/.ssh/authorized_keys

# 设置工作目录为 hadoop 用户的家目录
WORKDIR /home/hadoop
