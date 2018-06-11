package com.morladim.morganrss.base.network;

import android.support.annotation.NonNull;

import com.morladim.morganrss.base.util.SharedPreferencesUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.morladim.morganrss.base.network.Constants.COOKIE;

/**
 * <br>创建时间：2017/7/20.
 *
 * @author morladim
 */
class HeadInterceptor implements Interceptor {
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder()
                .header("Cookie", SharedPreferencesUtils.getInstance().loadString(original.url() + COOKIE))
                .method(original.method(), original.body());
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
