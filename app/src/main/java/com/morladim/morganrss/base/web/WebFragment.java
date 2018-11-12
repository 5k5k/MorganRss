package com.morladim.morganrss.base.web;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.morladim.morganrss.R;

/**
 * <br>创建时间：2017/8/16.
 *
 * @author morladim
 */
public class WebFragment extends Fragment {

    public static final String CONTENT = "content";
    public static final String NO_IMAGE = "noImage";

    public static final String MIME_TYPE = "text/html; charset=utf-8";
    public static final String ENCODING = "utf-8";
    public static final int DEFAULT_TEXT_SIZE = 14;

    public static WebFragment newInstance(String content, boolean noImage) {
        Bundle args = new Bundle();
        args.putString(CONTENT, content);
        args.putBoolean(NO_IMAGE, noImage);
        WebFragment fragment = new WebFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web, container, false);
    }

    private boolean noImage;
    private WebView webView;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        webView = view.findViewById(R.id.webView);
        noImage = getArguments().getBoolean(NO_IMAGE);
        settingWebView(webView);

        String content = getArguments().getString(CONTENT);
        if (noImage && content != null) {
            //無圖模式去掉所有圖片
            content = content.replaceAll("<img(.*?)>", "");
        } else {
            //有圖模式最大寬度設置為屏幕寬度
            content = content.replace("<img", "<img style='max-width:100%;height:auto;'");
        }

        // TODO: 2018/6/11 在當前頁面加入可以顯示隱藏圖片的開關
        webView.loadData(content, MIME_TYPE, ENCODING);
//        webView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    WebView view1 = (WebView) view;
//                    WebView.HitTestResult v = view1.getHitTestResult();
//                    if (v.getType() == WebView.HitTestResult.IMAGE_TYPE) {
//                        System.out.println("Vv " + v.getType() + " " + v.getExtra());
//                    }
//                }
//
//                return false;
//            }
//        });
    }

    private void settingWebView(WebView webView) {

        WebSettings webSettings = webView.getSettings();
        webSettings.setSupportZoom(true); // 可以缩放
        webSettings.setDefaultFontSize(DEFAULT_TEXT_SIZE);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // <meta name="viewport" content="width=device-width, initial-scale=1">
//        webSettings.setUseWideViewPort(true);
//        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);//把html中的内容放大webview等宽的一列中

        webSettings.setJavaScriptEnabled(true);
        webSettings.setBlockNetworkImage(noImage);
//        webView.addJavascriptInterface(new JavaScriptInterface(), "HTMLOUT");

        webView.setWebViewClient(new SimpleWebViewClient(noImage));
//        webView.addJavascriptInterface(new JavascriptHandler(), "imageListener");
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.setTransitionGroup(true);
    }

//    private static class JavascriptHandler {
//
//        @JavascriptInterface
//        public void loadImage() {
//            Toast.makeText(RssApplication.getContext(), "123123", Toast.LENGTH_SHORT).show();
//        }
//    }

    private static class SimpleWebViewClient extends WebViewClient {

        private boolean noImage;

        public SimpleWebViewClient(boolean noImage) {
            this.noImage = noImage;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
//            if (!noImage) {
//            resetImage(view);
//            }
//            view.loadUrl("javascript:window.HTMLOUT.getContentWidth(document.getElementsByTagName('html')[0].scrollWidth);");
        }

        private void resetImage(WebView webView) {
            webView.loadUrl("javascript:(function(){" +
                    "var objs = document.getElementsByTagName('img'); " +
                    "for(var i=0;i<objs.length;i++)  " +
                    "{"
                    + "var img = objs[i];   " +
                    "    img.style.maxWidth = '100%'; img.style.height = 'auto';  " +
                    "}" +
                    "})()");
        }
    }

    class JavaScriptInterface {
        @JavascriptInterface
        public void getContentWidth(String value) {
            if (value != null) {
                int webviewContentWidth = Integer.parseInt(value);
//                Log.d("a", "Result from javascript: " + webviewContentWidth);
//                Toast.makeText(getActivity(),
//                        "ContentWidth of webpage is: " +
//                                webviewContentWidth +
//                                "px", Toast.LENGTH_SHORT).show();
//                if (webviewContentWidth > 360) {
//                    webView.getSettings().setUseWideViewPort(false);
//                    webView.setInitialScale(71);
            }
        }
    }
}
