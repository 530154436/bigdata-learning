package org.zcb.spark.streaming

import org.zcb.common.conf.Global
import org.apache.spark.SparkContext
import org.apache.spark.streaming.{Seconds, StreamingContext}

import java.nio.file.Paths

/**
 * (1)终端启动(windows): nc -l -p 9999
 * (2)在nc窗口中随意输入一些单词，监听窗口就会自动获得单词数据流信息，在监听窗口每隔5秒就会打印出词频统计信息。
 */
object ch01_2_套接字流 {
    val hostname = "127.0.0.1"
    val port = 9999

    def main(args: Array[String]): Unit = {
        val sparkConf = SparkGlobal.getSparkConf()
        val ssc = new StreamingContext(sparkConf, Seconds(5))  // 时间间隔为5秒
        ssc.sparkContext.setLogLevel("ERROR")

        // 统计词频
        val lines = ssc.socketTextStream(hostname, port)
        val wordCount = lines.flatMap(x => x.split(",")).map(x => (x, 1)).reduceByKey((x, y) => x + y)
        wordCount.print()

        ssc.start()
        // 用于让StreamingContext保持运行状态，直到手动停止或发生错误。
        ssc.awaitTermination()
    }
}

