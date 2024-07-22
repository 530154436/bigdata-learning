package baseline

/**
 1. 列表的遍历
 2. 映射的遍历
 */
object ch6_3_集合1_遍历操作for和foreach {

    def list_traverse(): Unit ={
        println("列表的遍历 for循环")
        val list = List(1, 2, 3, 4, 5)
        for (item <- list) {
            println(item)
        }
        println("列表的遍历 foreach")
        list.foreach(x => println(x))
        println()
    }

    def map_traverse(): Unit = {
        println("映射 for循环遍历")
        val university = Map(
            "BJTU" -> "Beijing Jiao Tong University",
            "THU" -> "Tsinghua University",
            "PKU"->"Peking University"
        )
        for ((k, v) <- university) {
            printf("k=%s, v=%s\n",k,v)
        }
        for (k <- university.keys)
            println(k)
        for (v <- university.values)
            println(v)

        println("\n映射的遍历 foreach")
        university.foreach(kv => printf("k=%s, v=%s\n", kv._1, kv._2))
        university.foreach({case(k, v) => printf("k=%s, v=%s\n", k, v)})
    }

    def main(args: Array[String]): Unit = {
        list_traverse()
        map_traverse()
    }
}
