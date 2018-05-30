package com.morladim.morganrss.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.morladim.morganrss.base.database.entity.Channel;

import java.util.HashMap;
import java.util.List;

/**
 * 展示所有Rss频道信息的PagerAdapter
 * <br>创建时间：2017/8/15.
 *
 * @author morladim
 */
public class RssPagerAdapter extends FragmentStatePagerAdapter {

    public RssPagerAdapter(FragmentManager fm, List<Channel> data) {
        super(fm);
        this.data = data;
        map = new HashMap<>();
    }

    @Override
    public Fragment getItem(int position) {
        Channel channel = data.get(position);
        RssFragment fragment = RssFragment.newInstance(channel.getTitle(), channel.getRequestUrl(), channel.getId());
        map.put(channel, fragment);
        return fragment;
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof RssFragment && ((RssFragment) object).isRefresh()) {
            ((RssFragment) object).setRefresh(false);
            return POSITION_NONE;
        }
        return POSITION_UNCHANGED;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return data.get(position).getTitle();
    }

    /**
     * 根据xml中存储的channel顺序加载数据
     *
     * @param channel 待加入的channel
     */
    public void addItemByOrder(Channel channel) {
        if (data.size() == 0) {
            data.add(channel);
        } else {
            boolean add = false;
            for (int i = 0, count = data.size(); i < count; i++) {
                if (add) {
                    //已經加入的情況設置對應fragment刷新
                    RssFragment fragment = map.get(data.get(i));
                    if (fragment != null) {
                        fragment.setRefresh(true);
                    }
                    continue;
                }

                if (channel.getOrderInCurrentGroup() < data.get(i).getOrderInCurrentGroup()) {
                    add = true;
                    data.add(i, channel);
                }
            }
            if (!add) {
                data.add(channel);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 數據
     */
    private volatile List<Channel> data;

    /**
     * 存儲頻道和fragment的對應關係，為了設置{@link RssPagerAdapter#getItemPosition(Object)}返回值。
     */
    private HashMap<Channel, RssFragment> map;
}
