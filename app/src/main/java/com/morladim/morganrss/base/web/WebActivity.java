package com.morladim.morganrss.base.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

import com.morladim.morganrss.R;

public class WebActivity extends Activity {

//    //只显示文本
//    public static final boolean TEXT_MODE = false;
//    //标准浏览器模式
//    public static final boolean WEB_MODE = true;


    public static final String NO_IMAGE = "webMode";

    public static final String CONTENT = "content";

    private boolean noImage;

    private String content;

    /**
     * @param noImage 无图模式
     */
    public static void startNewActivity(Context context, boolean noImage, String content) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(NO_IMAGE, noImage);
        intent.putExtra(CONTENT, content);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        getIntentData();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        getIntentData();
    }

    private void getIntentData() {
        noImage = getIntent().getBooleanExtra(NO_IMAGE, false);
        content = getIntent().getStringExtra(CONTENT);
        if (noImage) {
            getFragmentManager().beginTransaction().replace(R.id.content_web, WebFragment.newInstance(content)).commit();
// TODO: 2018/5/7 要做無圖處理
        } else {
            getFragmentManager().beginTransaction().replace(R.id.content_web, WebFragment.newInstance(content)).commit();

        }
    }
}
