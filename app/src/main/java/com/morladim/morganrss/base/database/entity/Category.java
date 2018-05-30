package com.morladim.morganrss.base.database.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;

/**
 * item中的标签
 * <br>创建时间：2017/7/17.
 *
 * @author morladim
 */
@SuppressWarnings("unused")
@Entity
public class Category {

    @Id(autoincrement = true)
    private Long id;

    private String name;

    private Date createAt;

    public Category(String name) {
        this.name = name;
        createAt = new Date();
    }

    @Generated(hash = 643128027)
    public Category(Long id, String name, Date createAt) {
        this.id = id;
        this.name = name;
        this.createAt = createAt;
    }

    @Generated(hash = 1150634039)
    public Category() {
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

    public Date getCreateAt() {
        return this.createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }


}
