package org.zcb.flink.baseline

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.zcb.common.conf.Global

import java.nio.file.Paths


/**
 * 流处理-单词频次统计
 * 基本思路是：先逐行读入文件数据，然后将每行文字都拆分成单词，接着按照单词分组，统计每组数据的个数，就是对应单词的频次。
 *
 * socket文本流主机名和端口号：
 *  windows：nc -L -p 7777
 *  linux：nc -l -p 7777
 */
object ch02_03_StreamUnbounded {
    val file: String = Paths.get(Global.BASE_DIR, "data", "flink", "input", "words.txt").toAbsolutePath.toString
    private val hostName: String = "hadoop103"
    private val port: Int = 7777

    def main(args: Array[String]): Unit = {
        // 创建执行环境对象并配置并行度（获取执行环境对象）
        val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

        // 读取文本文件
        val lineDs: DataStream[String] = env.socketTextStream(hostName, port)

        // 格式转换
        val sum: DataStream[(String, Int)] = lineDs
            .flatMap(x => x.split(" "))
            .map(x => (x, 1))
            .keyBy(x => x._1)
            .sum(1)

        // 3> (hello,1)
        // 1> (scala,1)
        // 7> (flink,1)
        // 5> (world,1)
        // 3> (hello,2)
        // 3> (hello,3)
        sum.print()

        // 执行任务：数据逐个处理，每来一条数据就会处理输出一次
        env.execute()
    }
}
