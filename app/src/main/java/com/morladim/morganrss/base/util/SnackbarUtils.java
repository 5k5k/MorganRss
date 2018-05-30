package com.morladim.morganrss.base.util;

import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.morladim.morganrss.R;

/**
 * Snackbar工具，適用於通常情況。
 *
 * @author morladim
 * @date 2018/5/10
 */
public class SnackbarUtils {

    public static void showError(View view, String text) {
        if (errorStyle == null) {
            errorStyle = new Style(R.color.snackbar_error_bg, R.color.snackbar_error_text, R.drawable.ic_highlight_off_black_24dp, "遇到错误");
        }
        Snackbar snackbar = getSnackbarByStyle(errorStyle, view, text);
        if (snackbar != null) {
            snackbar.show();
        }
    }

    public static void showInfo(View view, String text) {
        if (infoStyle == null) {
            infoStyle = new Style(R.color.snackbar_info_bg, R.color.snackbar_info_text, R.drawable.ic_error_outline_black_24dp, null);
        }
        Snackbar snackbar = getSnackbarByStyle(infoStyle, view, text);
        if (snackbar != null) {
            snackbar.show();
        }
    }

    public static void showWarn(View view, String text) {
        if (warnStyle == null) {
            warnStyle = new Style(R.color.snackbar_warn_bg, R.color.snackbar_warn_text, R.drawable.ic_priority_high_black_24dp, null);
        }
        Snackbar snackbar = getSnackbarByStyle(warnStyle, view, text);
        if (snackbar != null) {
            snackbar.show();
        }
    }

    public static void showSuccess(View view, String text) {
        if (successStyle == null) {
            successStyle = new Style(R.color.snackbar_success_bg, R.color.snackbar_success_text, R.drawable.ic_done_black_24dp, "操作成功");
        }
        Snackbar snackbar = getSnackbarByStyle(successStyle, view, text);
        if (snackbar != null) {
            snackbar.show();
        }
    }

    public static Snackbar getSnackbarByStyle(Style style, View view, String text) {
        if (text == null) {
            text = style.getDefaultText();
        }
        if (findSuitableParent(view) == null) {
            return null;
        }
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setBackgroundColor(style.getBackgroundColor());
        TextView textView = snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(style.getTextColor());
        Drawable drawable = view.getResources().getDrawable(style.getDrawableResourceId(), view.getContext().getTheme());
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

    private static Style errorStyle, infoStyle, warnStyle, successStyle;

    static class Style {

        Style(int backgroundColor, int textColor, int drawableResourceId, String defaultText) {
            this.backgroundColor = backgroundColor;
            this.textColor = textColor;
            this.drawableResourceId = drawableResourceId;
            this.defaultText = defaultText;
        }

        private int backgroundColor;
        private int textColor;
        private int drawableResourceId;
        private String defaultText;

        public int getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public int getTextColor() {
            return textColor;
        }

        public void setTextColor(int textColor) {
            this.textColor = textColor;
        }

        public int getDrawableResourceId() {
            return drawableResourceId;
        }

        public void setDrawableResourceId(int drawableResourceId) {
            this.drawableResourceId = drawableResourceId;
        }

        public String getDefaultText() {
            return defaultText;
        }

        public void setDefaultText(String defaultText) {
            this.defaultText = defaultText;
        }
    }
}