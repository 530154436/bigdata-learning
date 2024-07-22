package spark.structured_streaming

import conf.Global
import org.apache.spark.sql.{DataFrame, Dataset}
import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}

import java.nio.file.Paths


/**
 * 在 DF/DS 上大多数通用操作都支持作用在 Streaming DataFrame/Streaming DataSet 上
 */
object ch0301_基本操作_直接执行sql {
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

        // 创建临时表
        peopleDF.createTempView("people")
        val df: DataFrame = sparkSession.sql("SELECT * FROM people WHERE age > 20")

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

