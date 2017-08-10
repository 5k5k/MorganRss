package com.morladim.morganrss.base.util;

import java.util.Date;

/**
 * <br>创建时间：2017/8/8.
 *
 * @author morladim
 */
public class DateUtils {

    private static final long MINUTE = 60000;
    private static final long HOUR = 3600000;
    private static final long DAY = 86400000;
    private static final long WEEK = 604800000;
    private static final long MONTH = 2419200000L;

    private static final String SECOND_BEFORE = "秒前";
    private static final String MINUTE_BEFORE = "分钟前";
    private static final String HOUR_BEFORE = "小时前";
    private static final String DAY_BEFORE = "天前";
    private static final String WEEK_BEFORE = "周前";
    private static final String MONTH_BEFORE = "月前";
    private static final String YEAR_BEFORE = "年前";

    private static String getFormatDate(long deltaTime) {
        if (deltaTime < 0) {
            return "日期错误";
        }
        if (deltaTime < MINUTE) {
            long seconds = deltaTime / 1000;
            return (seconds <= 0 ? 1 : seconds) + SECOND_BEFORE;
        }
        if (deltaTime < 60 * MINUTE) {
            long minutes = deltaTime / MINUTE;
            return (minutes <= 0 ? 1 : minutes) + MINUTE_BEFORE;
        }
        if (deltaTime < 24 * HOUR) {
            long hours = deltaTime / HOUR;
            return (hours <= 0 ? 1 : hours) + HOUR_BEFORE;
        }
        if (deltaTime < 7 * DAY) {
            long days = deltaTime / DAY;
            return (days <= 0 ? 1 : days) + DAY_BEFORE;
        }
        if (deltaTime < 4 * WEEK) {
            long months = deltaTime / WEEK;
            return (months <= 0 ? 1 : months) + WEEK_BEFORE;
        }
        if (deltaTime < 12 * MONTH) {
            long months = deltaTime / MONTH;
            return (months <= 0 ? 1 : months) + MONTH_BEFORE;
        } else {
            long years = deltaTime / MONTH / 12;
            return (years <= 0 ? 1 : years) + YEAR_BEFORE;
        }
    }

    public static String getTimeToNow(Date date) {
        if (date == null) {
            return "未知时间";
        }
        long time = new Date().getTime() - date.getTime();
        return getFormatDate(time);
    }
}
