package com.swolebrain.officefitness.history

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


val sdf : SimpleDateFormat = SimpleDateFormat("E hh:mm aaa")


fun getUTCMidnightTimeMillis(): Long{
    val now = Calendar.getInstance()
    val millisInDay = now.get(Calendar.HOUR_OF_DAY).toLong()*1000L*60L*60L +
            now.get(Calendar.MINUTE).toLong()*1000L*60L +
            now.get(Calendar.SECOND).toLong()*1000L +
            now.get(Calendar.MILLISECOND).toLong()
    val timeToMidnight = 1000L*60L*60L*24L - millisInDay
    return timeToMidnight + System.currentTimeMillis()
}

fun getGMTOffset() : Int{
    //    val mCalendar = GregorianCalendar()
    val mTimeZone = Calendar.getInstance().timeZone
    val mGMTOffset = mTimeZone.getOffset(System.currentTimeMillis()) + if (!mTimeZone.inDaylightTime(Date())) 0 else mTimeZone.dstSavings
    return mGMTOffset
}

fun getUTCTime7DaysAgo() : Long{
    return getUTCMidnightTimeMillis() - 7 * 24 * 60 *60 * 1000
}

fun historyFormatDate(timeStamp: Long) :String{
    return sdf.format(Date(timeStamp ))
}