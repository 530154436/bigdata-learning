package org.zcb.spark.rdd
import org.zcb.common.conf.Global

import java.nio.file.{Path, Paths}
import org.apache.spark.sql.SparkSession
import org.zcb.spark.SparkGlobal

/**
 * 创建RDD
 * (1) 从文件系统中加载数据创建RDD
 * （2）从分布式文件系统HDFS中加载数据
 * （3）通过并行集合（数组）创建RDD
 */
object ch01_1_RDD创建 {

    def createFromFile(sparkSession: SparkSession): Unit = {
        val file: Path = Paths.get(Global.BASE_DIR, "data", "spark", "wordcount", "word1.txt").toAbsolutePath
        val lines = sparkSession.sparkContext.textFile(file.toString)
        println("createRddFromFile", lines.count())
    }

    def createFromHdfs(sparkSession: SparkSession): Unit = {
        val lines = sparkSession.sparkContext.textFile("org.zcb.hadoop.hdfs://localhost:9000/spark/word1.txt")
        println("createRddFromHdfs", lines.count())
    }

    def createFromParallelize(sparkSession: SparkSession): Unit = {
        val array: Array[Int] = Array(1, 2, 3, 4, 5)
        val rdd = sparkSession.sparkContext.parallelize(array)
        println("createRddFromParallelize", rdd.count())

        val list: List[Int] = List(1, 2, 3, 4, 5)
        val rdd1 = sparkSession.sparkContext.parallelize(list)
        println("createRddFromParallelize", rdd1.count())
    }

    def main(args: Array[String]): Unit = {
        val sparkSession = SparkGlobal.getSparkSession()
        //（1）从本地文件系统中加载数据
        createFromFile(sparkSession)

        //（2）从分布式文件系统HDFS中加载数据
        // createFromHdfs(sparkSession)

        //（3）通过并行集合（数组）创建RDD
        createFromParallelize(sparkSession)
    }
}
