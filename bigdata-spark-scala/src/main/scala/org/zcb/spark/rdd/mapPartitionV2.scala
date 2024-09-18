package org.zcb.spark.rdd

import org.apache.spark.sql.Row
import org.zcb.spark.SparkGlobal
/**
 * mapPartition高效用法
 * 自定义一个迭代器类，无需缓存数据
 * 参考：https://zhuanlan.zhihu.com/p/41879924
 */
class BatchIterator(iter: Iterator[Row], batchSize: Int, threshold: Int = 0) extends Iterator[(String, String)] {
    private var currentBatch: List[(String, String)] = List.empty[(String, String)]
    private var currentIndex: Int = 0

    override def hasNext: Boolean = {
        // 当前批次已处理完，获取下一个批次的数据、重置索引
        if (currentIndex >= currentBatch.length) {
            currentBatch = getNextBatch
            currentIndex = 0
        }

        // 保证下一个批次批次非空
        while (currentBatch.isEmpty && iter.hasNext) {
            currentBatch = getNextBatch
            currentIndex = 0
        }

        // 说明当前批次
        currentIndex < currentBatch.length || iter.hasNext
    }

    override def next(): (String, String) = {
        val data = currentBatch(currentIndex)
        currentIndex += 1
        data
    }

    private def getNextBatch: List[(String, String)] = {
        var count = 0
        val raw = collection.mutable.ListBuffer[Long]()
        val batch = collection.mutable.ListBuffer[(String, String)]()

        // 组装一批数据
        while (count < batchSize && iter.hasNext) {
            val data = iter.next()
            val id = data.getAs[Long](0)
            raw.append(id)
            count += 1
        }

        // 对该批次的数据调用接口进行处理(包括调用接口啥的)
        // val entities: Array[String] = server.extract_ner(sentences.toArray)
        for(id <- raw){
            if(id >= threshold){
                batch.append((id.toString, (id+1).toString))
            }
        }
        batch.toList
    }
}


object ch00_mapPartitionV2 {
    def main(args: Array[String]): Unit = {
        val spark = SparkGlobal.getSparkSession("MapPartitionV2")

        // 生成示例数据 RDD
        val inputDF = spark.range(1, 100).toDF("id").cache()

        // 使用自定义迭代器处理每个分区的数据
        import spark.implicits._
        for (threshold <- 0 to 100) {
            val res = inputDF.mapPartitions { iter =>
                val batchSize = 10
                new BatchIterator(iter, batchSize, threshold)
            }.toDF("id", "id_plus_1")
            println(s"阈值: ${threshold}, 数量: ${res.count()}")
        }
    }
}


