package spark.streaming

import conf.Global
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import java.nio.file.Paths

/**
 *词频统计实例：
 * 对于有状态转换操作而言，本批次的词频统计，会在之前批次的词频统计结果的基础上进行不断累加，所以，最终统计得到的词频，是所有批次的单词的总的词频统计结果。
 *
 * -> 先启动 ch01_2_套接字流_自定义数据源.scala, 在启动这个脚本
 */
object ch02_updateStateByKey {

    def main(args: Array[String]): Unit = {
        val ssc = new StreamingContext(SparkGlobal.getSparkConf(), Seconds(4))
        ssc.sparkContext.setLogLevel("ERROR")

        // 套接字流
        val lines: ReceiverInputDStream[String] = ssc.socketTextStream(ch01_2_套接字流.hostname, ch01_2_套接字流.port)
        val wordAndOne: DStream[(String, Int)] = lines.flatMap(_.split(",")).map((_, 1))

        /*
        1. 定义状态: 每个单词的个数就是我们需要更新的状态
        2. 状态更新函数. 每个key(word)上使用一次更新新函数
            参数1: 在当前阶段 一个新的key对应的value组成的序列  在我们这个案例中是: 1,1,1,1...
            参数2: 上一个阶段 这个key对应的value
         */
        def updateFunc(values: Seq[Int], state: Option[Int]): Option[Int] = {
            val currentCount = values.sum
            val previousCount = state.getOrElse(0)
            Some(currentCount + previousCount)
        }

        // invReduceFunc 需设置检查点目录，不然报错
        ssc.checkpoint(Paths.get(Global.BASE_DIR, "data", "checkpoint").toAbsolutePath.toString)
        val wordCount: DStream[(String, Int)] = wordAndOne.updateStateByKey[Int](updateFunc(_, _))
        wordCount.print()

        ssc.start()
        // 用于让StreamingContext保持运行状态，直到手动停止或发生错误。
        ssc.awaitTermination()
    }
}

