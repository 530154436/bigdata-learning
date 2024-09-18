package org.zcb.spark.sql

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.avg
import org.zcb.spark.SparkGlobal

object example_calc_avg {
    /**
     * 题目：给定一组键值对("spark",2),("hadoop",6),("hadoop",4),("spark",6)，
     * 键值对的key表示图书名称，value表示某天图书销量，请计算每个键对应的平均值，也就是计算每种图书的每天平均销量。
     */
    def example_calc_avg(sparkSession: SparkSession): Unit = {
        val df = sparkSession.createDataFrame(
            Array(("sparkRDD", 2), ("hadoop", 6), ("hadoop", 4), ("sparkRDD", 6)))
            .toDF("book", "amount")
        df.show()

        val aggDF = df.groupBy("book").agg(avg("amount"))
        aggDF.show()
    }

    def main(args: Array[String]): Unit = {
        example_calc_avg(SparkGlobal.getSparkSession(this.getClass.getName))
    }
}
