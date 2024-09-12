package org.zcb.common.baseline

/**
 * 模式匹配
 * 1. 简单匹配
 * 2. 类型模式
 * 3. “守卫(guard)”语句
 * 4. for表达式中的模式
 *
 * 5. case类的匹配
 *  case类是一种特殊的类，它们经过优化以被用于模式匹配。
 *  实例化case class类时，不需要使用关键字New，case class类编译成class文件之后会自动生成apply方法，这个方法负责对象的创建。
 *
 * 6. Option类型
 *  Scala Option(选项)类型用来表示一个值是可选的（有值或无值)。
 *
 *  (1)当预计到变量或者函数返回值可能不会引用任何值的时候，建议使用Option类型。
 *  (3)Option类包含一个子类Some，当存在可以被引用的值的时候，就可以使用Some来包含这个值，例如Some(5)。
 *     而None则被声明为一个对象，而不是一个类，表示没有值。
 *  (4)Option类型还提供了getOrElse方法，这个方法在这个Option是Some的实例时返回对应的值，而在是None的实例时返回传入的参数(默认值)。
 *  (5)Option[T]是一个类型为 T 的可选值的容器，其中的T可以是Sting或Int或其他各种数据类型。
 *     如果值存在，Option[T] 就是一个只包含1个元素的 Some[T] ，
 *     如果不存在，Option[T] 就是对象 None 。
 *     可以对它使用map、foreach或者filter等方法。
 */
case class MyCar(brand: String, price: Int)


object ch5_2_case_option {

    def caseMatch(): Unit = {
        println("case类的匹配")
        val myBYDCar1 = MyCar("BYD", 89000)
        val myBYDCar2 = MyCar("BYD", 81000)
        val myBMWCar = MyCar("BMW", 1210000)
        val myBenzCar = MyCar("Benz", 1500000)
        for (car <- List(myBYDCar1, myBYDCar2, myBMWCar, myBenzCar)) {
            car match {
                case MyCar("BYD", price) => println("Hello, BYD! " + "Price: " + price)
                case MyCar("BMW", 1200000) => println("Hello, BMW!")
                case MyCar(brand, price) => println("Brand:" + brand + ", Price:" + price + ", do you want it?")
            }
        }
        println()
    }

    def optionMatch(): Unit = {
        println("Option类型")
        val books=Map("cluster/hadoop" -> 5, "sparkRDD" -> 10, "hbase" -> 7)
        println(books.get("cluster/hadoop"))                    // Option[Int] = Some(5)
        println(books.get("org.zcb.hive"))                      // Option[Int] = None
        println(books.getOrElse("org.zcb.hive", "default"))     // Any = org.zcb.hive

        val a: Option[Int] = Some(5)
        val b: Option[Int] = None
        println("a.getOrElse(0): " + a.getOrElse(0))
        println("b.getOrElse(10): " + b.getOrElse(10))

        a.foreach(println)
        println()
    }

    def main(args: Array[String]): Unit = {
        caseMatch()
        optionMatch()
    }
}
