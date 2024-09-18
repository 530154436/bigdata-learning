package org.zcb.spark.sql

import org.zcb.common.conf.Global
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}
import org.zcb.spark.SparkGlobal

import java.nio.file.Paths

/**
 *
 */
object ch03_从RDD转换得到df {

    case class Person(name: String, age: Long)

    def reflectToRdd(sparkSession: SparkSession): Unit = {
        // 导入包，支持把一个RDD隐式转换为一个DataFrame
        import sparkSession.implicits._

        // 利用反射机制推断RDD模式
        val path = Paths.get(Global.BASE_DIR, "data", "spark", "resources", "people.txt").toAbsolutePath.toString
        val df = sparkSession.sparkContext.textFile(path)
            .map(_.split(","))
            .map(attributes => Person(attributes(0), attributes(1).trim.toLong))
            .toDF()
        df.show()
    }

    def programToRdd(sparkSession: SparkSession): Unit = {
        // schema描述了模式信息，模式中包含name和age两个字段
        val fields = Array(
            StructField("name", StringType, nullable = true),
            StructField("age", LongType, nullable = true)
        )
        // shcema就是“表头”
        val schema = StructType(fields)

        // 每个Row对象都是rowRDD中的一行
        val path = Paths.get(Global.BASE_DIR, "data", "spark", "resources", "people.txt").toAbsolutePath.toString
        val peopleRDD = sparkSession.sparkContext.textFile(path)
        val rowRDD = peopleRDD.map(_.split(","))
            .map(attributes => Row(attributes(0), attributes(1).trim.toLong))
        rowRDD.foreach(println)

        //把“表头”和“表中的记录”拼装起来
        val peopleDF = sparkSession.createDataFrame(rowRDD, schema)
        peopleDF.show()
    }

    def main(args: Array[String]): Unit = {
        reflectToRdd(SparkGlobal.getSparkSession(this.getClass.getName))
        programToRdd(SparkGlobal.getSparkSession(this.getClass.getName))
    }
}
