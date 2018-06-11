package com.morladim.morganrss.base.image;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.transition.Transition;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.morladim.morganrss.R;
import com.morladim.morganrss.base.RssApplication;
import com.morladim.morganrss.base.util.ImageLoader;
import com.squareup.picasso.Callback;

/**
 * 展示单张图片Activity，读取ImageHolder保存图片为默认图片。
 * <br>创建时间：2017/8/10.
 *
 * @author morladim
 */
public class SingleTouchImageViewActivity extends Activity {

    /**
     * 传递图片地址用名称
     */
    public static final String IMAGE_URL = "imageUrl";

    /**
     * 共享元素名称
     */
    public static final String SHARE_IMAGE_VIEW = "sharedImageView";

    private TouchImageView imageView;

    /**
     * 带元素共享的跳转
     *
     * @param imageUrl 加载图片地址
     * @param view     元素共享ImageView
     */
    public static synchronized void startActivityBySceneTrans(String imageUrl, ImageView view) {
        Context context = view.getContext();
        Intent postIntent = new Intent(context, SingleTouchImageViewActivity.class);
        postIntent.putExtra(SingleTouchImageViewActivity.IMAGE_URL, imageUrl);
        ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(((Activity) context), view, SingleTouchImageViewActivity.SHARE_IMAGE_VIEW);
        context.startActivity(postIntent, transitionActivityOptions.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        initImageView();
        loadImage();
    }

    /**
     * 初始化窗口属性
     */
    private void initWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_single_touch_image_view);
        getWindow().setStatusBarColor(ContextCompat.getColor(RssApplication.getContext(), R.color.color_primary_dark));
    }

    /**
     * 初始化图片视图
     */
    private void initImageView() {
        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.resetZoom();
                onBackPressed();
            }
        });
    }

    private void loadImage() {
        postponeEnterTransition();
        final String imageUrl = getIntent().getStringExtra(IMAGE_URL);
        ImageLoader.getInstance().load(imageUrl).noFade().into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                startPostponedEnterTransition();
            }

            @Override
            public void onError() {
                startPostponedEnterTransition();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        loadImage();
    }

    /**
     * 设置元素共享动画
     */
    @SuppressWarnings("unused")
    private void initAnimation() {
        getWindow().getEnterTransition().setDuration(500);
        getWindow().getSharedElementEnterTransition().addListener(
                new Transition.TransitionListener() {
                    @Override
                    public void onTransitionStart(Transition transition) {
                    }

                    @Override
                    public void onTransitionEnd(Transition transition) {
                        getWindow().getSharedElementEnterTransition().removeListener(this);
                        Bitmap bitmap = ImageHolder.getInstance().load();
                        final String imageUrl = getIntent().getStringExtra(IMAGE_URL);
                        if (bitmap == null) {
                            ImageLoader.getInstance().load(imageUrl).into(imageView);
                        } else {
                            ImageLoader.getInstance().load(imageUrl).placeholder(new BitmapDrawable(imageView.getResources(), bitmap)).into(imageView);
                        }
                    }

                    @Override
                    public void onTransitionCancel(Transition transition) {

                    }

                    @Override
                    public void onTransitionPause(Transition transition) {

                    }

                    @Override
                    public void onTransitionResume(Transition transition) {

                    }
                });
    }

}
