package org.zcb.common.baseline

/**
 * 类和对象是Java、C++等面向对象编程的基础概念。
 * 类是抽象的，不占用内存，而对象是具体的，占用存储空间。
 * 类是用来创建对象的蓝图。定义好类以后，就可以使用new关键字来创建对象。
 * Scala中的类不声明为public，一个Scala源文件中可以有多个类，彼此可见。
 *
 * 1. 成员属性
 *  private修饰符: 私有属性，外界无法访问，只有在类内部可以访问该字段，默认public，外部可以访问该字段。
 *
 * 2. 成员方法
 *  (1) 通过def关键字定义方法。如 def increment(step: Int): Unit = { privateValue += step }
 *      其中，increment(step: Int)是方法，参数是 step，Int类型，冒号后面的Unit是表示返回值的类型。
 *  (2) Unit是表示返回值的类型，在Scala中不返回任何值，相当于Java中的void类型。
 *  (3) 返回值，不需要靠return语句，方法里面的最后一个表达式的值就是方法的返回值。
 *      如 def current(): Int = {privateValue}，
 *      其中privateValue的值就是该方法的返回值；如果大括号里面只有一行语句，那么也可以直接去掉大括号。
 *
 * 3. getter和setter方法
 *  给类中的字段设置值以及读取值。
 *  在Scala中，也提供了getter和setter方法的实现，但是并没有定义成getXxx和setXxx
 *  在Scala中，可以通过定义类似getter和setter的方法，分别叫做value和value_=
 *
 * 4. 构造器
 *  Scala构造器包含1个主构造器和若干个（0个或多个）辅助构造器。
 *  辅助构造器的名称为 this，每个辅助构造器都必须调用一个此前已经定义的辅助构造器或主构造器。
 *  Scala的每个类都有主构造器。
 *  Scala的主构造器和Java有着明显的不同，Scala的主构造器是整个类体，需要在类名称后面罗列出构造器所需的所有参数，这些参数被编译成字段，字段的值就是创建对象时传入的参数的值。
 */
class Counter(){
    private var privateValue: Int = 0
    private var name: String = ""       //表示计数器的名称
    private var mode: Int = 1           //mode用来表示计数器类型（比如，1表示步数计数器，2表示时间计数器）

    def this(name: String ){            // 第1个辅助构造器
        this()                          // 调用主构造器
        this.name = name
    }
    def this(name: String, mode: Int){  // 第2个辅助构造器
        this(name)
        this.mode = mode
    }

    // getter、setter
    def value: Int = this.privateValue
    def value_=(newValue: Int): Unit = {
        if (newValue > 0)
            this.privateValue = newValue
    }

    def increment(step: Int = 1): Unit = {
        this.privateValue += step
    }

    def current(): Int = {
        this.privateValue
    }
    def currentV1(): Int = this.privateValue

    def info(): Unit = {
        printf("Counter: Name is %s and mode is %d\n", name, mode)
    }
}

class CounterV1(val name: String, val mode: Int) {
    private var value = 0 //value用来存储计数器的起始值
    def increment(step: Int): Unit = {value += step}
    def current(): Int = value
    def info(): Unit = {printf("Name:%s and mode is %d\n",name,mode)}
}

/**
 * Windows:
 *       cd \Users\chubin.zheng\JavaProjects\bigdata-learnning
 *  编译：scalac src\main\scala\course\ch4_1_类.scala
 *  执行：scala src.main.scala.course.ch4_类
 *       java -classpath %SCALA_HOME%/lib/scala-library.jar;. src.main.scala.course.ch4_类
 */
object ch4_1_类 {
    def main(args: Array[String]): Unit = {
        val myCounter: Counter = new Counter()
        myCounter.increment()
        println(myCounter.current())
        println(myCounter.currentV1())

        // getter,setter
        myCounter.value = 100
        println(myCounter.value)

        // 辅助构造器
        val myCounter1 = new Counter("Runner")
        myCounter1.increment(2)
        myCounter1.info()
        printf("Current Value is: %d\n", myCounter1.current())

        val myCounter2 = new Counter("Timer", 2)
        myCounter2.info()
        myCounter2.increment(3)
        printf("Current Value is: %d\n", myCounter2.current())

        // 构造器
        val myCounter3 = new CounterV1("MainConStructure", 5)
        myCounter3.info()
        myCounter3.increment(5)
        printf("Current Value is: %d\n", myCounter3.current())
    }
}
