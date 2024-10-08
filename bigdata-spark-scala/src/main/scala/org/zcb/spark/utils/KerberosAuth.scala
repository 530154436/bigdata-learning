package org.zcb.spark.utils


import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.security.UserGroupInformation
import org.apache.spark.SparkConf
import org.zcb.common.conf.Global

import java.nio.file.Paths

object KerberosAuth {
    val user: String = "qzdsf_dev"
    val keytabConf: String = Paths.get(Global.BASE_DIR, "src", "main", "resources", "krb5.ini").toAbsolutePath.toString
    val keytabPath: String = Paths.get(Global.BASE_DIR, "src", "main", "resources", "qzdsf_dev.keytab").toAbsolutePath.toString

    def login(): Boolean = {
        try {
            val localEnv: String = System.getenv.get("OS")
            if (localEnv != null &&
                (localEnv.toLowerCase().contains("windows") || localEnv.toLowerCase().contains("macos"))) {
                println("localEnv: the system os is ", localEnv)

                val conf = new Configuration
                conf.set("hadoop.security.authentication", "Kerberos")
                conf.addResource("org.zcb.hadoop.hdfs-site.xml")
                conf.addResource("core-site.xml")
                conf.addResource("org.zcb.hive-site.xml")
                UserGroupInformation.setConfiguration(conf)

                System.setProperty("java.security.krb5.org.zcb.common.conf", keytabConf)
                System.setProperty("javax.security.auth.useSubjectCredsOnly", "false")
                System.setProperty("sun.security.krb5.debug", "true")
                UserGroupInformation.loginUserFromKeytab(user, keytabPath)
                return true
            }
        } catch {
            case e: Exception => e.printStackTrace()
        }
        false
    }

    def login(appName: String): SparkConf = {
        val isLocalEnv = this.login()
        val sparkConf: SparkConf = new SparkConf()
        sparkConf.setAppName(appName)
        if (isLocalEnv) {
            sparkConf.setMaster("local[4]")
        }
        sparkConf
    }
}
