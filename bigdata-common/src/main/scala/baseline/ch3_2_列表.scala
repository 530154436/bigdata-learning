package baseline

object ch3_2_列表 {
    /**
     * 列表
     * 列表是一种共享相同类型的不可变的对象序列。Scala的List定义在scala.collection.immutable包中
     * scala的List一旦被定义，其值就不能改变，因此声明List时必须初始化。
     * 列表有头部和尾部的概念，可以分别使用head和tail方法来获取。
     *  a.head返回的是列表第一个元素的值
     *  b.tail返回的是除第一个元素外的其它值构成的新列表，这体现出列表具有递归的链表结构
     * 构造列表常用的方法是通过在已有列表前端增加元素，使用的操作符为“::”。
     * Scala还定义了一个空列表对象Nil，借助Nil，可以将多个元素用操作符::串起来初始化一个列表
     *     a. val intList = 1::2::3::Nil 与val intList = List(1,2,3)等效
     *     b. 也可以使用“:::”操作符对不同的列表进行连接得到新的列表
     * Scala还为列表提供了一些常用的方法，比如，如果要实现求和，可以直接调用sum方法。
     */
    def main(args: Array[String]): Unit = {
        // 声明必须初始化
        val list1: List[Int] = List(1, 2, 3)
        println(list1, list1.getClass)
        println(list1.head, list1.tail)

        // 在已有列表前端增加元素
        val list2: List[Any] =  "AA1" :: list1
        println(list2, list2.getClass)

        // 在已有列表末尾增加元素
        val list21: List[Any] =   list1 :+ "AA2"
        println(list21, list21.getClass)

        // Nil对象
        val list3: List[Int] = 1 :: 2 :: 3 :: Nil
        println(list3, list3.getClass)

        // 使用:::操作符对不同的列表进行连接得到新的列表
        val list4: List[Int] = list1 ::: list3
        println(list4, list4.getClass)
        println(list4.sum)
    }
}
