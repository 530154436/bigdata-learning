package spark.structured_streaming

import conf.SparkGlobal
import org.apache.spark.sql.streaming.{StreamingQuery, Trigger}
import org.apache.spark.sql.{DataFrame, Dataset}


object ch01_4_Rate源 {
    def main(args: Array[String]): Unit = {
        val sparkSession = SparkGlobal.getSparkSession(name = "Structured Streaming WordCount")
        sparkSession.sparkContext.setLogLevel("WARN")

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

