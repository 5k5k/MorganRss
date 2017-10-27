package com.morladim.morganrss.base.database;

import com.morladim.morganrss.base.database.dao.ItemJoinCategoryDao;
import com.morladim.morganrss.base.database.entity.ItemJoinCategory;

/**
 * <br>创建时间：2017/7/18.
 *
 * @author morladim
 */
@SuppressWarnings("WeakerAccess")
public class ItemJoinCategoryManager extends BaseTableManager<ItemJoinCategory, ItemJoinCategoryDao> {

    private volatile static ItemJoinCategoryManager instance;

    public static ItemJoinCategoryManager getInstance() {
        if (instance == null) {
            synchronized (ItemJoinCategoryManager.class) {
                if (instance == null) {
                    instance = new ItemJoinCategoryManager();
                }
            }
        }
        return instance;
    }

    @Override
    protected ItemJoinCategoryDao getDao() {
        return DbManager.getDaoSession().getItemJoinCategoryDao();
    }

    public boolean existInDB(Long itemId, Long categoryId) {
        return getDao().queryBuilder().where(ItemJoinCategoryDao.Properties.CategoryId.eq(categoryId), ItemJoinCategoryDao.Properties.ItemId.eq(itemId)).unique() != null;
    }
}
