package sparkRDD.exercises

import conf.{Global, SparkGlobal}
import org.apache.spark.sql.SparkSession

import java.nio.file.{Path, Paths}

/**
任务要求：
对于一个给定的文件（数据如file.txt所示），请对数据进行排序：
首先根据第1列数据降序排序，如果第1列数据相等，则根据第2列数据降序排序

二次排序，具体的实现步骤如下
第一步：按照Ordered和Serializable接口实现自定义排序的key
第二步：将要进行二次排序的文件加载进来生成<key,value>类型的RDD
第三步：使用sortByKey基于自定义的Key进行二次排序
第四步：去除掉排序的Key,只保留排序的结果

file.txt
5 3
1 6
4 9
4 7
8 3
5 6
3 2
 */
class SecondarySortKey(val first: Int, val second: Int) extends Ordered[SecondarySortKey] with Serializable {
    override def compare(other: SecondarySortKey): Int = {
        if(this.first - other.first != 0){
            this.first - other.first
        }else{
            this.second - other.second
        }
    }
}

object ex04_二次排序 {
    def main(args: Array[String]): Unit ={
        val directory: Path = Paths.get(Global.BASE_DIR, "data", "sparkrdd", "04").toAbsolutePath

        val sparkSession: SparkSession = SparkGlobal.getSparkSession(this.getClass.getName)
        val rdd = sparkSession.sparkContext.textFile(directory.toString, 2)
        // val rdd = sparkSession.sparkContext.wholeTextFiles(directory.toString)

        // 使用sortByKey基于自定义的Key进行二次排序
        val pairWithSortKey = rdd.filter(line => line.trim().nonEmpty && line.trim().split(" ").length==2)
          .map(
              line => (
                new SecondarySortKey(line.trim().split(" ")(0).toInt,
                                     line.trim().split(" ")(1).toInt),
                line
              )
          )

        pairWithSortKey.sortByKey(ascending = false)
          .map(sortedLine => sortedLine._2)
          .foreach (println)
    }
}
