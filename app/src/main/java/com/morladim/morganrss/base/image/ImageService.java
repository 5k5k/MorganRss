package com.morladim.morganrss.base.image;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.morladim.morganrss.IImageManager;

import static com.morladim.morganrss.base.util.ImageUtils.getBitmapBytes;

/**
 * picture线程接受图片service，保存到单例供SingleTouchImageViewActivity使用。
 */
public class ImageService extends Service {

    public ImageService() {
    }

    private Binder binder = new IImageManager.Stub() {

        public void setBitmap(byte[] b) {
            ImageHolder.getInstance().save(getBitmapBytes(b));
        }

    };

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

}
