package com.morladim.morganrss.base.util;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.morladim.morganrss.R;

import static android.support.design.widget.Snackbar.SnackbarLayout;

/**
 * 有颜色的snackbar持有者
 * <br>创建时间：2017/7/19.
 *
 * @author morladim
 */
@SuppressWarnings("unused")
public enum SnackbarHolder {

    INFO(R.color.snackbarBackgroundInfo, R.color.snackbarTextInfo, R.drawable.ic_error_outline_black_24dp, null),
    WARNING(R.color.snackbarBackgroundWarn, R.color.snackbarTextWarn, R.drawable.ic_priority_high_black_24dp, null),
    ERROR(R.color.snackbarBackgroundError, R.color.snackbarTextError, R.drawable.ic_highlight_off_black_24dp, "遇到错误"),
    SUCCESS(R.color.snackbarBackgroundSuccess, R.color.snackbarTextSuccess, R.drawable.ic_done_black_24dp, "操作成功");

    private int bgColorId, textColorId, iconId;
    private String text;

    SnackbarHolder(@ColorRes int bgColorId, @ColorRes int textColorId, @DrawableRes int iconId, String text) {
        this.bgColorId = bgColorId;
        this.textColorId = textColorId;
        this.iconId = iconId;
        this.text = text;
    }

    public Snackbar getNew(View view, String text) {
        if (text == null) {
            text = this.text;
        }
        if (findSuitableParent(view) == null) {
            return null;
        }
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
        SnackbarLayout snackbarLayout = (SnackbarLayout) snackbar.getView();
        snackbarLayout.setBackgroundColor(bgColorId);
        TextView textView = snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(textColorId);
        Drawable drawable = view.getResources().getDrawable(iconId, view.getContext().getTheme());
        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        textView.setCompoundDrawablePadding(40);
        return snackbar;
    }

    //从Snackbar中复制
    private static ViewGroup findSuitableParent(View view) {
        ViewGroup fallback = null;
        do {
            if (view instanceof CoordinatorLayout) {
                // We've found a CoordinatorLayout, use it
                return (ViewGroup) view;
            } else if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    // If we've hit the decor content view, then we didn't find a CoL in the
                    // hierarchy, so use it.
                    return (ViewGroup) view;
                } else {
                    // It's not the content view but we'll use it as our fallback
                    fallback = (ViewGroup) view;
                }
            }

            if (view != null) {
                // Else, we will loop and crawl up the view hierarchy and try to find a parent
                final ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);

        // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
        return fallback;
    }

    public Snackbar getNew(View view) {
        return getNew(view, text);
    }

    public static void show(Snackbar snackbar) {
        if (snackbar != null) {
            snackbar.show();
        }
    }
}
