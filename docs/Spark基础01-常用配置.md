[TOC]

### Spark常用配置

| 配置项                                             | 默认值      | 描述                                                                                                          |
|-------------------------------------------------|----------|-------------------------------------------------------------------------------------------------------------|
| `spark.hadoop.hive.exec.dynamic.partition`      | `false`  | 开启或关闭动态分区插入功能。设为`true`时允许使用动态分区插入数据到Hive表中。                                                                 |
| `spark.hadoop.hive.exec.dynamic.partition.mode` | `strict` | 定义动态分区插入模式。<br>- `strict`: 至少有一个静态分区必须被指定，防止覆盖整个表的数据。<br>- `nonstrict`: 允许所有分区都采用动态方式插入。                    |
| `spark.hadoop.hive.exec.max.dynamic.partitions` | `5000`   | 在一个单一DML操作中可以创建的最大动态分区数量，以防止性能问题或资源耗尽。                                                                      |
| `spark.sql.sources.partitionOverwriteMode`      | `static` | 控制如何处理分区表中的数据覆盖。<br>- `static`: 只有当写入路径与已存在的分区完全匹配时才会进行覆盖。<br>- `dynamic`: 删除目标表中对应的所有分区数据，即使它们不在当前写入的数据集中。 |

### 参考引用
+ [Spark官方-configuration](https://spark.apache.org/docs/latest/configuration.html#custom-hadoophive-configuration)
+ [Spark官方-API文档](https://spark.apache.org/docs/latest/api/python/reference/pyspark.sql/api/pyspark.sql.DataFrameWriter.html)

