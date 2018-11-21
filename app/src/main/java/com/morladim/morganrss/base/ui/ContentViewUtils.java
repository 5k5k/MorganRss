package com.morladim.morganrss.base.ui;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author morladim
 * @date 2018/11/14
 */
public class ContentViewUtils {

    public static void handle(Activity activity) {
        if (activity.getClass().isAnnotationPresent(ContentView.class)) {
            ContentView contentView = activity.getClass().getAnnotation(ContentView.class);
            activity.setContentView(contentView.value());
        }
    }

    public static View handle(Fragment fragment, @NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        if (fragment.getClass().isAnnotationPresent(ContentView.class)) {
            ContentView contentView = fragment.getClass().getAnnotation(ContentView.class);
            return inflater.inflate(contentView.value(), container, false);
        }
        return null;
    }
}
