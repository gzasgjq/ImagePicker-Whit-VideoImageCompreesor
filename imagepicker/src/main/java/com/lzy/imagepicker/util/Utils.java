package com.lzy.imagepicker.util;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static android.os.Environment.DIRECTORY_DCIM;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class Utils {
    private static final String TAG = "ImagePick_Utils";

    public static String getCacheImagePath(Context context) {
        String path = "";
        if (Utils.existSDCard()) {
            path = Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM) + "/dinpay/cache";
        } else {
            path = context.getCacheDir().toString();
        }

        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                path = context.getCacheDir().toString();
            }
        }
        try {
            if (file.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.contains(".nomedia");
                }
            }).length <= 0) {
                new File(path + "/.nomedia").createNewFile();
            }
        } catch (Exception e) {
            Log.e(TAG, "getCacheImagePath: ", e);
        }
        return path;
    }

    public static boolean copyFile(File srcPath, File dstPath) {
        if (!srcPath.exists()) {
            return false;
        }
        if (!dstPath.exists()) {
            try {
                if (!dstPath.createNewFile())
                    return false;
            }catch (IOException e){
                Log.e(TAG, "copyFile: ", e);
                return false;
            }
        }
        if (srcPath.equals(dstPath)) {
            return false;
        }
        FileChannel fcin = null;
        FileChannel fcout = null;
        try {
            fcin = new FileInputStream(srcPath).getChannel();
            fcout = new FileOutputStream(dstPath).getChannel();
            ByteBuffer tmpBuffer = ByteBuffer.allocateDirect(4096);
            while (fcin.read(tmpBuffer) != -1) {
                tmpBuffer.flip();
                fcout.write(tmpBuffer);
                tmpBuffer.clear();
            }
            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fcin != null) {
                    fcin.close();
                }
                if (fcout != null) {
                    fcout.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 获得状态栏的高度
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 根据屏幕宽度与密度计算GridView显示的列数， 最少为三列，并获取Item宽度
     */
    public static int getImageItemWidth(Activity activity) {
        int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        int densityDpi = activity.getResources().getDisplayMetrics().densityDpi;
        int cols = screenWidth / densityDpi;
        cols = cols < 3 ? 3 : cols;
        int columnSpace = (int) (2 * activity.getResources().getDisplayMetrics().density);
        return (screenWidth - columnSpace * (cols - 1)) / cols;
    }

    /**
     * 判断SDCard是否可用
     */
    public static boolean existSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取手机大小（分辨率）
     */
    public static DisplayMetrics getScreenPix(Activity activity) {
        DisplayMetrics displaysMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);
        return displaysMetrics;
    }

    public static String getFileProviderName(Context context) {
        return context.getPackageName() + ".provider";
    }
}
