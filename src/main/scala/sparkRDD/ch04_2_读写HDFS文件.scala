package sparkRDD

import conf.SparkGlobal
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession


/**
#### 读写HDFS文件
从分布式文件系统HDFS中读取数据，也是采用textFile()方法，可以为textFile()方法提供一个HDFS文件或目录地址。<br>
如果是一个文件地址，它会加载该文件，如果是一个目录地址，它会加载该目录下的所有文件的数据。

 */
object ch04_2_读写HDFS文件 {

    def readFromHDFSFile(sparkSession: SparkSession): RDD[String] = {
        val lines = sparkSession.sparkContext.textFile("hdfs://localhost:9000/spark/word1.txt")
        println("readFromHDFSFile", lines.count())
        lines
    }

    def writeToHDFSFile(sparkSession: SparkSession): Unit = {
        val lines = readFromHDFSFile(sparkSession)
        lines.saveAsTextFile("hdfs://localhost:9000/spark/writeback.txt")
        println("writeToHDFSFile", lines.count())
    }

    def main(args: Array[String]): Unit = {
        readFromHDFSFile(SparkGlobal.sparkSession)
        writeToHDFSFile(SparkGlobal.sparkSession)
    }
}
