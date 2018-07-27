package com.gjq.demo.deltaupdatelib;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.gjq.demo.deltaupdatelib.bsdiff.BSPatch;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.BuildConfig;
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
//        checkPermission(context);
        Single.create((SingleOnSubscribe<File>) e -> {
            File patchsDirFile = new File(patchsDir);
            if (!patchsDirFile.exists()) {
                patchsDirFile.mkdirs();
            }
            File[] patchs = patchsDirFile.listFiles();
            if (patchs.length <= 0) {
                Log.e(TAG, "No Patch Find");
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
            Log.i(TAG, "Choose patch is " + patch.getAbsolutePath());
            if (Long.parseLong(patch.getName()) <= BuildConfig.VERSION_CODE) {

            }

            ApplicationInfo applicationInfo = context.getApplicationContext().getApplicationInfo();
            String apkPath = applicationInfo.sourceDir;
            Log.d(TAG, "Current APK file is " + apkPath);
            File currentApk = new File(apkPath);
            if (currentApk.exists() && currentApk.canRead()) {
                Log.i(TAG, "loading file");
            } else {
                Log.e(TAG, "file is not readable");
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
//                        ApkUpdateUtils.installApp(context, r);
                        d.dispose();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Patch Fail: " + e);
                        e.printStackTrace();
                        d.dispose();
                    }
                });
    }

//    @TargetApi(Build.VERSION_CODES.O)
//    private static boolean checkPermission(Context context) {
//        boolean haveInstallPermission;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            //先获取是否有安装未知来源应用的权限
//            haveInstallPermission = context.getPackageManager().canRequestPackageInstalls();
//            if (!haveInstallPermission) {//没有权限
////                DialogUtils.showDialog(context, "安装应用需要打开未知来源权限，请去设置中开启权限",
////                        new View.OnClickListener() {
////                            @Override
////                            public void onClick(View v) {
////                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                                    startInstallPermissionSettingActivity();
////                                }
////                            }
////                        }, null);
//                return false;
//            }
//        }
//        //有权限，开始安装应用程序
//        installApk(apk);
//        return true;
//    }
//
//
//    private static void startInstallPermissionSettingActivity(Context context) {
////注意这个是8.0新API
//        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
//        context.startActivityForResult(intent, 10086);
//    }
//
//    //安装应用
//    private static void installApk(File apk,Context context) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//            intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
//        } else {//Android7.0之后获取uri要用contentProvider
//            Uri uri = FileProvider.getUriForFile(context, "com.dinpay.trip.fileprovider", appFile);
//            intent.setDataAndType(uri, "application/vnd.android.package-archive");
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        }
//
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
//    }

    public static Observable<File> patchFile(File file, Context context) {
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
                ApplicationInfo applicationInfo = context.getApplicationContext().getApplicationInfo();
                String apkPath = applicationInfo.sourceDir;
                Log.d(TAG, "Current APK file is " + apkPath);
                File currentApk = new File(apkPath);
                if (currentApk.exists() && currentApk.canRead()) {
                    Log.i(TAG, "loading file");
                } else {
                    Log.e(TAG, "file is not readable");
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
                } catch (IOException exce) {
                    e.onError(exce);
                    return;
                }
                Log.d(TAG, MD5Utils.encode(patchedApk));
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
//            Log.d(TAG, "Current APK file is " + apkPath);
//            File currentApk = new File(apkPath);
//            if (currentApk.exists() && currentApk.canRead()) {
//                Log.i(TAG, "loading file");
//            } else {
//                Log.e(TAG, "file is not readable");
//                return;
//            }
//            File patchedApk = new File(patchedApkDir + System.currentTimeMillis() + ".apk");
//            if (!patchedApk.getParentFile().exists())
//                patchedApk.getParentFile().mkdirs();
//            if (!patchedApk.exists()) {
//                patchedApk.createNewFile();
//            }
//            BSPatch.patchFast(currentApk, patchedApk, file, 0);
//            Log.d(TAG, MD5Utils.encode(patchedApk));
//            e.onSuccess(patchedApk);
//        });
    }
}
