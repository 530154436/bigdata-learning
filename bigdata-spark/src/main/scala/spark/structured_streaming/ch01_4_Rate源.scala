package spark.structured_streaming

import org.apache.spark.sql.streaming.{StreamingQuery, Trigger}
import org.apache.spark.sql.{DataFrame, Dataset}


object ch01_4_Rate源 {
    def main(args: Array[String]): Unit = {
        val sparkSession = SparkGlobal.getSparkSession(name = "Structured Streaming WordCount")
        sparkSession.sparkContext.setLogLevel("WARN")

        val lines = sparkSession
            .readStream
            .format("rate")
            .option("rowsPerSecond", 5)  // 设置每秒产生的数据的条数, 默认是 1
            .option("rampUpTime", 1)     // 设置多少秒到达指定速率 默认为 0
            .option("numPartitions", 2)  // 设置分区数  默认是 spark 的默认并行度
            .load()
        // StructType(StructField(timestamp,TimestampType,true), StructField(value,LongType,true))
        print(lines.schema)

        // 启动流计算并输出结果
        val query: StreamingQuery = lines
            .writeStream
            .outputMode("update")
            .trigger(Trigger.Continuous(1000))
            .format("console")
            .start()
        query.awaitTermination()
        sparkSession.stop()
    }
}

