package org.zcb.spark.sql

import org.zcb.common.conf.Global
import org.apache.spark.sql.SparkSession
import org.zcb.spark.SparkGlobal

import java.nio.file.Paths

/**
 * ### 存储文件格式
 * #### Parquet
 * Parquet是Spark的默认数据源，很多大数据处理框架和平台都支持Parquet格式，它是一种开源的列式存储文件格式，提供多种I/O优化措施。<br>
 * 比如压缩，以节省存储空间，支持快速访问数据列；存储Parquet文件的目录中包含了`_SUCCESS文件`和很多像`part-XXXXX`这样的压缩文件。<br>
 *
 * #### JSON
 * JSON（JavaScript Object Notation）是一种常见的数据格式，与XML相比，JSON的可读性更强，更容易解析。<br>
 * JSON有两种表示格式，即`单行模式`和`多行模式`，这两种模式Spark都支持。<br>
 *
 *
 */
object ch01_df的创建和保存 {

    def read_save_parquet(sparkSession: SparkSession): Unit = {
        val df = sparkSession.createDataFrame(
            Array(("sparkRDD", 2), ("hadoop", 6), ("hadoop", 4), ("sparkRDD", 6)))
            .toDF("book", "amount")
        val path = Paths.get(Global.BASE_DIR, "data", "output", "parquet")

        // 将DataFrame保存为Parquet文件
        df.write.format("parquet")
            .mode("overwrite")
            .option("compression", "snappy") // snappy压缩算法
            // .save("org.zcb.hadoop.hdfs://127.0.0.1:9000/spark/parquet")   // 存储到hdfs
            .save(s"file://${path}") // 存储到文件系统

        // 读取Parquet文件
        val df_read = sparkSession.read
            .parquet(s"file://${path}")
        df_read.show()
    }

    def read_save_json(sparkSession: SparkSession): Unit = {
        val path = Paths.get(Global.BASE_DIR, "data", "resources", "people.json").toAbsolutePath
        val otherPath = Paths.get(Global.BASE_DIR, "data", "resources", "otherPeople.json").toAbsolutePath

        // 从JSON文件创建DataFrame
        val df = sparkSession.read.format("json").load(path.toString)
        // val df = sparkSession.read.json(path.toString)
        df.show()

        // 将DataFrame保存为JSON文件
        df.write.format("json")
            .mode("overwrite")
            .save(s"file://${otherPath}")
    }

    def read_save_csv(sparkSession: SparkSession): Unit = {
        val path = Paths.get(Global.BASE_DIR, "data", "resources", "people.csv").toAbsolutePath
        val otherPath = Paths.get(Global.BASE_DIR, "data", "resources", "otherPeople.csv").toAbsolutePath
        val schema = "name STRING,age INT,job STRING"

        // 从CSV文件创建DataFrame
        val df = sparkSession.read.format("csv")
            .schema(schema) // 用于设置每行数据的模式，也就是每行记录包含哪些字段，每个字段是什么数据类型
            .option("header", "true") // 用于表明这个CSV文件是否包含表头
            .option("sep", ",") // 用于表明这个CSV文件中字段之间使用的分割符是分号，默认使用逗号作为分隔符
            .load(path.toString)
        df.show()

        // 将DataFrame保存为JSON文件
        df.write.format("json")
            .mode("overwrite")
            .save(s"file://${otherPath}")
    }

    def read_save_text(sparkSession: SparkSession): Unit = {
        val path = Paths.get(Global.BASE_DIR, "data", "resources", "word.txt").toAbsolutePath
        val otherPath = Paths.get(Global.BASE_DIR, "data", "resources", "otherWord").toAbsolutePath

        // 从文本文件创建DataFrame
        val df = sparkSession.read.format("text").load(path.toString)
        df.show()

        // 把DataFrame保存成文本文件
        df.write.format("text").save(s"file:///${otherPath}")
    }

    def main(args: Array[String]): Unit = {
        // read_save_parquet(SparkGlobal.getSparkSession(this.getClass.getName))
        // read_save_json(SparkGlobal.getSparkSession(this.getClass.getName))
        // read_save_csv(SparkGlobal.getSparkSession(this.getClass.getName))
        read_save_text(SparkGlobal.getSparkSession(this.getClass.getName))
    }
}
