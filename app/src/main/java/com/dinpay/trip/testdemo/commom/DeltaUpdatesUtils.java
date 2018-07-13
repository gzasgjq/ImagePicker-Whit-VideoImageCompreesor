package com.dinpay.trip.testdemo.commom;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Environment;
import android.text.TextUtils;

import com.dinpay.dingding.dinpaysdkkd.util.MD5Utils;
import com.dinpay.trip.BuildConfig;
import com.dinpay.trip.act.KuDouApplication;
import com.kudou.androidutils.utils.LogCat;
import com.tencent.tinker.bsdiff.BSPatch;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
    private static final String patchsDir = Environment.getExternalStorageDirectory() + "/dinpay/apk/patchs/";
    private static final String patchedApkDir = Environment.getExternalStorageDirectory() + "/dinpay/apk/patched/";
    private static final String TAG = "DeltaUpdatesUtils";

    public static void checkUpdate(Context context) {
        Single.create((SingleOnSubscribe<File>) e -> {
            File patchsDirFile = new File(patchsDir);
            if (!patchsDirFile.exists()) {
                patchsDirFile.mkdirs();
            }
            File[] patchs = patchsDirFile.listFiles();
            if (patchs.length <= 0) {
                LogCat.e(TAG, "No Patch Find");
                return;
            }
            Arrays.sort(patchs, (o1, o2) -> {
                if (TextUtils.isDigitsOnly(o1.getName()) && TextUtils.isDigitsOnly(o2.getName())) {
                    long o1time = Long.parseLong(o1.getName());
                    long o2time = Long.parseLong(o2.getName());
                    if (o1time > o2time)
                        return 1;
                    else if (o1time == o2time)
                        return 0;
                    else
                        return -1;
                }
                return -1;
            });
            File patch = patchs[0];
            LogCat.i(TAG, "Choose patch is " + patch.getAbsolutePath());
            if (Long.parseLong(patch.getName()) <= BuildConfig.VERSION_CODE){

            }

            ApplicationInfo applicationInfo = KuDouApplication.getGlobalContext().getApplicationInfo();
            String apkPath = applicationInfo.sourceDir;
            LogCat.d(TAG, "Current APK file is " + apkPath);
            File currentApk = new File(apkPath);
            if (currentApk.exists() && currentApk.canRead()) {
                LogCat.i(TAG, "loading file");
            } else {
                LogCat.e(TAG, "file is not readable");
                return;
            }
            File patchedApk = new File(patchedApkDir + System.currentTimeMillis() + ".apk");
            if (!patchedApk.getParentFile().exists())
                patchedApk.getParentFile().mkdirs();
            if (!patchedApk.exists()) {
                patchedApk.createNewFile();
            }
            BSPatch.patchFast(currentApk, patchedApk, patch, 0);
            e.onSuccess(patchedApk);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<File>() {
                    private Disposable d;

                    @Override
                    public void onSubscribe(Disposable d) {
                        this.d = d;
                    }

                    @Override
                    public void onSuccess(File r) {
                        ApkUpdateUtils.installApp(context, r);
                        d.dispose();
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogCat.e(TAG, "Patch Fail: " + e);
                        e.printStackTrace();
                        d.dispose();
                    }
                });
    }

    public static Observable<File> patchFile(File file){
        return new Observable<File>() {
            @Override
            protected void subscribeActual(Observer<? super File> e) {
                if (!file.canRead()){
                    e.onError(new IOException("差分包不可访问"));
                    return;
                }
                if (Long.parseLong(file.getName()) <= BuildConfig.VERSION_CODE){
                    e.onError(new IllegalArgumentException("差分包版本不是最新"));
                    return;
                }
                ApplicationInfo applicationInfo = KuDouApplication.getGlobalContext().getApplicationInfo();
                String apkPath = applicationInfo.sourceDir;
                LogCat.d(TAG, "Current APK file is " + apkPath);
                File currentApk = new File(apkPath);
                if (currentApk.exists() && currentApk.canRead()) {
                    LogCat.i(TAG, "loading file");
                } else {
                    LogCat.e(TAG, "file is not readable");
                    return;
                }
                File patchedApk = new File(patchedApkDir + System.currentTimeMillis() + ".apk");
                if (!patchedApk.getParentFile().exists())
                    patchedApk.getParentFile().mkdirs();
                try {
                if (!patchedApk.exists()) {
                    patchedApk.createNewFile();
                }
                BSPatch.patchFast(currentApk, patchedApk, file, 0);
                }catch (IOException exce){
                    e.onError(exce);
                    return;
                }
                LogCat.d(TAG, MD5Utils.encode(patchedApk));
                e.onNext(patchedApk);
            }
        };
//        return Single.create(e -> {
//            if (!file.canRead()){
//                e.tryOnError(new IOException("差分包不可访问"));
//                return;
//            }
//            if (Long.parseLong(file.getName()) <= BuildConfig.VERSION_CODE){
//                e.tryOnError(new IllegalArgumentException("差分包版本不是最新"));
//                return;
//            }
//            ApplicationInfo applicationInfo = KuDouApplication.getGlobalContext().getApplicationInfo();
//            String apkPath = applicationInfo.sourceDir;
//            LogCat.d(TAG, "Current APK file is " + apkPath);
//            File currentApk = new File(apkPath);
//            if (currentApk.exists() && currentApk.canRead()) {
//                LogCat.i(TAG, "loading file");
//            } else {
//                LogCat.e(TAG, "file is not readable");
//                return;
//            }
//            File patchedApk = new File(patchedApkDir + System.currentTimeMillis() + ".apk");
//            if (!patchedApk.getParentFile().exists())
//                patchedApk.getParentFile().mkdirs();
//            if (!patchedApk.exists()) {
//                patchedApk.createNewFile();
//            }
//            BSPatch.patchFast(currentApk, patchedApk, file, 0);
//            LogCat.d(TAG, MD5Utils.encode(patchedApk));
//            e.onSuccess(patchedApk);
//        });
    }
}
