package spark.sql

import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}

import java.util.Properties

/**
 *
 */
object ch04_读写数据库_mysql {

    def read(sparkSession: SparkSession): Unit = {
        val jdbcDF = sparkSession.read
            .format("jdbc")
            .option("url", "jdbc:mysql://localhost:3306/spark")
            .option("driver", "com.mysql.jdbc.Driver")
            .option("dbtable", "student")
            .option("user", "root")
            .option("password", "123456")
            .load()
        jdbcDF.show()
    }

    def insert(sparkSession: SparkSession): Unit = {
        val arr = Array("5 Chubin M 26", "6 Xinxin M 27")

        // 设置模式信息
        val fields = List(
            StructField("id", IntegerType, nullable = true),
            StructField("name", StringType, nullable = true),
            StructField("gender", StringType, nullable = true),
            StructField("age", IntegerType, nullable = true)
        )
        val schema = StructType(fields)

        // 每个Row对象都是rowRDD中的一行
        val studentRDD = sparkSession.sparkContext
            .parallelize(arr)
            .map(_.split(" "))
            .map(p => Row(p(0).toInt, p(1).trim, p(2).trim, p(3).toInt))

        // 建立起Row对象和模式之间的对应关系，也就是把数据和模式对应起来
        val studentDF = sparkSession.createDataFrame(studentRDD, schema)

        // 创建一个prop变量用来保存JDBC连接参数
        val prop = new Properties()
        prop.put("user", "root") // 表示用户名是root
        prop.put("password", "123456") // 表示密码是123456
        prop.put("driver", "com.mysql.jdbc.Driver") // 表示驱动程序是com.mysql.jdbc.Driver

        studentDF.write
            .mode("append")
            .jdbc("jdbc:mysql://localhost:3306/spark", "spark.student", prop)
    }

    def main(args: Array[String]): Unit = {
        insert(SparkGlobal.getSparkSession(this.getClass.getName))
        read(SparkGlobal.getSparkSession(this.getClass.getName))
    }
}
