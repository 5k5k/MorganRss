package com.morladim.morganrss.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.morladim.morganrss.R;
import com.morladim.morganrss.base.database.ChannelGroupManager;
import com.morladim.morganrss.base.database.ChannelJoinGroupManager;
import com.morladim.morganrss.base.database.ChannelManager;
import com.morladim.morganrss.base.database.entity.Channel;
import com.morladim.morganrss.base.database.entity.ChannelGroup;
import com.morladim.morganrss.base.database.entity.ChannelJoinGroup;
import com.morladim.morganrss.base.image.ImageService;
import com.morladim.morganrss.base.util.SnackbarUtils;
import com.morladim.morganrss.base.util.StringUtils;
import com.morladim.morganrss.base.web.WebService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
        disposable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) {
                //啟動其他進程
                startService(new Intent(SplashActivity.this, ImageService.class));
                startService(new Intent(SplashActivity.this, WebService.class));
                e.onNext(ChannelManager.getInstance().getAll().size());
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                        //沒有任何頻道設定時，提示用戶是否載入默認頻道
                        if (integer == 0) {
                            showLoadDataDialog();
                        } else {
//                            saveDefaultData();
                            startMainActivity();
                        }
                    }
                });
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
            public void subscribe(ObservableEmitter<Boolean> e) {
                ChannelGroup channelGroup = new ChannelGroup();
                channelGroup.setName(getString(R.string.default_group));
                channelGroup.setSelected(true);
                channelGroup.setDescription(getString(R.string.default_group_description));
                channelGroup.setOrder(ChannelGroupManager.getInstance().getMaxOrder());
                ChannelGroupManager.getInstance().insert(channelGroup);

                Long channelGroupId = channelGroup.getId();

                String[] urls = StringUtils.getInstance().getStringArrayById(R.array.default_urls);
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
                    public void accept(Boolean aBoolean) {
                        startMainActivity();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
        if (saveDisposable != null) {
            saveDisposable.dispose();
        }
    }

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
}
