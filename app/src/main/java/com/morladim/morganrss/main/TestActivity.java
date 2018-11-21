package com.morladim.morganrss.main;

import android.content.Context;
import android.content.Intent;

import com.morladim.morganrss.R;
import com.morladim.morganrss.base.ui.BaseActivity;
import com.morladim.morganrss.base.ui.ContentView;

/**
 * @author morladim
 * @date 2018/11/12
 */
@ContentView(R.layout.test)
public class TestActivity extends BaseActivity {
    public static void start(Context context) {
        Intent starter = new Intent(context, TestActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
