package cases

import conf.{Global, SparkGlobal}
import org.apache.spark.sql.functions.{col, collect_list, concat_ws, date_format, to_date}
import org.apache.spark.sql.{DataFrame, SparkSession}
import utils.{AggFunc4DF, DateUtils, UdfFunc}

import java.nio.file.{Path, Paths}
import scala.collection.mutable


object qzd_智慧面板_专利资讯_df {
    def readFromCsvFile(sparkSession: SparkSession, fName: String): DataFrame = {
        val file: Path = Paths
            .get(Global.BASE_DIR, "data", "smartPanel", fName)
            .toAbsolutePath

        // option("quote", "\"") 和 option("escape", "\"") 用于正确处理包含双引号的字段。
        val df: DataFrame = sparkSession.read
            .option("header", "true")
            .option("quote", "\"")
            .option("escape", "\"")
            //.option("inferSchema", "true")
            .csv(file.toString)
            .filter(row => row.getAs[String]("company_name")=="武汉华星光电技术有限公司")
        //df.show()
        df
    }

    def main(args: Array[String]): Unit = {
        val sparkSession = SparkGlobal.getSparkSession("智慧面板-统计聚合")
        val df: DataFrame = readFromCsvFile(sparkSession, "dws_smart_panel_patent_recall_by_enterprise.csv")
            .withColumn(
                "publish_date",
                date_format(
                    to_date(col("publish_date"), "yyyy-MM-dd HH:mm:ss"),
                    "yyyy-MM-dd"
                ).alias("publish_date")
            )
        println(s"候选集共有: ${df.count()}条数据")
        val groupByCols: Seq[String] = Seq("company_id", "company_name", "recall_type", "search_name", "patent_event_type")

        // ---------------------------------------------------------------------------------------
        // 统计专利量、主要领域、主要技术主题、主要功效
        // ---------------------------------------------------------------------------------------
        val df_count = AggFunc4DF.getCount(df, groupByCols, resultColName="patent_count")
        //df_count.show()
        val df_top3_tech_word = AggFunc4DF.getTopNKeyword(df, groupByCols, aggColName="tech_word")
        val df_top3_product_word = AggFunc4DF.getTopNKeyword(df, groupByCols, aggColName="product_word")
        val df_top3_stand_effect_word = AggFunc4DF.getTopNKeyword(df, groupByCols, aggColName="stand_effect_word")
        //df_top3_tech_word.show()
        //df_top3_product_word.show()
        //df_top3_stand_effect_word.show(100)

        // ---------------------------------------------------------------------------------------
        // 统计企业的新产品词、新功效词
        // 存在数据相关依赖，只能collect到master对日期进行排序后聚合
        // ---------------------------------------------------------------------------------------
        // 存量部分30天之前的历史关键词
        val keyword30DayAgo: mutable.Map[List[String], mutable.Set[String]] = mutable.Map()
        val dfKeywords30DayAgo: DataFrame = readFromCsvFile(sparkSession, "dws_algo_rs_smart_panel_ent_keywords_30d_ago.csv")
        //dfKeywords30DayAgo.show()
        dfKeywords30DayAgo.collect().foreach(row => {
            val company_id = row.getAs[String]("company_id")
            val eid = row.getAs[String]("company_name")
            val search_name = row.getAs[String]("search_name")
            val word_type = row.getAs[String]("word_type")
            val keywords = row.getAs[String]("keywords")
            val set: mutable.Set[String] = mutable.Set()
            if(keywords!=null && keywords.nonEmpty){
                keywords.split(";").foreach(word => set += word)
                keyword30DayAgo += (List(company_id, eid, search_name, word_type) -> set)
            }
        })
        //keyword30DayAgo.foreach(println)

        // 合并增量部分（30天内）：必须先按日期排序
        val keywordAll: mutable.Map[List[String], mutable.Set[String]] = mutable.Map()
        val keywordDiff: mutable.Map[List[String], String] = mutable.Map()
        val aggDiffKeys = List("company_id", "company_name", "search_name", "publish_date")
        val df_agg = df.groupBy(aggDiffKeys.map(col): _*)
            .agg(
                concat_ws(";", collect_list(col("product_word"))).alias("product_word"),
                concat_ws(";", collect_list(col("stand_effect_word"))).alias("stand_effect_word")
            )
        df_agg.collect().sortBy(row => row.getAs[String]("publish_date")).foreach(row => {
            val company_id = row.getAs[String]("company_id")
            val eid = row.getAs[String]("company_name")
            val search_name = row.getAs[String]("search_name")
            val publish_date = row.getAs[String]("publish_date")
            val product_word = row.getAs[String]("product_word")
            val stand_effect_word = row.getAs[String]("stand_effect_word")

            // 当前日期-1~30天前日期
            val curDate = DateUtils.parseDate(publish_date)
            val curDateStr = DateUtils.formatDate(curDate)
            val date1dAgoStr = DateUtils.formatDate(DateUtils.getDaysAgo(curDate, 1))
            val date30dAgoStr = DateUtils.formatDate(DateUtils.getDaysAgo(curDate, 30))

            val wordlist = List(("功效标准词", stand_effect_word), ("技术主题标准词", product_word))
            for((word_type, words) <- wordlist){
                val filterSet: mutable.Set[String] = mutable.Set()
                val key30dAgo = List(company_id, eid, search_name, word_type)
                // 30天前的词
                if(keyword30DayAgo.contains(key30dAgo)){
                    filterSet ++= keyword30DayAgo(key30dAgo)
                }
                // 30前~T-1Day的词
                DateUtils.getDatesBetween(date30dAgoStr, date1dAgoStr).foreach(date => {
                    val key1dAgoDates = key30dAgo :+ date
                    if(keywordAll.contains(key1dAgoDates)){
                        filterSet ++= keywordAll(key1dAgoDates)
                    }
                })

                // 取当前日期的专利关键词：1.过滤历史关键词，2.聚合求词频前三
                val key = key30dAgo :+ curDateStr
                if(words!=null && words.nonEmpty){
                    val wordCounts = words
                        .split(";")
                        .filter(x => !filterSet.contains(x))
                        .groupBy(identity)
                        .mapValues(_.length)
                    val diffTop3: String = wordCounts
                        .toSeq
                        .sortBy(-_._2)
                        .take(3)
                        .map { case (word, count) => s"$word:$count" }
                        .mkString(";")
                    // 更新结果集合
                    keywordDiff += (key -> diffTop3)
                    println(key, diffTop3)

                    // 更新全集
                    filterSet ++= wordCounts.keys.toSet
                    keywordAll += (key -> filterSet)
                }
            }
        })
        //(List(433207268495855616, 武汉华星光电技术有限公司, 武汉天马微电子有限公司, 技术主题标准词, 2024-07-09),盖板显示装置:1)
        //(List(4332072, 武汉华星光电技术有限公司, 武汉天马微电子有限公司, 功效标准词, 2024-07-09),制作简单:2;尺寸小:2;散热效果好:2)
        //keywordDiff.foreach(println)
        keywordDiff
        val df_agg1 = sparkSession
            .createDataFrame(
                keywordDiff.toSeq.map { case (keywords, diff) =>
                    (keywords.head, keywords(1), keywords(2), keywords(3), keywords(4), diff)
                }
            )
            .toDF("company_id", "company_name", "search_name", "word_type", "publish_date", "word_diff_top3")
        val df_agg11 = df_agg1
            .filter(row=>row.getAs[String]("word_type")=="功效标准词").drop("word_type")
            .withColumnRenamed("word_diff_top3", "product_word_diff_top3")
        val df_agg12 = df_agg1
            .filter(row=>row.getAs[String]("word_type")=="技术主题标准词").drop("word_type")
            .withColumnRenamed("word_diff_top3", "stand_effect_word_diff_top3")

        // ---------------------------------------------------------------------------------------
        // 生成最终的结果
        // 日期	专利量	主要领域	主要技术主题	主要功效	新产品词	新产品词功效	单篇专利的技术原理	摘要
        // ---------------------------------------------------------------------------------------
        val df_result: DataFrame = df
            .join(df_count, groupByCols, "right")
            .join(df_top3_tech_word, groupByCols, "right")
            .withColumn("tech_word_in_top3", UdfFunc.findWordInOthers(col("tech_word"), col("tech_word_top3")))
            .join(df_top3_product_word, groupByCols, "right")
            .withColumn("product_word_in_top3", UdfFunc.findWordInOthers(col("product_word"), col("product_word_top3")))
            .join(df_top3_stand_effect_word, groupByCols, "right")
            .withColumn("stand_effect_word_in_top3", UdfFunc.findWordInOthers(col("stand_effect_word"), col("stand_effect_word_top3")))
            .join(df_agg11, aggDiffKeys, "right")
            .join(df_agg12, aggDiffKeys, "right")
        df_result.show(1000)
    }
}
