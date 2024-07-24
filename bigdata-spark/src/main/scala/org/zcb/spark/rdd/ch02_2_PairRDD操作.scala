package org.zcb.spark.rdd

import org.zcb.common.conf.Global
import org.apache.commons.io.FileUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

import java.nio.file.{Path, Paths}

/**
 *  ### PairRDD转换操作
 *  常用的键值对转换操作包括：
 *  + reduceByKey(func)：使用func函数合并具有相同键的值。
 *  + groupByKey()：对具有相同键的值进行分组。
 *  + keys()：只会把Pair RDD中的key返回形成一个新的RDD。
 *  + values()：values只会把Pair RDD中的value返回形成一个新的RDD。
 *  + sortByKey()：返回一个根据键排序的RDD
 *  + sortBy(func)：返回一个根据传入函数排序的RDD
 *  + mapValues(func)：对键值对RDD中的每个value都应用一个函数，但是，key不会发生变化
 *  + join（内连接）：对于给定的两个输入数据集(K,V1)和(K,V2)，只有在两个数据集中都存在的key才会被输出，最终得到一个(K,(V1,V2))类型的数据集。
 *
 *  reduceByKey和groupByKey的区别：
 *  + reduceByKey用于对每个key对应的多个value进行merge操作<br>
 *  在本地对分区内相同key的数据集进行预聚合，减少落盘的数据量，并且merge操作可以通过函数自定义
 *  + groupByKey也是对每个key进行操作，但只生成一个sequence，groupByKey本身不能自定义函数<br>
 *  需要先用groupByKey生成RDD，然后才能对此RDD通过map进行自定义函数操作
 *
 *  #### combineByKey
 *  combineByKey是Spark中一个比较核心的高级函数，其他一些高阶键值对函数底层都是用它实现的。诸如 groupByKey,reduceByKey等等。
 *  ```scala
 *def combineByKey[C](
 *  createCombiner: V => C,
 *  mergeValue: (C, V) => C,
 *  mergeCombiners: (C, C) => C,
 *  partitioner: Partitioner,
 *  mapSideCombine: Boolean = true,
 *  serializer: Serializer = null)
 *  ```
 *+ `createCombiner`：在第一次遇到Key时创建组合器函数，将RDD数据集中的V类型值转换C类型值（V => C）
 *  + `mergeValue`：合并值函数，再次遇到相同的Key时，将createCombiner的C类型值与这次传入的V类型值合并成一个C类型值（C,V）=>C<br>
 *  （在每个分区内部进行）
 *  + `mergeCombiners`：合并组合器函数，将C类型值两两合并成一个C类型值
 *  （在不同分区间进行）
 *  + `partitioner`：使用已有的或自定义的分区函数，默认是HashPartitioner
 *  + `mapSideCombine`：是否在map端进行Combine操作,默认为true
 */

object ch02_2_PairRDD操作 {
    def reduceByKey(pairRDD: RDD[(String, Int)]): Unit = {
        // (Spark,2)
        pairRDD.reduceByKey((a, b) => a + b).foreach(x => println(s"reduceByKey: $x"))
    }

    def groupByKey(pairRDD: RDD[(String, Int)]): Unit = {
        // (Spark, CompactBuffer(1, 1)) =map=> (Spark,2)
        pairRDD.groupByKey()
            .map(t => (t._1, t._2.sum))
            .foreach(x => println(s"groupByKey+map: ${x}"))
    }

    def keys(pairRDD: RDD[(String, Int)]): Unit = {
        pairRDD.keys.foreach(x => println(s"keys: ${x}"))
    }

    def values(pairRDD: RDD[(String, Int)]): Unit = {
        pairRDD.values.foreach(x => println(s"values: ${x}"))
    }

    def sortByKey(pairRDD: RDD[(String, Int)]): Unit = {
        pairRDD.sortByKey().foreach(x => println(s"sortByKey: ${x}"))
    }

    def sortBy(pairRDD: RDD[(String, Int)]): Unit = {
        pairRDD.reduceByKey((a, b) => a + b)
            .sortBy(pair => pair._2, ascending = false)
            .foreach(x => println(s"sortBy: ${x}"))
    }

    def mapValues(pairRDD: RDD[(String, Int)]): Unit = {
        pairRDD.mapValues(v => v + 10).foreach(x => println(s"mapValues: ${x}"))
    }

    def join(pairRDD: RDD[(String, Int)], pairRDD2: RDD[(String, Int)]): Unit = {
        pairRDD.join(pairRDD2).foreach(x => println(s"join: ${x}"))
    }

    /**
     * 例：编程实现自定义Spark合并方案。给定一些销售数据，数据采用键值对的形式<公司，收入>，求出每个公司的总
     * 收入和平均收入，保存在本地文件
     *
     * 提示：可直接用sc.parallelize在内存中生成数据，在求每个公司总收入时，先分三个分区进行求和，然后再把三个分区
     * 进行合并。只需要编写RDD combineByKey函数的前三个参数的实现
     */
    def combineByKey(sparkSession: SparkSession): Unit = {
        val data = sparkSession.sparkContext.parallelize(
            Array(("company-1", 92.0), ("company-1", 85.0), ("company-1", 82.0), ("company-2", 78.0),
                ("company-2", 96.0), ("company-2", 85.0), ("company-3", 88.0), ("company-3", 94.0),
                ("company-3", 80.0)), 3
        )
        type C = (Int, Double) // Multiple value, 定义一个元组类型(公司数,总收入)
        val res = data.combineByKey(
            income => (1, income), // 第一次遇到Key时，V => (C, V)
            (i2: C, otherIncome) => (i2._1 + 1, i2._2 + otherIncome), // 同一分区内，再次遇到Key, (C, V) => C
            (i1: C, i2: C) => (i1._1 + i2._1, i2._2 + i2._2) // 不同分区间合并，(C1, C2) => C
        ).map {
            case (k, v) => (k, v._1, v._2 / v._1)
        }

        // 判断文件是否存在
        val file: Path = Paths.get(Global.BASE_DIR, "data", "output", "combineByKey.txt").toAbsolutePath
        if (file.toFile.exists()) {
            FileUtils.deleteDirectory(file.toFile)
        }
        res.repartition(1).saveAsTextFile(file.toString)
    }

    /**
     * 题目：给定一组键值对("spark",2),("hadoop",6),("hadoop",4),("spark",6)，
     * 键值对的key表示图书名称，value表示某天图书销量，请计算每个键对应的平均值，也就是计算每种图书的每天平均销量。
     */
    def example_calc_avg(sparkSession: SparkSession): Unit = {
        val data = sparkSession.sparkContext.parallelize(
            Array(("sparkRDD", 2), ("hadoop", 6), ("hadoop", 4), ("sparkRDD", 6)))
        data.mapValues(x => (x, 1)) // 计数
            .reduceByKey((v1, v2) => (v1._1 + v2._1, v1._2 + v2._2)) // 合并
            .mapValues(v => v._1 / v._2) // 计算平均值
            .foreach(x => println(s"example: ${x}"))
    }

    def main(args: Array[String]): Unit = {
        val list = List("Hadoop", "Spark", "Hive", "Spark", "Flink")
        val list2 = List("Hive", "Spark")

        val sparkSession = SparkGlobal.getSparkSession()
        val pairRDD = sparkSession.sparkContext.parallelize(list).map(word => (word, 1))
        val pairRDD2 = sparkSession.sparkContext.parallelize(list2).map(word => (word, 1))

        reduceByKey(pairRDD)
        groupByKey(pairRDD)
        keys(pairRDD)
        values(pairRDD)
        sortByKey(pairRDD)
        sortBy(pairRDD)
        mapValues(pairRDD)
        join(pairRDD, pairRDD2)
        combineByKey(sparkSession)

        // 计算平均值
        example_calc_avg(sparkSession)
    }
}
