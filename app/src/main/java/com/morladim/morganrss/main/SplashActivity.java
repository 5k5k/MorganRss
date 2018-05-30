package com.morladim.morganrss.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.facebook.stetho.Stetho;
import com.morladim.morganrss.BuildConfig;
import com.morladim.morganrss.R;
import com.morladim.morganrss.base.RssApplication;
import com.morladim.morganrss.base.database.ChannelGroupManager;
import com.morladim.morganrss.base.database.ChannelJoinGroupManager;
import com.morladim.morganrss.base.database.ChannelManager;
import com.morladim.morganrss.base.database.entity.Channel;
import com.morladim.morganrss.base.database.entity.ChannelGroup;
import com.morladim.morganrss.base.database.entity.ChannelJoinGroup;
import com.morladim.morganrss.base.util.ImageLoader;
import com.morladim.morganrss.base.util.MorladimDebugTree;
import com.morladim.morganrss.base.util.NetworkUtils;
import com.morladim.morganrss.base.util.SharedPreferencesUtils;
import com.morladim.morganrss.base.util.SnackbarUtils;
import com.morladim.morganrss.base.util.StringUtils;
import com.squareup.leakcanary.LeakCanary;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * 閃屏頁，加載數據跳轉主頁
 * <br>创建时间：2018/5/9.
 *
 * @author morladim
 */
public class SplashActivity extends Activity implements AddChannelDialogFragment.Callback {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加載數據
        disposable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                initApplicationData(RssApplication.getContext());
                e.onNext(ChannelManager.getInstance().getAll().size());
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        //沒有任何頻道設定時，提示用戶是否載入默認頻道
                        if (integer == 0) {
                            showLoadDataDialog();
                        } else {
                            startMainActivity();
                        }
                    }
                });
    }

    /**
     * 初始化應用工具及數據等
     *
     * @param application 應用
     */
    private void initApplicationData(Application application) {
        SharedPreferencesUtils.init(application);
        ImageLoader.init(application);
        NetworkUtils.init(application);
        StringUtils.init(application);
        //chrome://inspect
        Stetho.initializeWithDefaults(application);

        if (BuildConfig.DEBUG) {
            Timber.plant(new MorladimDebugTree());
        }

        if (LeakCanary.isInAnalyzerProcess(application)) {
            return;
        }
        LeakCanary.install(application);
    }

    /**
     * 啟動主頁面
     */
    private void startMainActivity() {
        if (addFirstDialog != null) {
            addFirstDialog.dismiss();
        }
        if (fragment != null) {
            fragment.setCallback(null);
        }
        disposable.dispose();
        if (saveDisposable != null) {
            saveDisposable.dispose();
        }
        MainActivity.start(this);
        finish();
    }

    /**
     * 顯示是否加載推薦數據對話框
     */
    private void showLoadDataDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        addFirstDialog = builder.setTitle(R.string.splash_load_data_dialog_title)
                .setMessage(R.string.splash_load_data_dialog_message)
                .setPositiveButton(R.string.splash_load_data_dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveDefaultData();
                    }
                }).setNegativeButton(R.string.splash_load_data_dialog_negative, null).setCancelable(false).create();
        addFirstDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = addFirstDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳轉到新建第一條頻道頁面
                        if (fragment == null) {
                            fragment = AddChannelDialogFragment.newInstance(true);
                            fragment.setCallback(SplashActivity.this);
                        }
                        if (!fragment.isAdded()) {
                            fragment.show(getFragmentManager(), null);
                        }
                    }
                });
            }
        });
        addFirstDialog.show();
    }

    /**
     * 加載默認推薦數據，將預先設定的地址列表存入數據庫中。
     */
    @SuppressLint("CheckResult")
    private void saveDefaultData() {
        saveDisposable = Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                ChannelGroup channelGroup = new ChannelGroup();
                channelGroup.setName(getString(R.string.default_group));
                channelGroup.setSelected(true);
                channelGroup.setDescription(getString(R.string.default_group_description));
                channelGroup.setOrder(ChannelGroupManager.getInstance().getMaxOrder());
                ChannelGroupManager.getInstance().insert(channelGroup);

                Long channelGroupId = channelGroup.getId();

                String[] urls = StringUtils.getStringArrayById(R.array.default_urls);
                List<Channel> channelList = new ArrayList<>(urls.length);
                for (String s : urls) {
                    Channel channel = new Channel();
                    channel.setRequestUrl(s);
                    channelList.add(channel);
                }

                ChannelManager.getInstance().insertInTx(channelList);
                long begin = ChannelJoinGroupManager.getInstance().getMaxOrder();
                List<ChannelJoinGroup> channelJoinGroupList = new ArrayList<>(channelList.size());
                for (int i = 0; i < channelList.size(); i++) {
                    ChannelJoinGroup join = new ChannelJoinGroup();
                    join.setChannelGroupId(channelGroupId);
                    join.setChannelId(channelList.get(i).getId());
                    join.setOrder(begin + i);
                    channelJoinGroupList.add(join);
                }
                ChannelJoinGroupManager.getInstance().insertInTx(channelJoinGroupList);
                e.onNext(true);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        startMainActivity();
                    }
                });

//        //分組id
//        Observable<Long> channelGroupObservable = Observable.create(new ObservableOnSubscribe<Long>() {
//
//            @Override
//            public void subscribe(ObservableEmitter<Long> e) throws Exception {
//                ChannelGroup channelGroup = new ChannelGroup();
//                channelGroup.setName(getString(R.string.default_group));
//                channelGroup.setSelected(true);
//                channelGroup.setDescription(getString(R.string.default_group_description));
//                channelGroup.setOrder(ChannelGroupManager.getInstance().getMaxOrder());
//                ChannelGroupManager.getInstance().insert(channelGroup);
//                e.onNext(channelGroup.getId());
//                e.onComplete();
//            }
//        });
//
//        //頻道列表
//        Observable<List<Channel>> channelsObservable = Observable.create(new ObservableOnSubscribe<String[]>() {
//
//            @Override
//            public void subscribe(ObservableEmitter<String[]> e) throws Exception {
//                String[] urls = StringUtils.getStringArrayById(R.array.default_urls);
//                e.onNext(urls);
//                e.onComplete();
//            }
//        }).concatMap(new Function<String[], ObservableSource<String>>() {
//            @Override
//            public ObservableSource<String> apply(String[] strings) throws Exception {
//                return Observable.fromArray(strings);
//            }
//        }).map(new Function<String, Channel>() {
//            @Override
//            public Channel apply(String s) throws Exception {
//                Channel channel = new Channel();
//                channel.setRequestUrl(s);
//                return channel;
//            }
//        }).toList().toObservable();
//
//        //存入關聯表
//        saveDisposable = Observable.combineLatest(channelGroupObservable, channelsObservable, new BiFunction<Long, List<Channel>, Boolean>() {
//            @Override
//            public Boolean apply(Long channelGroupId, List<Channel> channelList) throws Exception {
//                ChannelManager.getInstance().insertInTx(channelList);
//                long begin = ChannelJoinGroupManager.getInstance().getMaxOrder();
//                List<ChannelJoinGroup> channelJoinGroupList = new ArrayList<>(channelList.size());
//                for (int i = 0; i < channelList.size(); i++) {
//                    ChannelJoinGroup join = new ChannelJoinGroup();
//                    join.setChannelGroupId(channelGroupId);
//                    join.setChannelId(channelList.get(i).getId());
//                    join.setOrder(begin + i);
//                    channelJoinGroupList.add(join);
//                }
//                ChannelJoinGroupManager.getInstance().insertInTx(channelJoinGroupList);
//                Thread.sleep(25000);
//                return true;
//            }
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean aBoolean) throws Exception {
//                        startMainActivity();
//                    }
//                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 保存數據
     */
    private Disposable saveDisposable;
    /**
     * 無數據對話框
     */
    private AlertDialog addFirstDialog;
    /**
     * 增加頻道對話框
     */
    private AddChannelDialogFragment fragment;

    /**
     * 錨點View
     */
    private View contentView;

    /**
     * 獲取是否有數據
     */
    private Disposable disposable;

    @Override
    public void onDone() {
        startMainActivity();
    }

    @Override
    public void onFailed(String message) {
        // TODO: 2018/5/15 此處後續修改為在dialog中進行提示
        if (contentView == null) {
            contentView = findViewById(android.R.id.content);
        }
        SnackbarUtils.showError(contentView, message);
    }
}