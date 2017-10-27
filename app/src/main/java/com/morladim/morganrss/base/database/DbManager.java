package com.morladim.morganrss.base.database;

import com.morladim.morganrss.base.RssApplication;
import com.morladim.morganrss.base.database.dao.DaoMaster;
import com.morladim.morganrss.base.database.dao.DaoSession;

/**
 * 单例获得DaoSession
 * <br>Created on 2017/7/15 下午3:43
 *
 * @author morladim.
 */

class DbManager {

    ///////////////////////////////////////////////////////////////////////////
    // 调试时报错，需要关闭Instant Run。
    ///////////////////////////////////////////////////////////////////////////

    private static final String DB_NAME = "rss_db";
    private volatile static DbManager instance;
    private DaoSession daoSession;

    private DbManager(RssApplication context) {
        DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(context, DB_NAME);
        DaoMaster daoMaster = new DaoMaster(openHelper.getWritableDatabase());
        daoSession = daoMaster.newSession();
    }

    private static DbManager getInstance() {
        if (instance == null) {
            synchronized (DbManager.class) {
                if (instance == null) {
                    instance = new DbManager(RssApplication.getContext());
                }
            }
        }
        return instance;
    }

    static DaoSession getDaoSession() {
        return getInstance().daoSession;
    }
}
