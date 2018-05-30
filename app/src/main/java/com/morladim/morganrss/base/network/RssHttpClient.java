package com.morladim.morganrss.base.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import static com.morladim.morganrss.base.network.Constants.CONNECT_TIME_OUT;
import static com.morladim.morganrss.base.network.Constants.READ_TIME_OUT;
import static com.morladim.morganrss.base.network.Constants.WRITE_TIME_OUT;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Retrofit封裝
 * <br>创建时间：2017/7/20.
 *
 * @author morladim
 */
class RssHttpClient {

    private static final String BASE_URL = "http://www.morladim.com";

    private NewsApi newsApi;
    private volatile static RssHttpClient client;

    private RssHttpClient() {
        Retrofit retrofit = getRetrofit();
        newsApi = retrofit.create(NewsApi.class);
    }

    private static RssHttpClient getInstance() {
        if (client == null) {
            synchronized (RssHttpClient.class) {
                if (client == null) {
                    client = new RssHttpClient();
                }
            }
        }
        return client;
    }

    private OkHttpClient getClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIME_OUT, SECONDS)
                .readTimeout(READ_TIME_OUT, SECONDS)
                .writeTimeout(WRITE_TIME_OUT, SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(new CookieInterceptor())
                .addInterceptor(new HeadInterceptor())
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
    }

    private Retrofit getRetrofit() {
        AnnotationStrategy annotationStrategy = new AnnotationStrategy();
        Persister persister = new Persister(annotationStrategy);
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getClient())
                .addConverterFactory(SimpleXmlConverterFactory.create(persister))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    static NewsApi getNewsApi() {
        return RssHttpClient.getInstance().newsApi;
    }

}
