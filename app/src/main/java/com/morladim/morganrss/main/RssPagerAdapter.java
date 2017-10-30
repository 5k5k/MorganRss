package com.morladim.morganrss.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.morladim.morganrss.R;
import com.morladim.morganrss.base.RssApplication;
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

    /**
     * 根据xml中存储的channel顺序加载数据
     *
     * @param channel 待加入的channel
     */
    public synchronized void addItemByOrder(Channel channel) {
        int positionInXml = RssPagerAdapter.getChannelPositionByUrl(channel.getRequestUrl());
        if (positionInXml < 0 || data.size() == 0) {
            data.add(channel);
        } else {
            for (int i = 0, count = data.size(); i < count; i++) {
                int j = getChannelPositionByUrl(data.get(i).getRequestUrl());
                if (j > positionInXml) {
                    data.add(i, channel);
                    break;
                }
                if (i == count - 1) {
                    data.add(channel);
                }
            }

        }
        notifyDataSetChanged();
    }


    private static final String[] URLS = RssApplication.getContext().getResources().getStringArray(R.array.defaultUrls);

    public static int getChannelPositionByUrl(String channelUrl) {
        for (int i = 0, count = URLS.length; i < count; i++) {
            if (URLS[i].equals(channelUrl)) {
                return i;
            }
        }
        return -1;
    }

}
