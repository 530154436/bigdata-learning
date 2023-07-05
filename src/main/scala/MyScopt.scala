import scopt.OptionParser


/**
 Scala 支持使用多种方式解析命令行参数。
    1. 使用 Scala 自带的 args 数组：在 Scala 中，main 函数的参数是 String 类型的数组，你可以直接对这个数组进行操作。
    2. 使用第三方库：Scala 有很多第三方库可以帮助你解析命令行参数，例如 scopt 和 argonaut，支持选项、参数、帮助信息等。

 参数配置：-i C:\Users\chubin.zheng\JavaProjects\bigdata_learning\README.md
 */
object MyScopt {

    /***
     * 解析命令行参数
     */
    case class Config(input: String = "", output: String = "")
    val parser: OptionParser[Config] = new OptionParser[Config](this.getClass.getName) {
        head(this.getClass.getName)
        opt[String]('i', "input")
            .required()
            .valueName("<file>")
            .action((x, c) => c.copy(input = x))  // 定义参数被解析后的行为：当前的配置c 的 output 属性被设置为参数的值 x。
            .text("input is a required file property")
        opt[String]('o', "output")
            .optional()
            .valueName("<file>")
            .action((x, c) => c.copy(output = x))
            .text("output is an optional file property")
    }

    /***
     * 业务流程
     */
    def process(config: Config): Unit ={
        println(config)
    }

    /***
     * 主函数
     */
    def main(args: Array[String]): Unit = {

        // 使用模式匹配(match)来处理解析结果:
        // 如果解析成功，返回一个包含配置信息的 Some 对象，将其绑定到 config 变量中。
        // 如果解析失败，返回一个 None 对象。
        parser.parse(args, Config()) match {
            case Some(config) => process(config)
            case _ => throw new RuntimeException("params parse error.")
        }
    }
}