# -*- coding: utf-8 -*-
from pyspark import SparkConf
from pyspark.sql import SparkSession, DataFrame
from pyspark.sql.types import StructType, IntegerType, StructField, StringType

conf = SparkConf()\
    .setMaster('local[*]')\
    .setAppName("TestRemoteHive")
session = SparkSession.builder.config(conf=conf).enableHiveSupport().getOrCreate()

# -----------------------------------------------------------------------------------------------------
# 数据库操作：删除、创建、读
# -----------------------------------------------------------------------------------------------------
session.sql("drop database if exists algo_recommend")
# session.sql("create database if not exists algo_recommend")  # 本地执行有问题，通过hiveserver2可以
df = session.sql("show databases")
df.show()

# -----------------------------------------------------------------------------------------------------
# 表操作：删除、创建、写、读
# -----------------------------------------------------------------------------------------------------
session.sql("use default")
session.sql("drop table if exists test")
session.sql(r"""
create table if not exists test (
  id int,
  name String
)
stored as orc
TBLPROPERTIES('transactional'='false')
""")

# 写表
# 方法1：省略列名
session.sql("INSERT INTO test VALUES (1, 'allen')")
# 方法2：用 SELECT
session.sql("INSERT INTO test SELECT 2 AS id, 'edward' AS name")
# 方法3：dataframe写入
data = [(3, "bob")]
schema = StructType([
    StructField("id", IntegerType(), True),
    StructField("name", StringType(), True)
])
df = session.createDataFrame(data, schema=schema)
df.write.mode("append").insertInto("test", overwrite=False)

# 读表
df: DataFrame = session.sql("select * from test")
df.show()
