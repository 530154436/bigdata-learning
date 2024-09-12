package org.zcb.common.baseline

/**
 * 模式匹配
 * Java中有switch-case语句，但是，只能按顺序匹配简单的数据类型和表达式。
 * 相对而言，Scala中的模式匹配的功能则要强大得多，可以应用到switch语句、类型检查、“解构”等多种场合。
 *
 * 1. 简单匹配
 *  Scala的模式匹配最常用于match语句中。
 *
 * 2. 类型模式
 *  Scala可以对表达式的类型进行匹配。
 *
 * 3. “守卫(guard)”语句
 *  可以在模式匹配中添加一些必要的处理逻辑。如在模式后面加上if表达式
 *
 * 4. for表达式中的模式
 * 5. case类的匹配
 * 6. Option类型
 */
object ch5_1_模式匹配 {

    def simpleMatch(): Unit = {
        println("简单匹配")
        for(colorNum <- 1 to 5){
            val colorStr: String = colorNum match {
                case 1 => "Red"
                case 2 => "Yellow"
                case 3 => "Bule"
                case unexpected => unexpected + " is not allowed."
                // case _ => "Not Allowed"
            }
            println(colorNum, colorStr)
        }
        println()
    }

    def typeMatch(): Unit = {
        println("类型模式")
        for(elem <- List(9, 12.4, "Spark", "Hadoop", 'Hello)){
            val msg = elem match {
                case i: Int => i + " is an int value."
                case d: Double => d + " is a double value."
                case "Spark" => "Spark is found."
                case s: String => s + " is a string value."
                case _ => "This is an unexpected value."
            }
            println(msg)
        }
        println()
    }

    def guardMatch(): Unit = {
        println("“守卫(guard)”语句")
        for(elem <- 1 to 5){
            elem match {
                case _ if elem %2 == 0 => println(elem + " is even.")
                case _ => println(elem + " is odd.")
            }
        }
        println()
    }

    def forMatch(): Unit = {
        println("for表达式中的模式")
        val university = Map(
            "XMU" -> "Xiamen University",
            "THU" -> "Tsinghua University",
            "PKU"->"Peking University"

        )
        for((k, v) <- university){
            printf("Code is : %s and name is: %s\n",k,v)
        }
        println()
    }

    def main(args: Array[String]): Unit = {
        simpleMatch()
        typeMatch()
        guardMatch()
        forMatch()
    }
}
