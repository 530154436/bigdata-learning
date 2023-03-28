package baseline

/**
 * apply方法和update方法
 * 在Scala中，apply方法和update方法都会遵循相关的约定被调用，约定如下:
 *  (1) 用括号传递给变量(对象)一个或多个参数时，Scala 会把它转换成对 apply方法 的调用
 *  (2) 当对带有括号并包括一到若干参数的对象进行赋值时，编译器将调用对象的 update方法，
 *      在调用时，是把括号里的参数和等号右边的对象一起作为update方法的输入参数来执行调用。
 *
 * 在进行元组赋值的时候，之所以没有采用Java中的方括号myStrArr[0]，而是采用圆括号的形式，myStrArr(0)，是因为存在上述的update方法的机制。
 */
object TestApplyObject {
    def apply(param: String): String = {
        println("TestApplyObject.apply method called, parameter is: " + param)
        "TestApplyObject"
    }
}

class TestApplyClass {
    def apply(param: String): String = {
        println("TestApplyClass.apply method called, parameter is: " + param)
        "TestApplyClass"
    }
}

class ApplyTest {
    def apply(): Unit = {println("apply method in class is called.")}
    def greetingOfClass(): Unit = {
        println("Greeting method in class is called.")
    }
}

object ApplyTest {
    def apply(): ApplyTest = {
        println("apply method in object is called.")
        new ApplyTest()
    }
}


object ch4_3_apply和update {

    def testArray(): Unit = {
        // 调用Array类的伴生对象Array的apply方法，完成数组的初始化
        // apply方法中实际返回 Array对象
        val myStrArr = Array("BigData", "Hadoop", "Spark")
        println(myStrArr.mkString("Array(", ", ", ")"))

        // 调用 update 方法
        myStrArr(0) = "BigData01"
        myStrArr(1) = "Hadoop01"
        myStrArr(2) = "Spark01"
        println(myStrArr.mkString("Array(", ", ", ")"))
    }

    def main(args: Array[String]): Unit = {
        // 类调用apply
        val myClass = new TestApplyClass()
        println(myClass("class"))

        // 单例对象调用apply
        val group = TestApplyObject("object")
        println(group)
        println()

        // 伴生对象调用apply
        val a = ApplyTest() // 这里会调用伴生对象中的apply方法，返回ApplyTest的实例化对象
        a.greetingOfClass()
        a()                 // 这里会调用伴生类中的apply方法
        println()

        // 测试Array的apply和update方法
        testArray()
    }
}
