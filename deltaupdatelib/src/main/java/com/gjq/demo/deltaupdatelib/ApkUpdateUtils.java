package com.gjq.demo.deltaupdatelib;

/**
 * 创建人：tzh-t121
 * 创建时间：2016/9/14 12:28
 * 类描述：应用更新工具
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class ApkUpdateUtils {
//
//    private final int UPDATE_APP_NOTIFI_ID = 0x254;
//    static String appName = "kudou.apk";
//    public static String mApkDownTag = "apkDownload";
//    public static String mApkPatchDownTag = "apkPatchDownload";
//    private Context context;
//    private NotificationManager mNotificationManager;
//    private int preProgress;
//
//    private OkDownload okDownload;
//    private Map<String, DownloadTask> tasks = new HashMap<>();
//    //    private String filePath;
//    protected CompositeDisposable mCompositeDisposable;
//    private onDownloadErrorListener onDownloadErrorListener;
//
//
//    private NotificationCompat.Builder mBuilder;
//
//    public ApkUpdateUtils(Context context) {
//        this.context = context;
//        KudouNotificationChannelCompat26.createKudouUpdateNotificationChannel(context);
//        okDownload = OkDownload.getInstance();
//        initNotify();
//
//        String filePath;
//        if (Utils.existSDCard()) {
//            filePath = Environment.getExternalStorageDirectory() + "/dinpay/apk";
//        } else {
//            filePath = context.getCacheDir().toString();
//        }
//
//        File dir = new File(filePath);
//        if (!dir.exists()) {
//            if (!dir.mkdirs()) {
//                filePath = context.getCacheDir().toString();
//            }
//        }
//
//        deleteOldApk(filePath);
//        okDownload.setFolder(filePath);
//    }
//
//    public Observable<DownloadBean> downloadApk(final String url, String tag) {
//        return new Observable<DownloadBean>() {
//            @Override
//            protected void subscribeActual(final Observer<? super DownloadBean> observer) {
//                String[] split = url.split("/");
//                tasks.put(tag, OkDownload.request(tag, UpdateApi.getInstance().downloadApk(url, tag))
//                        .priority(Integer.MAX_VALUE)
//                        .fileName(split[split.length - 1])
//                        .register(new DownloadListener(tag) {
//                            private boolean isRemoved;
//
//                            @Override
//                            public void onStart(Progress progress) {
//                                if (!isRemoved)
//                                    observer.onNext(new DownloadBean(progress, null, DownloadBean.ACTION_START));
//                            }
//
//                            @Override
//                            public void onProgress(Progress progress) {
//                                if (!isRemoved)
//                                    observer.onNext(new DownloadBean(progress, null, DownloadBean.ACTION_PROGRESS));
//                            }
//
//                            @Override
//                            public void onError(Progress progress) {
//                                if (!isRemoved)
//                                    observer.onError(progress.exception);
////                                observer.onComplete();
//                            }
//
//                            @Override
//                            public void onFinish(File file, Progress progress) {
//                                if (!isRemoved)
//                                    observer.onNext(new DownloadBean(progress, file, DownloadBean.ACTION_FINISH));
////                                observer.onComplete();
//                            }
//
//                            @Override
//                            public void onRemove(Progress progress) {
//                                isRemoved = true;
//                            }
//                        })
//                        .start());
//            }
//        }.doOnSubscribe(disposable -> mCompositeDisposable.add(disposable));
//    }
//
//    public Observable<DownloadBean> downloadPatch(String patchUrl, String apkPatch) {
//        return downloadApk(patchUrl, mApkPatchDownTag)
//                .flatMap(downloadBean -> {
//                    if (downloadBean.getAction() == DownloadBean.ACTION_FINISH) {
//                        return DeltaUpdatesUtils.patchFile(downloadBean.getFile())
//                                .map(file -> {
//                                    downloadBean.file = file;
//                                    downloadBean.getProgress().fraction = 1;
//                                    return downloadBean;
//                                })
//                                .onErrorResumeNext(throwable -> {
//                                    return downloadApk(apkPatch, mApkDownTag);
//                                });
//                    } else {
//                        return new Observable<DownloadBean>() {
//                            @Override
//                            protected void subscribeActual(Observer<? super DownloadBean> observer) {
//                                downloadBean.getProgress().fraction = downloadBean.getProgress().fraction * 0.8f;
//                                observer.onNext(downloadBean);
//                            }
//                        };
//                    }
//                }, true);
////        task = OkDownload.request(mApkDownTag, UpdateApi.getInstance().downloadApk(patchUrl, mApkDownTag))
////                .priority(Integer.MAX_VALUE)
////                .fileName(appName)
////                .register(new DownloadListener(mApkDownTag) {
////                    @Override
////                    public void onStart(Progress progress) {
////                        showProgressNotify();
////                    }
////
////                    @Override
////                    public void onProgress(Progress progress) {
////                        if (onDownloadErrorListener != null) {
////                            onDownloadErrorListener.onProgress(progress);
////                        }
////                        notifyNotification((int) (100 * progress.fraction));
////                    }
////
////                    @Override
////                    public void onError(Progress progress) {
////                        LogCat.i("downloadFile", "onError: " + progress.exception.getMessage());
////                        cancelAll();
////                        if (onDownloadErrorListener != null) {
////                            onDownloadErrorListener.onError(progress.exception);
////                        }
////                    }
////
////                    @Override
////                    public void onFinish(File file, Progress progress) {
////                        LogCat.i("downloadFile", "onFinish: " + file.getAbsolutePath());
////                        if (onDownloadErrorListener != null) {
////                            onDownloadErrorListener.onFinish(file, progress);
////                        }
////                        showIntentActivityNotify(file);
////                        installApp(context, file);
////
////                    }
////
////                    @Override
////                    public void onRemove(Progress progress) {
////                    }
////                })
////                .start();
//    }
//
//    public void download(String url, String patchUrl) {
//        if (!url.endsWith("apk")) {
//            TipsUtils.toastShort(context, "Url异常");
//            return;
//        }
//        downloadPatch(patchUrl, url)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<DownloadBean>() {
//                    private Disposable d;
//
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        this.d = d;
//                    }
//
//                    @Override
//                    public void onNext(DownloadBean downloadBean) {
//                        switch (downloadBean.getAction()) {
//                            case DownloadBean.ACTION_FINISH:
//                                LogCat.i("downloadFile", "onFinish: " + downloadBean.getFile().getAbsolutePath());
//                                if (onDownloadErrorListener != null) {
//                                    onDownloadErrorListener.onFinish(downloadBean.getFile(), downloadBean.getProgress());
//                                }
//                                showIntentActivityNotify(downloadBean.getFile());
//                                installApp(context, downloadBean.getFile());
//                                break;
//                            case DownloadBean.ACTION_PROGRESS:
//                                if (onDownloadErrorListener != null) {
//                                    onDownloadErrorListener.onProgress(downloadBean.getProgress());
//                                }
//                                notifyNotification((int) (100 * downloadBean.getProgress().fraction));
//                                break;
//                            case DownloadBean.ACTION_START:
//                                showProgressNotify();
//                                break;
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        LogCat.i("downloadFile", "onError: " + e.getMessage());
//                        cancelAll();
//                        if (onDownloadErrorListener != null) {
//                            onDownloadErrorListener.onError(e);
//                        }
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        d.dispose();
//                    }
//                });
//    }
//
//    class DownloadBean {
//        public static final int ACTION_START = 257;
//        public static final int ACTION_PROGRESS = 730;
//        public static final int ACTION_FINISH = 463;
//
//        private Progress progress;
//        private File file;
//        private int action;
//
//        public DownloadBean(Progress progress, File file, int action) {
//            this.progress = progress;
//            this.file = file;
//            this.action = action;
//        }
//
//        public void setProgress(Progress progress) {
//            this.progress = progress;
//        }
//
//        public void setFile(File file) {
//            this.file = file;
//        }
//
//        public void setAction(int action) {
//            this.action = action;
//        }
//
//        public Progress getProgress() {
//            return progress;
//        }
//
//        public File getFile() {
//            return file;
//        }
//
//        public int getAction() {
//            return action;
//        }
//    }
//
//    public void download(final String url) {
//
//        DownloadListener downloadFile = new DownloadListener(mApkDownTag) {
//            @Override
//            public void onStart(Progress progress) {
//                showProgressNotify();
//            }
//
//            @Override
//            public void onProgress(Progress progress) {
//
//                if (onDownloadErrorListener != null) {
//                    onDownloadErrorListener.onProgress(progress);
//                }
//
//                notifyNotification((int) (100 * progress.fraction));
//            }
//
//            @Override
//            public void onError(Progress progress) {
//                LogCat.i("downloadFile", "onError: " + progress.exception.getMessage());
//
//                cancelAll();
//
//                if (onDownloadErrorListener != null) {
//                    onDownloadErrorListener.onError(progress.exception);
//                }
//
//            }
//
//            @Override
//            public void onFinish(File file, Progress progress) {
//                LogCat.i("downloadFile", "onFinish: " + file.getAbsolutePath());
//
//                if (onDownloadErrorListener != null) {
//                    onDownloadErrorListener.onFinish(file, progress);
//                }
//
//                showIntentActivityNotify(file);
//                installApp(context, file);
//
//            }
//
//            @Override
//            public void onRemove(Progress progress) {
//
//            }
//        };
//
//        tasks.put(mApkDownTag, OkDownload.request(mApkDownTag, UpdateApi.getInstance().downloadApk(url, mApkDownTag))
//                .priority(Integer.MAX_VALUE)
//                .fileName(appName)
//                .register(downloadFile)
//                .start());
//    }
//
//
//    /**
//     * 获取拓展存储Cache的绝对路径
//     *
//     * @param context
//     */
//    public static String getExternalCacheDir(Context context) {
//
//        StringBuilder sb = new StringBuilder();
//        File file = context.getExternalCacheDir();
//        // In some case, even the sd card is mounted, getExternalCacheDir will return null may be it is nearly full.
//        if (file != null) {
//            sb.append(file.getAbsolutePath()).append(File.separator);
//        } else {
//            sb.append(Environment.getExternalStorageDirectory().getPath())
//                    .append("/Android/data/").append(context.getPackageName())
//                    .append("/cache/").append(File.separator).toString();
//        }
//
//        return sb.toString();
//    }
//
//    /**
//     * 删除之前升级时下载的老的 apk 文件
//     */
//    public void deleteOldApk(String path) {
//        if (TextUtils.isEmpty(path)) return;
//        File apkDir = new File(path);
//        if (!apkDir.exists() || apkDir.listFiles() == null || apkDir.listFiles().length == 0) {
//            return;
//        }
//        // 删除文件
//        deleteFile(apkDir);
//    }
//
//
//    /**
//     * 删除文件或文件夹
//     *
//     * @param file
//     */
//    static void deleteFile(File file) {
//        try {
//            if (file == null || !file.exists()) {
//                return;
//            }
//
//            if (file.isDirectory()) {
//                File[] files = file.listFiles();
//                if (files != null && files.length > 0) {
//                    for (File f : files) {
//                        if (f.exists()) {
//                            if (f.isDirectory()) {
//                                deleteFile(f);
//                            } else {
//                                f.delete();
//                            }
//                        }
//                    }
//                }
//            } else {
//                file.delete();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//    /**
//     * 显示带进度条的通知
//     */
//    private void showProgressNotify() {
//        mBuilder.setContentTitle("正在下载")
//                .setContentText("进度:")
//                .setTicker("开始下载");// 通知首次出现在通知栏，带上升动画效果的
//        mBuilder.setProgress(0, 0, true);
//        mNotificationManager.notify(UPDATE_APP_NOTIFI_ID, mBuilder.build());
//    }
//
//
//    /**
//     * 初始化通知栏
//     */
//    private void initNotify() {
//        mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//        mBuilder = new NotificationCompat.Builder(context, KudouNotificationChannelCompat26.getKudouUpdateChannelId(context));
//        mBuilder.setAutoCancel(true)
//                .setWhen(System.currentTimeMillis())
//                .setOngoing(false)
//                .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
//                .setContentIntent(getDefalutIntent(0))
//                .setPriority(Notification.PRIORITY_DEFAULT) // 设置该通知优先级
//                .setSmallIcon(context.getApplicationInfo().icon);
//    }
//
//    /**
//     * 点击跳转安装界面
//     */
//    protected void showIntentActivityNotify(File appFile) {
//
////        File appFile = new File(path + "/" + appName);
//        if (!appFile.exists()) {
//            return;
//        }
//
//        mBuilder.setContentTitle("下载完成")
//                .setContentText("点击进行安装APK")
//                .setTicker("下载完成")
//                .setAutoCancel(true)
//                .setProgress(0, 0, false);// 通知首次出现在通知栏，带上升动画效果的
//
//        Intent intent = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
//            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            Uri contentUri = FileProvider.getUriForFile(context, "com.dinpay.trip.fileprovider", appFile);
//            context.grantUriPermission("com.android.packageinstaller", contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
//        } else {
//            intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(Uri.fromFile(appFile), "application/vnd.android.package-archive");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        }
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        mBuilder.setContentIntent(pendingIntent);
//        mNotificationManager.notify(UPDATE_APP_NOTIFI_ID, mBuilder.build());
//    }
//
//    /**
//     * 安装新版本应用
//     */
//    public static void installApp(Context context, File appFile) {
////        File appFile = new File(path + File.separator + appName);
//        if (!appFile.exists()) {
//            return;
//        }
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            Uri contentUri = FileProvider.getUriForFile(context, "com.dinpay.trip.fileprovider", appFile);
//            context.grantUriPermission("com.android.packageinstaller", contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
//        } else {
//            intent.setDataAndType(Uri.fromFile(appFile), "application/vnd.android.package-archive");
//        }
//        if (context.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
//            context.startActivity(intent);
//            ((Activity) context).finish();
////            System.exit(0);
//        }
//
//    }
//
//    /**
//     * @获取默认的pendingIntent,为了防止2.3及以下版本报错
//     * @flags属性: 在顶部常驻:Notification.FLAG_ONGOING_EVENT
//     * 点击去除： Notification.FLAG_AUTO_CANCEL
//     */
//    public PendingIntent getDefalutIntent(int flags) {
//        return PendingIntent.getActivity(context, 1, new Intent(), flags);
//    }
//
//
//    public void cancelAll() {
//        if (okDownload != null && okDownload.hasTask("apkDownload")) {
//            okDownload.removeTask("apkDownload");
//        }
//        for (DownloadTask task : tasks.values())
//            task.remove(false);
////        if (task != null) {
////            task.remove(false);
////        }
//        okDownload.removeAll(false);
//        if (mNotificationManager != null) {
//            mNotificationManager.cancel(UPDATE_APP_NOTIFI_ID);
//            mNotificationManager.cancelAll();
//        }
//    }
//
//    private void notifyNotification(int percent) {
//        //优化卡顿
//        if (preProgress < percent && (percent % 5 == 0)) {
//            // 更新进度
//            mBuilder.setProgress(100, percent, false);
//            mBuilder.setContentText("进度:" + percent + "%");
//            mNotificationManager.notify(UPDATE_APP_NOTIFI_ID, mBuilder.build());
//        }
//
//        preProgress = percent;
//    }
//
//
//    public void onDestroy() {
//        okDownload.removeAll(false);
//    }
//
//
//    public void setonDownloadErrorListener(onDownloadErrorListener listener) {
//        this.onDownloadErrorListener = listener;
//    }
//
//    public interface onDownloadErrorListener {
//        void onError(Throwable exception);
//
//        void onFinish(File file, Progress progress);
//
//        void onProgress(Progress progress);
//    }
}


