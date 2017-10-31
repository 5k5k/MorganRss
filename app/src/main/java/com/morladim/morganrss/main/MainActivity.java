package com.morladim.morganrss.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.morladim.morganrss.R;
import com.morladim.morganrss.base.database.ChannelManager;
import com.morladim.morganrss.base.database.entity.Channel;
import com.morladim.morganrss.base.ui.BaseActivity;
import com.morladim.morganrss.base.util.AppUtils;
import com.morladim.morganrss.base.util.SnackbarHolder;
import com.morladim.tools.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @author morladim
 */
public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private List<Channel> list;
    private RssPagerAdapter rssPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        loadData();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void loadData() {
        Observable.just(0)
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(@NonNull Integer integer) throws Exception {
                        list = ChannelManager.getInstance().getAll();
                        switch (NetworkUtils.checkNetworkState()) {
                            case NetworkUtils.NO_WIFI:
                                if (!AppUtils.isNoImageMode()) {
                                    return 1;
                                }
                                break;
                            case NetworkUtils.NO_NETWORK:
                                SnackbarHolder.ERROR.getNew(findViewById(R.id.content_main), "网络无法连接！");
                                break;
                            case NetworkUtils.WIFI_CONNECTED:
                            default:
                                break;
                        }
                        return 0;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Integer integer) throws Exception {
                        if (integer > 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("仅在WIFI下载入图片")
                                    .setMessage("是否只在Wifi网络环境下加载图片，强烈建议开启该设置以节约流量。\n您可以随时在设置中更改该设置。").setPositiveButton("开启", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    AppUtils.setNoImageMode(true);
                                    showData();
                                }
                            }).setNegativeButton("不开启", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    showData();
                                }
                            }).setCancelable(false).show();
                        } else {
                            showData();
                        }
                    }
                });
    }

    private void showData() {
        if (list.size() == 0) {
            getDataFromNet();
        } else {
            initViewPager(ChannelManager.getInstance().getAll());
        }
    }

    private void initViewPager(List<Channel> list) {
        rssPagerAdapter = new RssPagerAdapter(getSupportFragmentManager(), list);
        ViewPager viewPager = findViewById(R.id.container);
        viewPager.setAdapter(rssPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    private void getDataFromNet() {
        initViewPager(new ArrayList<Channel>());

        MultipleRequestManager.getInstance().generateChannels(new MultipleRequestManager.GenerateChannelsListener() {
            @Override
            public void allDone(int success, int error) {
                Timber.d("channel load done success %s, error %s", success, error);
            }

            @Override
            public void oneChannelDone(Channel channel) {
                rssPagerAdapter.addItemByOrder(channel);
                Timber.d("channel loaded title  %s", channel.getTitle());
            }

            @Override
            public void oneChannelError() {

            }

        });
    }

    @Override
    protected void onDestroy() {
//        unbindService(connection);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
