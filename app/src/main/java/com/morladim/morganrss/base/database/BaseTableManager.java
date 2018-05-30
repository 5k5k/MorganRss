package com.morladim.morganrss.base.database;

import android.database.Cursor;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 表管理工具基类
 * <br>创建时间：2017/7/18.
 *
 * @author morladim
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class BaseTableManager<E, DAO extends AbstractDao<E, Long>> {

    protected abstract DAO getDao();

    public long insert(E entity) {
        return getDao().insert(entity);
    }

    public void deleteByKey(@NotNull Long id) {
        getDao().deleteByKey(id);
    }

    public void update(E entity) {
        getDao().update(entity);
    }

    public List<E> getAll() {
        return getDao().loadAll();
    }

    public void insertInTx(List<E> entityList) {
        getDao().insertInTx(entityList);
    }

    /**
     * 獲取最大行數
     *
     * @return order最大值
     */
    public Long getMaxOrder() {
        // TODO: 2018/5/16 如果有刪除row的情況需要修改本方法
        return getDao().queryBuilder().count();
    }

    /**
     * 根據sql查詢，使用本方法必須要重寫{@link BaseTableManager#getEntityByCursor(Cursor)}
     *
     * @param sql sql語句
     * @return 查詢結果列表
     */
    public List<E> getColumns(String sql) {
        List<E> columns = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getDao().getDatabase().rawQuery(sql, null);
            if (cursor.moveToNext()) {
                columns.add(getEntityByCursor(cursor));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return columns;
    }

    protected E getEntityByCursor(Cursor cursor) {
        return null;
    }

}
