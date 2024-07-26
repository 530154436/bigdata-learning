package org.zcb.spark.structured_streaming

import org.apache.spark.sql.functions.{asc, window}
import org.apache.spark.sql.streaming.{StreamingQuery, Trigger}
import org.apache.spark.sql.types.{StringType, StructField, StructType, TimestampType}
import org.zcb.spark.SparkGlobal


/**
 * 这里以一个JSON格式文件的处理来演示File源的使用方法，主要包括以下两个步骤：
 * 创建程序生成JSON格式的File源测试数据: -> 先执行ch01_1_SparkFileSourceGenerate
 * 创建程序对数据进行统计
 */
object ch01_1_File源 {
    def main(args: Array[String]): Unit = {
        // 定义模式，为时间戳类型的eventTime、字符串类型的操作和省份组成
        val schema = StructType(Array(
            StructField("eventTime", TimestampType, nullable = true),
            StructField("action", StringType, nullable = true),
            StructField("district", StringType, nullable = true)
        ))

        val sparkSession = SparkGlobal.getSparkSession(name = "Structured Streaming WordCount")
        sparkSession.sparkContext.setLogLevel("WARN")

        val lines = sparkSession
            .readStream
            .format("json")
            .schema(schema)
            .option("maxFilesPerTrigger", 100)
            .load(ch01_1_SparkFileSourceGenerate.TEST_DATA_DIR.toAbsolutePath.toString)

        // 定义完查询语句: 分组计数 [value: string]
        import sparkSession.implicits._
        val windowDuration = "1 minutes"  // 统计的事件时间范围
        val windowedCounts = lines.filter($"action" === "purchase")
            .groupBy($"district", window($"eventTime", windowDuration))
            .count()
            .sort(asc("window"))

        // 启动流计算并输出结果
        val query: StreamingQuery = windowedCounts
            .writeStream
            .outputMode("complete")
            .format("console")
            .trigger(Trigger.ProcessingTime(10000)) // 触发间隔, trigger interval
            .start()
        query.awaitTermination()
        sparkSession.stop()
    }
}

