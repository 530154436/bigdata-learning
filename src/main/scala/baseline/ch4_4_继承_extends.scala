package baseline

/**
 * 继承
 * Scala中的继承与Java有着显著的不同:
 *  (1)重写一个非抽象方法必须使用override修饰符。
 *  (2)只有主构造器可以调用超类的主构造器。
 *  (3)在子类中重写超类的抽象方法时，不需要使用override关键字。
 *  (4)可以重写超类中的字段。
 * Scala和Java一样，不允许类从多个超类继承。
 *
 * 抽象类
 * （1）定义一个抽象类，需要使用关键字abstract。
 * （2）定义一个抽象类的抽象方法，也不需要关键字abstract，只要把方法体空着，不写方法体就可以。
 * （3）抽象类中定义的字段，只要没有给出初始化值，就表示是一个抽象字段，但是，抽象字段必须要声明类型，
 *     比如：val carBrand: String，就把carBrand声明为字符串类型，这个时候，不能省略类型，否则编译会报错。
 */
abstract class Car{         // 抽象类，不能直接被实例化
    val carBrand: String    // 字段没有初始化值，就是一个抽象字段
    def info(): Unit        // 抽象方法，不需要使用abstract关键字
    def greeting(): Unit = println("Welcome to my car!")
}
class BMWCar extends Car() {
    val carBrand: String = "BMW"
    def info(): Unit = printf("This is a %s car. It is on sale.\n", carBrand)
    override def greeting(): Unit = printf("Welcome to %s car!\n", carBrand)
}
class BYDCar extends Car() {
    override val carBrand: String = "BYD"
    override def info(): Unit = printf("This is a %s car. It is on sale.\n", carBrand)
    override def greeting(): Unit = printf("Welcome to %s car!\n", carBrand)
}


object ch4_4_继承_extends {
    def main(args: Array[String]): Unit = {
        val bmw = new BMWCar()
        bmw.info()
        bmw.greeting()

        val byd = new BYDCar()
        byd.info()
        byd.greeting()
    }
}
