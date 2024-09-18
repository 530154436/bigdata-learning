import org.junit.jupiter.api.{Assertions, DisplayName, Test}
import org.zcb.common.utils.CmdOptionParser
import scopt.OptionParser

/**
 * Scala 支持使用多种方式解析命令行参数。
 * 1. 使用 Scala 自带的 args 数组：在 Scala 中，main 函数的参数是 String 类型的数组，你可以直接对这个数组进行操作。
 * 2. 使用第三方库：Scala 有很多第三方库可以帮助你解析命令行参数，例如 scopt 和 argonaut，支持选项、参数、帮助信息等。
 *
 * 参数配置：-i C:\Users\chubin.zheng\JavaProjects\bigdata_learning\Spark基础01-设计与运行原理.md
 */
class  CmdOptionParserTest {

    @Test
    @DisplayName("解析带有输入和输出的命令行参数")
    def testParseArgsWithInputAndOutput(): Unit = {
        val args = Array("-i", "input.txt", "-o", "output.txt")
        val result = CmdOptionParser.parseArgs(args)

        Assertions.assertTrue(result.isDefined)
        val config = result.get
        Assertions.assertEquals("input.txt", config.input)
        Assertions.assertEquals("output.txt", config.output)
    }
}
