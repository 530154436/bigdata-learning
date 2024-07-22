package spark.rdd.exercises

import java.nio.file.{Path, Paths}
import conf.Global
import org.apache.spark.sql.SparkSession

/**
任务描述：求Top N个payment值
文件格式:
orderid,userid,payment,productid

file1.txt
1,1768,50,155
2,1218, 600,211
3,2239,788,242
4,3101,28,599
5,4899,290,129
6,3110,54,1201
7,4436,259,877
8,2369,7890,27

file2.txt
100,4287,226,233
101,6562,489,124
102,1124,33,17
103,3267,159,179
104,4569,57,125
105,1438,37,116
 */
object ex01_求top值 {
    def main(args: Array[String]): Unit ={
        val topN: Int = 5
        val directory: Path = Paths.get(Global.BASE_DIR, "data", "sparkrdd").toAbsolutePath

        val sparkSession: SparkSession = SparkGlobal.getSparkSession(this.getClass.getName)
        val rdd = sparkSession.sparkContext.textFile(directory.toString, 2)
        // val rdd = sparkSession.sparkContext.wholeTextFiles(directory.toString)
        rdd.filter(line => line.trim().nonEmpty && line.trim().split(",").length ==4)
          .map(line => line.split(",")(2).trim().toInt)
          .sortBy(payment => payment, false)
          .take(topN)
          .foreach(println)
    }
}
