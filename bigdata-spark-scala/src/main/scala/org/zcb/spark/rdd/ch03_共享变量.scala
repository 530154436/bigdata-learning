package org.zcb.spark.rdd
import org.apache.spark.sql.SparkSession
import org.zcb.spark.SparkGlobal

/**
 *  ### 共享变量
 *  Spark中的两个重要抽象是RDD和`共享变量`。<br>
 *  当Spark在集群的多个不同节点的多个任务上并行运行一个函数时，它会把函数中涉及到的每个变量，在每个任务上都生成一个副本。<br>
 *  但是，有时候，需要在`多个任务之间共享变量`，或者在任务（Task）和任务控制节点（Driver Program）之间共享变量。<br>
 *  为了满足这种需求，Spark提供了两种类型的变量：<br>
 *  + 广播变量（broadcast variables）：把变量在所有节点的内存之间进行共享
 *  + 累加器（accumulators）：支持在所有不同节点之间进行累加计算（比如计数或者求和）
 *
 *  #### 广播变量
 *  允许程序开发人员`在每个机器上缓存一个只读的变量`，而不是为机器上的每个任务都生成一个副本。<br>
 *  Spark的“行动”操作会跨越多个阶段（stage），对于每个阶段内的所有任务所需要的公共数据，Spark都会自动进行广播。
 *  + 可以通过调用`SparkContext.broadcast(v)`来从一个普通变量v中创建一个广播变量。
 *  + 广播变量是对普通变量v的一个包装器，通过调用`value方法`就可以获得广播变量的值。
 *  + 广播变量被创建以后，那么在集群中的任何函数中，都应该使用广播变量broadcastVar的值，而不是使用v的值，这样就不会把v重复分发到这些节点上
 *  + 一旦广播变量创建后，普通变量v的值就不能再发生修改，从而确保所有节点都获得这个广播变量的相同的值
 *
 *  #### 累加器
 *  累加器是仅仅被相关操作累加的变量，通常可以被用来实现计数器（counter）和求和（sum）。<br>
 *  Spark原生地支持数值型（numeric）的累加器，程序开发人员可以编写对新类型的支持。
 *  + 数值型累加器：可以通过调用SparkContext.longAccumulator()或者SparkContext.doubleAccumulator()来创建。
 *  + 运行在集群中的任务，就可以使用add方法来把数值累加到累加器上，但是，这些任务只能做累加操作，不能读取累加器的值。<br>
 *  只有任务控制节点（Driver Program）可以使用value方法来读取累加器的值
 */

object ch03_共享变量 {

    def broadCastValue(sparkSession: SparkSession): Unit = {
        // 创建广播变量
        val broadV = sparkSession.sparkContext.broadcast(3)
        val lists = List(1, 2, 3, 4, 5)
        val listRDD = sparkSession.sparkContext.parallelize(lists)
        val results = listRDD.map(x => x * broadV.value)
        results.foreach(x => println("broadCastValue: " + x))
    }

    def accumulator(sparkSession: SparkSession): Unit = {
        // 创建广播变量
        val accum = sparkSession.sparkContext.longAccumulator("MyAccumulator")
        val lists = List(1, 2, 3, 4, 5)
        val listRDD = sparkSession.sparkContext.parallelize(lists)
        listRDD.foreach(x => accum.add(x))
        println(s"accumulator: ${accum.value}")
    }

    def main(args: Array[String]): Unit = {
        val sparkSession = SparkGlobal.getSparkSession()
        broadCastValue(sparkSession)
        accumulator(sparkSession)
    }
}
