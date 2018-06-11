package com.morladim.morganrss.base.util;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

/**
 * 可以打印所在类及行，tag改为固定
 * <br>创建时间：2017/10/26.
 *
 * @author morladim
 */
@SuppressWarnings("unused")
public class MorladimDebugTree extends Timber.Tree {
    private static final int MAX_LOG_LENGTH = 4000;
    private static final int MAX_TAG_LENGTH = 23;
    private static final int CALL_STACK_INDEX = 5;
    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");

    private static final String LINE = " (%s.java:%s)";
    private String userTag;

    public MorladimDebugTree() {
        userTag = "Morladim";
    }

    /**
     * @param tag 默认tag
     */
    public MorladimDebugTree(String tag) {
        userTag = tag;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace.length <= CALL_STACK_INDEX) {
            throw new IllegalStateException(
                    "Synthetic stacktrace didn't have enough elements: are you using proguard?");
        }
        StackTraceElement element = stackTrace[CALL_STACK_INDEX];
        String className = createStackElementTag(element);
        if (tag == null) {
            tag = userTag;
        }
        message = message + String.format(LINE, className, element.getLineNumber());
        if (message.length() < MAX_LOG_LENGTH) {
            if (priority == Log.ASSERT) {
                Log.wtf(tag, message);
            } else {
                Log.println(priority, tag, message);
            }
            return;
        }

        // Split by line, then ensure each line can fit into Log's maximum length.
        for (int i = 0, length = message.length(); i < length; i++) {
            int newline = message.indexOf('\n', i);
            newline = newline != -1 ? newline : length;
            do {
                int end = Math.min(newline, i + MAX_LOG_LENGTH);
                String part = message.substring(i, end);
                if (priority == Log.ASSERT) {
                    Log.wtf(tag, part);
                } else {
                    Log.println(priority, tag, part);
                }
                i = end;
            } while (i < newline);
        }
    }

    private String createStackElementTag(StackTraceElement element) {
        String tag = element.getClassName();
        Matcher m = ANONYMOUS_CLASS.matcher(tag);
        if (m.find()) {
            tag = m.replaceAll("");
        }
        tag = tag.substring(tag.lastIndexOf('.') + 1);
        return tag.length() > MAX_TAG_LENGTH ? tag.substring(0, MAX_TAG_LENGTH) : tag;
    }

}
