package com.morladim.morganrss.base.util;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.morladim.morganrss.base.RssApplication;

/**
 * 网络工具类
 * <br>创建时间：2017/8/16.
 *
 * @author morladim
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class NetworkUtils {

    private Application application;
    public static final int WIFI_CONNECTED = 0;
    public static final int NO_NETWORK = 1;
    public static final int NO_WIFI = 2;

    private NetworkUtils() {
        application = RssApplication.getContext();
    }

    private volatile static NetworkUtils instance;

    public static NetworkUtils getInstance() {
        if (instance == null) {
            synchronized (NetworkUtils.class) {
                if (instance == null) {
                    instance = new NetworkUtils();
                }
            }
        }
        return instance;
    }

    public NetworkInfo getNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            return cm.getActiveNetworkInfo();
        }
        return null;
    }

    public boolean isConnected() {
        NetworkInfo info = getNetworkInfo();
        return (info != null && info.isConnected());
    }

    public boolean isConnectedWifi() {
        NetworkInfo info = getNetworkInfo();
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public boolean isConnectedMobile() {
        NetworkInfo info = getNetworkInfo();
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    public int checkNetworkState() {
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
