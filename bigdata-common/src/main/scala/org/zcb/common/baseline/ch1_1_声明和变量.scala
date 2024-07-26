package org.zcb.common.baseline

object ch1_1_define {
    /**
     * Scala有两种类型的变量:
     *  1. val:是不可变的，在声明时就必须被初始化，而且初始化以后就不能再赋值;
     *  2. var:是可变的，声明的时候需要进行初始化，初始化以后还可以再次对其赋值。
     */
    def main(args: Array[String]): Unit = {
        val myStr = "HelloWorld."
        val myStr2: String = "HelloWorld."
        val myStr3: java.lang.String = "HelloWorld."

        var myPrice: Double = 9.9
        myPrice = 6.6

        println(myStr)
        println(myStr2)
        println(myStr3)
        println(myPrice)
    }
}
