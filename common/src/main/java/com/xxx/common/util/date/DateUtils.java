//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xxx.common.util.date;

import cn.hutool.core.date.LocalDateTimeUtil;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static final String TIME_ZONE_DEFAULT = "GMT+8";
    public static final long SECOND_MILLIS = 1000L;
    public static final String FORMAT_YEAR_MONTH_DAY = "yyyy-MM-dd";
    public static final String FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = "yyyy-MM-dd HH:mm:ss";

    public static Date of(LocalDateTime date) {
        if (date == null) {
            return null;
        } else {
            ZonedDateTime var1 = date.atZone(ZoneId.systemDefault());
            Instant var2 = var1.toInstant();
            return Date.from(var2);
        }
    }

    public static LocalDateTime of(Date date) {
        if (date == null) {
            return null;
        } else {
            Instant var1 = date.toInstant();
            return LocalDateTime.ofInstant(var1, ZoneId.systemDefault());
        }
    }

    public static Date addTime(Duration duration) {
        return new Date(System.currentTimeMillis() + duration.toMillis());
    }

    public static boolean isExpired(LocalDateTime time) {
        LocalDateTime var1 = LocalDateTime.now();
        return var1.isAfter(time);
    }

    public static Date buildTime(int year, int mouth, int day) {
        return buildTime(year, mouth, day, 0, 0, 0);
    }

    public static Date buildTime(int year, int mouth, int day, int hour, int minute, int second) {
        Calendar var6 = Calendar.getInstance();
        var6.set(1, year);
        var6.set(2, mouth - 1);
        var6.set(5, day);
        var6.set(11, hour);
        var6.set(12, minute);
        var6.set(13, second);
        var6.set(14, 0);
        return var6.getTime();
    }

    public static Date max(Date a, Date b) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else {
            return a.compareTo(b) > 0 ? a : b;
        }
    }

    public static LocalDateTime max(LocalDateTime a, LocalDateTime b) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else {
            return a.isAfter(b) ? a : b;
        }
    }

    public static boolean isToday(LocalDateTime date) {
        return LocalDateTimeUtil.isSameDay(date, LocalDateTime.now());
    }

    public static boolean isYesterday(LocalDateTime date) {
        return LocalDateTimeUtil.isSameDay(date, LocalDateTime.now().minusDays(1L));
    }
}

