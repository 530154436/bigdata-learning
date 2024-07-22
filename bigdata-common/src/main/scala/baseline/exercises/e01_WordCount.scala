package baseline.exercises

import java.io.{BufferedReader, File, FileReader}
import java.nio.file.Paths
import conf.Global

/**
 * 任务：按照函数式编程的风格，编写一个程序，对某个目录下所有文件中的单词进行词频统计
 * 做法：请进入Linux系统，打开“终端”，进入Shell命令提示符状态，然后，在“${BASE_DIR}/test/data”目录下，新建一个wordcount子目录，
 * 并在“${BASE_DIR}/test/data”目录下新建两个包含了一些语句的文本文件word1.txt和word2.txt（你可以在文本文件中随意
 * 输入一些单词，用空格隔开），我们会编写Scala程序对该目录下的这两个文件进行单词词频统计。
 */
object e01_WordCount {
    def wordCount(): Unit = {
        // 获取所有的文件路径
        val currentDir = Paths.get(Global.BASE_DIR, "data", "wordcount")
        val files: Array[File] = currentDir.toFile.listFiles()
        for (file <- files) println(file)

        // 统计词频
        val mapper = scala.collection.mutable.Map[String, Int]()
        files.foreach(file => {
            // 逐行读取文件，避免文件太大内存溢出
            val br = new BufferedReader(new FileReader(file))
            var line: String = null
            while ( {
                line = br.readLine(); line != null
            }) {
                // 处理每行数据的逻辑
                line.split("[\\s,.]").foreach(word =>
                    if (word != "") {
                        if (mapper.contains(word)) {
                            mapper(word) += 1
                        } else {
                            mapper += (word -> 1)
                        }
                    }
                )
            }
        })

        mapper.foreach(kv => println(kv._1, kv._2))
    }

    def main(args: Array[String]): Unit = {
        wordCount()
    }
}
