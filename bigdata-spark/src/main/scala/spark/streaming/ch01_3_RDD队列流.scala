package spark.streaming

import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable

/**
 * 在调试Spark Streaming应用程序的时候，我们可以使用streamingContext.queueStream(queueOfRDD)创建基于RDD队列的DStream
 * 功能是：每隔1秒创建一个RDD，Streaming每隔2秒就对数据进行处理
 *
 * 通过使用ssc.queueStream(queueOfRDDs)来创建DStream，每一个推送到这个队列中的RDD，都会作为一个DStream处理。
 */
object ch01_3_RDD队列流 {
    def main(args: Array[String]): Unit ={
        val sparkConf = SparkGlobal.getSparkConf()
        val ssc = new StreamingContext(sparkConf, Seconds(2))  // 时间间隔为5秒
        ssc.sparkContext.setLogLevel("ERROR")

        // 消费者
        val rddQueue = new mutable.SynchronizedQueue[RDD[Int]]()  // 同步的队列
        val queueStream = ssc.queueStream(rddQueue)
        val mappedStream = queueStream.map(r => (r % 10, 1))
        val reducedStream = mappedStream.reduceByKey(_ + _)

        reducedStream.print()
        ssc.start()

        // 生产者
        for (i <- 1 to 10){
            rddQueue += ssc.sparkContext.makeRDD(1 to 100,2)
            Thread.sleep(5000)
        }
        ssc.stop()
    }
}

