package org.zcb.spark.utils

import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.udf

object UdfFunc {

    /**
     * 解析top3并查找words的值
     *
     * @return DataFrame
     */
    val findWordInOthers: UserDefinedFunction = udf((words: String, top3: String) => {
        val techWordsSet = words.split(";").toSet
        val top3Map = top3.split(";").map(_.split(":")).map(arr => (arr(0), arr(1).toInt)).toMap

        techWordsSet.collectFirst {
            case techWord if top3Map.contains(techWord) => techWord
        }.orNull // 如果没有找到，返回0（或根据需要返回null）
    })
}
