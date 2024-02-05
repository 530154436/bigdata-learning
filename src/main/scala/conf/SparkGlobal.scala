package conf
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object SparkGlobal {
    def getSparkSession(name: String = "SparkApp"): SparkSession = {
        val sparkSession: SparkSession = SparkSession
          .builder()
          .appName(name)
          .config("spark.master", "local[2]") // 需设置spark.master为local[N]才能直接运行，N为并发数
          .enableHiveSupport()
          .getOrCreate()
        sparkSession
    }

    def getSparkConf(name: String = "SparkApp", conf: Map[String, String] = Map.empty): SparkConf = {
        val sparkConf = new SparkConf()
            .setAppName(name)
            .setMaster("local[2]") // 设置为本地运行模式，2个线程，一个监听，另一个处理数据
        conf.foreach(kv => sparkConf.set(kv._1, kv._2))
        sparkConf
    }
}
