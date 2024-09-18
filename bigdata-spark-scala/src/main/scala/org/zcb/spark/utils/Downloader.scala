package org.zcb.spark.utils

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import scopt.OptionParser


case class DownloadParam(dbName: String = "",
                         fromHiveTbName: String = "",
                         columns: String = "",
                         limit: Int = 0,
                         savePath: String = "output.csv")

object Downloader {

    /** *
     * 解析命令行参数
     *
     * -- 招投标
     * --dbName algo_recommend_dev --fromHiveTbName bigdata_application.ods_pingan_company_bid_main --columns id,title,original_content,purchaser,bid_win,notice_type --limit 10 --savePath /tmp/zhengchubin/招投标.csv
     */
    val parser: OptionParser[DownloadParam] = new OptionParser[DownloadParam](this.getClass.getName) {
        head(this.getClass.getName)
        opt[String]('d', "dbName")
            .required().valueName("<dbName>").action((x, c) => c.copy(dbName = x))
            .text("数据库")
        opt[String]('f', "fromHiveTbName")
            .required().valueName("<fromHiveTbName>").action((x, c) => c.copy(fromHiveTbName = x))
            .text("来源hive表名")
        opt[String]('c', "columns")
            .optional().valueName("<columns>").action((x, c) => c.copy(columns = x))
            .text("字段名称,逗号分隔")
        opt[Int]('l', "limit")
            .optional().valueName("<limit>").action((x, c) => c.copy(limit = x))
            .text("返回数据大小,默认全部")
        opt[String]('l', "savePath")
            .optional().valueName("<savePath>").action((x, c) => c.copy(savePath = x))
            .text("保存的文件路径")
    }

    def process(param: DownloadParam, spark: SparkSession): Unit = {
        println(param)

        // 加载数据
        spark.sql(s"use ${param.dbName}")

        val sb = new StringBuilder()
        sb.append(s"SELECT ${param.columns}\n")
        sb.append(s"FROM ${param.fromHiveTbName}\n")
        if(param.limit != 0){
            sb.append(s"FROM LIMIT ${param.limit}\n")
        }
        val _sql = sb.toString()
        println(_sql)

        val df = spark.sql(_sql)
        df.show()

        // 使用 DataFrameWriter 写入 CSV 文件
        df.coalesce(1) // 保存为单个文件
            .write
            .mode("overwrite") // 写入模式，这里使用覆盖模式
            .option("header", "true")
            .option("charset", "UTF-8")
            .format("csv")
            .save(s"${param.savePath}")
        println(s"下载成功: ${param.savePath}")
    }

    def main(args: Array[String]): Unit = {
        // Kerberos认证
        val sparkConf: SparkConf = KerberosAuth.login(this.getClass.getSimpleName.stripSuffix("$"))
        val spark: SparkSession = SparkSession
            .builder()
            .config(sparkConf)
            .enableHiveSupport()
            .config("org.zcb.hive.exec.dynamic.partition", "true")
            .config("org.zcb.hive.exec.dynamic.partition.mode", "nonstrict")
            .getOrCreate()

        parser.parse(args, DownloadParam()) match {
            case Some(param) => process(param, spark)
            case _ => throw new RuntimeException("params parse error.")
        }
    }
}
