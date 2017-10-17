package com.morladim.morganrss.base.database;

import com.morladim.morganrss.base.database.dao.RssVersionDao;
import com.morladim.morganrss.base.database.entity.RssVersion;

import org.greenrobot.greendao.annotation.NotNull;

import java.util.Date;

/**
 * RssVersion表管理类，提供数据访问方法封装
 * <br>Created on 2017/7/16 上午7:32
 *
 * @author  morladim.
 */

@SuppressWarnings("WeakerAccess")
public class RssVersionManager extends BaseTableManager<RssVersion, RssVersionDao> {

    private volatile static RssVersionManager instance;

    private RssVersionManager() {

    }

    public static RssVersionManager getInstance() {
        if (instance == null) {
            synchronized (RssVersionManager.class) {
                if (instance == null) {
                    instance = new RssVersionManager();
                }
            }
        }
        return instance;
    }

    @Override
    protected RssVersionDao getDao() {
        return DBManager.getDaoSession().getRssVersionDao();
    }

    public RssVersion getVersionByName(@NotNull String name) {
        return getDao().queryBuilder().where(RssVersionDao.Properties.Name.eq(name.trim())).unique();
    }

    /**
     * 根据版本名向表中添加数据，如果已经存在同名版本，则times加1。
     *
     * @param versionName rss版本名
     * @return 插入行号
     */
    public synchronized long insertOrUpdate(@NotNull String versionName) {
        RssVersion versionInDB = getVersionByName(versionName);
        if (versionInDB == null) {
            return insert(new RssVersion(versionName));
        } else {
            versionInDB.setUpdateAt(new Date());
            versionInDB.setTimes(versionInDB.getTimes() + 1);
            update(versionInDB);
            return versionInDB.getId();
        }
    }
}
