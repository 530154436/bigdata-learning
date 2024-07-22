package baseline

object ch2_2_while循环 {
    /**
     * Scala中也有和Java类似的while循环语句。
     */
    def main(args: Array[String]): Unit = {
        var i = 9
        while (i > 0) {
            i -= 1
            printf("i is %d\n", i)
        }

        var j = 0
        do {
            j += 1
            println(j)
        } while (j < 5)
    }
}
