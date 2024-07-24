package org.zcb.spark.rdd.exercises

import org.zcb.common.conf.Global
import org.apache.spark.sql.SparkSession
import org.zcb.spark.SparkGlobal

import java.nio.file.{Path, Paths}

/**
任务描述：在推荐领域有一个著名的开放测试集，下载链接是：http://grouplens.org/datasets/movielens/，该测试集包含三个文件，
分别是ratings.dat、sers.dat、movies.dat，具体介绍可阅读：README.txt。请编程实现：通过连接ratings.dat和movies.dat
两个文件得到平均得分超过4.0的电影列表，采用的数据集是：ml-1m

movies.dat
MovieID::Title::Genres

ratings.dat
UserID::MovieID::Rating::Timestamp
 */
object ex05_聚合连接 {
    def main(args: Array[String]): Unit ={
        val moviesPath: Path = Paths.get(Global.BASE_DIR, "data", "sparkrdd", "05", "movies.dat").toAbsolutePath
        val ratingsPath: Path = Paths.get(Global.BASE_DIR, "data", "sparkrdd", "05", "ratings.dat").toAbsolutePath

        val sparkSession: SparkSession = SparkGlobal.getSparkSession(this.getClass.getName)

        // 对电影进行聚合，平均得分超过4.0的电影列表
        val minRating = 4.0
        val ratings = sparkSession.sparkContext.textFile(ratingsPath.toString)
          .map(line => line.trim().split("::"))
          .filter(line => line.length==4)
          .map(line => (line(1), (line(2).toInt, 1)))
          .reduceByKey((v1, v2) => (v1._1+v2._1, v1._2+v2._2))  // 求和、计数
          .mapValues(v => v._1/v._2)
          .filter(x => x._2 >= minRating)
        // (7,4)
        ratings.foreach(println)

        // 电影详情
        val movies = sparkSession.sparkContext.textFile(moviesPath.toString)
          .map(line => line.trim().split("::"))
          .filter(line => line.length==3)
          .map(line => (line(0), (line(1), line(2))))
        // (7,(Sabrina (1995),Comedy|Romance))
        movies.foreach(println)

        // 连接
        val res = ratings
          .keyBy(x => x._1)
          .join(movies)
          .map(x => (x._1, x._2._2._1, x._2._2._2, x._2._1._2))
        // (7,Sabrina (1995),Comedy|Romance,4)
        res.foreach(println)
    }
}
