package org.zcb.common.utils

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, ZoneId}
import java.util.Date
import scala.collection.mutable.ListBuffer


object DateUtils {

    /**
     * 解析日期字符串为 LocalDate
     * 解析日期时间字符串为 LocalDateTime
     */
    def parseDate(dateStr: String, pattern: String = "yyyy-MM-dd"): LocalDate = {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        LocalDate.parse(dateStr, formatter)
    }
    def parseDateTime(dateTimeStr: String, pattern: String = "yyyy-MM-dd HH:mm:ss"): LocalDateTime = {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        LocalDateTime.parse(dateTimeStr, formatter)
    }

    /**
     * 日期格式化
     * 日期时间格式化
     */
    def formatDate(date: LocalDate, format: String = "yyyy-MM-dd"): String = {
        val formatter = DateTimeFormatter.ofPattern(format)
        date.format(formatter)
    }
    def formatDateTime(dateTime: LocalDateTime, format: String = "yyyy-MM-dd HH:mm:ss"): String = {
        val formatter = DateTimeFormatter.ofPattern(format)
        dateTime.format(formatter)
    }

    /**
     * 获取n天前的日期
     * 获取 n 天后的日期
     * 根据当前日期获取n天前的日期
     * 根据当前日期获取 n 天后的日期
     * 获取两个日期之间的所有日期
     */
    def getDaysAgo(date: LocalDate, days: Int): LocalDate = {
        date.minusDays(days)
    }
    def getDaysAfter(date: LocalDate, days: Long): LocalDate = {
        date.plusDays(days)
    }
    def getDaysAgoFromNow(days: Int): LocalDate = {
        LocalDate.now().minusDays(days)
    }
    def getDaysAfterCurrent(days: Long): LocalDate = {
        LocalDate.now().plusDays(days)
    }
    def getDatesBetween(startDate: String, endDate: String, format: String = "yyyy-MM-dd"): List[String] = {
        val start = parseDate(startDate, format)
        val end = parseDate(endDate, format)
        val dates = ListBuffer[String]()
        var current = start

        while (!current.isAfter(end)) {
            dates += formatDate(current, format=format)
            current = current.plusDays(1)
        }
        dates.toList
    }

    /**
     * 将java.util.Date转换为java.time.LocalDate
     * 将java.util.Date转换为java.time.LocalDateTime
     * 将java.time.LocalDate转换为java.util.Date
     * 将java.time.LocalDateTime转换为java.util.Date
     */
    def convertToLocalDate(date: Date): LocalDate = {
        date.toInstant.atZone(ZoneId.systemDefault()).toLocalDate
    }
    def convertToLocalDateTime(date: Date): LocalDateTime = {
        date.toInstant.atZone(ZoneId.systemDefault()).toLocalDateTime
    }
    def convertToDate(localDate: LocalDate): Date = {
        Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant)
    }
    def convertToDate(localDateTime: LocalDateTime): Date = {
        Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant)
    }

    def main(args: Array[String]): Unit = {
        // 示例使用
        val now = LocalDate.now()
        val nowDateTime = LocalDateTime.now()

        println(s"日期解析: ${parseDate("2024-07-18")}")
        println(s"日期时间解析: ${parseDateTime("2024-07-18 09:39:49")}")
        println(s"当前日期: ${formatDate(now)}")
        println(s"当前日期时间: ${formatDateTime(nowDateTime)}")

        val daysAgo = 10
        println(s"${daysAgo}天前的日期: ${formatDate(getDaysAgo(now, daysAgo))}")
        println(s"根据当前日期获取${daysAgo}天前的日期: ${formatDate(getDaysAgoFromNow(daysAgo))}")

        val date = new Date()
        val localDate = convertToLocalDate(date)
        val localDateTime = convertToLocalDateTime(date)
        println(s"java.util.Date转换为java.time.LocalDate: ${formatDate(localDate)}")
        println(s"java.util.Date转换为java.time.LocalDateTime: ${formatDateTime(localDateTime)}")

        val convertedDate = convertToDate(localDate)
        val convertedDateTime = convertToDate(localDateTime)
        println(s"java.time.LocalDate转换为java.util.Date: $convertedDate")
        println(s"java.time.LocalDateTime转换为java.util.Date: $convertedDateTime")

        val startDate = "2024-07-01"
        val endDate = "2024-07-10"
        val dates = getDatesBetween(startDate, endDate)
        println(s"获取两个日期之间的所有日期: $dates")
    }
}
