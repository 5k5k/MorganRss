package com.morladim.morganrss.base.network;

import com.morladim.morganrss.base.database.ChannelManager;
import com.morladim.morganrss.base.database.ItemManager;
import com.morladim.morganrss.base.database.RssVersionManager;
import com.morladim.morganrss.base.database.entity.Channel;
import com.morladim.morganrss.base.database.entity.Item;
import com.morladim.morganrss.base.rss2.Rss2Xml;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.morladim.morganrss.base.network.Constants.RETRY_TIMES;

/**
 * <br>创建时间：2017/7/20.
 *
 * @author morladim
 */
public class NewsProvider {

    private NewsProvider() {

    }

    public static void getXml(String url, Consumer<List<Item>> onNext, Consumer<Throwable> onError, final int offset, final int limit) {
        RssHttpClient.getNewsApi().getXml(url)
                .subscribeOn(Schedulers.io())
                .map(new Function<Rss2Xml, List<Item>>() {
                    @Override
                    public List<Item> apply(@NonNull Rss2Xml rss2Xml) throws Exception {
                        String version = rss2Xml.version;
                        if (version != null) {
                            long versionId = RssVersionManager.getInstance().insertOrUpdate(version);
                            long channelId = ChannelManager.getInstance().insertOrUpdate(rss2Xml.channel, versionId);
                            return ItemManager.getInstance().getList(channelId, offset, limit);
                        }
                        return null;
                    }
                })
                .retry(RETRY_TIMES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    public static void getChannel(String url, Consumer<Channel> onNext, Consumer<Throwable> onError) {
        RssHttpClient.getNewsApi().getXml(url)
                .subscribeOn(Schedulers.io())
                .map(new Function<Rss2Xml, Channel>() {

                    @Override
                    public Channel apply(@NonNull Rss2Xml rss2Xml) throws Exception {
                        String version = rss2Xml.version;
                        if (version != null) {
                            long versionId = RssVersionManager.getInstance().insertOrUpdate(version);
                            long channelId = ChannelManager.getInstance().insertOrUpdate(rss2Xml.channel, versionId);
                            return ChannelManager.getInstance().getChannelById(channelId);
                        }
                        return null;
                    }
                }).retry(RETRY_TIMES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }
}
