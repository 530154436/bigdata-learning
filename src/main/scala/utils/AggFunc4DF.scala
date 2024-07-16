package utils
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, count, desc, explode, rank, split}
import org.apache.spark.sql.DataFrame


object AggFunc4DF {

    /**
     * 计算给定分组的数量
     * @param df 数据帧
     * @param groupByCols 分组列名
     * @return DataFrame
     */
    def getCount(df: DataFrame,
                 groupByCols: Seq[String],
                 resultColName: String="count"
                ): DataFrame = {
        df.select(groupByCols.map(col): _*)
            .groupBy(groupByCols.map(col): _*)
            .agg(count("*").as(resultColName))
    }

    /**
     * 计算给定分组的topN关键词
     * @param df 数据帧
     * @param groupByCols 分组列名
     * @param aggColName 统计的列名
     * @param delimiter 分隔符
     * @param topN topN
     * @return DataFrame
     *
     * @example
     * Input:
     * company_id|tech_word
     * 862755675366961152|电池;电池外壳
     * 862755675366961152|用电系统;电池
     * Output:
     * company_id|tech_word_top3|count|rank
     * 862755675366961152|电池|2|1
     * 862755675366961152|电池外壳|1|2
     * 862755675366961152|用电系统|1|3
     */
    def getTopNKeyword(df: DataFrame,
                       groupByCols: Seq[String],
                       aggColName: String,
                       delimiter: String = ";",
                       topN: Int = 3
                       ): DataFrame = {
        val resultCol = s"${aggColName}_top${topN}"
        // 拆分关键词字符串并展开为多个行
        val explodedDF = df
            .select((groupByCols :+ aggColName).map(col): _*)
            .filter(row => row.getAs[String](aggColName)!=null&&row.getAs[String](aggColName)!="")
            .withColumn(resultCol, explode(split(col(aggColName), delimiter)))

        // 计算分组内每个关键词的词频
        val productCountDF = explodedDF.groupBy((groupByCols :+ resultCol).map(col): _*)
            .agg(count("*").alias("count"))

        // 计算每个分组的前N个关键词词频
        val windowSpec = Window
            .partitionBy(groupByCols.map(col): _*)
            .orderBy(desc("count"))
        val topNProductsDF = productCountDF.withColumn("rank", rank().over(windowSpec))
            .filter(s"rank <= ${topN}")
        topNProductsDF
    }
}
