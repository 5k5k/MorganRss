package com.morladim.morganrss.base.network;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.morladim.morganrss.base.database.ChannelManager;
import com.morladim.morganrss.base.database.ItemManager;
import com.morladim.morganrss.base.database.RssVersionManager;
import com.morladim.morganrss.base.database.entity.Channel;
import com.morladim.morganrss.base.database.entity.Item;
import com.morladim.morganrss.base.rss2.Link;
import com.morladim.morganrss.base.rss2.Rss2Channel;
import com.morladim.morganrss.base.rss2.Rss2Xml;

import java.util.Date;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.morladim.morganrss.base.network.Constants.RETRY_TIMES;

/**
 * 網絡數據提供類
 * <br>创建时间：2017/7/20.
 *
 * @author morladim
 */
@SuppressLint("CheckResult")
public class NewsProvider {

    private NewsProvider() {

    }

    public static void getXml(final String url, Consumer<List<Item>> onNext, Consumer<Throwable> onError, final int offset, final int limit) {
        RssHttpClient.getNewsApi().getXml(url)
                .subscribeOn(Schedulers.from(RequestPool.getInstance()))
                .map(new Function<Rss2Xml, List<Item>>() {
                    @Override
                    public List<Item> apply(@NonNull Rss2Xml rss2Xml) throws Exception {
                        String version = rss2Xml.version;
                        if (version != null) {
                            long versionId = RssVersionManager.getInstance().insertOrUpdate(version);
                            long channelId = ChannelManager.getInstance().insertOrUpdate(rss2Xml.channel, versionId, url);
                            return ItemManager.getInstance().getList(channelId, offset, limit);
                        }
                        return null;
                    }
                })
                .retry(RETRY_TIMES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    /**
     * 通過rss獲取頻道信息
     *
     * @param channel 頻道
     * @param onNext  回調
     * @param onError 回調
     */
    public static void getChannel(final Channel channel, Consumer<Channel> onNext, Consumer<Throwable> onError) {
        RssHttpClient.getNewsApi().getXml(channel.getRequestUrl())
                .map(new Function<Rss2Xml, Channel>() {

                    @Override
                    public Channel apply(@NonNull Rss2Xml rss2Xml) throws Exception {
                        String version = rss2Xml.version;
                        if (version != null) {
                            long versionId = RssVersionManager.getInstance().insertOrUpdate(version);
                            Rss2Channel rss2Channel = rss2Xml.channel;

                            if (TextUtils.isEmpty(rss2Channel.title) || rss2Channel.linkList == null) {
                                return null;
                            }

                            String linkUrl = null;
                            String atomLinkUrl = null;
                            for (Link link : rss2Channel.linkList) {
                                if (link.value != null) {
                                    linkUrl = link.value;
                                }
                                if (link.href != null) {
                                    atomLinkUrl = link.href;
                                }
                            }
                            channel.setTitle(rss2Channel.title);
                            channel.setLink(linkUrl);
                            channel.setAtomLink(atomLinkUrl);
                            channel.setDescription(rss2Channel.description);
                            channel.setImageUrl(rss2Channel.image == null ? null : rss2Channel.image.url);
                            channel.setImageLink(rss2Channel.image == null ? null : rss2Channel.image.link);
                            channel.setLastBuildDate(rss2Channel.lastBuildDate);
                            channel.setRssVersionId(versionId);

                            channel.setUpdateAt(new Date());
                            channel.setTimes(channel.getTimes() == null ? 1 : channel.getTimes() + 1);

                            ChannelManager.getInstance().update(channel);
                            return channel;
                        }
                        return null;
                    }
                }).retry(RETRY_TIMES)
                .subscribeOn(Schedulers.from(RequestPool.getInstance()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    /**
     * 獲取所有頻道，此處有背壓問題
     *
     * @param channels 頻道列表
     * @param onNext   回調
     * @param onError  錯誤回調
     */
    public static void getChannels(final List<Channel> channels, final Consumer<Channel> onNext, final Consumer<Throwable> onError) {

        Flowable.fromArray(channels.toArray(new Channel[channels.size()])).

                map(new Function<Channel, Object>() {
                    @Override
                    public Object apply(final Channel channel) {
                        RssHttpClient.getNewsApi().getXml(channel.getRequestUrl())
                                .map(new Function<Rss2Xml, Channel>() {

                                    @Override
                                    public Channel apply(@NonNull Rss2Xml rss2Xml) {
                                        String version = rss2Xml.version;
                                        if (version != null) {
                                            long versionId = RssVersionManager.getInstance().insertOrUpdate(version);
                                            Rss2Channel rss2Channel = rss2Xml.channel;

                                            if (TextUtils.isEmpty(rss2Channel.title) || rss2Channel.linkList == null) {
                                                return null;
                                            }

                                            String linkUrl = null;
                                            String atomLinkUrl = null;
                                            for (Link link : rss2Channel.linkList) {
                                                if (link.value != null) {
                                                    linkUrl = link.value;
                                                }
                                                if (link.href != null) {
                                                    atomLinkUrl = link.href;
                                                }
                                            }
                                            channel.setTitle(rss2Channel.title);
                                            channel.setLink(linkUrl);
                                            channel.setAtomLink(atomLinkUrl);
                                            channel.setDescription(rss2Channel.description);
                                            channel.setImageUrl(rss2Channel.image == null ? null : rss2Channel.image.url);
                                            channel.setImageLink(rss2Channel.image == null ? null : rss2Channel.image.link);
                                            channel.setLastBuildDate(rss2Channel.lastBuildDate);
                                            channel.setRssVersionId(versionId);

                                            channel.setUpdateAt(new Date());
                                            channel.setTimes(channel.getTimes() == null ? 1 : channel.getTimes() + 1);

                                            ChannelManager.getInstance().update(channel);
                                            return channel;
                                        }
                                        return null;
                                    }
                                }).subscribeOn(Schedulers.from(RequestPool.getInstance()))
                                .retry(RETRY_TIMES)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(onNext, onError);
                        return channel;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {

                    }
                });
    }
}
