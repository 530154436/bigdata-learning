package spark.structured_streaming

import conf.Global
import utils.FileUtil

import java.io.{File, PrintWriter}
import java.nio.file.{Path, Paths}
import java.util.Date
import scala.util.Random

object ch01_1_SparkFileSourceGenerate {
    val TEST_DATA_DIR: Path = Paths.get(Global.BASE_DIR, "data", "structuredStreaming")
    val ACTION_DEF = List("login", "logout", "purchase")
    val DISTRICT_DEF = List("fujian", "beijing", "shanghai", "guangzhou")

    // 测试环境的恢复，对文件夹进行清理
    def tearDown(): Unit = {
        val dir = TEST_DATA_DIR.toFile
        if(dir.exists()) {
            val files : Array[File] = dir.listFiles()
            for(file <- files) {
                FileUtil.delete(file)
            }
        }
        dir.delete()
    }

    // 测试的环境搭建，判断文件夹是否存在，如果存在则删除旧数据，并建立文件夹
    def setUp(): Unit = {
        tearDown()
        TEST_DATA_DIR.toFile.mkdir()
    }

    // 生成测试文件
    def write_and_move(filename: String, data : String): Unit = {
        val file = Paths.get(TEST_DATA_DIR.toAbsolutePath.toString, filename).toFile
        val writer = new PrintWriter(file)
        writer.write(data)
        writer.close()
    }

    def main(args: Array[String]): Unit =  {
        setUp()
        for(i <- 1 to 1000) {
            val filename = "e-mall-" + i + ".json"
            var content = ""
            for(j <- 1 to 100) {
                //格式为{"evenTime":1546939167,"action":"logout","district":"fujian"}\n
                val eventime = new Date().getTime.toString.substring(0, 10)
                val action_def = Random.shuffle(ACTION_DEF).head
                val district_def = Random.shuffle(DISTRICT_DEF).head
                content = content + "{\"eventTime\": " + eventime + ", \"action\": \"" + action_def + "\", \"district\": \"" + district_def + "\"}\n"
            }
            write_and_move(filename, content)
            Thread.sleep(5000)
        }
        tearDown()
    }

}
