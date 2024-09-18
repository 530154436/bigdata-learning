package org.zcb.spark.streaming

import org.zcb.common.conf.Global
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.zcb.spark.SparkGlobal

import java.nio.file.Paths

/**
 *-> 先启动 ch01_2_套接字流_自定义数据源.scala, 在启动这个脚本
 */
object ch03_DStream输出到文本文件 {

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
        ssc.checkpoint(Paths.get(Global.BASE_DIR, "data", "spark", "checkpoint").toAbsolutePath.toString)
        val stateDStream: DStream[(String, Int)] = wordAndOne.updateStateByKey[Int](updateFunc(_, _))
        stateDStream.print()

        // 保存到文本文件(会保留每个批次的结果)
        val directory = Paths.get(Global.BASE_DIR, "data", "spark", "sparkStreaming", "output").toAbsolutePath
        stateDStream.saveAsTextFiles(s"file:///${directory}")

        ssc.start()
        // 用于让StreamingContext保持运行状态，直到手动停止或发生错误。
        ssc.awaitTermination()
    }
}

