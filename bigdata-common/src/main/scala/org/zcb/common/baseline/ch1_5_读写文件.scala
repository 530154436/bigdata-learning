package org.zcb.common.baseline

import java.io.PrintWriter
import scala.io.Source

object ch1_5_读写文件 {
    /**
     * 读写文件
     * 写入文件: Scala需要使用java.io.PrintWriter实现把数据写入到文件
     * 读取文件: 可以使用Scala.io.Source的getLines方法实现对文件中所有行的读取
     */
    def output(): Unit = {
        val writer = new PrintWriter("output.txt")
        println(writer.getClass)
        for (i <- 1 to 10) {
            writer.println(i)
            writer.flush()
        }
        writer.close()
    }

    def input(): Unit = {
        val reader = Source.fromFile("output.txt")
        println(reader.getClass)

        val lines = reader.getLines
        println(lines.getClass)

        for (line <- lines)
            println(line)
    }

    def main(args: Array[String]): Unit = {
        output()
        input()
    }
}
