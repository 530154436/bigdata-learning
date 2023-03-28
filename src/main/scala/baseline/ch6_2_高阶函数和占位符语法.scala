package baseline

/**
 1. 高阶函数
  一个接受其他函数作为参数或者返回一个函数的函数就是高阶函数。

 2. 占位符语法
  为了让函数字面量更加简洁，我们可以使用下划线作为一个或多个参数的占位符，只要每个参数在函数字面量内仅出现一次。
  _ + _将扩展成带两个参数的函数字面量。这也是仅当每个参数在函数字面量中最多出现一次的情况下你才能运用这种短格式的原由。
  多个下划线指代多个参数，而不是单个参数的重复运用。第一个下划线代表第一个参数，第二个下划线代表第二个，第三个......，如此类推。

 */
object ch6_2_高阶函数和占位符语法 {
    // 从a到b的f(n)的累加形式（其中a<=n<=b），唯一的区别就是各种场景下f(n)的具体实现不同
    def sum(f: Int => Int, x: Int, y: Int): Int = {
        if(x > y) 0 else f(x) + sum(f, x + 1, y)
    }

    // 对给定两个数区间中的所有整数求和
    def self(x: Int): Int = x
    def sumInts(x: Int, y: Int): Int = {
        if (x > y) 0 else self(x) + sumInts(x + 1, y)
    }

    // 求出连续整数的平方和
    def square(x: Int): Int = x * x
    def sumSquares(a: Int, b: Int): Int = {
        if (a > b) 0 else square(a) + sumSquares(a + 1, b)
    }

    // 求出连续整数的关于2的幂次和
    def powerOfTwo(x: Int): Int = {
        if(x == 0) 1 else 2 * powerOfTwo(x - 1)
    }
    def sumPowersOfTwo(x: Int, y: Int): Int = {
        if(x > y) 0 else powerOfTwo(x) + sumPowersOfTwo(x + 1, y)
    }

    def main(args: Array[String]): Unit = {
        println("高阶函数")
        println(sumInts(1, 5))
        println(sumSquares(1, 5))
        println(sumPowersOfTwo(1, 5))
        println(sum(self, 1, 5))
        println(sum(square, 1, 5))
        println(sum(powerOfTwo, 1, 5))

        println("占位符语法")
        val numList = List(-1, 1, 5, 0, -30)
        println(numList)

        // x => x>0 等价与 _ > 0
        println(numList.filter(x => x > 0))
        println(numList.filter(_ > 0))
        val f = (_: Int) + (_: Int)  // (Int, Int) => Int
        println(f(5, 10))
    }
}
