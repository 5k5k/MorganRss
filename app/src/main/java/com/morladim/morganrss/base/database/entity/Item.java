package com.morladim.morganrss.base.database.entity;

import android.text.TextUtils;

import com.morladim.morganrss.base.database.DbDateUtils;
import com.morladim.morganrss.base.database.dao.CategoryDao;
import com.morladim.morganrss.base.database.dao.DaoSession;
import com.morladim.morganrss.base.database.dao.ItemDao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.Date;
import java.util.List;

/**
 * 信息
 * <br>创建时间：2017/7/17.
 *
 * @author morladim
 */
@SuppressWarnings("unused")
@Entity
public class Item {

    @Id(autoincrement = true)
    private Long id;

    private String title;

    private String link;

    private String comments;

    private Date pubDate;

    private String creator;

    @ToMany
    @JoinEntity(entity = ItemJoinCategory.class, sourceProperty = "itemId", targetProperty = "categoryId")
    private List<Category> categoryList;

    private String guid;

    private String description;

    private String content;

    private String imageUrl;

    private String imageWidth;

    private String imageHeight;

    private String commentRss;

    private Long channelId;

    private Date createAt;

    private Date updateAt;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 182764869)
    private transient ItemDao myDao;

    @Generated(hash = 1989851213)
    public Item(Long id, String title, String link, String comments, Date pubDate, String creator, String guid,
            String description, String content, String imageUrl, String imageWidth, String imageHeight,
            String commentRss, Long channelId, Date createAt, Date updateAt) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.comments = comments;
        this.pubDate = pubDate;
        this.creator = creator;
        this.guid = guid;
        this.description = description;
        this.content = content;
        this.imageUrl = imageUrl;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.commentRss = commentRss;
        this.channelId = channelId;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    @Generated(hash = 1470900980)
    public Item() {
    }

    public void setPubDate(String pubDate) {
        if (TextUtils.isEmpty(pubDate)) {
            return;
        }
        this.pubDate = DbDateUtils.convertStringToDate(pubDate);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getPubDate() {
        return this.pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getGuid() {
        return this.guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageWidth() {
        return this.imageWidth;
    }

    public void setImageWidth(String imageWidth) {
        this.imageWidth = imageWidth;
    }

    public String getImageHeight() {
        return this.imageHeight;
    }

    public void setImageHeight(String imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getCommentRss() {
        return this.commentRss;
    }

    public void setCommentRss(String commentRss) {
        this.commentRss = commentRss;
    }

    public Long getChannelId() {
        return this.channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Date getCreateAt() {
        return this.createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return this.updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1444970442)
    public List<Category> getCategoryList() {
        if (categoryList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CategoryDao targetDao = daoSession.getCategoryDao();
            List<Category> categoryListNew = targetDao._queryItem_CategoryList(id);
            synchronized (this) {
                if (categoryList == null) {
                    categoryList = categoryListNew;
                }
            }
        }
        return categoryList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1140998005)
    public synchronized void resetCategoryList() {
        categoryList = null;
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

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 881068859)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getItemDao() : null;
    }

}
