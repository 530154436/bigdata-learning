package spark.structured_streaming

import conf.SparkGlobal
import org.apache.spark.sql.{DataFrame, Dataset}
import org.apache.spark.sql.streaming.{StreamingQuery, Trigger}

/**
 * -> 先启动 ch01_2_套接字流_自定义数据源.scala, 在启动这个脚本
 */
object ch01_3_Socket源 {
    val hostname = "127.0.0.1"
    val port = 9999

    def main(args: Array[String]): Unit = {
        val sparkSession = SparkGlobal.getSparkSession(name = "Structured Streaming WordCount")
        sparkSession.sparkContext.setLogLevel("WARN")

        // 监听在本机（localhost）的9999端口上的服务
        val lines = sparkSession
            .readStream
            .format("socket")
            .option("host", hostname)
            .option("port", port)
            .load()

        // 定义完查询语句: 分组计数 [value: string]
        import sparkSession.implicits._
        val words: Dataset[String] = lines.as[String].flatMap(_.split(","))
        val wordCounts: DataFrame = words.groupBy("value").count()

        // 启动流计算并输出结果
        val query: StreamingQuery = wordCounts
            .writeStream
            .outputMode("complete")
            .format("console")
            .trigger(Trigger.ProcessingTime(8000))
            .start()
        query.awaitTermination()
        sparkSession.stop()
    }
}

