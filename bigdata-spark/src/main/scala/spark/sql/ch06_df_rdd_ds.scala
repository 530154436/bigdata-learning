package spark.sql

import conf.Global
import org.apache.spark.sql.SparkSession

import java.nio.file.Paths

/**
 *
 */
object ch06_df_rdd_ds {

    case class Person(name: String, age: Int)

    /**
     * RDD和DataFrame之间的转换
     */
    def rdd_df(sparkSession: SparkSession): Unit = {
        // 从DataFrame到RDD的转换，需要调用DataFrame上的rdd方法。
        val path1 = Paths.get(Global.BASE_DIR, "data", "resources", "people.json").toAbsolutePath
        val peopleDF = sparkSession.read.json(path1.toString)
        val peopleRDD = peopleDF.rdd
        peopleRDD.foreach(println)
    }

    /**
     * RDD和DataSet之间的转换
     */
    def rdd_ds(sparkSession: SparkSession): Unit = {
        import sparkSession.implicits._
        val path = Paths.get(Global.BASE_DIR, "data", "resources", "people.txt").toAbsolutePath
        val rdd = sparkSession.sparkContext
            .textFile(path.toString)
            .map(_.split(","))
            .map(attributes => Person(attributes(0), attributes(1).trim.toInt))
        val ds = rdd.toDS()
        ds.show()

        val anotherPeopleRDD = ds.rdd
        println(anotherPeopleRDD.collect().mkString("Array(", ", ", ")"))
    }

    /**
     * DataFrame和DataSet之间的转换
     */
    def df_ds(sparkSession: SparkSession): Unit = {
        import sparkSession.implicits._

        val data = List(
            Person("ZhangSan", 23),
            Person("LiSi", 35)
        )
        val peopleDS = data.toDS
        peopleDS.show()

        val peopleDF = peopleDS.toDF
        val anotherPeopleDS = peopleDF.as[Person]
        peopleDF.show()
        anotherPeopleDS.show()
    }

    def main(args: Array[String]): Unit = {
        rdd_df(SparkGlobal.getSparkSession(this.getClass.getName))
        rdd_ds(SparkGlobal.getSparkSession(this.getClass.getName))
        df_ds(SparkGlobal.getSparkSession(this.getClass.getName))
    }
}
