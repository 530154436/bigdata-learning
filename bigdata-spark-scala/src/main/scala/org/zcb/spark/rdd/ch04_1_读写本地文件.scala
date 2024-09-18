package org.zcb.spark.rdd
import org.apache.commons.io.FileUtils

import java.nio.file.{Path, Paths}
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.RDD
import org.json4s.jackson.JsonMethods
import org.json4s._
import org.zcb.common.conf.Global
import org.zcb.spark.SparkGlobal

/**
 *  ### 数据读写
 *  #### 读写本地文件
 *  1.从文本文件中读取数据创建RDD
 *  2.把RDD写入到文本文件中
 *  3. JSON文件的读取：JSON(JavaScript Object Notation) 是一种轻量级的数据交换格式
 */
object ch04_1_读写本地文件 {

    def readFromTextFile(sparkSession: SparkSession): RDD[String] = {
        val file: Path = Paths.get(Global.BASE_DIR, "data", "spark", "wordcount", "word2.txt").toAbsolutePath
        val lines = sparkSession.sparkContext.textFile(file.toString)
        println("readFromFile", lines.count())
        lines.foreach(println)
        lines
    }

    def writeToTextFile(sparkSession: SparkSession): Unit = {
        val textFile = readFromTextFile(sparkSession)
        val file: Path = Paths.get(Global.BASE_DIR, "data", "spark", "wordcount", "writeback.txt").toAbsolutePath
        if (file.toFile.exists()) {
            FileUtils.deleteDirectory(file.toFile)
        }
        textFile.saveAsTextFile(file.toString)
        println("writeToFile", file.toString)
    }

    /**
     * 任务：编写程序完成对JSON数据的解析工作
     * Scala中有一个自带的JSON库——scala.util.parsing.json.JSON，可以实现对JSON数据的解析
     * JSON.parseFull(jsonString:String)函数，以一个JSON字符串作为输入并进行解析，如果解析成功则返回一个Some(map: Map[String, Any])，如果解析失败则返回None
     */
    def readFromJsonFile(sparkSession: SparkSession): Unit = {
        val file: Path = Paths.get(Global.BASE_DIR, "data", "spark", "resources", "people.json").toAbsolutePath
        val lines: RDD[String] = sparkSession.sparkContext.textFile(file.toString)

        val jSONObjects: RDD[JValue] = lines.map(x => JsonMethods.parse(x))

        println("readFromJsonFile", lines.count())
        jSONObjects.foreach(x => println(x.toString, x.getClass)) // 打印原始 JValue
        jSONObjects.foreach {
            case jsonObj: JValue =>
                // Json转任意指定类型必须指定隐式参数
                implicit val formats: DefaultFormats.type = DefaultFormats
                // 将 JValue 转换为 Map
                val mapResult: Map[String, Any] = jsonObj.extract[Map[String, Any]]
                println(mapResult)
            case _ => println("Parsing failed")
        }
    }

    def main(args: Array[String]): Unit = {
        val sparkSession = SparkGlobal.getSparkSession()
        //readFromTextFile(sparkSession)
        //writeToTextFile(sparkSession)
        readFromJsonFile(sparkSession)
    }
}
