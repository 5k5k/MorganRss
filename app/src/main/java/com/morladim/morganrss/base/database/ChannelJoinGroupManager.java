package com.morladim.morganrss.base.database;

import com.morladim.morganrss.base.database.dao.ChannelJoinGroupDao;
import com.morladim.morganrss.base.database.entity.ChannelJoinGroup;

/**
 * 頻道和分組關聯表
 *
 * @author morladim
 * @date 2018/5/16
 */
public class ChannelJoinGroupManager extends BaseTableManager<ChannelJoinGroup, ChannelJoinGroupDao> {
    @Override
    protected ChannelJoinGroupDao getDao() {
        return DbManager.getDaoSession().getChannelJoinGroupDao();
    }

    private volatile static ChannelJoinGroupManager instance;

    public static ChannelJoinGroupManager getInstance() {
        if (instance == null) {
            synchronized (ChannelJoinGroupManager.class) {
                if (instance == null) {
                    instance = new ChannelJoinGroupManager();
                }
            }
        }
        return instance;
    }

    private ChannelJoinGroupManager() {

    }
}
