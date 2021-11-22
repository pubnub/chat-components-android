package com.pubnub.framework.util

import java.util.*

typealias Timetoken = Long

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