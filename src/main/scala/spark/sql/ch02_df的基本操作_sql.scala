package spark.sql

import conf.{Global, SparkGlobal}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SparkSession}

import java.nio.file.Paths

/**
 * ### DataFrame的基本操作
 * #### DSL语法风格
 * `DSL`（Domain Specific Language）意为“领域专用语言”。<br>
 * DSL语法类似于RDD中的操作，允许开发者通过调用方法对DataFrame内部的数据进行分析。<br>
 * DataFrame创建好以后，可以执行一些常用的DataFrame操作，包括：<br>
 * printSchema()、show()、select()、filter()、groupBy()、sort()、withColumn()和drop()等。
 *
 * #### SQL语法风格
 * 熟练使用SQL语法的开发者，可以直接使用SQL语句进行数据操作。<br>
 * 相比于DSL语法风格，在执行SQL语句之前，需要通过DataFrame实例创建临时视图。<br>
 * 创建临时视图的方法是调用DataFrame实例的createTempView或createOrReplaceTempView方法，二者的区别是，后者会进行判断。<br>
 * + createOrReplaceTempView方法<br>
 * 如果在当前会话中存在相同名称的临时视图，则用新视图替换原来的临时视图
 * 如果在当前会话中不存在相同名称的临时视图，则创建临时视图。
 * + createTempView方法，如果在当前会话中存在相同名称的临时视图，则会直接报错。
 *
 */
object ch02_df的基本操作_sql {

    def basicOp(sparkSession: SparkSession): Unit = {
        val path = Paths.get(Global.BASE_DIR, "data", "resources", "people.json").toAbsolutePath.toString
        val df = sparkSession.read.format("json").load(path)

        df.createOrReplaceTempView("tmp_v_people")
        sparkSession.sql("SELECT * FROM tmp_v_people").show()
        sparkSession.sql("SELECT name FROM tmp_v_people WHERE age > 20").show()
        // +----+
        // |name|
        // +----+
        // |Andy|
        // +----+
    }

    /**
     * 假设在一张用户信息表中有name、age、create_time三列数据，这里要求使用Spark的系统函数from_unixtime，
     * 将时间戳类型的create_time格式化成时间字符串，然后，使用用户自定义函数将用户名转化为大写英文字母。
     */
    def example(sparkSession: SparkSession): Unit = {
        val schema = StructType(List(
            StructField("name", StringType, nullable = true),
            StructField("age", IntegerType, nullable = true),
            StructField("create_time", LongType, nullable = true)
        ))
        val javaList = new java.util.ArrayList[Row]()
        javaList.add(Row("Xiaomei", 21, System.currentTimeMillis() / 1000))
        javaList.add(Row("Xiaoming", 22, System.currentTimeMillis() / 1000))
        javaList.add(Row("Xiaoxue", 23, System.currentTimeMillis() / 1000))
        val df = sparkSession.createDataFrame(javaList, schema)

        df.createOrReplaceTempView("user_info")
        sparkSession.udf.register("toUpperCaseUDF", (column: String) => column.toUpperCase)
        sparkSession.sql(
            s"""
               |SELECT
               |    name
               |    , toUpperCaseUDF(name) AS upperName
               |    , age
               |    , from_unixtime(create_time,'yyyy-MM-dd HH:mm:ss') AS time
               |FROM user_info
               |""".stripMargin).show()
        // +--------+---------+---+-------------------+
        // |    name|upperName|age|               time|
        // +--------+---------+---+-------------------+
        // | Xiaomei|  XIAOMEI| 21|2023-07-19 08:09:47|
        // |Xiaoming| XIAOMING| 22|2023-07-19 08:09:47|
        // | Xiaoxue|  XIAOXUE| 23|2023-07-19 08:09:47|
        // +--------+---------+---+-------------------+
    }

    def main(args: Array[String]): Unit = {
        // basicOp(SparkGlobal.getSparkSession(this.getClass.getName))
        example(SparkGlobal.getSparkSession(this.getClass.getName))
    }
}
