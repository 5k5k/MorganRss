package com.morladim.morganrss.base.database;

import android.text.TextUtils;

import com.morladim.morganrss.base.database.dao.ChannelDao;
import com.morladim.morganrss.base.database.entity.Channel;
import com.morladim.morganrss.base.rss2.Link;
import com.morladim.morganrss.base.rss2.Rss2Channel;

import org.greenrobot.greendao.annotation.NotNull;

import java.util.Date;

/**
 * channel表service类
 * <br>Created on 2017/7/15 下午4:10
 *
 * @author morladim.
 */

@SuppressWarnings("WeakerAccess")
public class ChannelManager extends BaseTableManager<Channel, ChannelDao> {

    private volatile static ChannelManager channelManager;

    private ChannelManager() {

    }

    public static ChannelManager getInstance() {
        if (channelManager == null) {
            synchronized (ChannelManager.class) {
                if (channelManager == null) {
                    channelManager = new ChannelManager();
                }
            }
        }
        return channelManager;
    }

    @Override
    protected ChannelDao getDao() {
        return DBManager.getDaoSession().getChannelDao();
    }

    public long insertOrUpdate(@NotNull Rss2Channel rss2Channel, long versionId, String requestUrl) {
        Channel channelInDB = getChannelByTitleAndLink(rss2Channel.title, requestUrl);

        long channelId;
        if (channelInDB == null) {
            channelId = insert(convertXmlToEntity(rss2Channel, versionId, requestUrl));
        } else {
            channelInDB.setUpdateAt(new Date());
            channelInDB.setTimes(channelInDB.getTimes() + 1);
            channelInDB.setLastBuildDate(rss2Channel.lastBuildDate);
            update(channelInDB);
            channelId = channelInDB.getId();
        }
        ItemManager.getInstance().insertOrUpdateList(rss2Channel.itemList, channelId);
        return channelId;
    }

    public Channel getChannelById(long channelId) {
        return getDao().queryBuilder().where(ChannelDao.Properties.Id.eq(channelId)).unique();
    }

    public Channel getChannelByTitleAndLink(String title, String link) {
        return getDao().queryBuilder().where(ChannelDao.Properties.Title.eq(title), ChannelDao.Properties.RequestUrl.eq(link)).unique();
    }

    public Channel convertXmlToEntity(@NotNull Rss2Channel rss2Channel, long versionId, String requestUrl) {
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

        Channel channel = new Channel(rss2Channel.title);
        channel.setLink(linkUrl);
        channel.setAtomLink(atomLinkUrl);
        channel.setDescription(rss2Channel.description);
        channel.setImageUrl(rss2Channel.image == null ? null : rss2Channel.image.url);
        channel.setImageLink(rss2Channel.image == null ? null : rss2Channel.image.link);
        channel.setLastBuildDate(rss2Channel.lastBuildDate);
        channel.setRssVersionId(versionId);
        channel.setRequestUrl(requestUrl);
        return channel;
    }
}
