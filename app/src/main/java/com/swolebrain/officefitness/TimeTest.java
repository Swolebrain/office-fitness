package com.swolebrain.officefitness;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeTest {
    public static void main(String[] args) {
        Calendar mCalendar = new GregorianCalendar();
//        TimeZone mTimeZone = mCalendar.getTimeZone();
//        int mGMTOffset = mTimeZone.getRawOffset() + (mTimeZone.inDaylightTime(new Date()) ? mTimeZone.getDSTSavings() : 0);
//        long dayInMillis = 24*60*60*1000;
//        long timeStampMillis = System.currentTimeMillis() + mGMTOffset;
//        System.out.printf("Current local time of the day in millis is %s \n", timeStampMillis);
//        long timeToMidnight = dayInMillis - (timeStampMillis % dayInMillis);
//        System.out.printf("Midnight local time will happen at timestamp %s in %s ms \n", System.currentTimeMillis() + timeToMidnight, timeToMidnight);
        long millisInDay = ((long)mCalendar.get(Calendar.HOUR_OF_DAY))*1000L*60L*60L +
                ((long)mCalendar.get(Calendar.MINUTE))*1000L*60L +
                        ((long)mCalendar.get(Calendar.SECOND))*1000L +
                                ((long)mCalendar.get(Calendar.MILLISECOND));
        System.out.println(millisInDay);
        System.out.println(1000L*60L*60L*24L - millisInDay);
    }
}
