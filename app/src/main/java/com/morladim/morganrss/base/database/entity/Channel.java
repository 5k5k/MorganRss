package com.morladim.morganrss.base.database.entity;

import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.util.Date;

import static com.morladim.morganrss.base.database.DbDateUtils.convertStringToDate;

/**
 * 因为要对item进行分页，所以不toMany item。
 * <br>Created on 2017/7/15 下午12:19
 *
 * @author morladim.
 */
@SuppressWarnings("unused")
@Entity
public class Channel {

    @Id(autoincrement = true)
    private Long id;

    private String title;

    private String description;

    private String imageUrl;

    private String imageLink;

    private String link;

    private String atomLink;

    private String requestUrl;

    @Transient
    private long orderInCurrentGroup;

    /**
     * 请求次数
     */
    private Integer times;

    /**
     * 最新消息获取时间
     */
    private Date lastBuildDate;

    private Long rssVersionId;

    private Date createAt;

    /**
     * 插入数据库时间
     */
    private Date updateAt;

    @Override
    public int hashCode() {
        return ((id == null ? "" : id) + (requestUrl == null ? "" : requestUrl)).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Channel && (id != null && id.equals(((Channel) obj).id) || requestUrl != null && requestUrl.equals(((Channel) obj).requestUrl));
    }

    public long getOrderInCurrentGroup() {
        return orderInCurrentGroup;
    }

    public void setOrderInCurrentGroup(long orderInCurrentGroup) {
        this.orderInCurrentGroup = orderInCurrentGroup;
    }

    public void setLastBuildDate(String lastBuildDate) {
        if (TextUtils.isEmpty(lastBuildDate)) {
            return;
        }
        this.lastBuildDate = convertStringToDate(lastBuildDate);
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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageLink() {
        return this.imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAtomLink() {
        return this.atomLink;
    }

    public void setAtomLink(String atomLink) {
        this.atomLink = atomLink;
    }

    public Integer getTimes() {
        return this.times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public Date getLastBuildDate() {
        return this.lastBuildDate;
    }

    public void setLastBuildDate(Date lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }

    public Long getRssVersionId() {
        return this.rssVersionId;
    }

    public void setRssVersionId(Long rssVersionId) {
        this.rssVersionId = rssVersionId;
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

    public String getRequestUrl() {
        return this.requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Channel(String title) {
        this.title = title;
        createAt = new Date();
        updateAt = new Date();
        times = 0;
    }

    @Generated(hash = 674873480)
    public Channel(Long id, String title, String description, String imageUrl,
                   String imageLink, String link, String atomLink, String requestUrl,
                   Integer times, Date lastBuildDate, Long rssVersionId, Date createAt,
                   Date updateAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.imageLink = imageLink;
        this.link = link;
        this.atomLink = atomLink;
        this.requestUrl = requestUrl;
        this.times = times;
        this.lastBuildDate = lastBuildDate;
        this.rssVersionId = rssVersionId;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    @Generated(hash = 459652974)
    public Channel() {
    }

}
