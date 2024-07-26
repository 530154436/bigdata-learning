package org.zcb.common.utils

import play.api.libs.json._


object JsonUtil {
    implicit val listWrites: Writes[List[Any]] = new Writes[List[Any]] {
        def writes(list: List[Any]): JsValue = {
            JsArray(
                list.map {
                    case v: Int => JsNumber(v)
                    case v: Double => JsNumber(v)
                    case v: Option[Double] => JsNumber(v.get)
                    case v: Option[Int] => JsNumber(v.get)
                    case v: String => JsString(v)
                    case _ => JsNull
                })
        }
    }

    implicit val listListWrites: Writes[List[List[Any]]] = new Writes[List[List[Any]]] {
        def writes(list: List[List[Any]]): JsValue = {
            JsArray(list.map {
                case v: List[_] => Json.toJson(v)(listWrites)
                case _ => JsNull
            })
        }
    }

    /**
     * 数据格式
     * {"projectCount":46,"totalMoney":2665,"maxSubsidyMoney":1000,"projects":[["332564141875134464","浙江省“浙江制造精品”认定",10,30]]}
     */
    implicit val mapWrites: Writes[Map[String, Any]] = new Writes[Map[String, Any]] {
        def writes(map: Map[String, Any]): JsValue = {
            JsObject(map.map {
                case (key, value) =>
                    key -> (value match {
                        case v: Int => JsNumber(v)
                        case v: Double => JsNumber(v)
                        case v: Option[Double] => JsNumber(v.get)
                        case v: Option[Int] => JsNumber(v.get)
                        case v: String => JsString(v)
                        case v: List[List[_]] => Json.toJson(v)(listListWrites)
                        case _ => JsNull
                    }): (String, JsValue)
            }.toSeq)
        }
    }

    /**
     * JSON反序列化器：Option[String]
     */
    implicit val optionStringReads: Reads[Option[String]] = Reads.optionWithNull[String]
    implicit val seqSeqOptionStringReads: Reads[Seq[Seq[Option[String]]]] = Reads.seq(
        Reads.seq(optionStringReads)
    )

    /**
     * 将Map转为Json格式
     */
    def mapToJsonString(map: Map[String, Any]): String = {
        // 将Map转换为Json
        val json: JsValue = Json.toJson(map)

        // 将Json转换为字符串
        Json.stringify(json)
    }
}


object JsonUtilTest {

    def testWrite(): Unit = {
        //val data: Option[Map[String, Any]] = Some(Map(
        //    "projectCount" -> 46,
        //    "totalMoney" -> 2665.0,
        //    "maxSubsidyMoney" -> 1000.0,
        //    "projects" -> List(
        //        List("332564141875134464", "浙江省“浙江制造精品”认定", 10.0, 30.0),
        //        List("728906177360195584", "制造业中小微企业延缓缴纳部分税费", null, null),
        //        List("728978037611458560", "制造业及部分服务业企业符合条件的仪器、设备加速折旧", null, null)
        //    )
        //))
        //println(JsonUtil.mapToJsonString(data.get))

        val test = "121"
        val json: String = Json.stringify(Json.toJson(test))
        println(json)
        val jv: String = Json.parse(json).as[String]
        println(jv)
    }

    def testRead(): Unit = {
        val jsonString =
            """{"projectCount":22,"totalMoney":30.0,"maxSubsidyMoney":null,
              |"projects":[["332563714639134720","北京市企业科技研究开发机构认定",null,"100.0"]]}""".stripMargin
        // 解析JSON字符串为JsValue
        val json: JsValue = Json.parse(jsonString)

        // 提取字段的值
        val projectCount = (json \ "projectCount").as[Int]
        val totalMoney = (json \ "totalMoney").asOpt[Double]
        val maxSubsidyMoney = (json \ "maxSubsidyMoney").asOpt[Double]
        val projects = (json \ "projects").as[Seq[Seq[Option[String]]]](JsonUtil.seqSeqOptionStringReads)

        // 打印提取的值
        println(s"projectCount: $projectCount")
        println(s"totalMoney: $totalMoney")
        println(s"maxSubsidyMoney: $maxSubsidyMoney")
        println(s"maxSubsidyMoney: $projects")
    }

    def main(args: Array[String]): Unit = {
        testWrite()
        //testRead()
    }
}