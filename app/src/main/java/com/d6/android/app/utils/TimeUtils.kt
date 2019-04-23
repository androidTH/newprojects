package com.d6.android.app.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间处理
 */
/**
时间戳转换默认格式yyyy-MM-dd
 */
var timeFormat = "yyyy年MM月dd日"

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
        else -> this.toTime(timeFormat)
    }
    return desc
}

fun isDateOneBigger(str1:String,str2:String):Boolean{
    var  isBigger:Boolean = false;
    var sdf:SimpleDateFormat  = SimpleDateFormat("yyyy-MM-dd");
    var dt1:Date
    var dt2:Date
    dt1 = sdf.parse(str1)
    dt2 = sdf.parse(str2)
    if (dt1.getTime() > dt2.getTime()) {
        isBigger = true;
    } else if (dt1.getTime() < dt2.getTime()) {
        isBigger = false;
    }
    return isBigger
}

fun converTime(timestamp: Long): String {
    val currentSeconds = System.currentTimeMillis()
    val timeGap = (timestamp -currentSeconds)/1000 // 与现在时间相差秒数
    var timeStr: String? = null
    if (timeGap > 24 * 60 * 60) {// 1天以上
        timeStr = (timeGap / (24 * 60 * 60)).toString() + "天"
    } else if (timeGap > 60 * 60) {// 1小时-24小时
        timeStr = (timeGap / (60 * 60)).toString() + "小时"
    } else if (timeGap > 60) {// 1分钟-59分钟
        timeStr = (timeGap / 60).toString() + "分钟"
    } else {// 1秒钟-59秒钟
        timeStr = "0秒"
    }
    return timeStr
}

fun converToTime(timestamp: Long): String {
    val currentSeconds = System.currentTimeMillis()
    val timeGap = (currentSeconds -timestamp)/1000 // 与现在时间相差秒数
    var timeStr: String? = null
    if (timeGap > 24 * 60 * 60) {// 1天以上
        timeStr = (timeGap / (24 * 60 * 60)).toString() + "天"
    } else if (timeGap > 60 * 60) {// 1小时-24小时
        timeStr = (timeGap / (60 * 60)).toString() + "小时"
    } else if (timeGap > 60) {// 1分钟-59分钟
        timeStr = (timeGap / 60).toString() + "分钟"
    } else {// 1秒钟-59秒钟
        timeStr = "0秒"
    }
    return timeStr
}

fun getTodayTime(): String {
    val f = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    return f.format(Date())
}
