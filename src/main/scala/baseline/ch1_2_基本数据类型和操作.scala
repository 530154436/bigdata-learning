package baseline

object ch1_2_基本数据类型和操作 {
    /**
     * 基本数据类型
     * 1. Scala的数据类型包括:
     * Byte、Char、Short、Int、Long、Float、Double和 Boolean
     * 和Java不同的是，在Scala中，这些类型都是“类”，并且都是包scala的成员，比如，Int的全名是scala.Int。
     * 对于字符串，Scala用java.lang.String 类来表示字符串
     * 2. 字面量(literal)
     * 字面量就是在用户不指定变量类型的情况下，系统默认把变量定位某种类型，这种类型就是字面量。
     * 包括整数字面量、浮点数字面量、布尔型字面量、字符字面量、字符串字面量、符号字面量、函数字面量和元组字面量。
     */
    def dataType(): Unit = {
        val i1 = 123
        val i2 = 3.14
        val i3 = true
        val i4 = 'A'
        val i5 = "ABC"

        println(i1, i1.getClass)
        println(i2, i2.getClass)
        println(i3, i3.getClass)
        println(i4, i4.getClass)
        println(i5, i5.getClass)
    }

    /**
     * 操作符
     * 1. 在Scala中，可以使用加(+)、减(-) 、乘(*) 、除(/) 、余数（%）等操作符，而且，这些操作符就是方法。
     * 例如，5 + 3和(5).+(3)是等价的，也就是说：a 方法 b  <=>  a.方法(b)
     * 2. 注意的是，和Java不同，在Scala中并没有提供++和–操作符，当需要递增和递减时，采用 i+=1、i-=1
     */
    def operator(): Unit = {
        val sum1 = 5 + 3
        val sum2 = (5).+(3)

        var i = 0
        i += 1

        println(sum1)
        println(sum2)
        println(i)
    }

    /**
     * 富包装类
     * 1. Scala还提供了许多常用运算的方法，只是这些方法不是在基本类里面定义，而是被封装到一个对应的富包装类中。
     * 2. 每个基本类型都有一个对应的富包装类，例如Int有一个RichInt类、String有一个RichString类，这些类位于包 scala.runtime中
     * 3. 当对一个基本数据类型的对象调用其富包装类提供的方法， Scala会自动通过隐式转换将该对象转换为对应的富包装类型，
     * 然后再调用相应的方法。例如:3 max 5
     */
    def richWrapper(): Unit = {
        val i = 3
        val j = 5

        println(i.max(5))
        println(math.max(i, j))
    }

    def main(args: Array[String]): Unit = {
        dataType()
        operator()
        richWrapper()
    }
}
