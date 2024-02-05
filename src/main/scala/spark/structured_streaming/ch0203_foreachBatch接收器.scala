package spark.structured_streaming

import conf.{Global, SparkGlobal}
import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.sql.DataFrame
import spark.streaming.ch01_2_套接字流_自定义数据源

import java.nio.file.Paths
import java.util.Properties


/**
 * foreachBatch接收器：只能用于输出批处理的数据.
 *
 * -> 先启动 ch01_2_套接字流_自定义数据源.scala, 在启动这个脚本
 */
object ch0203_foreachBatch接收器 {
    def main(args: Array[String]): Unit = {
        val sparkSession = SparkGlobal.getSparkSession(name = "StructuredStreamingSink")
        sparkSession.sparkContext.setLogLevel("WARN")

        val lines = sparkSession
            .readStream
            .format("socket")
            .option("host", ch01_2_套接字流_自定义数据源.hostname)
            .option("port", ch01_2_套接字流_自定义数据源.port)
            .load()

        // 定义完查询语句: 分组计数 [value: string]
        import sparkSession.implicits._
        val wordCount: DataFrame = lines.as[String]
            .flatMap(_.split(","))
            .groupBy("value")
            .count()

        // 启动流计算并输出结果
        val props = new Properties()
        props.setProperty("user", "root")
        props.setProperty("password", "123456")
        val query: StreamingQuery = wordCount.writeStream
            .outputMode("complete")
            .foreachBatch((df, batchId) => {  // 当前分区id, 当前批次id
                val file = Paths.get(Global.BASE_DIR, "data", "sink", "fileSink", s"$batchId").toAbsolutePath.toString
                if (df.count() != 0) {
                    df.cache()
                    //df.write.json(s"$file")
                    df.write
                        .mode("overwrite")
                        .jdbc("jdbc:mysql://localhost:3306/spark?useUnicode=true&characterEncoding=utf8&useSSL=false", "word_count", props)
                }
            })
            .start()

        query.awaitTermination()
        sparkSession.stop()
    }
}

