package org.zcb.spark.streaming

import org.zcb.common.conf.Global
import java.io.PrintWriter
import java.net.ServerSocket
import java.nio.file.Paths
import java.util.Random
import scala.io.Source

/**
 * 服务器:数据源头的产生方式不要使用nc程序，而是采用自己编写的程序产生Socket数据源
 * 客户端：启动ch01_2_套接字流，屏幕上不断打印出词频统计信息
 */
object ch01_2_套接字流_自定义数据源 {

    val hostname = "127.0.0.1"
    val port = 9999

    // 返回位于0到length-1之间的一个随机数
    def index(length: Int): Int = {
        val rdm = new Random
        rdm.nextInt(length)
    }

    def main(args: Array[String]): Unit ={
        val fileName = Paths
            .get(Global.BASE_DIR, "data", "spark", "resources", "people.txt")
            .toAbsolutePath
            .toFile
        val millisecond = 3000

        val buffer = Source.fromFile(fileName)
        val lines = buffer.getLines().toList
        val rowCount = lines.length            // 计算出文件的行数
        val listener = new ServerSocket(port)  // 创建监听特定端口的ServerSocket对象
        while(true) {
            val socket = listener.accept()
            new Thread() {
                override def run(): Unit = {
                    println("Got client connected from: " + socket.getInetAddress)
                    val out = new PrintWriter(socket.getOutputStream, true)
                    while(true){
                        Thread.sleep(millisecond)            // 每隔多长时间发送一次数据
                        val content = lines(index(rowCount)) // 从lines列表中取出一个元素
                        println(content)
                        out.write(content + '\n')   // 写入要发送给客户端的数据
                        out.flush()                 // 发送数据给客户端
                    }
                    socket.close()
                }
            }.start()
        }
        buffer.close()
    }
}

