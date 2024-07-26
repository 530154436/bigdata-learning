package org.zcb.common.baseline

import scala.io.StdIn.readInt

object ch2_1_if条件表达式 {
    /**
     * 有一点与Java不同的是，Scala中的if表达式的值可以赋值给变量
     */
    def main(args: Array[String]): Unit = {
        val x = readInt()
        if (x > 0) {
            println("This is a positive number.")
        } else if (x == 0) {
            println("This is a zero.")
        } else {
            println("This is a negative number.")
        }

        val y = readInt()
        val a = if (y > 0) y else -1
        println(a)
    }
}
