package com.morladim.morganrss.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.morladim.morganrss.R;
import com.morladim.morganrss.base.database.ChannelGroupManager;
import com.morladim.morganrss.base.database.entity.Channel;
import com.morladim.morganrss.base.database.entity.ChannelGroup;
import com.morladim.morganrss.base.ui.BaseActivity;
import com.morladim.morganrss.base.ui.ContentView;
import com.morladim.morganrss.base.util.AppUtils;
import com.morladim.morganrss.base.util.NetworkUtils;
import com.morladim.morganrss.base.util.SnackbarUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * 主頁面
 *
 * @author morladim
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    // TODO: 2018/6/6 在onresume中加入啟動其他進程service ；修改各個activity的主題
    // TODO: 2018/6/6 tab位置和fragment位置不一致

    /**
     * 啟動MainActivity
     *
     * @param context 上下文
     */
    public static void start(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //有include的时候要以这种方式绑定view
        ButterKnife.bind(R.layout.content_activity_main, rootView);
        initDrawer();
        loadData();
    }

    /**
     * 初始化抽屜菜單
     */
    private void initDrawer() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        expandableListView.setAdapter(new MenuAdapter(this));
    }

    private void loadData() {
        showLoadImageHint();
        showData();
    }

    /**
     * 顯示提示
     */
    private void showLoadImageHint() {
        if (NetworkUtils.getInstance().isConnected()) {
            //沒處在wifi環境下
            if (!NetworkUtils.getInstance().isConnectedWifi()) {
                if (AppUtils.getImageLoadMode() == AppUtils.IMAGE_LOAD_ALWAYS) {
                    SnackbarUtils.showInfo(rootView, showImageWithoutWifi);
                } else {
                    SnackbarUtils.showInfo(rootView, notShowImageWithoutWifi);
                }
            } else {
                //wifi下但是設置為始終不顯示圖片時提示
                if (AppUtils.getImageLoadMode() == AppUtils.IMAGE_NOT_LOAD) {
                    SnackbarUtils.showInfo(rootView, notShowImageAlways);
                }
            }
        } else {
            SnackbarUtils.showError(rootView, noInternet);
        }
    }

    @SuppressLint("CheckResult")
    private void showData() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) {
                if (list == null) {
                    ChannelGroup group = ChannelGroupManager.getInstance().getSelectedGroup();
                    assert getSupportActionBar() != null;
                    getSupportActionBar().setTitle(group.getName());
                    list = group.getChannelList();
                }
                e.onNext(list);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        getDataFromNet();
                    }
                });
    }

    private void initViewPager(List<Channel> list) {
        rssPagerAdapter = new RssPagerAdapter(getSupportFragmentManager(), list);
        viewPager.setAdapter(rssPagerAdapter);
        viewPager.setOffscreenPageLimit(10);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    private void getDataFromNet() {
        initViewPager(new ArrayList<Channel>());

        List<Channel> newChannelList = new ArrayList<>();
        for (Channel channel : list) {
            if (channel.getTitle() != null) {
                rssPagerAdapter.addItemByOrder(channel);
                tabLayout.scrollTo((int) tabLayout.getChildAt(tabLayout.getSelectedTabPosition()).getX(), (int) tabLayout.getChildAt(tabLayout.getSelectedTabPosition()).getY());
            } else {
                newChannelList.add(channel);
            }
        }

        MultipleRequestManager.getInstance().loadChannels(newChannelList, new MultipleRequestManager.GenerateChannelsListener() {
            @Override
            public void allDone(int success, int error) {
                Timber.d(loadResult, success, error);
                if (error > 0) {
                    SnackbarUtils.showWarn(rootView, String.format(Locale.CHINA, failedCount, error));
                }
            }

            @Override
            public void oneChannelDone(Channel channel) {
                rssPagerAdapter.addItemByOrder(channel);
                tabLayout.scrollTo((int) tabLayout.getChildAt(tabLayout.getSelectedTabPosition()).getX(), (int) tabLayout.getChildAt(tabLayout.getSelectedTabPosition()).getY());
                Timber.d(loadOne, channel.getTitle());
            }

            @Override
            public void oneChannelError() {

            }

        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            TestActivity.start(this);
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 當前顯示的頻道列表
     */
    private List<Channel> list;

    /**
     * 內容adapter
     */
    private RssPagerAdapter rssPagerAdapter;

    /**
     * 根視圖
     */
    View rootView;

    /**
     * 菜单
     */
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.list)
    ExpandableListView expandableListView;

    @BindView(R.id.container)
    ViewPager viewPager;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    /**
     * 不在wifi下时显示图片提示
     */
    @BindString(R.string.net_state_not_wifi_show_image)
    String showImageWithoutWifi;

    /**
     * 不在wifi下时不显示图片提示
     */
    @BindString(R.string.net_state_not_wifi_not_show_image)
    String notShowImageWithoutWifi;

    /**
     * 始终不显示图片
     */
    @BindString(R.string.net_state_always_no_image)
    String notShowImageAlways;

    /**
     * 无网络
     */
    @BindString(R.string.net_state_no_internet)
    String noInternet;

    /**
     * 加载结果
     */
    @BindString(R.string.log_multiple_done)
    String loadResult;

    /**
     * 失败条数
     */
    @BindString(R.string.hint_multiple_load_failed)
    String failedCount;

    /**
     * 加载一个频道成功提示
     */
    @BindString(R.string.log_multiple_one_channel_done)
    String loadOne;
}
