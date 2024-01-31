package spark.streaming

import conf.{SparkGlobal, Global}
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}

import java.nio.file.Paths

object ch01_文件流 {

    def main(args: Array[String]): Unit = {
        val sparkSession: SparkSession = SparkGlobal.getSparkSession(this.getClass.getName)
        val sparkConf = sparkSession.sparkContext.getConf
        val ssc = new StreamingContext(sparkConf, Seconds(2))  // 时间间隔为2秒

        // 监听本地目录
        val directory = Paths.get(Global.BASE_DIR, "data", "resources").toAbsolutePath
        val lines = ssc.textFileStream(s"file://${directory}")

        // 统计词频
        val words = lines.flatMap(x => x.split(" "))
        val wordCount = words.map(x => (x, 1)).reduceByKey((x, y) => x + y)
        wordCount.print()

        ssc.start()
        // 用于让StreamingContext保持运行状态，直到手动停止或发生错误。
        ssc.awaitTermination()
    }
}
