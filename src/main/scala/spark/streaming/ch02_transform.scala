package spark.streaming

import conf.{Global, SparkGlobal}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import java.nio.file.Paths

/**
 * `transform`(func)：通过对源DStream的每个RDD应用RDD-to-RDD函数，创建一个新的DStream。支持在新的DStream中做任何RDD操作
 */
object ch02_transform {

    def main(args: Array[String]): Unit = {
        val ssc = new StreamingContext(SparkGlobal.getSparkConf(), Seconds(3))  // 时间间隔为5秒
        ssc.sparkContext.setLogLevel("ERROR")
        val directory = Paths.get(Global.BASE_DIR, "data", "wordcount").toAbsolutePath

        // "_"是一个占位符，用于表示未命名的参数或变量。
        val lines: ReceiverInputDStream[String] = ssc.socketTextStream(ch01_2_套接字流.hostname, ch01_2_套接字流.port)
        val wordCount = lines.transform(rdd => {
            rdd.flatMap(_.split(",")).map((_, 1)).reduceByKey(_ + _)
        })
        wordCount.print()

        ssc.start()
        // 用于让StreamingContext保持运行状态，直到手动停止或发生错误。
        ssc.awaitTermination()
    }
}

