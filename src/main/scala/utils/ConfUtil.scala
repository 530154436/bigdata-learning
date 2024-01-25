package utils
import org.apache.commons.codec.binary.Base64
import utils.RSAUtil.decrypt

case class RedisConfig(host: String, port: Int, auth: String, db: Int)
case class MysqlConfig(host: String, port: Int, user: String, pass: String, db: String)
case class ESConfig(host: String, port: Int, user: String, pass: String)

object ConfUtil {

    /**
     * 解析加密的配置信息，来自接口
     */
    def parseConfigMap(rsp: String): scala.collection.immutable.Map[String, Any] = {
        val configArrayByte = BigInt(rsp, 16).toByteArray
        val right_size = if (configArrayByte.length > 256) {
            256
        } else {
            configArrayByte.length
        }
        val config_decrypt = decrypt(configArrayByte.takeRight(right_size))
        val config_json = new String(Base64.decodeBase64(config_decrypt), "utf-8")
        //      import org.json4s._
        import org.json4s.jackson.JsonMethods.{parse => json_parse}
        val config_json_obj = json_parse(config_json)
        config_json_obj.values.asInstanceOf[scala.collection.immutable.Map[String, Any]]
    }

    def parseRedisConfig(rsp: String): RedisConfig = {
        val config_map = parseConfigMap(rsp)
        val redisHost = config_map.getOrElse("host", "").asInstanceOf[String]
        val redisPort = config_map.getOrElse("port", "").asInstanceOf[String].toInt
        val redisPass = config_map.getOrElse("password", "").asInstanceOf[String]
        val redisDb = config_map.getOrElse("database", "").asInstanceOf[String].toInt
        RedisConfig(redisHost, redisPort, redisPass, redisDb)
    }

    def parseMysqlConfig(rsp: String): MysqlConfig = {
        val config_map = parseConfigMap(rsp)
        val host = config_map.getOrElse("host", "").asInstanceOf[String]
        val port = config_map.getOrElse("port", "").asInstanceOf[String].toInt
        val user = config_map.getOrElse("username", "").asInstanceOf[String]
        val pass = config_map.getOrElse("password", "").asInstanceOf[String]
        val db = config_map.getOrElse("database", "").asInstanceOf[String]
        MysqlConfig(host, port, user, pass, db)
    }

    def parseESConfig(rsp: String): ESConfig = {
        val config_map = parseConfigMap(rsp)
        val host = config_map.getOrElse("eshost", "").asInstanceOf[String]
        val port = config_map.getOrElse("esport", "").asInstanceOf[String].toInt
        val user = config_map.getOrElse("esuser", "").asInstanceOf[String]
        val pass = config_map.getOrElse("espass", "").asInstanceOf[String]
        ESConfig(host, port, user, pass)
    }

}


object ConfigDefault{
    def redisConfig: RedisConfig = {
        val localEnv: String = System.getenv.get("OS")
        if (localEnv != null &&
            (localEnv.toLowerCase().contains("windows") || localEnv.toLowerCase().contains("macos"))) {
            println("localEnv: the system os is ", localEnv)
            // 测试环境Redis
            RedisConfig(host="wzalgo-redis-v2.qizhidao.net", port=6382, auth="Zhiyun_6382", db=0)
        } else{
            // 混合云开发环境Redis
            RedisConfig(host="172.18.32.34", port=6379, auth="qzd123456", db=0)
        }
    }
}