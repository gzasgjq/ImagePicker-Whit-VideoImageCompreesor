package com.dinpay.trip.cropvideo.tools;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/03/19 14:48
 * 类描述：$INTRO$
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class VideoUtils {

    public static String getVideoLength(Context context, Uri uri){
        MediaMetadataRetriever mMetadataRetriever = new MediaMetadataRetriever();
        mMetadataRetriever.setDataSource(context, uri);
        return mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
    }

    /**
     * 获取文件路径
     *
     * @return
     */
    public static String filePathFromIntent(Context context, Uri uri) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor == null) {
                //miui 2.3 有可能为null
                return uri.getPath();
            } else {
                cursor.moveToFirst();
                return cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)); // 文件路径
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
