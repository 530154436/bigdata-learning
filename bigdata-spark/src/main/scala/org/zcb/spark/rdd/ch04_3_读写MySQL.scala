package org.zcb.spark.rdd
import org.apache.spark.sql.SparkSession
import org.apache.spark.rdd.{JdbcRDD, RDD}

import java.sql.DriverManager

/**
 *  #### 读写MySQL
 *  ```sql
 *create table student (id int(4), name char(20), gender char(4), age int(4));
 *  insert into student values(1,'Xueqian','F',23);
 *  insert into student values(2,'Weiliang','M',24);
 *  ```
 */
object ch04_3_读写MySQL {

    def readFromMySQL(sparkSession: SparkSession): RDD[Any] = {
        sparkSession.read
        val inputMySQL: RDD[Any] = new JdbcRDD(
            sparkSession.sparkContext,
            () => {
                Class.forName("com.mysql.jdbc.Driver")
                DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/spark?useUnicode=true&characterEncoding=utf8&useSSL=true",
                    "root",
                    "123456"
                )
                //root是数据库用户名，123456是密码
            },
            "SELECT * FROM student where id >= ? and id <= ?;",
            1, //设置条件查询中id的下界
            2, //设置条件查询中id的上界
            1, //设置分区数
            r => (
                r.getInt(1),
                r.getString(2),
                r.getString(3),
                r.getInt(4)
            )
        )
        println("readFromMySQL", inputMySQL.count())
        inputMySQL.foreach(println)
        inputMySQL
    }

    def writeToMySQL(sparkSession: SparkSession): Unit = {
        Class.forName("com.mysql.jdbc.Driver")
        val rddData = sparkSession
            .sparkContext
            .parallelize(
                List(
                    (3, "Rongcheng", "M", 26),
                    (4, "Guanhua", "M", 27)
                )
            )
        rddData.foreachPartition((iter: Iterator[(Int, String, String, Int)]) => {
            val conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/spark?useUnicode=true&characterEncoding=utf8&useSSL=true",
                "root",
                "123456"
            )
            conn.setAutoCommit(false)
            val preparedStatement = conn.prepareStatement(
                "INSERT INTO student(id,name,gender,age) VALUES (?,?,?,?)"
            )
            iter.foreach(t => {
                preparedStatement.setInt(1, t._1)
                preparedStatement.setString(2, t._2)
                preparedStatement.setString(3, t._3)
                preparedStatement.setInt(4, t._4)
                preparedStatement.addBatch()
            })
            preparedStatement.executeBatch()
            conn.commit()
            conn.close()
        })
    }

    def main(args: Array[String]): Unit = {
        val sparkSession = SparkGlobal.getSparkSession("ReadWriteMySQL")
        readFromMySQL(sparkSession)
        writeToMySQL(sparkSession)
    }
}
