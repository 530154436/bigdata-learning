package spark.rdd

import conf.Global
import org.apache.commons.io.FileUtils
import org.apache.spark.Partitioner
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel.MEMORY_AND_DISK

import java.nio.file.{Path, Paths}

/**
### RDD操作
#### 转换操作
对于RDD而言，每一次转换操作都会产生不同的RDD，供给下一个“转换”使用。<br>
转换得到的RDD是`惰性求值`的，即整个转换过程只是记录了转换的轨迹，并不会发生真正的计算，只有遇到行动操作时，才会发生真正的计算。<br>
常见的转换操作（Transformation API）：
+ filter(func)：筛选出满足函数func的元素，并返回一个新的数据集
+ map(func)：将每个元素传递到函数func中，并将结果返回为一个新的数据集
+ flatMap(func)：与map()相似，但每个输入元素都可以映射到0或多个输出结果
+ groupByKey()：应用于(K,V)键值对的数据集时，返回一个新的(K, Iterable)形式的数据集
+ reduceByKey(func)：应用于(K,V)键值对的数据集时，返回一个新的(K, V)形式的数据集，其中的每个值是将每个key传递到函数func中进行聚合

#### 行动操作
Spark程序执行到行动操作时，才会执行真正的计算，从文件中加载数据，完成一次又一次转换操作，最终，完成行动操作得到结果。<br>
下面列出一些常见的行动操作（Action API）：
+ count() 返回数据集中的元素个数
+ collect() 以数组的形式返回数据集中的所有元素
+ first() 返回数据集中的第一个元素
+ take(n) 以数组的形式返回数据集中的前n个元素
+ reduce(func) 通过函数func（输入两个参数并返回一个值）聚合数据集中的元素
+ foreach(func) 将数据集中的每个元素传递到函数func中运行

#### 持久化
在Spark中，RDD采用惰性求值的机制，每次遇到行动操作，都会从头开始执行计算。<br>
每次调用行动操作，都会触发一次从头开始的计算。这对于迭代计算而言，代价是很大的，迭代计算经常需要多次重复使用同一组数据。<br>
+ 可以通过持久化（缓存）机制避免这种重复计算的开销
+ 可以使用persist()方法对一个RDD标记为持久化
+ 之所以说“标记为持久化”，是因为出现persist()语句的地方，并不会马上计算生成RDD并把它持久化，而是要等到遇到第一个行动操作触发真正计算以后，才会把计算结果进行持久化
+ 持久化后的RDD将会被保留在计算节点的内存中被后面的行动操作重复使用

persist()的圆括号中包含的是持久化级别参数：
+ persist(MEMORY_ONLY)：表示将RDD作为反序列化的对象存储于JVM中，如果内存不足，就要按照LRU原则替换缓存中的内容
+ persist(MEMORY_AND_DISK)表示将RDD作为反序列化的对象存储在JVM中，如果内存不足，超出的分区将会被存放在硬盘上
+ 一般而言，使用`cache()`方法时，会调用persist(MEMORY_ONLY)
+ 可以使用unpersist()方法手动地把持久化的RDD从缓存中移除

#### 分区
RDD是弹性分布式数据集，通常RDD很大，会被分成很多个分区，分别保存在不同的节点上。<br>
**为什么要分区？**
+ 增加并行度
+ 减少通信开销

只有当数据集多次在诸如连接这种基于键的操作中使用时，分区才会有帮助。若RDD只需要扫描一次，就没有必要进行分区处理。<br>
能从spark分区中获取的操作有：
+ cogroup()、groupWith()
+ join()、leftOuterJoin()、rightOuterJoin()、groupByKey()、
+ reduceByKey()、combineByKey()以及lookup()

RDD分区的一个`分区原则`是使得分区的个数尽量等于集群中的CPU核心（core）数目。<br>
对于不同的Spark部署模式而言，都可以通过设置`spark.default.parallelism`这个参数的值，来配置默认的分区数目，一般而言：<br>
+ 本地模式：默认为本地机器的CPU数目，若设置了local[N],则默认为N
+ Apache Mesos：默认的分区数为8
+ Standalone或YARN：max(集群中所有CPU核心数目总和, 2)

如何手动设置分区：
1. 创建 RDD 时：在调用 textFile 和 parallelize 方法时候手动指定分区个数。<br>
   对于parallelize而言，如果没有在方法中指定分区数，则默认为spark.default.parallelism<br>
   对于textFile而言，如果没有在方法中指定分区数，则默认为min(spark.default.parallelism,2)<br>
   如果是从HDFS中读取文件，则分区数为文件分片数(比如，128MB/片)<br>
2, 通过转换操作得到新 RDD 时：直接调用 repartition 方法

#### 打印元素
在实际编程中，经常需要把RDD中的元素打印输出到屏幕上（标准输出stdout），一般会采用语句：
+ rdd.foreach(println)
+ rdd.map(println)

当采用集群模式执行时，在worker节点上执行打印语句是输出到worker节点的stdout中，而不是输出到任务控制节点Driver Program中。<br>
因此，任务控制节点Driver Program中的stdout是不会显示打印语句的这些输出内容的。<br>
为了能够把所有worker节点上的打印输出信息也显示到DriverProgram中，可以使用`collect()方法`，比如：
+ rdd.collect().foreach(println)：由于collect()方法会把各个worker节点上的所有RDD元素都抓取到Driver Program中，可能会导致内存溢出。
+ dd.take(100).foreach(println)：当只需要打印RDD的部分元素时，可以采用该语句
 */

//自定义分区类，需继承Partitioner类
class UsrIdPartitioner(numParts:Int) extends Partitioner{
    //覆盖分区数
    override def numPartitions: Int = numParts
    //覆盖分区号获取函数
    override def getPartition(key: Any): Int = {
        key.toString.toInt % 10
    }
}

object ch01_2_RDD操作 {
    def map_reduce(sparkSession: SparkSession): Unit = {
        // 找出文本文件中单行文本所包含的单词数量的最大值
        val file: Path = Paths.get(Global.BASE_DIR, "data", "wordcount", "word1.txt").toAbsolutePath
        val lines = sparkSession.sparkContext.textFile(file.toString)
        val maxLength = lines.map(line => line.split(" ").length)
          .reduce((a, b) => if (a > b) a else b)
        println(maxLength)
    }

    def persist(sparkSession: SparkSession): Unit = {
        val list = List("Hadoop","Spark","Hive")
        val rdd = sparkSession.sparkContext.parallelize(list)
        rdd.persist(MEMORY_AND_DISK)

        println(rdd.count())
        println(rdd.collect().mkString(","))
    }

    /**
     * 实例：根据key值的最后一位数字，写到不同的文件
     *  例如：
     *  10写入到part-00000
     *  11写入到part-00001
     *  ...
     *  19写入到part-00009
     */
    def partition(sparkSession: SparkSession): Unit = {
        // 模拟5个分区的数据
        val data = sparkSession.sparkContext.parallelize(1 to 10,5)
        //根据尾号转变为10个分区，分写到10个文件
        val dataRP = data.map((_, 1)).partitionBy(new UsrIdPartitioner(10))

        println("partition")
        println(dataRP)
        dataRP.partitions.foreach(println)

        // 判断文件是否存在
        val file: Path = Paths.get(Global.BASE_DIR, "data", "output", "partition.txt").toAbsolutePath
        if (file.toFile.exists()) {
            FileUtils.deleteDirectory(file.toFile)
        }
        dataRP.saveAsTextFile(file.toString)
    }

    def main(args: Array[String]): Unit = {
        map_reduce(SparkGlobal.getSparkSession())
        persist(SparkGlobal.getSparkSession())
        partition(SparkGlobal.getSparkSession())
    }
}
