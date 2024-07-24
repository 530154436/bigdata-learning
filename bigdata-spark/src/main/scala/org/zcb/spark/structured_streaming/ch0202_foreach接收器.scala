package org.zcb.spark.structured_streaming

import org.zcb.common.conf.Global
import org.apache.spark.sql.{DataFrame, ForeachWriter, Row}
import org.apache.spark.sql.streaming.{StreamingQuery, Trigger}
import org.zcb.spark.SparkGlobal
import org.zcb.spark.streaming.ch01_2_套接字流_自定义数据源

import java.nio.file.Paths
import java.sql.{Connection, DriverManager, PreparedStatement}


/**
 Foreach Sink：会遍历表中的每一行, 允许将流查询结果按开发者指定的逻辑输出.

 -> 先启动 ch01_2_套接字流_自定义数据源.scala, 在启动这个脚本
 */
object ch0202_foreach接收器 {
    def main(args: Array[String]): Unit = {
        val sparkSession = SparkGlobal.getSparkSession(name = "StructuredStreamingSink")
        sparkSession.sparkContext.setLogLevel("WARN")

        val lines = sparkSession
            .readStream
            .format("socket")
            .option("host", ch01_2_套接字流_自定义数据源.hostname)
            .option("port", ch01_2_套接字流_自定义数据源.port)
            .load()

        // 定义完查询语句: 分组计数 [value: string]
        import sparkSession.implicits._
        val wordCount: DataFrame = lines.as[String]
            .flatMap(_.split(","))
            .groupBy("value")
            .count()

        // 启动流计算并输出结果
        val query: StreamingQuery = wordCount.writeStream
            .outputMode("update")

            // 使用 foreach 的时候, 需要传递ForeachWriter实例, 三个抽象方法需要实现.
            // 每个批次的所有分区都会创建 ForeeachWriter 实例
            .foreach(new ForeachWriter[Row] {
                var conn: Connection = _
                var preparedStatement: PreparedStatement = _
                var batchCount = 0

                val url = "jdbc:mysql://localhost:3306/spark?useUnicode=true&characterEncoding=utf8&useSSL=false"
                val user = "root"
                val password = "123456"

                // 一般用于打开链接. 返回 false 表示跳过该分区的数据,
                override def open(partitionId: Long, epochId: Long): Boolean = {
                    conn = DriverManager.getConnection(url, user, password)
                    // 插入数据, 当有重复的 key 的时候更新
                    val sql = "INSERT INTO wordcount (word, count) VALUES (?, ?) ON DUPLICATE KEY UPDATE count = VALUES(count);"
                    preparedStatement = conn.prepareStatement(sql)
                    conn != null && !conn.isClosed && preparedStatement != null
                }

                // 把数据写入到连接
                override def process(value: Row): Unit = {
                    val word: String = value.getString(0)
                    val count: Long = value.getLong(1)
                    preparedStatement.setString(1, word)
                    preparedStatement.setLong(2, count)
                    preparedStatement.execute()
                }

                // 用户关闭连接
                override def close(errorOrNull: Throwable): Unit = {
                    preparedStatement.close()
                    conn.close()
                }
            })
            .start

        query.awaitTermination()
        sparkSession.stop()
    }
}

