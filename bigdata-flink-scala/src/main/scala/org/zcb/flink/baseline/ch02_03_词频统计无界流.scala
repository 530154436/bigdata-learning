package org.zcb.flink.baseline


/**
 * 流处理-单词频次统计
 * 基本思路是：先逐行读入文件数据，然后将每行文字都拆分成单词，接着按照单词分组，统计每组数据的个数，就是对应单词的频次。
 *
 * socket文本流主机名和端口号：
 *  windows：nc -L -p 7777
 *  linux：nc -l -p 7777
 */
object ch02_03_词频统计无界流 {
    def main(args: Array[String]): Unit = {
        ch02_03_StreamUnbounded.doRun()
    }
}
