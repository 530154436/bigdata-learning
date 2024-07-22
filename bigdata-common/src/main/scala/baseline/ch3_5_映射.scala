package baseline

object ch3_5_映射 {
    /**
     * 映射(Map)
     * 是一系列键值对的容器。在一个映射中，键是唯一的，但值不一定是唯一的。可以根据键来对值进行快速的检索。
     * 映射包括可变和不可变两种，默认情况下创建的是不可变映射。
     *
     * 1. 不可变映射
     *  scala.collection.immutable.Map
     *  无法更新映射中的元素的，也无法增加新的元素。
     *
     * 2. 可变映射
     *  scala.collection.mutable.Map
     *  可以使用+=操作来添加新的元素。
     *
     * 3. 循环遍历映射，是经常需要用到的操作，基本格式是：
     *  for ((k,v) <- 映射) 语句块
     *  遍历key：for (k<-university.keys)
     *  遍历value：for (v<-university.values)
     */
    def immutableMap(): Unit ={
        println("不可变映射")
        val university = Map(
            "XMU" -> "Xiamen University",
            "THU" -> "Tsinghua University",
            "PKU" -> "Peking University"
        )
        println(university)

        // 通过键来获取获取映射中的值
        println(university("XMU"))

        // 检查映射中是否包含某个值，可以使用contains方法
        val value = if(university.contains("XMU"))  university("XMU") else null
        println(value)

        // 循环遍历映射
        for((k, v) <- university)
            printf("Code is : %s and name is: %s\n", k, v)
        for(k <- university.keys)
            println(k)
        for(v <- university.values)
            println(v)
    }

    def mutableMap(): Unit ={
        println("可变映射")
        val university2 = scala.collection.mutable.Map(
            "XMU" -> "Xiamen University",
            "THU" -> "Tsinghua University",
            "PKU" -> "Peking University"
        )
        university2("XMU") = "Ximan University"
        university2 += ("GZU" -> "Guangzhou University", "BJTU" -> "Beijing Jiaotong University")
        println(university2)
    }

    def main(args: Array[String]): Unit = {
        immutableMap()
        mutableMap()
    }
}
