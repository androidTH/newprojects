package com.d6.android.app.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间处理
 */
/**
时间戳转换默认格式yyyy-MM-dd
 */

fun Long?.toYMDTime(): String {
    if (this == null) {
        return ""
    }
    val f = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    return f.format(Date(this))
}

fun Long?.toYMDTime1(): String {
    if (this == null) {
        return ""
    }
    val f = SimpleDateFormat("yyyy.MM.dd", Locale.CHINA)
    return f.format(Date(this))
}

fun Long?.toTime(format: String = "yyyy-MM-dd HH:mm"): String {
    if (this == null) {
        return ""
    }
    val f = SimpleDateFormat(format, Locale.CHINA)
    return f.format(Date(this))
}

fun Long.toDefaultTime(): String {
    val f = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    return f.format(Date(this))
}

fun String?.parserTime(format: String = "yyyy-MM-dd HH:mm:ss"): Long {
    if (this == null) {
        return 0
    }

    val ft = if (format.isEmpty()) {
        "yyyy-MM-dd HH:mm:ss"
    } else {
        format
    }
    val f = SimpleDateFormat(ft, Locale.CHINA)
    return try {
        val date = f.parse(this)
        date.time
    } catch (e: Exception) {
        0L
    }
}

/**
 * 两个时间戳间隔字符
 * @param nowTime 当前时间,默认读取系统时间
 */
fun Long.interval(nowTime: Long = System.currentTimeMillis()): String {
    val desc: String
    val d = Date(this)
    val n = Date(nowTime)
    val delay = n.time - d.time
    val secondsOfHour = (60 * 60).toLong()
    val secondsOfDay = secondsOfHour * 24
    val secondsOfTwoDay = secondsOfDay * 2
    val secondsOfThreeDay = secondsOfDay * 3
    val secondsOfFourDay = secondsOfDay * 4
    val secondsOfFiveDay = secondsOfDay * 5
    val secondsOfSixDay = secondsOfDay * 6
    val secondsOfSevenDay = secondsOfDay * 7
    val secondsOfEightDay = secondsOfDay * 7
    // 相差的秒数
    val delaySeconds = delay / 1000
    desc = when {
        delaySeconds < 60 -> "刚刚" //10
//        delaySeconds <= 60 -> delaySeconds.toString() + "秒前"
        delaySeconds < secondsOfHour -> (delaySeconds / 60).toString() + "分前"
        delaySeconds < secondsOfDay -> (delaySeconds / 60 / 60).toString() + "小时前"
        delaySeconds < secondsOfTwoDay -> "1天前"
        delaySeconds < secondsOfThreeDay -> "2天前"
        delaySeconds < secondsOfFourDay ->"3天前"
        delaySeconds < secondsOfFiveDay ->"4天前"
        delaySeconds < secondsOfSixDay ->"5天前"
        delaySeconds < secondsOfSevenDay ->"6天前"
        delaySeconds < secondsOfEightDay ->"7天前"
        else -> this.toTime("yyyy-MM-dd")
    }
    return desc
}