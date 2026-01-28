<nav>
<a href="#1spark-yarn模式使用特定的-python-解释器">1、Spark Yarn模式使用特定的 Python 解释器</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-构建-python31011-环境并打包">1.1 构建 Python3.10.11 环境并打包</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-日志输出配置">1.2 日志输出配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#13-使用示例">1.3 使用示例</a><br/>
<a href="#参考引用">参考引用</a><br/>
</nav>

---

## 1、Spark Yarn模式使用特定的 Python 解释器

+ 当前环境：`Python3.6.8` + `Spark2.3.1`
+ 升级环境：`Python3.10.11` + [Spark3.3.1](https://archive.apache.org/dist/spark/docs/3.3.1/api/python/getting_started/install.html)（要求 Python 3.7 及以上版本）

### 1.1 构建 Python3.10.11 环境并打包
```shell
# 172.16.18.234
cd /data/devNFS/zhengchubin

# 创建虚拟环境
# conda env remove -n spark331
conda create -n spark331 python=3.10.11
conda activate spark331

# 安装依赖
python -m pip install --upgrade pip
python -m pip install --no-cache -r ./shufan-spark331-requirements.txt -i http://maven.qizhidao.net:8081/repository/algo-public/simple/ --trusted-host  maven.qizhidao.net

# 打包环境
conda-pack -n spark331 -o python310-env.tar.gz
```

报错：
```
(spark331) $ conda-pack -n spark331 -o python310-env.tar.gz
Collecting packages...
CondaPackError: 
Files managed by conda were found to have been deleted/overwritten in the
following packages:

- conda-pack 0.8.1:
    lib/python3.1/site-packages/conda_pack-0.8.1.dist-info/INSTALLER
    lib/python3.1/site-packages/conda_pack-0.8.1.dist-info/LICENSE.txt
    lib/python3.1/site-packages/conda_pack-0.8.1.dist-info/METADATA
    + 20 others
- pip 25.3:
    lib/python3.1/site-packages/pip-25.3.dist-info/INSTALLER
    lib/python3.1/site-packages/pip-25.3.dist-info/METADATA
    lib/python3.1/site-packages/pip-25.3.dist-info/RECORD
    + 469 others
```
原因：pip安装的包的版本与conda安装的包的版本冲突了，需要进行统一。
```shell
(spark331) $ conda list | grep -E "pip|conda-pack"
conda-pack                0.8.1                    pypi_0    pypi
pip                       25.2               pyhc872135_1    defaults
```
升级pip，使得版本统一
```shell
(spark331) $ conda list | grep -E "pip|conda-pack"
conda-pack                0.8.1                    pypi_0    pypi
pip                       25.3                     pypi_0    pypi


(spark331) $ conda-pack -n spark331 -o python310-env.tar.gz
Collecting packages...
Packing environment at '/home/dev/.conda/envs/spark331' to 'python310-env.tar.gz'
[########################################] | 100% Completed | 15.0s
```
### 1.2 日志输出配置
Spark日志输出大量INFO的信息，会将重要信息淹没，因而需要精简下输出。

+ 新建`log4j2.properties`
```
log4j.rootCategory=WARN
```
+ Spark Yarn模式下指定配置文件
```shell
   --files ${ROOT_PATH}/log4j2.properties \
   --conf "spark.driver.extraJavaOptions=-Dlog4j.configurationFile=log4j2.properties" \
   --conf "spark.executor.extraJavaOptions=-Dlog4j.configurationFile=log4j2.properties" \
```

### 1.3 使用示例
```shell
# 解释器存放路径
# hdfs://easyops-cluster2//user/algo_recommend_prod/project_public/python_utils/python310-env.tar.gz
HADOOP_CONF_V3=/usr/easyops/spark2/3.3.1.53-cluster2_spark_client/current/bin/
ENV_TYPE=DEV
HIVE_TABLE_LOCATION_DIR=hdfs://easyops-cluster2/user/algo_recommend_dev
ROOT_PATH=hdfs://easyops-cluster2/user/algo_recommend_dev/project_public/algo-rs-spark-task
LIB_PATH=hdfs://easyops-cluster2/user/algo_recommend_dev/project_public/python_utils
QUEUE_NAME="root.algo_longtime.default"

${HADOOP_CONF_V3}/spark-submit \
   --master yarn \
   --deploy-mode cluster \
   --num-executors 2 \
   --executor-memory 1G \
   --executor-cores 2 \
   --driver-memory 1G \
   --queue ${QUEUE_NAME} \
   --archives ${LIB_PATH}/python310-env.tar.gz#environment \
   --conf spark.yarn.appMasterEnv.PYSPARK_PYTHON=./environment/bin/python \
   --conf spark.executorEnv.PYSPARK_PYTHON=./environment/bin/python \
   --conf spark.yarn.appMasterEnv.ENV_TYPE=$ENV_TYPE \
   --conf spark.yarn.appMasterEnv.HIVE_TABLE_LOCATION_DIR=$HIVE_TABLE_LOCATION_DIR \
   --files ${ROOT_PATH}/log4j2.properties \
   --conf "spark.driver.extraJavaOptions=-Dlog4j.configurationFile=log4j2.properties" \
   --conf "spark.executor.extraJavaOptions=-Dlog4j.configurationFile=log4j2.properties" \
   --conf spark.dynamicAllocation.enabled=false \
   --conf spark.default.parallelism=100 \
   --conf spark.network.timeout=800s \
   --conf spark.executor.heartbeatInterval=60s \
   --conf spark.sql.autoBroadcastJoinThreshold=-1 \
   --conf spark.executor.memoryOverhead=10g \
   --conf spark.driver.memoryOverhead=4g \
   --conf spark.hadoop.hive.metastore.client.socket.timeout=864000s \
   --py-files ${ROOT_PATH}/src.zip ${ROOT_PATH}/src/test/test_spark_connection.py
```



## 参考引用
[1] [Spark 3.3.1 官方文档](https://archive.apache.org/dist/spark/docs/3.3.1/api/python/getting_started/install.html)<br>