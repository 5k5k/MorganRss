package com.morladim.morganrss.base.database.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;

/**
 * <br>Created on 2017/7/15 下午12:22 <p>
 *
 * @author morladim.
 */
@SuppressWarnings("unused")
@Entity
public class RssVersion {

    @Id(autoincrement = true)
    private Long id;

    private String name;

    /**
     * 获取次数
     */
    private Integer times;

    private Date createAt;

    private Date updateAt;

    public RssVersion(String name) {
        this.name = name;
        createAt = new Date();
        updateAt = new Date();
        times = 0;
    }

    @Generated(hash = 623294046)
    public RssVersion(Long id, String name, Integer times, Date createAt,
                      Date updateAt) {
        this.id = id;
        this.name = name;
        this.times = times;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    @Generated(hash = 601683626)
    public RssVersion() {
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

    public Integer getTimes() {
        return this.times;
    }

    public void setTimes(Integer times) {
        this.times = times;
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
}
