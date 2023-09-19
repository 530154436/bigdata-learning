package sparkRDD

import conf.SparkGlobal
import org.apache.spark.sql.Row

object ch00_mapPartitionV1 {
    /**
     * mapPartition一般使用方法
     * 在mapPartition执行期间，在内存中定义一个数组并且将缓存所有的数据。假如数据集比较大，内存不足，会导致内存溢出，任务失败。
     */
    def batchProcessing(iter: Iterator[Row], batchSize: Int): Iterator[(String, String)] = {
        val raw = collection.mutable.ListBuffer[Long]()
        val batch = collection.mutable.ListBuffer[(String, String)]()
        while (iter.hasNext) {
            val data = iter.next()
            val id = data.getAs[Long](0)
            if (id >= 10) {
                raw.append(id)
            }
            if(raw.length >= batchSize || (!iter.hasNext && raw.nonEmpty)){
                // 对该批次的数据调用接口进行处理(包括调用接口啥的)
                // val entities: Array[String] = server.extract_ner(sentences.toArray)
                for (id <- raw) {
                    if (id >= 10) {
                        batch.append((id.toString, (id + 1).toString))
                    }
                }
                raw.clear()
            }
        }
        batch.toList.iterator
    }

    def main(args: Array[String]): Unit = {
        val spark = SparkGlobal.getSparkSession("MapPartitionV1")

        // 生成示例数据 RDD
        val inputDF = spark.range(1, 1000).toDF("id")

        // 使用自定义迭代器处理每个分区的数据
        import spark.implicits._
        val res = inputDF.mapPartitions { iter =>batchProcessing(iter, 100) }
          .toDF("id", "id_plus_1")
        res.show()
    }
}


