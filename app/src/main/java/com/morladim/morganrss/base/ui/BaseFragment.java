package com.morladim.morganrss.base.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * <br>创建时间：2017/10/20.
 *
 * @author morladim
 */
@SuppressWarnings("unused")
public abstract class BaseFragment extends Fragment {


    public final String TAG = this.getClass().getSimpleName();

    private Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResource(), container, false);
        onCreateView(view);
        return view;
    }

    public Activity getAttachActivity() {
        return activity;
    }

    protected void trace(String msg) {
        Log.d(TAG, msg);
    }

    /**
     * 设定布局资源
     *
     * @return 资源id
     */
    protected abstract int getLayoutResource();

    protected void onCreateView(View view) {

    }
}
