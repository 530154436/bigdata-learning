package spark.rdd
import java.nio.file.{Path, Paths}
import org.apache.spark.sql.SparkSession
import conf.{Global, SparkGlobal}

/**
 *  ### PairRDD创建
 *  虽然RDD中可以包含任何类型的对象，但是“键值对”是一种比较常见的RDD元素类型，分组和聚合操作中经常会用到。<br>
 *  Spark操作中经常会用到“键值对RDD”（Pair RDD），用于完成聚合计算。<br>
 *  普通RDD里面存储的数据类型是Int、String等，而“键值对RDD”里面存储的数据类型是“键值对”。<br>
 *  1. 从文件系统中加载数据创建PairRDD
 *  2. 通过并行集合（数组）创建PairRDD
 */
object ch02_1_PairRDD创建 {
    def createFromFile(sparkSession: SparkSession): Unit = {
        val file: Path = Paths.get(Global.BASE_DIR, "data", "wordcount", "word1.txt").toAbsolutePath
        val lines = sparkSession.sparkContext.textFile(file.toString)
        val pairRDD = lines.flatMap(line => line.split(" "))
            .map(word => (word, 1))
        println(pairRDD.getClass)
        pairRDD.foreach(println)
    }

    def createFromParallelize(sparkSession: SparkSession): Unit = {
        val list = List("Hadoop", "Spark", "Hive", "Spark")
        val rdd = sparkSession.sparkContext.parallelize(list)
        val pairRDD = rdd.map(word => (word, 1))
        println(pairRDD.getClass)
        pairRDD.foreach(println)
    }

    def main(args: Array[String]): Unit = {
        // 从本地文件系统中加载数据
        createFromFile(SparkGlobal.sparkSession)
        // 通过并行集合（数组）创建PairRDD
        createFromParallelize(SparkGlobal.sparkSession)
    }
}
