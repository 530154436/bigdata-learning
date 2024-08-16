FROM 15521147129/bigdata:debian-base
ENV HADOOP_HOME=/usr/local/mysql-5.6.37

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

# 设置工作目录为 hadoop 用户的家目录
WORKDIR /home/mysql
