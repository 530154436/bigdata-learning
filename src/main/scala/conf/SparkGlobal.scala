package conf
import org.apache.spark.sql.SparkSession

object SparkGlobal {
    val sparkSession: SparkSession = SparkSession
      .builder()
      .appName(this.getClass.getName)
      .config("spark.master", "local[4]") // 需设置spark.master为local[N]才能直接运行，N为并发数
      .enableHiveSupport()
      .getOrCreate()

    def getSparkSession(name: String): SparkSession = {
        val sparkSession: SparkSession = SparkSession
          .builder()
          .appName(name)
          .config("spark.master", "local[4]") // 需设置spark.master为local[N]才能直接运行，N为并发数
          .enableHiveSupport()
          .getOrCreate()
        sparkSession
    }

}
