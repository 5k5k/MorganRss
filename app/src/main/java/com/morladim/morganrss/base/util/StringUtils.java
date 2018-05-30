package com.morladim.morganrss.base.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.text.TextUtils;
import android.util.Patterns;

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
    }

    @SuppressLint("StaticFieldLeak")
    private static Application application;

    public static final String SEPARATOR = "-";

    public static final String SEPARATOR_WITH_SPACE = " - ";

    public static void init(Application application) {
        StringUtils.application = application;
    }

    /**
     * 获取id对应的字符串资源
     *
     * @param id 资源id
     * @return 资源对应字符串
     */
    public static String getStringById(int id) {
        return application.getString(id);
    }

    /**
     * 获取id对应的字符串数组资源
     *
     * @param id 资源id
     * @return 资源对应字符串数组
     */
    public static String[] getStringArrayById(int id) {
        return application.getResources().getStringArray(id);
    }

    /**
     * 获取非null字符串
     *
     * @param string 原字符串
     * @return 字符串为null返回""，否则返回原字符串
     */
    public static String getNonNullString(String string) {
        return string == null ? "" : string;
    }

    /**
     * 为空返回""，否则返回原字符串前带空格
     */
    public static String getStringWithSpaceBehind(String string) {
        return TextUtils.isEmpty(string) ? "" : " " + string;
    }

    /**
     * 判断是否为网址
     *
     * @param address 需要校验网址
     * @return 是否符合
     */
    public static boolean isUrl(String address) {
        return Patterns.WEB_URL.matcher(address).matches();
    }

    /**
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        long lt = Long.valueOf(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
}
