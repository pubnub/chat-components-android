package com.pubnub.framework.util

import java.text.SimpleDateFormat
import java.util.*

typealias Timetoken = Long

private const val iso8601Pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
fun Timetoken.toIsoString(locale: Locale = Locale.getDefault()): String {
    val iso8601format = SimpleDateFormat(iso8601Pattern, locale).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return iso8601format.format(Date(this.seconds))
}

fun String.toTimetoken(locale: Locale = Locale.getDefault()): Timetoken {
    val iso8601format = SimpleDateFormat(iso8601Pattern, locale).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return iso8601format.parse(this)?.time?:0 * 10
}

val Timetoken.seconds: Seconds
    get() = this / 10_000L

val Timetoken.milliseconds: Milliseconds
    get() = this / 10L

typealias Seconds = Long
typealias Milliseconds = Long

val Seconds.timetoken: Timetoken
    get() = this * 10_000L

fun Timetoken.isSameDate(timetoken: Timetoken): Boolean {
    return isSameDay(Date(seconds), Date(timetoken.seconds))
}

fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance()
    cal1.time = date1
    val cal2 = Calendar.getInstance()
    cal2.time = date2
    return isSameDay(cal1, cal2)
}

fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1[Calendar.ERA] == cal2[Calendar.ERA]
            && cal1[Calendar.YEAR] == cal2[Calendar.YEAR]
            && cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR]
}