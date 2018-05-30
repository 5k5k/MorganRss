package com.morladim.morganrss.base.database.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 頻道表和頻道分組表，多對多關係關聯表
 *
 * @author morladim
 * @date 2018/5/10
 */
@Entity
public class ChannelJoinGroup {

    @Id(autoincrement = true)
    private Long id;

    private Long channelId;

    private Long channelGroupId;

    /**
     * 排序值
     */
    private Long order;

    @Generated(hash = 1747726703)
    public ChannelJoinGroup(Long id, Long channelId, Long channelGroupId,
            Long order) {
        this.id = id;
        this.channelId = channelId;
        this.channelGroupId = channelGroupId;
        this.order = order;
    }

    @Generated(hash = 2102188691)
    public ChannelJoinGroup() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChannelId() {
        return this.channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getChannelGroupId() {
        return this.channelGroupId;
    }

    public void setChannelGroupId(Long channelGroupId) {
        this.channelGroupId = channelGroupId;
    }

    public Long getOrder() {
        return this.order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }
}
