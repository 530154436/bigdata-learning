package org.zcb.flink.baseline

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, KeyedStream, StreamExecutionEnvironment}
import org.zcb.common.conf.Global
import java.nio.file.Paths


/**
 * 批处理-单词频次统计
 * 基本思路是：先逐行读入文件数据，然后将每行文字都拆分成单词，接着按照单词分组，统计每组数据的个数，就是对应单词的频次。
 */
object ch02_02_BoundedStreamWordCount {
    val file: String = Paths.get(Global.BASE_DIR, "data", "flink", "input", "words.txt").toAbsolutePath.toString

    def main(args: Array[String]): Unit = {
        // 创建执行环境对象并配置并行度（获取执行环境对象）
        val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

        // 读取文本文件
        val lineDs: DataStream[String] = env.readTextFile(file)

        // 格式转换
        val wordAndOne: DataStream[(String, Int)] = lineDs.flatMap(x => x.split(" ")).map(x => (x, 1))

        // 分组： 传入一个匿名函数作为键选择器(KeySelector)，指定当前分组的key是什么。
        val wordGroup: KeyedStream[(String, Int), String]  = wordAndOne.keyBy(x => x._1)

        // 聚合
        val sum = wordGroup.sum(1)

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
