package baseline

/**
 规约操作是对容器元素进行两两运算，将其“规约”为一个值

 1. reduce操作
    reduce方法：接受一个二元函数f作为参数，首先将f作用在某两个元素上并返回一个值，然后再将f作用在上一个返回值和容器的下一个元素上，
               再返回一个值，依此类推，最后容器中的所有值会被规约为一个值
    reduce包含reduceLeft和reduceRight两种操作，前者从集合的头部开始操作，后者从集合的尾部开始操作。

  2. fold操作
    折叠(fold)操作和reduce（归约）操作比较类似。
    fold操作需要从一个初始的“种子”值开始，并以该值作为上下文，处理集合中的每个元素。
    fold方法：一个双参数列表的函数，从提供的初始值开始规约。
             第一个参数列表接受一个规约的初始值，第二个参数列表接受与reduce中一样的二元函数参数。
    foldLeft和foldRight：前者从左到右进行遍历，后者从右到左进行遍历
 */
object ch6_3_集合3_reduce_fold {

    def reduce(): Unit ={
        println("reduce操作")
        val list: List[Int] = List(1,2,3,4,5)
        val list1 = list.reduce(_ - _)
        val list2 = list.reduceLeft(_ - _)
        val list3 = list.reduceRight(_ - _)

        println(list)
        println(list1)
        println(list2)
        println(list3)
        println()
    }

    def fold(): Unit ={
        println("fold操作")
        val list: List[Int] = List(1,2,3,4,5)
        val list1 = list.fold(10)(_ - _) // 计算顺序(((((10-1)-2)-3)-4)-5)
        val list2 = list.foldLeft(10)(_ - _)
        val list3 = list.foldRight(10)(_ - _) //计算顺序(1-(2-(3-(4-(5-10)))))

        println(list)
        println(list1)
        println(list2)
        println(list3)
        println()
    }

    def main(args: Array[String]): Unit = {
        reduce()
        fold()
    }
}
