package baseline

/**
 * 特质（定义使用关键字trait）
 * Java中提供了接口，允许一个类实现任意数量的接口
 * 在Scala中没有接口的概念，而是提供了“特质(trait)”，它不仅实现了接口的功能，还具备了很多其他的特性
 *
 * 1. Scala的特质，是代码重用的基本单元，可以同时拥有抽象方法和具体方法
 * 2. Scala中，一个类只能继承自一个超类，却可以实现多个特质，从而重用特质中的方法和字段，实现了多重继承
 * 3. 特质定义好以后，就可以使用extends或with关键字把特质混入类中。
 *
 * 注意，抽象方法不需要使用abstract关键字，特质中没有方法体的方法，默认就是抽象方法。
 */
trait CardId{
    var id: Int             // 抽象字段
    def currentId(): Int    // 抽象方法
}
trait Greeting{
    def greeting(msg: String): Unit = {println(msg)}
}

class MyBYDCar extends CardId with Greeting{  //使用extends关键字混入第1个特质，后面可以反复使用with关键字混入更多特质
    override var id: Int = 0
    override def currentId(): Int = {id += 1; id}
}
class MyBMWCar extends CardId with Greeting{
    override var id: Int = 10000
    override def currentId(): Int = {id += 1; id}
}

object ch4_5_特质_trait {
    def main(args: Array[String]): Unit = {
        val byd = new MyBYDCar()
        byd.greeting("Welcome my first car.")
        printf("My first CarId is %d.\n", byd.currentId())

        val bmw = new MyBMWCar()
        bmw.greeting("Welcome my first car.")
        printf("My first CarId is %d.\n", bmw.currentId())
    }
}
