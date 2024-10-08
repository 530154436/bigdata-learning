package org.zcb.spark.rdd.exercises

import org.zcb.common.conf.Global
import org.apache.spark.sql.SparkSession
import org.zcb.spark.SparkGlobal

import java.nio.file.{Path, Paths}

/**
任务描述：求出多个文件中数值的最大、最小值


 */
object ex02_求最大最小值 {
    def main(args: Array[String]): Unit ={
        val topN: Int = 5
        val directory: Path = Paths.get(Global.BASE_DIR, "data", "spark", "rdd", "02").toAbsolutePath

        val sparkSession: SparkSession = SparkGlobal.getSparkSession(this.getClass.getName)
        val rdd = sparkSession.sparkContext.textFile(directory.toString, 2)
        // val rdd = sparkSession.sparkContext.wholeTextFiles(directory.toString)
        val rdd1 = rdd.filter(line => line.trim().nonEmpty)
          .map(line => line.trim().toInt)
        println(s"最大值：${rdd1.max()}")
        println(s"最小值：${rdd1.min()}")
    }
}
