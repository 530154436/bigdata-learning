package baseline

/**
 1. 函数
  函数定义
    def 函数名([参数列表]):[返回值类型]={
        函数体
        [return] 表达式
    }
  函数调用: 函数名(参数列表)

 2. 匿名函数和lambda表达式：不含函数名称的函数。
  (x:Int)=>{函数体}
  x：表示输入参数类型；Int：表示输入参数类型；函数体：表示具体代码逻辑

  示例：
  def myNumFuncV1: Int => Int = {(num: Int) => num*2}               返回值类型为：Int => Int
  def myNumFuncV2: (Int, Int) => Int = {(x: Int, y: Int) => x+y}    返回值类型为：(Int, Int) => Int

  传递匿名函数至简原则：
    （1）参数的类型可以省略，会根据形参进行自动的推导
    （2）类型省略之后，发现只有一个参数，则圆括号可以省略；其他情况：没有参数和参数超过 1 的永远不能省略圆括号。
    （3）匿名函数如果只有一行，则大括号也可以省略
    （4）如果参数只出现一次，则参数省略且后面参数可以用_代替

  (1) 函数字面量
    字面量包括整数字面量、浮点数字面量、布尔型字面量、字符字面量、字符串字面量、符号字面量、函数字面量和元组字面量。
    函数字面量可以体现函数式编程的核心理念，如：{(num: Int) => num*2}。
  (2) 函数的类型和值
    在非函数式编程语言里，函数的定义包含了“函数类型”和“值”两种层面的内容。
    但是，在函数式编程中，函数是“头等公民”，可以像任何其他数据类型一样被传递和操作，即，函数的使用方式和其他数据类型的使用方式完全一致。
    这时，我们就可以像定义变量那样去定义一个函数，由此导致的结果是，函数也会和其他变量一样，开始有“值”。
    就像变量的“类型”和“值”是分开的两个概念一样，函数式编程中，函数的“类型”和“值”也成为两个分开的概念，函数的“值”，就是“函数字面量”。

 3. 闭包
    闭包是一个函数，返回值依赖于声明在函数外部的一个或多个变量。
    闭包通常来讲可以简单的认为是可以访问一个函数里面局部变量的另外一个函数。
    定义闭包的过程是将自由变量(函数外的变量)捕获而构成一个封闭的函数，反映了一个从开放到封闭的过程。
 */
object ch6_1_函数定义 {
    // 函数定义
    def counter(value: Int): Int = {
        value + 1
    }

    // 匿名函数
    def myNumFunc: Int => Int = (num: Int) => num*2
    def myNumFuncV2: (Int, Int) => Int = {(x: Int, y: Int) => x+y}

    // 闭包
    val addMore: Int => Boolean = (x: Int) => x > 0
    var more: Int = 9
    val addMoreV1: Int => Int = (x: Int) => x + more

    def main(args: Array[String]): Unit = {
        println("函数定义")
        println(counter(5))

        println("匿名函数")
        println(myNumFunc(5))
        println(myNumFuncV2(3, 4))

        println("闭包")
        println(addMore(10))
        println(addMoreV1(10))
        more = 5
        println(addMoreV1(10))
    }
}
