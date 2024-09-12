package org.zcb.common.baseline

import java.io.{FileNotFoundException, FileReader, IOException}

object ch1_6_异常处理 {
    /**
     * Scala不支持Java中的“受检查异常”(checked exception)， 将所有异常都当作“不受检异常”(或称为运行时异常)
     * Scala仍使用try-catch结构来捕获异常
     */
    def main(args: Array[String]): Unit = {
        var file: FileReader = null
        try {
            file = new FileReader("input.txt")
        } catch {
            case e: FileNotFoundException => e.printStackTrace()
            case e: IOException => e.printStackTrace()
        }
    }
}
