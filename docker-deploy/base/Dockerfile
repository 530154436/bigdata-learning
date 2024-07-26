FROM debian:11

RUN mv /etc/apt/sources.list /etc/apt/sources.list.bkp
COPY base/sources.list /etc/apt/sources.list

RUN apt-get -q update && apt-get -q install -y --no-install-recommends \
    openssh-client openssh-server rsync grsync curl gnupg sudo vim netcat python3 \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# 新建 hadoop组 和 hadoop用户
RUN groupadd hadoop
RUN useradd -s /bin/bash -g hadoop -d /home/hadoop -m hadoop
RUN echo 'hadoop:hadoop' | chpasswd
RUN echo 'root:root' | chpasswd

# environment variables for all users
ENV JAVA_HOME=/usr/local/jdk1.8.0_112
ENV SCALA_HOME=/usr/local/scala-2.11.12

# 安装Java
COPY CDP7.1.4/jdk-8u112-linux-x64.tar.gz /usr/local/CDP7.1.4/jdk-8u112-linux-x64.tar.gz
COPY CDP7.1.4/install_jdk.sh /dockerentry/install_jdk.sh
RUN chmod a+x /dockerentry/install_jdk.sh
RUN bash /dockerentry/install_jdk.sh
RUN rm /usr/local/CDP7.1.4/jdk-8u112-linux-x64.tar.gz

# 安装Scala
COPY CDP7.1.4/scala-2.11.12.tgz /usr/local/CDP7.1.4/scala-2.11.12.tgz
COPY CDP7.1.4/install_scala.sh /dockerentry/install_scala.sh
RUN chmod a+x /dockerentry/install_scala.sh
RUN bash /dockerentry/install_scala.sh
RUN rm /usr/local/CDP7.1.4/scala-2.11.12.tgz

# 提供一个默认的命令
CMD ["/bin/bash"]