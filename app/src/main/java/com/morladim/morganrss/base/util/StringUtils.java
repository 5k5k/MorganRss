package com.morladim.morganrss.base.util;

import android.app.Application;
import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Patterns;

import com.morladim.morganrss.base.RssApplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 字符串工具類，需要先初始化
 *
 * @author Morladim
 * @date 2016-06-07 17:48
 */
@SuppressWarnings("unused")
public class StringUtils {

    private StringUtils() {
        application = RssApplication.getContext();
    }

    private Application application;

    public static final String SEPARATOR = "-";

    public static final String SEPARATOR_WITH_SPACE = " - ";

    private volatile static StringUtils instance;

    public static StringUtils getInstance() {
        if (instance == null) {
            synchronized (StringUtils.class) {
                if (instance == null) {
                    instance = new StringUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 获取id对应的字符串资源
     *
     * @param id 资源id
     * @return 资源对应字符串
     */
    public String getStringById(@StringRes int id) {
        return application.getString(id);
    }

    /**
     * 获取id对应的字符串数组资源
     *
     * @param id 资源id
     * @return 资源对应字符串数组
     */
    public String[] getStringArrayById(@ArrayRes int id) {
        return application.getResources().getStringArray(id);
    }

    /**
     * 获取非null字符串
     *
     * @param string 原字符串
     * @return 字符串为null返回""，否则返回原字符串
     */
    public String getNonNullString(String string) {
        return string == null ? "" : string;
    }

    /**
     * 为空返回""，否则返回原字符串前带空格
     */
    public String getStringWithSpaceBehind(String string) {
        return TextUtils.isEmpty(string) ? "" : " " + string;
    }

    /**
     * 判断是否为网址
     *
     * @param address 需要校验网址
     * @return 是否符合
     */
    public boolean isUrl(String address) {
        return Patterns.WEB_URL.matcher(address).matches();
    }

    /**
     * 從指定格式建立字符串
     *
     * @param format 格式
     * @param args   參數
     * @return 結果
     */
    public String getStringFromFormat(String format, Object... args) {
        return String.format(Locale.CHINA, format, args);
    }

    /**
     * 從指定格式建立字符串
     *
     * @param formatRes 格式資源
     * @param args      參數
     * @return 結果
     */
    public String getStringFromFormat(@StringRes int formatRes, Object... args) {
        return String.format(Locale.CHINA, application.getString(formatRes), args);
    }

    /**
     * 将时间戳转换为时间
     */
    public String stampToDate(String s) {
        String res;
        long lt = Long.valueOf(s);
        Date date = new Date(lt);
        res = SIMPLE_DATE_FORMAT.format(date);
        return res;
    }

   public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

}
