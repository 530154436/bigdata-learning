package org.zcb.common.utils

import scopt.OptionParser

/**
 * Scala 支持使用多种方式解析命令行参数。
 * 1. 使用 Scala 自带的 args 数组：在 Scala 中，main 函数的参数是 String 类型的数组，你可以直接对这个数组进行操作。
 * 2. 使用第三方库：Scala 有很多第三方库可以帮助你解析命令行参数，例如 scopt 和 argonaut，支持选项、参数、帮助信息等。
 *
 * 参数配置：-i C:\Users\chubin.zheng\JavaProjects\bigdata_learning\Spark基础01-设计与运行原理.md
 */
object CmdOptionParser {

    /** *
     * 解析命令行参数
     */
    case class Config(input: String = "", output: String = "")
    val parser: OptionParser[Config] = new OptionParser[Config](this.getClass.getName) {
        head(this.getClass.getName)
        opt[String]('i', "input")
          .required()
          .valueName("<file>")
          .action((x, c) => c.copy(input = x)) // 定义参数被解析后的行为：当前的配置c 的 output 属性被设置为参数的值 x。
          .text("input is a required file property")
        opt[String]('o', "output")
          .optional()
          .valueName("<file>")
          .action((x, c) => c.copy(output = x))
          .text("output is an optional file property")
    }

    /**
     * 正确解析配置后的的处理逻辑
     * @param config 配置信息
     */
    private def process(config: Config): Unit = {
        println(s"输入文件: ${config.input}")
        println(s"输出文件: ${config.output}")
    }

    /**
     *
     * 解析参数的入口方法
     * @param args 入参
     * @return
     */
    def parseArgs(args: Array[String]): Option[Config] = {
        val config: Option[Config]  = parser.parse(args, Config())
        config match {
            case Some(config) => process(config) // 如果解析成功，调用 process 方法处理配置
            case None => println("参数解析失败，请检查输入") // 解析失败的情况
        }
        config
    }
}
