package com.morladim.morganrss.base.database;

import com.morladim.morganrss.base.database.dao.ChannelGroupDao;
import com.morladim.morganrss.base.database.dao.ChannelJoinGroupDao;
import com.morladim.morganrss.base.database.entity.Channel;
import com.morladim.morganrss.base.database.entity.ChannelGroup;
import com.morladim.morganrss.base.database.entity.ChannelJoinGroup;

import java.util.List;

/**
 * ChannelGroup管理類
 *
 * @author morladim
 * @date 2018/5/10
 */
public class ChannelGroupManager extends BaseTableManager<ChannelGroup, ChannelGroupDao> {
    @Override
    protected ChannelGroupDao getDao() {
        return DbManager.getDaoSession().getChannelGroupDao();
    }

    private ChannelGroupManager() {

    }

    public static ChannelGroupManager getInstance() {
        if (instance == null) {
            synchronized (ChannelGroupManager.class) {
                if (instance == null) {
                    instance = new ChannelGroupManager();
                }
            }
        }
        return instance;
    }

    /**
     * 獲取當前選擇的頻道列表
     *
     * @return 頻道列表
     */
    public ChannelGroup getSelectedGroup() {
        ChannelGroup channelGroup = getDao().queryBuilder().where(ChannelGroupDao.Properties.Selected.eq(true)).build().forCurrentThread().unique();
        List<ChannelJoinGroup> joinList = ChannelJoinGroupManager.getInstance().getDao().queryBuilder().where(ChannelJoinGroupDao.Properties.ChannelGroupId.eq(channelGroup.getId())).build().forCurrentThread().list();
        for (Channel channel : channelGroup.getChannelList()) {
            for (ChannelJoinGroup channelJoinGroup : joinList) {
                if (channelJoinGroup.getChannelId().equals(channel.getId())) {
                    channel.setOrderInCurrentGroup(channelJoinGroup.getOrder());
                    break;
                }
            }
        }
        return channelGroup;
    }

    private volatile static ChannelGroupManager instance;

}