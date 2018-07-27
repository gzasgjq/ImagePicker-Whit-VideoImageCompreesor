package com.gjq.demo.deltaupdatelib;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.gjq.demo.deltaupdatelib.bsdiff.BSPatch;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.BuildConfig;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/05/26 11:44
 * 类描述：$INTRO$
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class DeltaUpdatesUtils {
    private static final String TAG = "DeltaUpdatesUtils";

    public static File patchFile(Context context, File patch) {
        return patchFile(context, patch, null);
    }

    public static void patchFileAsyn(Context context, File patch, PatchApkListener listener) {
        patchFileAsyn(context, patch, null, listener);
    }

    public static Observable<File> patchFileRx(Context context, File patch) {
        return patchFileRx(context, patch, null);
    }

    /**
     * 合并生成Apk包，同步方法
     *
     * @param context context
     * @param patch   差分包
     * @param md5   Apk的MD5
     * @return 生成的Apk文件
     */
    public static File patchFile(Context context, File patch, String md5) {
        if (!patch.canRead()) {
            Toast.makeText(context, "差分包不可访问", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (Long.parseLong(patch.getName()) <= BuildConfig.VERSION_CODE) {
            Toast.makeText(context, "差分包版本不是最新", Toast.LENGTH_SHORT).show();
            return null;
        }
        ApplicationInfo applicationInfo = context.getApplicationContext().getApplicationInfo();
        String apkPath = applicationInfo.sourceDir;
        Log.d(TAG, "Current APK file is " + apkPath);
        File currentApk = new File(apkPath);
        if (currentApk.exists() && currentApk.canRead()) {
            Log.i(TAG, "loading file");
        } else {
            Log.e(TAG, "file is not readable");
            return null;
        }
        File patchedApk = new File(context.getCacheDir().getAbsolutePath() + System.currentTimeMillis() + ".apk");
        if (!patchedApk.getParentFile().exists())
            patchedApk.getParentFile().mkdirs();
        try {
            if (!patchedApk.exists()) {
                patchedApk.createNewFile();
            }
            BSPatch.patchFast(currentApk, patchedApk, patch, 0);
            if (md5 != null && !md5.equals(MD5Utils.encode(patchedApk))){
                Toast.makeText(context, "文件校验失败", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (IOException exce) {
            Toast.makeText(context, "合成失败", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "patchFile: ", exce);
            return null;
        }
        Log.d(TAG, MD5Utils.encode(patchedApk));
        return patchedApk;
    }

    /**
     * 合成Apk包，异步方法
     *  @param context  context
     * @param patch    差分包
     * @param md5   Apk的MD5
     * @param listener 回调
     */
    public static void patchFileAsyn(Context context, File patch, String md5, PatchApkListener listener) {
        Handler handler = new Handler();
        new Thread(() -> {
            if (!patch.canRead()) {
                handler.post(() -> listener.onPatchFail("差分包不可访问"));
                return;
            }
            if (Long.parseLong(patch.getName()) <= BuildConfig.VERSION_CODE) {
                handler.post(() -> listener.onPatchFail("差分包版本不是最新"));
                return;
            }
            ApplicationInfo applicationInfo = context.getApplicationContext().getApplicationInfo();
            String apkPath = applicationInfo.sourceDir;
            Log.d(TAG, "Current APK file is " + apkPath);
            File currentApk = new File(apkPath);
            if (currentApk.exists() && currentApk.canRead()) {
                Log.i(TAG, "loading file");
            } else {
                handler.post(() -> listener.onPatchFail("file is not readable"));
                Log.e(TAG, "file is not readable");
                return;
            }
            File patchedApk = new File(context.getCacheDir().getAbsolutePath() + System.currentTimeMillis() + ".apk");
            if (!patchedApk.getParentFile().exists())
                patchedApk.getParentFile().mkdirs();
            try {
                if (!patchedApk.exists()) {
                    patchedApk.createNewFile();
                }
                BSPatch.patchFast(currentApk, patchedApk, patch, 0);
                if (md5 != null && !md5.equals(MD5Utils.encode(patchedApk))){
                    handler.post(() -> listener.onPatchFail("文件校验失败"));
                    return;
                }
            } catch (IOException exce) {
                Log.e(TAG, "patchFile: ", exce);
                handler.post(() -> listener.onPatchFail("合成失败"));
                return;
            }
            Log.d(TAG, MD5Utils.encode(patchedApk));
            handler.post(() -> listener.onPatchSuccess(patchedApk));
        }).start();
    }

    /**
     * 合并生成Apk包，Rx
     *
     * @param context context
     * @param file    差分包
     * @param md5   Apk的MD5
     * @return Observable return Apk File
     */
    public static Observable<File> patchFileRx(Context context, File file, String md5) {
        ApplicationInfo applicationInfo = context.getApplicationContext().getApplicationInfo();
        String absolutePath = context.getCacheDir().getAbsolutePath();
        return new Observable<File>() {
            @Override
            protected void subscribeActual(Observer<? super File> e) {
                if (!file.canRead()) {
                    e.onError(new IOException("差分包不可访问"));
                    return;
                }
                if (Long.parseLong(file.getName()) <= BuildConfig.VERSION_CODE) {
                    e.onError(new IllegalArgumentException("差分包版本不是最新"));
                    return;
                }
                String apkPath = applicationInfo.sourceDir;
                Log.d(TAG, "Current APK file is " + apkPath);
                File currentApk = new File(apkPath);
                if (currentApk.exists() && currentApk.canRead()) {
                    Log.i(TAG, "loading file");
                } else {
                    Log.e(TAG, "file is not readable");
                    return;
                }
                File patchedApk = new File(absolutePath + System.currentTimeMillis() + ".apk");
                if (!patchedApk.getParentFile().exists())
                    patchedApk.getParentFile().mkdirs();
                try {
                    if (!patchedApk.exists()) {
                        patchedApk.createNewFile();
                    }
                    BSPatch.patchFast(currentApk, patchedApk, file, 0);
                    if (md5 != null && !md5.equals(MD5Utils.encode(patchedApk))){
                        e.onError(new SecurityException("文件校验失败"));
                        return;
                    }
                } catch (IOException exce) {
                    e.onError(exce);
                    return;
                }
                Log.d(TAG, MD5Utils.encode(patchedApk));
                e.onNext(patchedApk);
            }
        };
    }

    interface PatchApkListener {
        void onPatchSuccess(File apk);

        void onPatchFail(String e);
    }
}
