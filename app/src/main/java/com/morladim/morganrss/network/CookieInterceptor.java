package com.morladim.morganrss.network;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.morladim.morganrss.base.util.SharedUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.morladim.morganrss.network.Constants.COOKIE;

/**
 * <br>创建时间：2017/7/20.
 *
 * @author morladim
 */
class CookieInterceptor implements Interceptor {
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        String cookie = response.header("Set-Cookie");
        if (!TextUtils.isEmpty(cookie)) {
            Log.d("url + cookie", request.url() + " " + cookie);
            SharedUtils.saveString(request.url() + COOKIE, cookie);
        }
        return response;
    }
}