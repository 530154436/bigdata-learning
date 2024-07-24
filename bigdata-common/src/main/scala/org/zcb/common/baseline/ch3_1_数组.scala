package org.zcb.common.baseline

import scala.collection.mutable.ArrayBuffer


object ch3_1_数组 {
    /**
     * 数组
     * 数组是一种可变的、可索引的、元素具有相同类型的数据集合，一般包括定长数组和变长数组。
     *
     * 1. 定长数组
     * Scala提供了参数化类型的通用数组类Array[T]，其中T可以是任意的Scala类型，可以通过显式指定类型或者通过隐式推断来实例化一个数组。
     * 在Scala中，对数组元素的应用，是使用圆括号，而不是方括号，也就是使用intValueArr(0)，而不是intValueArr[0]。
     *
     * Array提供了函数ofDim来定义二维和三维数组，用法如下:
     *  a. myMatrixArray.ofDim[Int](3,4)  <=> Array[Array[Int]\]
     *  b. Array.ofDim[String](3,2,4) <=> Array[Array[Array[String]\]\]
     *  可以使用多级圆括号来访问多维数组的元素，例如myMatrix(0)(1)返回第 一行第二列的元素
     *
     *  2. 变长数组
     *  定义变长数组，需要使用ArrayBuffer参数类型，其位于包scala.collection.mutable中。
     */
    def immutableArray(): Unit ={
        println("定长数组")

        // 声明一个长度为3的整型数组，每个数组元素初始化为0
        val intValArr = new Array[Int](3)
        intValArr(0) = 12
        intValArr(1) = 45
        intValArr(2) = 33
        println(intValArr.mkString(","), intValArr.getClass)

        // 声明一个长度为3的字符串数组，每个数组元素初始化为null
        val strValArr = new Array[String](4)
        strValArr(0) = "BigData"
        strValArr(1) = "Hadoop"
        strValArr(2) = "Spark"
        for(i <- strValArr.indices)
            println(i, strValArr(i))

        // 静态初始化
        val intValArr1 = Array(1, 2, 3)
        val strValArr1 = Array("BigData", "Hadoop", "Spark")
        println(intValArr1.mkString("Array(", ", ", ")"))
        println(strValArr1.mkString("Array(", ", ", ")"))

        // 二维数组、三维数组
        val myMatrix = Array.ofDim[Int](3,4)
        val myCube = Array.ofDim[String](3, 2, 4)
        myMatrix.foreach(x => println(x.mkString("Array(", ", ", ")")))
        myCube.foreach(x => println(x.mkString("Array(", ", ", ")")))
    }

    def mutableArray(): Unit ={
        println("变长数组")

        // 变长数组
        // val aMutableArr = new ArrayBuffer[Int]()
        val aMutableArr = ArrayBuffer[Int](10, 20, 30)
        println(aMutableArr.mkString(","))

        aMutableArr += 40
        println(aMutableArr.mkString(","))

        aMutableArr.insert(2, 60,40)
        println(aMutableArr.mkString(","))

        aMutableArr -= 40
        println(aMutableArr.mkString(","))

        println(aMutableArr.remove(2))

        aMutableArr.append(10)
        println(aMutableArr.mkString(","))
    }

    def main(args: Array[String]): Unit = {
        immutableArray()
        mutableArray()
    }
}
