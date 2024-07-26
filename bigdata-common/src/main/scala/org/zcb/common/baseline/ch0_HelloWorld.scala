package org.zcb.common.baseline

/**
 * 单例对象，不需要实例化
 * 编译：scalac HelloWorld.scala
 * 执行：scala src.main.scala.org.zcb.common.baseline.HelloWorld
 * java -classpath .:$SCALA_HOME/lib/scala-library.jar src.main.scala.org.zcb.common.baseline.HelloWorld
 */
object ch0_HelloWorld {
    def show(name: String): Unit = {
        println(s"Hello World. I am $name.")
    }

    def main(args: Array[String]): Unit = {
        show("chubin.zheng");
    }
}
