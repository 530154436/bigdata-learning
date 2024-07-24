package org.zcb.common.baseline

object ch2_3_for循环 {
    /**
     * 1. for循环
     * Scala中的for循环语句格式：for (变量<-表达式) 语句块，其中，“变量<-表达式”被称为“生成器（generator）”
     * Scala也支持“多个生成器”的情形，可以用分号把它们隔开。
     * 有时候，我们可能不希望打印出所有的结果，我们可能希望过滤出一些满足制定条件的结果，这个时候，就需要使用到称为“守卫(guard)”的表达式。
     *
     * 2. for推导式（yield关键字）
     * (1) Scala的for结构可以在每次执行的时候创造一个值，然后将包含了所有产生值的集合作为for循环表达式的结果返回， 集合的类型由生成器中的集合类型确定
     * (2) 通过for循环遍历一个或多个集合，对集合中的元素进行“推导”，从而计算得到新的集合，用于后续的其他处理。
     * 语句格式：for (变量 <- 表达式) yield {语句块}
     */
    def main(args: Array[String]): Unit = {
        // for 循环
        for (i <- 1 to 5) {
            println(i)
        }
        for (i <- 1 to 5 by 2) {
            println(i)
        }
        for (i <- 1 to 5 if i % 2 == 0) {
            println(i)
        }
        for (i <- 1 to 5; j <- 6 to 10) {
            println(i, j)
        }

        // for 推导式
        val r = for (i <- 10 to 20 if i % 2 == 0) yield {
            println(i); i
        }
        println(r, r.getClass)
    }
}
