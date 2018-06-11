package com.morladim.morganrss.base.web;

import android.content.Context;
import android.content.Intent;

import com.morladim.morganrss.R;
import com.morladim.morganrss.base.ui.BaseToolbarActivity;

public class WebActivity extends BaseToolbarActivity {

//    //只显示文本
//    public static final boolean TEXT_MODE = false;
//    //标准浏览器模式
//    public static final boolean WEB_MODE = true;


    public static final String NO_IMAGE = "webMode";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";

    private boolean noImage;

    private String content, title;

    /**
     * @param noImage 无图模式
     */
    public static void startNewActivity(Context context, boolean noImage, String title, String content) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(NO_IMAGE, noImage);
        intent.putExtra(TITLE, title);
        intent.putExtra(CONTENT, content);
        context.startActivity(intent);
    }

    @Override
    protected void initView(int containerId) {
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
        title = getIntent().getStringExtra(TITLE);
        setToolbarTitle(title);
        getFragmentManager().beginTransaction().replace(R.id.container, WebFragment.newInstance(content, noImage)).commit();
    }

}
