package com.morladim.morganrss.base.util;

import android.app.Application;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * SharedPreferences工具类，適用於無並發訪問需求，存儲簡單值的情況，複雜情況不適用。
 * <br>创建时间：2017/7/20.
 *
 * @author morladim
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class SharedPreferencesUtils {
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    private static final String PREFERENCES_NAME = "morladim";
    private static Gson GSON;

    private SharedPreferencesUtils() {
        throw new AssertionError("on instance");
    }

    public static void init(Application context) {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
        editor = preferences.edit();
        editor.apply();
    }

    public static synchronized void saveObject(String key, Object value) {
        initGson();
        String var2 = GSON.toJson(value);
        editor.putString(key, var2);
        editor.apply();
    }

    public static synchronized void saveString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public static String loadString(String key) {
        return preferences.getString(key, "");
    }

    public static void clearString(String key) {
        editor.remove(key);
        editor.apply();
    }

    public static <T> T loadObject(String key, Class<T> tClass) {
        initGson();
        String var2 = preferences.getString(key, null);
        return GSON.fromJson(var2, tClass);
    }

    public static <T> T loadObject(String key, Type type) {
        initGson();
        String var2 = preferences.getString(key, null);
        return GSON.fromJson(var2, type);
    }

    public static int loadInt(String key) {
        return preferences.getInt(key, 0);
    }

    public static int loadInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    public static synchronized void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public static synchronized void saveBoolean(String key, boolean b) {
        editor.putBoolean(key, b);
        editor.apply();
    }

    public static synchronized boolean loadBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    private static void initGson() {
        if (GSON == null) {
            GSON = new Gson();
        }
    }

}
