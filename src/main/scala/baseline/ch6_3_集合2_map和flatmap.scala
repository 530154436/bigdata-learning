package baseline

/**
 1. map操作
  map操作是针对集合的典型变换操作，它将某个函数应用到集合中的每 个元素，并产生一个结果集合。

 2. flatMap操作
  flatMap是map的一种扩展。在flatMap中，我们会传入一个函数，该函数对每个输入都会返回一个集合(而不是一个元素)，
  然后，flatMap把生成的多个集合“拍扁”成为一个集合。
 */
object ch6_3_集合2_map和flatmap {

    def map(): Unit ={
        println("map操作")
        val list = List("hadoop", "hive", "spark")
        val list1 = list.map(x => x.toUpperCase())
        println(list)
        println(list1)

        println()
    }

    def flatmap(): Unit = {
        println("flatMap操作")
        val list = List("Hadoop", "Hive", "Spark")
        val list1 = list.flatMap(s => s.toList)
        println(list)
        println(list1)

        println()
    }

    def main(args: Array[String]): Unit = {
        map()
        flatmap()
    }
}
