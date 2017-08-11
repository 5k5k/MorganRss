package com.morladim.morganrss.base.database;

import com.morladim.morganrss.base.database.dao.CategoryDao;
import com.morladim.morganrss.base.database.entity.Category;
import com.morladim.morganrss.base.database.entity.ItemJoinCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * <br>创建时间：2017/7/18.
 *
 * @author morladim
 */
@SuppressWarnings("WeakerAccess")
public class CategoryManager extends BaseTableManager<Category, CategoryDao> {

    private volatile static CategoryManager instance;

    public static CategoryManager getInstance() {
        if (instance == null) {
            synchronized (CategoryManager.class) {
                if (instance == null) {
                    instance = new CategoryManager();
                }
            }
        }
        return instance;
    }

    @Override
    protected CategoryDao getDao() {
        return DBManager.getDaoSession().getCategoryDao();
    }

    public void insertOrUpdateList(List<String> categoryList, long itemId) {
        if (categoryList == null || categoryList.size() == 0) {
            return;
        }
        List<ItemJoinCategory> itemJoinCategoryList = new ArrayList<>(categoryList.size());
        List<Long> categoryIdList = new ArrayList<>(categoryList.size());
        for (String category : categoryList) {
            categoryIdList.add(insertOrUpdate(category));
        }

        for (Long id : categoryIdList) {
            if (!ItemJoinCategoryManager.getInstance().existInDB(itemId, id)) {
                itemJoinCategoryList.add(new ItemJoinCategory(itemId, id));
            }
        }
        ItemJoinCategoryManager.getInstance().insertInTx(itemJoinCategoryList);
    }

    public Long insertOrUpdate(String categoryFromXml) {
        if (categoryFromXml == null) {
            return null;
        }
        Category categoryInDB = getDao().queryBuilder().where(CategoryDao.Properties.Name.eq(categoryFromXml)).unique();
        if (categoryInDB == null) {
            return insert(new Category(categoryFromXml));
        } else {
            return categoryInDB.getId();
        }
    }
}