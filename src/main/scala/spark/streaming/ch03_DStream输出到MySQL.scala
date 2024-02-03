package spark.streaming

import conf.{Global, SparkGlobal}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import java.nio.file.Paths
import java.sql.{Connection, DriverManager, PreparedStatement}

/**
 * 新建MySQL数据库和表
 * use spark;
 * create table wordcount (word char(20), count int(4), PRIMARY KEY (word));
 *
 *-> 先启动 ch01_2_套接字流_自定义数据源.scala, 在启动这个脚本
 */
object ch03_DStream输出到MySQL {

    def main(args: Array[String]): Unit = {
        val ssc = new StreamingContext(SparkGlobal.getSparkConf(), Seconds(4))
        ssc.sparkContext.setLogLevel("ERROR")
        // 参考updateStateByKey
        val lines: ReceiverInputDStream[String] = ssc.socketTextStream(ch01_2_套接字流.hostname, ch01_2_套接字流.port)
        val wordAndOne: DStream[(String, Int)] = lines.flatMap(_.split(",")).map((_, 1))
        def updateFunc(values: Seq[Int], state: Option[Int]): Option[Int] = {
            val currentCount = values.sum
            val previousCount = state.getOrElse(0)
            Some(currentCount + previousCount)
        }
        ssc.checkpoint(Paths.get(Global.BASE_DIR, "data", "checkpoint").toAbsolutePath.toString)
        val stateDStream: DStream[(String, Int)] = wordAndOne.updateStateByKey[Int](updateFunc(_, _))
        stateDStream.print()

        // 保存到MySQL数据库
        stateDStream.foreachRDD(rdd => {
            // 内部函数
            def write(records: Iterator[(String, Int)]): Unit ={
                var conn: Connection = null
                var preparedStatement: PreparedStatement = null
                try {
                    val url = "jdbc:mysql://localhost:3306/spark?useUnicode=true&characterEncoding=utf8&useSSL=false"
                    val user = "root"
                    val password = "123456"
                    conn = DriverManager.getConnection(url, user, password)

                    //val sql = "insert into wordcount(word,count) values (?,?)"
                    val sql = "INSERT INTO wordcount (word, count) VALUES (?, ?) ON DUPLICATE KEY UPDATE count = VALUES(count);"
                    preparedStatement = conn.prepareStatement(sql)
                    records.foreach(p => {
                        preparedStatement.setString(1, p._1.trim)
                        preparedStatement.setInt(2, p._2.toInt)
                        preparedStatement.addBatch()
                    })
                    preparedStatement.executeBatch()
                } catch {
                    case e: Exception => e.printStackTrace()
                } finally {
                    if(preparedStatement != null) {
                        preparedStatement.close()
                    }
                    if(conn != null) {
                        conn.close()
                    }
                }
            }

            // 遍历分区
            val repartitionedRDD = rdd.repartition(3)
            repartitionedRDD.foreachPartition(write)
        })

        ssc.start()
        // 用于让StreamingContext保持运行状态，直到手动停止或发生错误。
        ssc.awaitTermination()
    }
}

