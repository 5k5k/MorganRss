package com.morladim.morganrss.main;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;

import com.morladim.morganrss.R;
import com.morladim.morganrss.base.database.ChannelGroupManager;
import com.morladim.morganrss.base.database.ChannelManager;
import com.morladim.morganrss.base.database.entity.Channel;
import com.morladim.morganrss.base.database.entity.ChannelGroup;
import com.morladim.morganrss.base.util.StringUtils;

/**
 * 增加頻道
 *
 * @author morladim
 * @date 2018/5/11
 */
public class AddChannelDialogFragment extends DialogFragment {

    /**
     * @param isFirst 數據庫中是否有其他頻道，如果是第一次添加那麼頻道分組變為增加分組
     * @return 實例
     */
    public static AddChannelDialogFragment newInstance(boolean isFirst) {
        Bundle args = new Bundle();
        args.putBoolean(IS_FIRST, isFirst);
        AddChannelDialogFragment fragment = new AddChannelDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.add_channel)
                .setCancelable(false)
                .setPositiveButton(R.string.add, null)
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }
                );

        View view = View.inflate(getActivity(), R.layout.fragment_dialog_add_channel, null);

        urlET = view.findViewById(R.id.url);
        if (getArguments().getBoolean(IS_FIRST)) {
            ViewStub viewStub = view.findViewById(R.id.first_group);
            viewStub.inflate();
            addGroupET = view.findViewById(R.id.add_group);

        } else {
            ViewStub viewStub = view.findViewById(R.id.group);
            viewStub.inflate();
            // TODO: 2018/5/11 正常添加的情況
        }
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null && saveChannel()) {
                            callback.onDone();
                        }
                    }
                });
            }
        });
        return dialog;
    }

    private boolean saveChannel() {
        if (!checkValues()) {
            return false;
        }
        if (getArguments().getBoolean(IS_FIRST)) {
            //增加頻道
            Channel channel = new Channel(null);
            channel.setRequestUrl(urlET.getText().toString());
            ChannelManager.getInstance().insert(channel);

            //增加分組
            ChannelGroup channelGroup = new ChannelGroup();
            channelGroup.setName(addGroupET.getText().toString());
            channelGroup.setSelected(true);
            ChannelGroupManager.getInstance().insert(channelGroup);
        } else {

        }

        return true;

    }


    /**
     * 校驗用戶輸入是否合法
     *
     * @return 是否合法值
     */
    private boolean checkValues() {
        if (getArguments().getBoolean(IS_FIRST)) {
            if (urlET == null || !StringUtils.isUrl(urlET.getText().toString())) {
                if (callback != null) {
                    callback.onFailed(getString(R.string.add_channel_incorrect_url));
                }
                return false;
            }
            if (addGroupET == null || TextUtils.isEmpty(addGroupET.getText().toString().trim())) {
                if (callback != null) {
                    callback.onFailed(getString(R.string.add_channel_group_name_null));
                }
                return false;
            }
            return true;
        } else {
            // TODO: 2018/5/11 正常添加情況
        }
        return false;
    }

    private EditText urlET;
    private EditText addGroupET;

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    interface Callback {

        /**
         * 增加頻道操作完成回調
         */
        void onDone();

        /**
         * 增加操作失敗
         *
         * @param message 原因
         */
        void onFailed(String message);
    }

    public static final String IS_FIRST = "isFirst";
}
