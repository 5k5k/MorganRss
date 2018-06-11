package com.morladim.morganrss.base.ui;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.morladim.morganrss.R;

/**
 * @author morladim
 * @date 2018/6/11
 */
public abstract class BaseToolbarActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abstract_toolbar);
        initToolbar();
        initView(R.id.container);
    }

    /**
     * 设置标题栏
     */
    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getToolbarTitle());
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }
        final Drawable up = toolbar.getNavigationIcon();
        if (up != null) {
            up.setColorFilter(ContextCompat.getColor(this, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(up);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationClick(view);
            }
        });
    }

    protected String getToolbarTitle() {
        return "";
    }

    protected void initView(@IdRes int containerId) {

    }

    protected void navigationClick(View view) {
        finish();
    }

    protected Toolbar getToolbar() {
        return toolbar == null ? (Toolbar) findViewById(R.id.toolbar) : toolbar;
    }

    /**
     * toolbar
     */
    private Toolbar toolbar;

}
