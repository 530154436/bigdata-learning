package baseline

object ch3_6_迭代器 {
    /**
     * 迭代器（Iterator）
     * 在Scala中，迭代器不是一个集合，但是，提供了访问集合的一种方法。
     * 当构建一个集合需要很大的开销时（比如把一个文件的所有行都读取内存），迭代器就可以发挥很好的作用。
     *
     * 1. 迭代器包含两个基本操作：next和hasNext。
     *  next可以返回迭代器的下一个元素，hasNext用于检测是否还有下一个元素。
     *  通常可以通过while循环或者for循环实现对迭代器的遍历。
     *
     * 2. Iterable有两个方法返回迭代器：grouped和sliding。
     *  这些迭代器返回的不是单个元素，而是原容器（collection）元素的全部子序列，这些最大的子序列作为参数传给这些方法。
     *  grouped方法返回元素的增量分块，sliding方法生成一个滑动元素的窗口。
     */
    def whileIterator(): Unit ={
        val iterator = Iterator("Hadoop","Spark","Scala")
        while (iterator.hasNext) {
            println(iterator.next())
        }
    }
    def forIterator(): Unit ={
        val iterator = Iterator("Hadoop","Spark","Scala")
        for(item <- iterator){
            println(item)
        }
    }
    def grouped(): Unit ={
        val list1: List[Int] = List(1, 2, 3, 4, 5, 6, 7)
        val git = list1.grouped(3)
        println(git)
        for(item <- git){
            println(item)
        }
    }
    def sliding(): Unit ={
        val list1: List[Int] = List(1, 2, 3, 4, 5, 6, 7)
        val sit = list1.sliding(3)
        println(sit)
        for(item <- sit){
            println(item)
        }
    }

    def main(args: Array[String]): Unit = {
        whileIterator()
        forIterator()
        grouped()
        sliding()
    }
}
