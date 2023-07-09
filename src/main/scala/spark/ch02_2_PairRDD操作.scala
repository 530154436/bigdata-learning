package spark

import conf.{Global, SparkGlobal}
import org.apache.spark.Partitioner
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel.MEMORY_AND_DISK

import java.nio.file.{Path, Paths}

/**
### PairRDD转换操作
常用的键值对转换操作包括：
+ reduceByKey(func)：使用func函数合并具有相同键的值。
+ groupByKey()：对具有相同键的值进行分组。
+ groupByKey()：对具有相同键的值进行分组。

reduceByKey和groupByKey的区别：
+ reduceByKey用于对每个key对应的多个value进行merge操作<br>
  在本地对分区内相同key的数据集进行预聚合，减少落盘的数据量，并且merge操作可以通过函数自定义
+ groupByKey也是对每个key进行操作，但只生成一个sequence，groupByKey本身不能自定义函数<br>
  需要先用groupByKey生成RDD，然后才能对此RDD通过map进行自定义函数操作
 */

object ch02_2_PairRDD操作 {
    def reduceByKey(pairRDD:  RDD[(String, Int)]): Unit = {
        // (Spark,2)
        pairRDD.reduceByKey((a,b)=>a+b).foreach(x => println(s"reduceByKey: $x"))
    }

    def groupByKey(pairRDD: RDD[(String, Int)]): Unit = {
        // (Spark, CompactBuffer(1, 1)) =map=> (Spark,2)
        pairRDD.groupByKey()
          .map(t => (t._1, t._2.sum))
          .foreach(x => println(s"groupByKey+map: ${x}"))
    }

    def main(args: Array[String]): Unit = {
        val list = List("Hadoop", "Spark", "Hive", "Spark", "Flink")
        val rdd = SparkGlobal.sparkSession.sparkContext.parallelize(list)
        val pairRDD = rdd.map(word => (word, 1))

        reduceByKey(pairRDD)
        groupByKey(pairRDD)
    }
}
