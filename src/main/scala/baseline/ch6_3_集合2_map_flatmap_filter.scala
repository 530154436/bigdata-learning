package baseline

/**
 1. map操作
  map操作是针对集合的典型变换操作，它将某个函数应用到集合中的每 个元素，并产生一个结果集合。

 2. flatMap操作
  flatMap是map的一种扩展。在flatMap中，我们会传入一个函数，该函数对每个输入都会返回一个集合(而不是一个元素)，
  然后，flatMap把生成的多个集合“拍扁”成为一个集合。

 3.filter操作
  过滤：遍历一个容器，从中获取满足指定条件的元素，返回一个新的容器
  filter方法：接受一个返回布尔值的函数f作为参数，并将f作用到每个元素上，将f返回真值的元素组成一个新容器返回
 */
object ch6_3_集合2_map_flatmap_filter {

    def map(): Unit ={
        println("map操作")
        val list = List("cluster/hadoop", "hive", "spark")
        val list1 = list.map(x => x.toUpperCase())
        println(list)
        println(list1)

        println()
    }

    def flatmap(): Unit = {
        println("flatMap操作")
        val list = List("Hadoop", "Hive", "Spark")
        val list1 = list.flatMap(s => s.toUpperCase())
        println(list)
        println(list1)

        println()
    }

    def filter(): Unit = {
        val university = Map(
            "XMU" -> "Xiamen University",
            "THU" -> "Tsinghua University",
            "PKU"->"Peking University",
            "XMUT"->"Xiamen University of Technology"
        )

        // 采用filter操作过滤得到那些学校名称中包含“Xiamen”的元素
        val universityOfXiamen = university.filter(kv => kv._2 contains "Xiamen")
        universityOfXiamen.foreach(kv => println(kv._1, kv._2))
        println()

        // 使用了占位符语法，过滤能被2整除的元素
        val l = List(1,2,3,4,5,6)
        val l2 = l.filter(_ % 2==0)
        println(l)
        println(l2)
    }

    def main(args: Array[String]): Unit = {
        map()
        flatmap()
        filter()
    }
}
