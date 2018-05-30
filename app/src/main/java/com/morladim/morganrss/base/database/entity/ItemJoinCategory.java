package com.morladim.morganrss.base.database.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;

/**
 * item绑定标签类，多對多關係表
 * <br>创建时间：2017/7/17.
 *
 * @author morladim
 */
@SuppressWarnings("unused")
@Entity
public class ItemJoinCategory {

    @Id(autoincrement = true)
    private Long id;

    private Long itemId;

    private Long categoryId;

    private Date createAt;

    public ItemJoinCategory(Long itemId, Long categoryId) {
        this.itemId = itemId;
        this.categoryId = categoryId;
        this.createAt = new Date();
    }

    @Generated(hash = 75556036)
    public ItemJoinCategory(Long id, Long itemId, Long categoryId, Date createAt) {
        this.id = id;
        this.itemId = itemId;
        this.categoryId = categoryId;
        this.createAt = createAt;
    }

    @Generated(hash = 119957640)
    public ItemJoinCategory() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getItemId() {
        return this.itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Date getCreateAt() {
        return this.createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

}
