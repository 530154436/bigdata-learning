package baseline


object ch3_3_元组 {
    /**
     * 元组
     * 不同类型的值的聚集。元组和列表不同，列表中各个元素必须是相同类型，而元组可以包含不同类型的元素
     * 当需要访问元组中的某个元素的值时，可以通过类似tuple._1、tuple._2、tuple._3这种方式就可以实现。
     */
    def main(args: Array[String]): Unit = {
        val tuple = ("BigData", 2020, 8, 1)
        println(tuple, tuple.getClass)
        println(tuple._1)
        println(tuple._2)
        println(tuple._3)
        println(tuple._4)
    }
}
