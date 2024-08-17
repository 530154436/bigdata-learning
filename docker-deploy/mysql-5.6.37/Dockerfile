FROM 15521147129/bigdata:debian-base
ENV MYSQL_HOME=/usr/local/mysql-5.6.37

RUN echo 'Asia/Shanghai' >/etc/timezone
RUN echo 'root:root' | chpasswd
#COPY base/sources.list /etc/apt/sources.list

# 新建 mysql组 和 mysql用户
RUN groupadd mysql \
    && useradd -s /bin/bash -g mysql -d /home/mysql -m mysql \
    && echo 'mysql:mysql' | chpasswd
# passwordless with sudo for mysql user
RUN echo "mysql ALL=(ALL)  NOPASSWD:ALL" > /etc/sudoers

# 安装MySQL
COPY CDP7.1.4/mysql-5.6.37-linux-glibc2.12-x86_64.tar.gz /usr/local/CDP7.1.4/mysql-5.6.37-linux-glibc2.12-x86_64.tar.gz
COPY mysql-5.6.37/install_mysql.sh /dockerentry/install_mysql.sh
RUN chmod a+x /dockerentry/install_mysql.sh
RUN bash /dockerentry/install_mysql.sh
RUN rm /usr/local/CDP7.1.4/mysql-5.6.37-linux-glibc2.12-x86_64.tar.gz

# 添加MySQL-hive配置脚本
COPY mysql-5.6.37/setup-mysql-for-hive.sh /dockerentry/setup-mysql-for-hive.sh
RUN chmod a+x /dockerentry/setup-mysql-for-hive.sh
#RUN bash /dockerentry/setup-mysql-for-hive.sh

# 设置工作目录为 hadoop 用户的家目录
WORKDIR /home/mysql
