package cases

import conf.{Global, SparkGlobal}
import org.apache.spark.sql.{DataFrame, SparkSession, functions}
import play.api.libs.json.{JsValue, Json}
import utils.AggFunc4DF
import java.nio.file.{Path, Paths}


object qzd_智慧面板_专利资讯_df {
    def readFromCsvFile(sparkSession: SparkSession): DataFrame = {
        val file: Path = Paths
            .get(Global.BASE_DIR, "data", "smartPanel", "dws_smart_panel_patent_recall_by_enterprise.csv")
            .toAbsolutePath

        // option("quote", "\"") 和 option("escape", "\"") 用于正确处理包含双引号的字段。
        val df: DataFrame = sparkSession.read
            .option("header", "true")
            .option("quote", "\"")
            .option("escape", "\"")
            .option("inferSchema", "true")
            .csv(file.toString)
            .filter(row => row.getAs[String]("company_name")=="天津国安盟固利新材料科技股份有限公司")
        //df.show()
        df
    }

    def main(args: Array[String]): Unit = {
        val sparkSession = SparkGlobal.getSparkSession("智慧面板-专利量")
        val df: DataFrame = readFromCsvFile(sparkSession)

        import sparkSession.implicits._
        val df_map: DataFrame = df.rdd.map(row => {
            val company_id: String = row.getAs[Long]("company_id").toString
            val company_name: String = row.getAs[Long]("company_name").toString
            val recall_type: String = row.getAs[String]("recall_type")
            val patent_event_type: String = row.getAs[String]("patent_event_type")
            val recall_detail: String = row.getAs[String]("recall_detail")
            val tech_word: String = row.getAs[String]("tech_word")
            val product_word: String = row.getAs[String]("product_word")
            val stand_effect_word: String = row.getAs[String]("stand_effect_word")
            // 解析json对象
            val json: JsValue = Json.parse(recall_detail)
            val peer_names = (json \ "peer_names").asOpt[String]
            var compareTo = company_name
            if(recall_type != "本企业" && peer_names.isDefined){
                compareTo = peer_names.get
            }
            (company_id, company_name, recall_type, patent_event_type, compareTo,
                tech_word, product_word, stand_effect_word)
        }).toDF("company_id", "company_name", "recall_type", "patent_event_type", "compare_to",
            "tech_word", "product_word", "stand_effect_word")
        df_map.show(1000)

        println(s"候选集共有: ${df_map.count()}条数据")
        val groupByCols: Seq[String] = Seq("company_id", "company_name", "recall_type", "patent_event_type", "compare_to")

        // 统计专利量
        //val df_count = AggFunc4DF.getCount(df_map, groupByCols, aggColName="patent_count")
        //df_count.show()

        // 统计主要领域、主要技术主题、主要功效的top3关键词
        val df_top3_tech_word = AggFunc4DF.getTopNKeyword(df_map, groupByCols, aggColName="tech_word", topN=3)
        df_top3_tech_word.show()
    }
}
