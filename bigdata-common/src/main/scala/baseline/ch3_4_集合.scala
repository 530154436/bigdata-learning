package baseline


object ch3_4_集合 {
    /**
     * 集(set)
     * 集(set)是不重复元素的集合。列表中的元素是按照插入的先后顺序来组织的，但是，
     * ”集”中的元素并不会记录元素的插入顺序，而是以“哈希”方法对元素的值进行组织，所以，它允许你快速地找到某个元素。
     *
     * 1. 不可变集
     *  scala.collection.immutable.Set
     *  缺省情况下创建的是不可变集，通常我们使用不可变集。
     *  如果使用val声明一个集合 mySet，mySet += “Scala”执行时会报错，所以需要声明为var。
     *
     * 2. 可变集
     *  scala.collection.mutable.Set
     *  可以声明myMutableSet为val变量（不是var变量），由于是可变集，因此，可以正确执行myMutableSet += “Cloud Computing”，不会报错。
     *
     * 注意：
     *  虽然可变集和不可变集都有添加或删除元素的操作，但是，二者有很大的区别。
     *  对不可变集进行操作，会产生一个新的集，原来的集并不会发生变化。而对可变集进行操作，改变的是该集本身，
     */
    def immutableSet(): Unit ={
        // 不可变集
        var myImmutableSet = scala.collection.immutable.Set("Hadoop", "Spark")
        println(myImmutableSet, myImmutableSet.getClass)

        myImmutableSet += "Scala"
        println(myImmutableSet)
    }
    def mutableSet(): Unit ={
        // 可变集
        val myMutableSet = scala.collection.mutable.Set("Hadoop", "Spark")
        println(myMutableSet, myMutableSet.getClass)

        myMutableSet += "Scala"
        println(myMutableSet)

        // 使用 ++= 操作符将 myMutableSet 的元素添加到 myMutableSet2 中
        val myMutableSet2 = scala.collection.mutable.Set("HaHa", "Spark")
        myMutableSet2 ++= myMutableSet
        println(myMutableSet2.toSet)

        // 求差集
        myMutableSet2 --= myMutableSet
        println(myMutableSet2.toSet)
    }

    def main(args: Array[String]): Unit = {
        mutableSet()
        //immutableSet()
    }
}
