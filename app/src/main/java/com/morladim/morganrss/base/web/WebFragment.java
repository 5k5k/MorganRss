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
import android.widget.Toast;

import com.morladim.morganrss.R;
import com.morladim.morganrss.base.RssApplication;

/**
 * <br>创建时间：2017/8/16.
 *
 * @author morladim
 */
public class WebFragment extends Fragment {

    public static final String CONTENT = "content";

    public static final String MIME_TYPE = "text/html; charset=utf-8";
    public static final String ENCODING = "utf-8";
    public static final int DEFAULT_TEXT_SIZE = 14;

    public static WebFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString(CONTENT, content);
        WebFragment fragment = new WebFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        WebView webView = view.findViewById(R.id.webView);
        settingWebView(webView);
        String content = getArguments().getString(CONTENT);
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
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//把html中的内容放大webview等宽的一列中
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new SimpleWebViewClient());
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

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            System.out.println("load url "+url);
            resetImage(view);
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
}
