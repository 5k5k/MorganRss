package com.morladim.morganrss.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.morladim.morganrss.base.database.entity.Channel;

import java.util.List;

/**
 * 展示所有Rss频道信息的PagerAdapter
 * <br>创建时间：2017/8/15.
 *
 * @author morladim
 */
public class RssPagerAdapter extends FragmentStatePagerAdapter {

    private volatile List<Channel> data;

    public RssPagerAdapter(FragmentManager fm, List<Channel> data) {
        super(fm);
        this.data = data;
    }

    @Override
    public Fragment getItem(int position) {
        Channel channel = data.get(position);
        return RssFragment.newInstance(channel.getTitle(), channel.getRequestUrl(), channel.getId());
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return data.get(position).getTitle();
    }

    public void addItem(Channel channel) {
        data.add(channel);
        notifyDataSetChanged();
    }

}
