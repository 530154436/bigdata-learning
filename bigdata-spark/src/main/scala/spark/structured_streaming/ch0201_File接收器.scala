package spark.structured_streaming

import conf.Global
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.streaming.{StreamingQuery, Trigger}
import spark.streaming.ch01_2_套接字流_自定义数据源

import java.nio.file.Paths


/**
 输出(Output)定义为写到外部存储. `输出模式`(outputMode)有 3 种:
    - `Complete Mode` 整个更新的结果表会被写入到外部存储.
    - `Append Mode` 从上次触发结束开始算起, 仅仅把那些新追加到结果表中的行写到外部存储(类似于无状态的转换).
    - `Update Mode` 从上次触发结束开始算起, 仅仅在结果表中更新的行会写入到外部存储，当查询不包括聚合时，这个模式等同于Append模式。

 -> 先启动 ch01_2_套接字流_自定义数据源.scala, 在启动这个脚本
 */
object ch0201_File接收器 {
    def main(args: Array[String]): Unit = {
        val sparkSession = SparkGlobal.getSparkSession(name = "StructuredNetworkWordCountFileSink")
        sparkSession.sparkContext.setLogLevel("WARN")

        val lines = sparkSession
            .readStream
            .format("socket")
            .option("host", ch01_2_套接字流_自定义数据源.hostname)
            .option("port", ch01_2_套接字流_自定义数据源.port)
            .load()

        // 定义完查询语句: 分组计数 [value: string]
        import sparkSession.implicits._
        val words: DataFrame = lines.as[String].flatMap(line => {
            line.split(",").map(word => {
                (word, word.reverse)
            })
        }).toDF("原单词", "反转单词")

        // 启动流计算并输出结果
        val checkpointDir = Paths.get(Global.BASE_DIR, "data", "sink", "fileCheckpoint").toAbsolutePath.toString
        val parquetDir = Paths.get(Global.BASE_DIR, "data", "sink", "fileSink").toAbsolutePath.toString

        val query: StreamingQuery = words
            .writeStream
            .outputMode("append")
            .format("json")  // 支持 "orc", "json", "csv"
            .option("path", s"file:///${parquetDir}")
            .option("checkpointLocation", s"file:///${checkpointDir}")
            .trigger(Trigger.ProcessingTime("10 seconds"))
            .start()

        query.awaitTermination()
        sparkSession.stop()
    }
}

