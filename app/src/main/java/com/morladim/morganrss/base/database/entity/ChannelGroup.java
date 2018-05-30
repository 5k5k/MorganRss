package com.morladim.morganrss.base.database.entity;

import com.morladim.morganrss.base.database.dao.ChannelDao;
import com.morladim.morganrss.base.database.dao.ChannelGroupDao;
import com.morladim.morganrss.base.database.dao.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

/**
 * 頻道分組表
 *
 * @author morladim
 * @date 2018/5/10
 */
@Entity
public class ChannelGroup {

    @Id(autoincrement = true)
    private Long id;

    private String name;

    private String description;

    private boolean selected;

    /**
     * 排序值
     */
    private Long order;

    @ToMany
    @JoinEntity(entity = ChannelJoinGroup.class, sourceProperty = "channelGroupId", targetProperty = "channelId")
    private List<Channel> channelList;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1179602967)
    private transient ChannelGroupDao myDao;

    @Generated(hash = 1124095484)
    public ChannelGroup(Long id, String name, String description, boolean selected, Long order) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.selected = selected;
        this.order = order;
    }

    @Generated(hash = 23941188)
    public ChannelGroup() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1184447587)
    public List<Channel> getChannelList() {
        if (channelList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ChannelDao targetDao = daoSession.getChannelDao();
            List<Channel> channelListNew = targetDao._queryChannelGroup_ChannelList(id);
            synchronized (this) {
                if (channelList == null) {
                    channelList = channelListNew;
                }
            }
        }
        return channelList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1235850938)
    public synchronized void resetChannelList() {
        channelList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 81856386)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getChannelGroupDao() : null;
    }

    public Long getOrder() {
        return this.order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }


}
