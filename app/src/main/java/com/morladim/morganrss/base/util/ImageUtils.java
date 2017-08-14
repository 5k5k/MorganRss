package com.morladim.morganrss.base.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * 图片工具类
 * <br>创建时间：2017/8/11.
 *
 * @author morladim
 */
@SuppressWarnings("WeakerAccess")
public class ImageUtils {

    // TODO: 2017/8/11 流畅模式 精细模式
    private static final int MAX_SIZE = 200000;

    private ImageUtils() {

    }

    public static Bitmap getBitmapBytes(byte[] buff) {
        if (buff == null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(buff, 0, buff.length);
    }

    // TODO: 2017/8/11 整体无图模式，另外根据网络状态进行ImageLoad加载
    // TODO: 2017/8/11 用低配手机测试
    public synchronized static byte[] bitmap2Bytes(Bitmap bm) {
        Log.d("mor", bm.getWidth() + "");
        Log.d("mor", bm.getHeight() + "");
        Log.d("mor", bm.getAllocationByteCount() + "");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.WEBP, 100, stream);
        byte[] array = stream.toByteArray();
        Log.d("mor", "array " + array.length);
        return array;
    }

    public synchronized static byte[] imageViewToBytes(ImageView view) {
        return bitmap2Bytes(((BitmapDrawable) view.getDrawable()).getBitmap());
//        return resizeBitmap(((BitmapDrawable) view.getDrawable()).getBitmap());
    }

    private static byte[] resizeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, stream);

        Log.d("mor", bitmap.getWidth() + "");
        Log.d("mor", bitmap.getHeight() + "");
        Log.d("mor", bitmap.getAllocationByteCount() + "");

        int options = 80;
        while (stream.toByteArray().length > MAX_SIZE) {
            System.out.println("in");
            stream.reset();
            bitmap.compress(Bitmap.CompressFormat.WEBP, options, stream);
            options -= 20;
        }
        byte[] array = stream.toByteArray();
        Log.d("mor", array.length + "");

        return array;
    }
}
