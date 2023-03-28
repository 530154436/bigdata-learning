package baseline

object ch1_3_range {
    /**
     * Range
     * 在执行for循环时，我们经常会用到数值序列。比如，i的值从1循环到5，这时就可以采用Range来实现。
     * Range可以支持创建不同数据类型的数值序列，包括Int、Long、Float、Double、Char、BigInt和BigDecimal等。
     * 在创建Range时，需要给出区间的起点和终点以及步长（默认步长为1）。
     */
    def range(): Unit = {
        // （1）创建一个从1到5的数值序列，包含区间终点5，步长为1
        val range1 = 1 to 5
        val _ = 1.to(5)
        println(range1, range1.getClass)

        // （2）创建一个从1到5的数值序列，不包含区间终点5，步长为1
        val range2 = 1 until 5
        println(range2, range2.getClass)

        // （3）创建一个从1到10的数值序列，包含区间终点10，步长为2
        val range3 = 1 to 10 by 2
        println(range3, range3.getClass)

        // （4）创建一个Float类型的数值序列，从0.5f到5.9f，步长为0.8f
        val range4 = 0.5f to 5.9f by 0.8f
        println(range4, range4.getClass)
    }

    def main(args: Array[String]): Unit = {
        range()
    }
}
