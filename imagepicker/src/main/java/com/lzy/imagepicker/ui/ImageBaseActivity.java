package com.lzy.imagepicker.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.dinpay.trip.videocompressorlib.LocalMediaCompress;
import com.dinpay.trip.videocompressorlib.model.BaseMediaBitrateConfig;
import com.dinpay.trip.videocompressorlib.model.LocalMediaConfig;
import com.dinpay.trip.videocompressorlib.model.MediaObject;
import com.dinpay.trip.videocompressorlib.model.VBRMode;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.R;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.shaohui.advancedluban.Luban;
import me.shaohui.advancedluban.OnMultiCompressListener;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ImageBaseActivity extends AppCompatActivity {

    //    protected SystemBarTintManager tintManager;
    protected View topBar;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setStateBarColor(this);
    }

    public void setStateBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0 全透明实现
            //getWindow.setStatusBarColor(Color.TRANSPARENT)
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4 全透明状态栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        topBar = findViewById(R.id.top_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup contentLayout = getWindow().getDecorView().findViewById(android.R.id.content);
            setupStatusBarView(this, contentLayout, getResources().getColor(R.color.status_bar));

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) topBar.getLayoutParams();
            params.topMargin = Utils.getStatusHeight(this);
            topBar.setLayoutParams(params);
        }
    }

    /**
     * 获得状态栏高度
     */
    protected static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    protected static void setupStatusBarView(Activity activity, ViewGroup contentLayout, int color) {
        View mStatusBarView = new View(activity);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        contentLayout.addView(mStatusBarView, lp);
        mStatusBarView.setBackgroundColor(color);

//        View mStatusBarView = null;
//        View statusBarView = new View(activity);
//        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
//        contentLayout.addView(statusBarView, lp);
//        mStatusBarView = statusBarView;
//        mStatusBarView.setBackgroundColor(color);
    }

    public boolean checkPermission(@NonNull String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void showToast(String toastText) {
        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
    }

    public void checkCompress(final ArrayList<ImageItem> mImageItems) {
        if (ImagePicker.getInstance().isNeedCompress()) {
            List<File> compressImages = new ArrayList<>();
            List<File> compressVideos = new ArrayList<>();
            for (ImageItem imageItem : mImageItems) {
                if (imageItem.mimeType.startsWith("video")) {
                    compressVideos.add(new File(imageItem.path));
                }
                if (imageItem.mimeType.startsWith("image")) {
                    compressImages.add(new File(imageItem.path));
                }
            }
            final String outputPath = TextUtils.isEmpty(ImagePicker.getInstance().getCachePath()) ?
                    Utils.getCacheImagePath(ImageBaseActivity.this) + "/"
                    : ImagePicker.getInstance().getCachePath() + "/";
            if (!checkFile(outputPath)) {
                showToast(getString(R.string.cachepath_error));
                return;
            }
            if (!compressVideos.isEmpty()) {
                Observable.fromIterable(compressVideos)
                        .map(new Function<File, LocalMediaCompress>() {
                            @Override
                            public LocalMediaCompress apply(File file) throws Exception {
                                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                                mediaMetadataRetriever.setDataSource(file.getPath());
                                String videoWidth = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                                String videoHeight = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                                float scale = Math.min(Float.parseFloat(videoWidth), Float.parseFloat(videoHeight)) / 540f;
                                LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
                                LocalMediaConfig config = buidler
                                        .setVideoPath(file.getPath())
                                        .setFramerate(30)
                                        .captureThumbnailsTime(1)
                                        .doH264Compress(new VBRMode(3000, 1800).setVelocity(BaseMediaBitrateConfig.Velocity.SUPERFAST))
                                        .setScale(scale)
                                        .build();
                                return new LocalMediaCompress(outputPath, config);
                            }
                        })
                        .concatMap(new Function<LocalMediaCompress, ObservableSource<MediaObject>>() {
                            @Override
                            public ObservableSource<MediaObject> apply(final LocalMediaCompress localMediaCompress) throws Exception {
                                return Observable.create(new ObservableOnSubscribe<MediaObject>() {
                                    @Override
                                    public void subscribe(final ObservableEmitter<MediaObject> observableEmitter) throws Exception {
                                        localMediaCompress.startCompressAsyn(new LocalMediaCompress.OnExecOverListener() {
                                            @Override
                                            public void onOver(int i, MediaObject mediaObject) {
                                                observableEmitter.onNext(mediaObject);
                                                observableEmitter.onComplete();
                                            }
                                        });
                                    }
                                });
                            }
                        })
                        .toList()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<List<MediaObject>>() {
                            Disposable disposable;

                            @Override
                            public void onSubscribe(Disposable disposable) {
                                this.disposable = disposable;
                                onCompressStart();
                            }

                            @Override
                            public void onSuccess(List<MediaObject> mediaObjects) {
                                onCompressStop();
                                ArrayList<ImageItem> resultList = new ArrayList<>();
                                for (MediaObject mediaObject : mediaObjects) {
                                    String path = mediaObject.getOutputTempTranscodingVideoPath();
                                    if (!path.contains("dinpay/cache")) {
                                        File dstPath = new File(outputPath + mediaObject.getBaseName() + ".mp4");
                                        File srcPath = new File(path);
                                        if (!Utils.copyFile(srcPath, dstPath)) {
                                            showToast(getString(R.string.compress_video_error));
                                            return;
                                        }
                                        path = dstPath.getPath();
                                    }
                                    ImageItem imageItem = new ImageItem();
                                    imageItem.mimeType = "video/*";
                                    imageItem.path = path;
                                    imageItem.name = mediaObject.getBaseName();
                                    Log.e("ImagePick", "onSuccess: " + path);
                                    resultList.add(imageItem);
                                }
                                if (mediaObjects.size() == mImageItems.size()) {
                                    setResultFinish(resultList);
                                } else {
                                    showToast(getString(R.string.compress_video_error));
                                }
                                disposable.dispose();
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                showToast(getString(R.string.compress_video_error));
                                onCompressStop();
                                disposable.dispose();
                            }
                        });
            }
            if (!compressImages.isEmpty()) {
                Luban.compress(compressImages, new File(outputPath))
                        .putGear(Luban.THIRD_GEAR)
                        .launch(new OnMultiCompressListener() {
                            @Override
                            public void onStart() {
                                onCompressStart();
                            }

                            @Override
                            public void onSuccess(List<File> fileList) {
                                onCompressStop();
                                ArrayList<ImageItem> resultList = new ArrayList<>();
                                for (File file : fileList) {
                                    String path = file.getPath();
                                    if (!path.contains("dinpay/cache")) {
                                        File dstPath = new File(outputPath + file.getName());
                                        if (!Utils.copyFile(file, dstPath)) {
                                            showToast(getString(R.string.compress_image_error));
                                            return;
                                        }
                                        path = dstPath.getPath();
                                    }
                                    ImageItem imageItem = new ImageItem();
                                    imageItem.mimeType = "image/*";
                                    imageItem.path = path;
                                    imageItem.name = file.getName();
                                    Log.e("ImagePick", "onSuccess: " + path);
                                    resultList.add(imageItem);
                                }
                                if (fileList.size() == mImageItems.size()) {
                                    setResultFinish(resultList);
                                } else {
                                    showToast(getString(R.string.compress_image_error));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                onCompressStop();
                                showToast(getString(R.string.compress_image_error));
                            }
                        });
            }
        } else {
            setResultFinish(mImageItems);
        }
    }

    private boolean checkFile(String outputPath) {
        if (TextUtils.isEmpty(outputPath)) return false;
        File file = new File(outputPath);
        if (file.exists()) {
            return file.canWrite();
        } else {
            return file.mkdirs();
        }
    }

    protected void onCompressStart() {

    }

    protected void onCompressStop() {

    }

    protected void setResultFinish(ArrayList<ImageItem> mImageItems) {
        Intent intent = new Intent();
        intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, mImageItems);
        setResult(ImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
        finish();
    }
}
