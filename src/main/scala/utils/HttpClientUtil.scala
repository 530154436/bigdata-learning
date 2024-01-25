package utils

import org.apache.commons.httpclient.methods.{GetMethod, PostMethod, StringRequestEntity}
import org.apache.commons.httpclient.{HttpClient, HttpStatus}
import org.apache.commons.io.IOUtils
import org.apache.spark.internal.Logging

object HttpClientUtil extends Logging {
    def get(uri: String, headers: Option[Map[String, String]] = null): String = {
        val client = new HttpClient()
        val method = new GetMethod(uri)

        // 设置请求头
        if(headers.nonEmpty){
            headers.get.foreach(x => method.setRequestHeader(x._1, x._2))
        }

        val rsp = try {
            val statusCode = client.executeMethod(method)
            val responseBodyStream = method.getResponseBodyAsStream
            val response = IOUtils.toString(responseBodyStream, "UTF-8")
            if (statusCode == HttpStatus.SC_OK) {
                return response
            }else{
                log.error(s"状态码异常: $statusCode, ${response}")
            }
            null
        } catch {
            case e: Exception => log.error(e.getMessage)
                null
        }
        method.releaseConnection()
        rsp
    }


    def post(url: String, payload: String, headers: Option[Map[String, String]] = None): String = {
        val httpClient = new HttpClient()
        val postMethod = new PostMethod(url)

        // 设置请求头
        postMethod.setRequestHeader("Content-Type", "application/json")
        if(headers.nonEmpty){
            headers.get.foreach(x => postMethod.setRequestHeader(x._1, x._2))
        }
        //postMethod.getRequestHeaders.foreach(println)

        postMethod.setRequestEntity(new StringRequestEntity(payload, "application/json", "UTF-8"))
        try {
            val statusCode = httpClient.executeMethod(postMethod)
            if (statusCode == HttpStatus.SC_OK) {
                val response = postMethod.getResponseBodyAsString()
                return response
            }else{
                log.error(s"状态码异常: $statusCode, ${postMethod.getResponseBodyAsString}")
            }
            null
        } catch {
            case e: NullPointerException => log.error(e.getMessage)
                null
            case e: Exception => log.error(e.getMessage)
                null
        } finally {
            postMethod.releaseConnection()
        }
    }
}
