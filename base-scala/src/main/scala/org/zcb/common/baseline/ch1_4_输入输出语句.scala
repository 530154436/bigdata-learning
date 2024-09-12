package org.zcb.common.baseline

object ch1_4_输入输出语句 {
    /**
     * 控制台输入输出语句
     * import scala.io.StdIn._: 下划线表示导入所有的包
     *
     * 1. 为了从控制台读写数据，可以使用以read为前缀的方法，
     * 包括:readInt、readDouble、readByte、readShort、
     * readFloat、readLong、readChar、readBoolean及 readLine，
     * 分别对应9种基本数据类型，其中前8种方法没有参数，readLine可以不提供参数，也可以带一个字符串参数的提示。
     * 所有这些函数都属于对象scala.io.StdIn的方法，使用前必须导入，或者直接用全称进行调用。
     *
     * 2. 为了向控制台输出信息，常用的两个函数是print()和println()、printf()， 可以直接输出字符串或者其它数据类型
     */
    def main(args: Array[String]): Unit = {
        val i = readLine("请输入你的姓名:")

        print("你的姓名是: %s\n".format(i))
        printf("你的资产是: %d 元\n", 100000)
        println("再见.")
    }
}
