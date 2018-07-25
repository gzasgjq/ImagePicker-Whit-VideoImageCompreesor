package com.gjq.demo.cropvideo.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/03/19 14:29
 * 类描述：$INTRO$
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class VideoThumbnailsUtils {
    private static VideoThumbnailsUtils instance;

    private MediaMetadataRetriever metadataRetriever;
    private float extractW = 80;// 图片宽度

    public static synchronized VideoThumbnailsUtils getInstance() {
        synchronized (VideoThumbnailsUtils.class){
            if (instance == null)
                instance = new VideoThumbnailsUtils();
        }
        return instance;
    }

    private VideoThumbnailsUtils() {
    }

    public Flowable<String> getVideoThumbnailsInfoForEditRx(final Context context, final Uri uri, final String OutPutFileDirPath, final long startPosition, final long endPosition, final long thumbnailsCount) {
        return Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {
                metadataRetriever = new MediaMetadataRetriever();
                metadataRetriever.setDataSource(context, uri);
                long interval = (endPosition - startPosition) / (thumbnailsCount - 1);
                for (int i = 0; i < thumbnailsCount; i++) {
                    if (metadataRetriever == null) {
                        Log.d("ExtractFrame", "-------ok-stop-stop-->>>>>>>>>");
                        break;
                    }
                    long time = startPosition + interval * i;
                    if (i == thumbnailsCount - 1) {
                        if (interval > 1000) {
                            String path = extractFrame(metadataRetriever, endPosition - 800, OutPutFileDirPath);
                            e.onNext(path);
                        } else {
                            String path = extractFrame(metadataRetriever, endPosition, OutPutFileDirPath);
                            e.onNext(path);
                        }
                    } else {
                        String path = extractFrame(metadataRetriever, time, OutPutFileDirPath);
                        e.onNext(path);
                    }
                }
                Log.d("ExtractFrame", "-------ok-stop-->>>>>>>>>");
                e.onComplete();
            }
        }, BackpressureStrategy.DROP).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                if (metadataRetriever != null) {
                    metadataRetriever.release();
                    metadataRetriever = null;
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public void getVideoThumbnailsInfoForEdit(String videoPath, String OutPutFileDirPath, long startPosition, long endPosition, int thumbnailsCount) {
        if (metadataRetriever == null) return;
        metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(videoPath);
        long interval = (endPosition - startPosition) / (thumbnailsCount - 1);
        for (int i = 0; i < thumbnailsCount; i++) {
            if (metadataRetriever == null) {
                Log.d("ExtractFrame", "-------ok-stop-stop-->>>>>>>>>");
                break;
            }
            long time = startPosition + interval * i;
            if (i == thumbnailsCount - 1) {
                if (interval > 1000) {
                    String path = extractFrame(metadataRetriever, endPosition - 800, OutPutFileDirPath);
                    sendAPic(path, endPosition - 800);
                } else {
                    String path = extractFrame(metadataRetriever, endPosition, OutPutFileDirPath);
                    sendAPic(path, endPosition);
                }
            } else {
                String path = extractFrame(metadataRetriever, time, OutPutFileDirPath);
                sendAPic(path, time);
            }
        }
        if (metadataRetriever != null)
            metadataRetriever.release();
    }

    /**
     * 成功一张add一张
     *
     * @param path path
     * @param time time
     */
    private void sendAPic(String path, long time) {
//        VideoEditInfo info = new VideoEditInfo();
//        info.path = path;
//        info.time = time;
//        Message msg = mHandler.obtainMessage(ExtractFrameWorkThread.MSG_SAVE_SUCCESS);
//        msg.obj = info;
//        mHandler.sendMessage(msg);
    }

    private String extractFrame(MediaMetadataRetriever metadataRetriever, long time, String OutPutFileDirPath) {
        Bitmap bitmap = metadataRetriever.getFrameAtTime(time * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        if (bitmap != null) {
            Bitmap bitmapNew = scaleImage(bitmap);
            String path = PictureUtils.saveImageToSDForEdit(bitmapNew, OutPutFileDirPath, System.currentTimeMillis() + "_" + time + PictureUtils.POSTFIX);
            if (bitmapNew != null && !bitmapNew.isRecycled()) {
                bitmapNew.recycle();
                bitmapNew = null;
            }
            return path;
        }
        return null;
    }

    /**
     * 设置固定的宽度，高度随之变化，使图片不会变形
     *
     * @param bm Bitmap
     * @return Bitmap
     */
    private Bitmap scaleImage(Bitmap bm) {
        if (bm == null) {
            return null;
        }
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = extractW * 1.0f / width;
//        float scaleHeight =extractH*1.0f / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
        Bitmap newBm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
                true);
        if (!bm.isRecycled()) {
            bm.recycle();
            bm = null;
        }
        return newBm;
    }

}
