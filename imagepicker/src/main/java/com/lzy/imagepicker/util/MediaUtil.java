package com.lzy.imagepicker.util;

import android.content.Context;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.R;
import com.lzy.imagepicker.bean.ImageItem;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/7/5 15:56
 * 类描述：
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class MediaUtil {
    private static final String TAG = "ImagePick_MediaUtil";

    public static MediaFormat getMediaInfo(String path) {
        MediaExtractor videoExtractor = new MediaExtractor();
        try {
            videoExtractor.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int videoTrackIndex;
        //获取视频所在轨道
        videoTrackIndex = getMediaTrackIndex(videoExtractor, "video/");
        if (videoTrackIndex >= 0) {
            MediaFormat mediaFormat = videoExtractor.getTrackFormat(videoTrackIndex);
            Log.e(TAG, "getMediaInfo: \n" + mediaFormat.toString() + "\n");
            videoExtractor.selectTrack(videoTrackIndex);
            videoExtractor.release();
            return mediaFormat;
        }
        videoExtractor.release();
        return null;
    }

    //获取指定类型媒体文件所在轨道
    private static int getMediaTrackIndex(MediaExtractor videoExtractor, String MEDIA_TYPE) {
        int trackIndex = -1;
        for (int i = 0; i < videoExtractor.getTrackCount(); i++) {
            //获取视频所在轨道
            MediaFormat mediaFormat = videoExtractor.getTrackFormat(i);
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith(MEDIA_TYPE)) {
                trackIndex = i;
                break;
            }
        }
        return trackIndex;
    }

    public static boolean isFileValid(Context mActivity, boolean isCheck, ImageItem imageItem, List<ImageItem> mSelectedImages) {
        if (imageItem.mimeType.startsWith("video")) {
            if (ImagePicker.getInstance().getSelectImageCount() > 0 && !ImagePicker.getInstance().isSelectedImageContainsVideo()) {
                Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.invalid_video_and_image), Toast.LENGTH_SHORT).show();
                return false;
            }
            int selectLimit = ImagePicker.getInstance().getSelectVideoLimit();
            if (isCheck && mSelectedImages.size() >= selectLimit) {
                Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.selectvideo_limit, selectLimit), Toast.LENGTH_SHORT).show();
                return false;
            } else {
                if (!new File(imageItem.path).exists()) {
                    Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.invalid_video), Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (ImagePicker.getInstance().getMaxVideoDuration() > 0 && imageItem.duration > ImagePicker.getInstance().getMaxVideoDuration()) {
                    Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.invalid_video_duration, ImagePicker.getInstance().getMaxVideoDuration() / 1000f), Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }
        } else {
            if (ImagePicker.getInstance().getSelectImageCount() > 0 && ImagePicker.getInstance().isSelectedImageContainsVideo()) {
                Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.invalid_video_and_image), Toast.LENGTH_SHORT).show();
                return false;
            }
            int selectLimit = ImagePicker.getInstance().getSelectLimit();
            if (isCheck && mSelectedImages.size() >= selectLimit) {
                Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.select_limit, selectLimit), Toast.LENGTH_SHORT).show();
                return false;
            } else {
                if (!new File(imageItem.path).exists()) {
                    Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.invalid), Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }
        }
    }
}
