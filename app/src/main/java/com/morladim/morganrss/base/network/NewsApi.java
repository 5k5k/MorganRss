package com.morladim.morganrss.base.network;

import com.morladim.morganrss.base.rss2.Rss2Xml;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * <br>创建时间：2017/7/13.
 *
 * @author morladim
 */
interface NewsApi {

    /**
     * 獲取xml內容
     *
     * @param url 請求地址
     * @return 觀察者
     */
    @GET
    Observable<Rss2Xml> getXml(@Url String url);
}
