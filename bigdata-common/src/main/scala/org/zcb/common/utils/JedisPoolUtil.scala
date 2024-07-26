package org.zcb.common.utils
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.{Jedis, JedisPool}

/**
 * Redis 连接池工具包
 *
 */
class JedisPoolUtil {
    // 测试环境Redis
    private var redisConfig: RedisConfig = _
    private var jedisPool: JedisPool = _

    def this(redisConfig: RedisConfig) {
        this()
        this.redisConfig = redisConfig
        this.setup()
    }

    def setup(): Unit = {
        val poolConfig = new GenericObjectPoolConfig()
        poolConfig.setMaxIdle(10)          // 最大连接数
        poolConfig.setMaxTotal(1000)       // 最大空闲连接数
        poolConfig.setMaxWaitMillis(1000)  // 最大等待时间
        poolConfig.setTestOnBorrow(true)   // 检查连接可用性, 确保获取的redis实例可用
        this.jedisPool = new JedisPool(
            poolConfig, redisConfig.host, redisConfig.port, 2000, redisConfig.auth, redisConfig.db)
    }

    def connect(): Jedis = {
        val jedis: Jedis = jedisPool.getResource // 获取连接池连接
        jedis
    }
}


object JedisPoolUtilTest {
    def main(args: Array[String]): Unit = {
        val redisConfig = RedisConfig(host="", port=6381, auth="", db=0)
        val client: JedisPoolUtil = new JedisPoolUtil(redisConfig)
        val conn = client.connect()
        println(conn.get("recommender-nlp-dev"))
    }
}