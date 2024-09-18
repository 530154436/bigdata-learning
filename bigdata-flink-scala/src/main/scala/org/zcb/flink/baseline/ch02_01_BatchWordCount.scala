package org.zcb.flink.baseline

import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment, createTypeInformation}
import org.zcb.common.conf.Global

import java.nio.file.Paths


/**
 * 批处理-单词频次统计
 * 基本思路是：先逐行读入文件数据，然后将每行文字都拆分成单词，接着按照单词分组，统计每组数据的个数，就是对应单词的频次。
 */
object ch02_01_BatchWordCount {
    val file: String = Paths.get(Global.BASE_DIR, "data", "flink", "input", "words.txt").toAbsolutePath.toString

    def main(args: Array[String]): Unit = {
        // 创建执行环境对象并配置并行度（获取执行环境对象）
        val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment

        // 读取文本文件
        val lineDs: DataSet[String] = env.readTextFile(file)

        // 格式转换
        val wordAndOne: DataSet[(String, Int)] = lineDs.flatMap(x => x.split(" ")).map(x => (x, 1))

        // 分组： 在分组时调用了groupBy()方法，它不能使用分组选择器，只能采用位置索引或类属性名称进行分组。
        val wordGroup = wordAndOne.groupBy(0)

        // 聚合： 在分组时调用了groupBy()方法，它不能使用分组选择器，只能采用位置索引或类属性名称进行分组。
        val sum = wordGroup.sum(1)

        // (scala,1)
        // (flink,1)
        // (world,1)
        // (hello,3)
        sum.print()
    }
}
