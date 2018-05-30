package com.morladim.morganrss.base;

import android.app.Application;

import com.morladim.morganrss.base.util.ImageLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * <br>创建时间：2017/7/13.
 *
 * @author morladim
 */
public class RssApplication extends Application {

    public static final String PICTURE_PROCESS_NAME = "com.morladim.morganrss:picture";

    private static RssApplication context;

    public static Application getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        //圖片進程初始化工具類
        if (PICTURE_PROCESS_NAME.equals(getProcessName())) {
            initPictureProcess(this);
        }
    }

    /**
     * 初始化圖片進程工具及數據
     */
    private void initPictureProcess(Application application) {
        ImageLoader.init(application);
    }

    /**
     * 獲取進程名稱
     *
     * @return 進程名稱
     */
    public static String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String processName = bufferedReader.readLine().trim();
            bufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (PICTURE_PROCESS_NAME.equals(getProcessName())) {
            initPictureProcess(null);
        }
    }
}
