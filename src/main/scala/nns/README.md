+ 测试分区的影响<br>
配置信息:
> --dbName algo_recommend_dev --fromHiveTbName tmp_t_ads_patent_wide --columns out_num,patent_name,keyword,tech_word,product_word --startDate 1990-01-01 --endDate 2023-07-14 --matchedHiveTbName tmp_t_ads_algo_rs_user_prod_word_agg_data --matchedColumns word --taskType textFuzzyMatch --dstHiveTbName dws_algo_rs_user_behavior_match_word_patent

| num-executors | executor-memory | executor-cores | 分区数 | 分区大小 | 记录数 | 耗时(时:分:秒) |
| ------- | ------- | ------- | ------- | ------- | ------- | ------- |
| 16 | 4G | 4 | 10152 | 5064 | 51232895 | 1:01:32 |
| 16 | 4G | 4 | 103 | 500000 | 51232895 | 0:03:56 |



### 海量数据如何计算相似度？
比如：有几千万的文章，如何计算文章的两两之间的相似度。

https://www.zhihu.com/question/32107594/answer/313900004

1. Spark LSH 近似最近邻矢量检索：LinkedInAttic ScANNS项目学习和工程使用
   https://www.jianshu.com/p/b72af2d27235
   https://github.com/xiaogp

   LSH局部敏感哈希：原理，Python手撕LSH实现embedding近邻检索
   https://www.jianshu.com/p/bbf142f8ec84

   局部敏感哈希(LSH)：高维数据下的最近邻查找
   https://blog.csdn.net/sgyuanshi/article/details/108132214
   
   LSH系列1：局部敏感哈希（LSH）——基本原理介绍
   https://blog.csdn.net/qq_39583450/article/details/109013389

   BucketedRandomProjectionLSHExample.scala
   https://github.com/apache/spark/blob/8a51cfdcad5f8397558ed2e245eb03650f37ce66/examples/src/main/scala/org/apache/spark/examples/ml/BucketedRandomProjectionLSHExample.scala

   局部敏感哈希（Locality-Sensitive Hashing，LSH）
   https://blog.rexking6.top/2018/10/09/%E5%B1%80%E9%83%A8%E6%95%8F%E6%84%9F%E5%93%88%E5%B8%8C-Locality-Sensitive-Hashing-LSH/

   海量数据挖掘（三）：Finding Similar Sets
   https://www.52coding.com.cn/2016/01/04/%E6%B5%B7%E9%87%8F%E6%95%B0%E6%8D%AE%E6%8C%96%E6%8E%98%EF%BC%88%E4%B8%89%EF%BC%89%EF%BC%9AFinding%20Similar%20Sets/
   

   可扩展的近似最近邻搜索 Scalable Approximate Nearest Neighbor Search (ScANNS)
   https://github.com/LinkedInAttic/scanns
   
2. LSH 局部敏感哈希 + topK
   https://spark.apache.org/docs/2.2.0/ml-features.html#approximate-nearest-neighbor-search


### Spark问题
Unable to infer schema for table bigdata_application_dev.ads_iur_kd_nsfc_gov_cn from file format ORC (inference mode: INFER_AND_SAVE). Using metastore schema.