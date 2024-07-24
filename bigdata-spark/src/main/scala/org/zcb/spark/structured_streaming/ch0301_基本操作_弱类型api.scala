package org.zcb.spark.structured_streaming

import org.zcb.common.conf.Global
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.streaming.{StreamingQuery, Trigger}
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType, TimestampType}

import java.nio.file.Paths


/**
 * 在 DF/DS 上大多数通用操作都支持作用在 Streaming DataFrame/Streaming DataSet 上
 */
object ch0301_基本操作_弱类型api {
    def main(args: Array[String]): Unit = {
        val sparkSession = SparkGlobal.getSparkSession(name = "StructuredStreaming")
        sparkSession.sparkContext.setLogLevel("WARN")

        // 定义模式
        val schema: StructType = StructType(Array(
            StructField("name", StringType, nullable = true),
            StructField("age", LongType, nullable = true),
            StructField("sex", StringType, nullable = true)
        ))

        val file = Paths.get(Global.BASE_DIR, "data", "structuredStreaming").toAbsolutePath.toString
        val peopleDf: DataFrame = sparkSession
            .readStream
            .schema(schema)
            .json(file)

        val df: DataFrame = peopleDf
            .select("name", "age", "sex")
            .where("age > 20")  // 弱类型 api

        // 启动流计算并输出结果
        val query: StreamingQuery = df
            .writeStream
            .outputMode("append")
            .format("console")
            .start()
        query.awaitTermination()
        sparkSession.stop()
    }
}

