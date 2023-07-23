package sparkSQL

import conf.SparkGlobal
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SparkSession}

import java.util.Properties

/**
 *
 */
object ch05_DataSet {

    def create(sparkSession: SparkSession): Unit = {

    }

    def main(args: Array[String]): Unit = {
        create(SparkGlobal.getSparkSession(this.getClass.getName))
    }
}
