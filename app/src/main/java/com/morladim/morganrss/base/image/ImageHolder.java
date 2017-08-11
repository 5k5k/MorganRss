package com.morladim.morganrss.base.image;

import android.graphics.Bitmap;

import java.lang.ref.WeakReference;

/**
 * <br>创建时间：2017/8/10.
 *
 * @author morladim
 */
class ImageHolder {

    private volatile WeakReference<Bitmap> bitmap;

    private ImageHolder() {

    }

    private static volatile ImageHolder imageHolder;

    public static ImageHolder getInstance() {
        if (imageHolder == null) {
            synchronized (ImageHolder.class) {
                if (imageHolder == null) {
                    imageHolder = new ImageHolder();
                }
            }
        }
        return imageHolder;
    }

    synchronized void save(Bitmap bitmap) {
//        if (this.bitmap != null && this.bitmap.get() != null) {
//            this.bitmap.get().recycle();
//        }
        this.bitmap = new WeakReference<>(bitmap);
    }

    synchronized Bitmap load() {
        if (bitmap == null) {
            return null;
        }
        return bitmap.get();
    }

}
