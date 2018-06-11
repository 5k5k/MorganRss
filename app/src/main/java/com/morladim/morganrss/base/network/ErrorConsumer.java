package com.morladim.morganrss.base.network;

import android.view.View;

import com.morladim.morganrss.R;
import com.morladim.morganrss.base.RssApplication;
import com.morladim.morganrss.base.util.SnackbarUtils;
import com.morladim.morganrss.base.util.StringUtils;

import java.lang.ref.SoftReference;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

/**
 * <br>创建时间：2017/7/20.
 *
 * @author morladim
 */
@SuppressWarnings("unused")
public class ErrorConsumer implements Consumer<Throwable> {

    private SoftReference<View> reference;

    public static final int NET_WORK_ERROR = 0;

    private Integer code;

    private String error;

    public ErrorConsumer(View snackView) {
        reference = new SoftReference<>(snackView);
        setError();
    }

    public ErrorConsumer(View snackView, int code) {
        this(snackView);
        this.code = code;
    }

    @Override
    public void accept(@NonNull Throwable throwable) {
        Timber.e(throwable);
        if (reference != null && reference.get() != null) {
            SnackbarUtils.showError(reference.get(), error == null ? StringUtils.getInstance().getStringById(R.string.can_not_get_net_data) : error);
        }
    }

    private void setError() {
        String[] errorArray = RssApplication.getContext().getResources().getStringArray(R.array.error_consumer_type);
        if (code != null && code < errorArray.length) {
            error = errorArray[code];
        }
    }
}
