export HADOOP_OS_TYPE=${HADOOP_OS_TYPE:-$(uname -s)}

case ${HADOOP_OS_TYPE} in
  Darwin*)
    export HADOOP_OPTS="${HADOOP_OPTS} -Djava.security.krb5.realm= "
    export HADOOP_OPTS="${HADOOP_OPTS} -Djava.security.krb5.kdc= "
    export HADOOP_OPTS="${HADOOP_OPTS} -Djava.security.krb5.conf= "
  ;;
esac

export HADOOP_PID_DIR=/home/hadoop_files
export JAVA_HOME=/usr/local/jdk1.8.0_112

