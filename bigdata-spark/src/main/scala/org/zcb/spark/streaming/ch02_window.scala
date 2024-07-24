package org.zcb.spark.streaming

import org.zcb.common.conf.Global
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import java.nio.file.Paths

/**
 **滑动窗口转换操作**
    1. 事先设定一个`滑动窗口的长度`（也就是窗口的持续时间）
    2. 设定滑动窗口的时间间隔（每隔多长时间执行一次计算），让窗口按照`指定时间间隔`在源DStream上滑动
    3. 每次窗口停放的位置上，都会有一部分Dstream（或者一部分RDD）被框入窗口内，形成一个小段的Dstream，可以启动对这个小段DStream的计算。<br>
       即一个窗口可以包含多个时间段，通过整合多个批次的结果，计算出整个窗口的结果。

    -> 先启动 ch01_2_套接字流_自定义数据源.scala, 在启动这个脚本
 */
object ch02_window {

    def main(args: Array[String]): Unit = {
        val ssc = new StreamingContext(SparkGlobal.getSparkConf(), Seconds(5))
        ssc.sparkContext.setLogLevel("ERROR")

        // 套接字流
        val lines: ReceiverInputDStream[String] = ssc.socketTextStream(ch01_2_套接字流.hostname, ch01_2_套接字流.port)
        val wordAndOne: DStream[(String, Int)] = lines.flatMap(_.split(",")).map((_, 1))

        /*
        参数1: reduce 计算规则
        [参数: invReduceFunc 计算规则]
        参数2: 窗口长度
        参数3: 窗口滑动步长. 每隔这么长时间计算一次.

        比没有invReduceFunc高效. 会利用旧值来进行计算.
        invReduceFunc: (V, V) => V 窗口移动了, 上一个窗口和新的窗口会有重叠部分, 重叠部分的值可以不用重复计算了.
                                   第一个参数就是新的值, 第二个参数是旧的值.
         */
        def reduceFunc(x: Int, y: Int): Int = {x + y}
        //val wordCount: DStream[(String, Int)] = wordAndOne
        //    .reduceByKeyAndWindow(reduceFunc, windowDuration=Seconds(15), slideDuration=Seconds(10))

        // invReduceFunc 需设置检查点目录，不然报错
        ssc.checkpoint(Paths.get(Global.BASE_DIR, "data", "checkpoint").toAbsolutePath.toString)
        val wordCount: DStream[(String, Int)] = wordAndOne
            .reduceByKeyAndWindow(reduceFunc, (x: Int, y: Int) => x - y, Seconds(15), Seconds(10))
        wordCount.print()

        ssc.start()
        // 用于让StreamingContext保持运行状态，直到手动停止或发生错误。
        ssc.awaitTermination()
    }
}

