FROM debian:11

#RUN mv /etc/apt/sources.list /etc/apt/sources.list.bkp
#COPY base/sources.list /etc/apt/sources.list

WORKDIR /root

RUN apt-get -q update && apt-get -q install -y --no-install-recommends \
    sshpass iputils-ping openssh-client openssh-server rsync grsync curl gnupg sudo vim netcat python3 \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# passwordless with ssh
RUN ssh-keygen -q -t rsa -N '' -f ~/.ssh/id_rsa \
    && cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys

# environment variables for all users
ENV JAVA_HOME=/usr/local/jdk1.8.0_112
ENV SCALA_HOME=/usr/local/scala-2.11.12

# 安装Java
COPY CDP7.1.4/jdk-8u112-linux-x64.tar.gz /usr/local/CDP7.1.4/jdk-8u112-linux-x64.tar.gz
COPY base/install_jdk.sh /dockerentry/install_jdk.sh
RUN chmod a+x /dockerentry/install_jdk.sh
RUN bash /dockerentry/install_jdk.sh
RUN rm /usr/local/CDP7.1.4/jdk-8u112-linux-x64.tar.gz

# 安装Scala
COPY CDP7.1.4/scala-2.11.12.tgz /usr/local/CDP7.1.4/scala-2.11.12.tgz
COPY base/install_scala.sh /dockerentry/install_scala.sh
RUN chmod a+x /dockerentry/install_scala.sh
RUN bash /dockerentry/install_scala.sh
RUN rm /usr/local/CDP7.1.4/scala-2.11.12.tgz

COPY base/entrypoint.sh /dockerentry/entrypoint.sh
RUN chmod a+x /dockerentry/entrypoint.sh

ENTRYPOINT ["/dockerentry/entrypoint.sh"]