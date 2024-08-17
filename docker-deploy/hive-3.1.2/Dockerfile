FROM 15521147129/bigdata:debian-base

ENV JAVA_HOME=/usr/local/jdk1.8.0_112
ENV HADOOP_HOME=/usr/local/hive-3.1.2

RUN echo 'root:root' | chpasswd

# 新建 hive组 和 hive用户
RUN groupadd hive \
    && useradd -s /bin/bash -g hive -d /home/hive -m hive \
    && echo 'hive:hive' | chpasswd
# passwordless with sudo for hive user
RUN echo "hive ALL=(ALL)  NOPASSWD:ALL" > /etc/sudoers

# 安装Java
COPY CDP7.1.4/jdk-8u112-linux-x64.tar.gz /usr/local/CDP7.1.4/jdk-8u112-linux-x64.tar.gz
COPY base/install_jdk.sh /dockerentry/install_jdk.sh
RUN chmod a+x /dockerentry/install_jdk.sh
RUN bash /dockerentry/install_jdk.sh
RUN rm /usr/local/CDP7.1.4/jdk-8u112-linux-x64.tar.gz

# 安装Hive
COPY CDP7.1.4/apache-hive-3.1.2-bin.编译.tar.gz /usr/local/CDP7.1.4/apache-hive-3.1.2-bin.编译.tar.gz
COPY hive-3.1.2/install_hive.sh /dockerentry/install_hive.sh
RUN chmod a+x /dockerentry/install_hive.sh
RUN bash /dockerentry/install_hive.sh
RUN rm /usr/local/CDP7.1.4/apache-hive-3.1.2-bin.编译.tar.gz

# 切换到 root 用户以创建 .ssh 目录并设置权限
USER root
RUN mkdir -p /home/hive/.ssh \
    && chown -R hive:hive /home/hive

# 切换到 hive 用户以生成 SSH 密钥
USER hive
RUN ssh-keygen -q -t rsa -N '' -f /home/hive/.ssh/id_rsa \
    && cat /home/hive/.ssh/id_rsa.pub >> /home/hive/.ssh/authorized_keys

# 设置工作目录为 hive 用户的家目录
WORKDIR /home/hive
