package org.zcb.spark.structured_streaming

import org.zcb.common.conf.Global
import org.apache.spark.sql.{DataFrame, Dataset}
import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}
import org.zcb.spark.SparkGlobal

import java.nio.file.Paths


/**
 * 在 DF/DS 上大多数通用操作都支持作用在 Streaming DataFrame/Streaming DataSet 上
 */
case class People(name: String = "", age: Long = 0, sex: String = "")


object ch03_基本操作_强类型api {
    def main(args: Array[String]): Unit = {
        val sparkSession = SparkGlobal.getSparkSession(name = "StructuredStreaming")
        sparkSession.sparkContext.setLogLevel("WARN")

        // 定义模式
        val schema: StructType = StructType(Array(
            StructField("name", StringType),
            StructField("age", LongType),
            StructField("sex", StringType)
        ))

        val file = Paths.get(Global.BASE_DIR, "data", "structuredStreaming").toAbsolutePath.toString
        val peopleDF: DataFrame = sparkSession
            .readStream
            .schema(schema)
            .json(file)

        // 转成 ds
        import sparkSession.implicits._
        val peopleDs: Dataset[People] = peopleDF.as[People]
        val df: Dataset[String] = peopleDs.filter(_.age > 20).map(_.name)

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

