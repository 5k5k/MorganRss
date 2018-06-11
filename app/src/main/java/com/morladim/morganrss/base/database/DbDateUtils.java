package com.morladim.morganrss.base.database;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * <br>Created on 2017/7/16 下午12:29
 *
 * @author morladim.
 */

public class DbDateUtils {

    /**
     * 轉換日期字符串為Date對象
     *
     * @param dateString 日期字符串
     * @return 日期
     */
    public static Date convertStringToDate(String dateString) {
        initFormat();

        for (SimpleDateFormat format : formatList) {
            ParsePosition parsePosition = new ParsePosition(0);
            Date date = format.parse(dateString.replaceAll("\n", ""), parsePosition);
            if (parsePosition.getErrorIndex() == -1) {
                return date;
            }
        }
        Timber.e("日期格式化失敗！ 原始值為" + dateString);
        return null;
    }

    /**
     * 初始化format
     */
    private static void initFormat() {
        if (formatList == null) {
            synchronized (DbDateUtils.class) {
                if (formatList == null) {
                    String[] formats = {
                            //Sun, 16 Jul 2017 01:12:26 +0000
                            "EEE, d MMM yyyy HH:mm:ss Z",
                            //Tue, 08 Aug 2017 08:21:28
                            "EEE, dd MMM yyyy HH:mm:ss",
                            //2017-08-07 15:05:35 +0800
                            "yyyy-MM-dd HH:mm:ss Z",
                            //Sat, 26 May 2018 18:32 +1000
                            "EEE, d MMM yyyy HH:mm Z"
                    };
                    formatList = new ArrayList<>(formats.length);
                    for (String format : formats) {
                        formatList.add(new SimpleDateFormat(format, Locale.US));
                    }
                }
            }
        }
    }

    /**
     * 格式數組
     */
    private volatile static List<SimpleDateFormat> formatList;

}
