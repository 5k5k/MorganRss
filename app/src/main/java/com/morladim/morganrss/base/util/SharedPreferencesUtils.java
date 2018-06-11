package com.morladim.morganrss.base.util;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.morladim.morganrss.base.RssApplication;

import java.lang.reflect.Type;

/**
 * SharedPreferences工具类，適用於無並發訪問需求，存儲簡單值的情況，複雜情況不適用。
 * <br>创建时间：2017/7/20.
 *
 * @author morladim
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class SharedPreferencesUtils {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static final String PREFERENCES_NAME = "morladim";
    private Gson gson;

    private volatile static SharedPreferencesUtils instance;

    public static SharedPreferencesUtils getInstance() {
        if (instance == null) {
            synchronized (SharedPreferencesUtils.class) {
                if (instance == null) {
                    instance = new SharedPreferencesUtils();
                }
            }
        }
        return instance;
    }

    private SharedPreferencesUtils() {
        preferences = RssApplication.getContext().getSharedPreferences(PREFERENCES_NAME, 0);
        editor = preferences.edit();
        editor.apply();
    }

    public synchronized void saveObject(String key, Object value) {
        initGson();
        String var2 = gson.toJson(value);
        editor.putString(key, var2);
        editor.apply();
    }

    public synchronized void saveString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String loadString(String key) {
        return preferences.getString(key, "");
    }

    public void clearString(String key) {
        editor.remove(key);
        editor.apply();
    }

    public <T> T loadObject(String key, Class<T> tClass) {
        initGson();
        String var2 = preferences.getString(key, null);
        return gson.fromJson(var2, tClass);
    }

    public <T> T loadObject(String key, Type type) {
        initGson();
        String var2 = preferences.getString(key, null);
        return gson.fromJson(var2, type);
    }

    public int loadInt(String key) {
        return preferences.getInt(key, 0);
    }

    public int loadInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    public synchronized void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public synchronized void saveBoolean(String key, boolean b) {
        editor.putBoolean(key, b);
        editor.apply();
    }

    public synchronized boolean loadBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    private void initGson() {
        if (gson == null) {
            gson = new Gson();
        }
    }

}

