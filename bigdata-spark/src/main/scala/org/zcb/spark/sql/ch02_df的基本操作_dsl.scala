package org.zcb.spark.sql

import org.zcb.common.conf.Global
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions

import java.nio.file.Paths

/**
 * ### DataFrame的基本操作
 * #### DSL语法风格
 * `DSL`（Domain Specific Language）意为“领域专用语言”。<br>
 * DSL语法类似于RDD中的操作，允许开发者通过调用方法对DataFrame内部的数据进行分析。<br>
 * DataFrame创建好以后，可以执行一些常用的DataFrame操作，包括：<br>
 * printSchema()、show()、select()、filter()、groupBy()、sort()、withColumn()和drop()等。
 *
 *
 */
object ch02_df的基本操作_dsl {

    def basicOp(sparkSession: SparkSession): Unit = {
        val path = Paths.get(Global.BASE_DIR, "data", "resources", "people.json").toAbsolutePath

        // 从JSON文件创建DataFrame
        val df = sparkSession.read.format("json").load(path.toString)

        df.printSchema()
        //root
        //  |-- age: long (nullable = true)
        //  |-- name: string (nullable = true)

        df.show()
        // +----+-------+
        // | age|   name|
        // +----+-------+
        // |null|Michael|
        // |  30|   Andy|
        // |  19| Justin|
        // +----+-------+

        df.select(df("name"), df("age") + 1).show()
        df.filter(df("age") > 20).show()
        df.groupBy("age").count().show()
        df.sort(df("age").desc).show()
        df.sort(df("age").desc, df("name").asc).show()

        // withColumn
        val df2 = df.withColumn("IfWithAge", functions.expr("if(age is null, 'No', 'Yes')"))
        df2.show()
        // +----+-------+---------+
        // | age|   name|IfWithAge|
        // +----+-------+---------+
        // |null|Michael|       No|
        // |  30|   Andy|      Yes|
        // |  19| Justin|      Yes|
        // +----+-------+---------+

        val df3 = df2.drop("IfWithAge")
        df3.show()

        // 其他常用操作
        df.select(
            functions.sum("age"),
            functions.avg("age"),
            functions.min("age"),
            functions.max("age"))
            .show()
        // +--------+--------+--------+--------+
        // |sum(age)|avg(age)|min(age)|max(age)|
        // +--------+--------+--------+--------+
        // |      49|    24.5|      19|      30|
        // +--------+--------+--------+--------+
    }

    def main(args: Array[String]): Unit = {
        basicOp(SparkGlobal.getSparkSession(this.getClass.getName))
    }
}
