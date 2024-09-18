package org.zcb.spark.sql

import org.zcb.common.conf.Global
import org.apache.spark.sql.SparkSession
import org.zcb.spark.SparkGlobal

import java.nio.file.Paths

/**
 *
 */
object ch05_DataSet {

    case class Person(name: String, age: Int)

    def create(sparkSession: SparkSession): Unit = {
        import sparkSession.implicits._

        // 使用createDataset方法创建
        val ds = sparkSession.createDataset(1 to 5)
        ds.show()

        val path = Paths.get(Global.BASE_DIR, "data", "spark", "resources", "people.txt").toAbsolutePath
        val ds1 = sparkSession.createDataset(sparkSession.sparkContext.textFile(path.toString))
        ds1.show()
        // +-----------+
        // |      value|
        // +-----------+
        // |Michael, 29|
        // |   Andy, 30|
        // | Justin, 19|
        // +-----------+

        // 通过toDS方法生成DataSet
        val data = List(Person("ZhangSan", 23), Person("LiSi", 35))
        val ds3 = data.toDS
        ds3.show()

        // 通过DataFrame转化生成DataSet
        val path1 = Paths.get(Global.BASE_DIR, "data", "spark", "resources", "people.json").toAbsolutePath
        val peopleDF = sparkSession.read.json(path1.toString)
        val ds4 = peopleDF.as[Person]
        ds4.show()
    }

    def main(args: Array[String]): Unit = {
        create(SparkGlobal.getSparkSession(this.getClass.getName))
    }
}
