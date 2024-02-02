package spark.streaming
import conf.{Global, SparkGlobal}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import java.nio.file.Paths

/**
 * (1)启动应用后进程进入监听状态（我们把运行这个监听程序的窗口称为监听窗口）
 * (2)在 ${directory} 目录下新建一个log2.txt文件，文件里面随便输入一些单词，保存好文件
 * (3)再次切换回“监听窗口”，最多等待5秒以后，就可以看到监听窗口的屏幕上会打印出单词统计信息
 */
object ch01_1_文件流 {

    def main(args: Array[String]): Unit = {
        val sparkConf = SparkGlobal.getSparkConf()
        val ssc = new StreamingContext(sparkConf, Seconds(5))  // 时间间隔为5秒

        // 监听本地目录:
        val directory = Paths.get(Global.BASE_DIR, "data", "resources").toAbsolutePath
        val lines: DStream[String]  = ssc.textFileStream(s"file:///${directory}")

        // 统计词频
        val words: DStream[String] = lines.flatMap(x => x.split(","))
        val wordCount: DStream[(String, Int)] = words.map(x => (x, 1)).reduceByKey((x, y) => x + y)
        wordCount.print()

        ssc.start()
        // 用于让StreamingContext保持运行状态，直到手动停止或发生错误。
        ssc.awaitTermination()
    }
}

