package com.morladim.morganrss.base.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络工具类
 * <br>创建时间：2017/8/16.
 *
 * @author morladim
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class NetworkUtils {

    @SuppressLint("StaticFieldLeak")
    private static Application application;
    public static final int WIFI_CONNECTED = 0;
    public static final int NO_NETWORK = 1;
    public static final int NO_WIFI = 2;

    private NetworkUtils() {
        throw new AssertionError("no instance");
    }

    public static void init(Application application) {
        NetworkUtils.application = application;
    }

    public static NetworkInfo getNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            return cm.getActiveNetworkInfo();
        }
        return null;
    }

    public static boolean isConnected() {
        NetworkInfo info = getNetworkInfo();
        return (info != null && info.isConnected());
    }

    public static boolean isConnectedWifi() {
        NetworkInfo info = getNetworkInfo();
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public static boolean isConnectedMobile() {
        NetworkInfo info = getNetworkInfo();
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    public static int checkNetworkState() {
        NetworkInfo info = getNetworkInfo();
        if (!(info != null && info.isConnected())) {
            return NO_NETWORK;
        }
        if (!(info.getType() == ConnectivityManager.TYPE_WIFI)) {
            return NO_WIFI;
        }
        return WIFI_CONNECTED;
    }
}
