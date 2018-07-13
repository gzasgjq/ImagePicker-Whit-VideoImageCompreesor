package com.lzy.imagepicker;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.lzy.imagepicker.bean.ImageFolder;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.util.SortCursor;

import java.io.File;
import java.util.ArrayList;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/6/15 12:07
 * 类描述：
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class MixDataSource implements LoaderManager.LoaderCallbacks<Cursor> {
    private FragmentActivity activity;
    private OnDataLoadedListener loadedListener;                     //视频加载完成的回调接口
    private ArrayList<ImageFolder> imageFolders = new ArrayList<>();   //所有的视频文件夹
    private Cursor mVideoCursor = null, mImageCursor = null;

    /**
     * @param activity       用于初始化LoaderManager，需要兼容到2.3
     * @param path           指定扫描的文件夹目录，可以为 null，表示扫描所有视频
     * @param loadedListener 视频加载完成的监听
     */
    public MixDataSource(FragmentActivity activity, String path, OnDataLoadedListener loadedListener) {
        this.activity = activity;
        this.loadedListener = loadedListener;

        new VideoDataSource(activity, path, null).setLoaderCallbacks(this);
        new ImageDataSource(activity, path, null).setLoaderCallbacks(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    // loader完成查询时调用，通常用于在查询到的cursor中提取数据
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (((CursorLoader) loader).getUri().equals(MediaStore.Video.Media.EXTERNAL_CONTENT_URI)) {
            mVideoCursor = data;
            mVideoCursor.moveToFirst();
        } else {
            mImageCursor = data;
            mImageCursor.moveToFirst();
        }
        if (mVideoCursor != null && mImageCursor != null) {
            MergeCursor mergeCursor = new MergeCursor(new Cursor[]{mVideoCursor, mImageCursor});
            SortCursor mixCursor = new SortCursor(mergeCursor, MediaStore.MediaColumns.DATE_ADDED);
            mixCursor.moveToFirst();
            imageFolders.clear();
            ArrayList<ImageItem> allImages = new ArrayList<>();   //所有视频的集合,不分文件夹
            if (!mixCursor.moveToFirst()) {
                return;
            }
            while (mixCursor.moveToNext()) {
                String imageMimeType = mixCursor.getString(mixCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE));
                if (imageMimeType == null) continue;
                boolean isVideo = imageMimeType.startsWith("video");
                //查询数据
                String imageName = mixCursor.getString(mixCursor.getColumnIndexOrThrow(
                        isVideo ? VideoDataSource.VIDEO_PROJECTION[0]
                                : ImageDataSource.IMAGE_PROJECTION[0]
                ));
                String imagePath = mixCursor.getString(mixCursor.getColumnIndexOrThrow(
                        isVideo ? VideoDataSource.VIDEO_PROJECTION[1]
                                : ImageDataSource.IMAGE_PROJECTION[1]
                ));

                File file = new File(imagePath);
                if (!file.exists() || file.length() <= 0) {
                    continue;
                }
                long imageSize = mixCursor.getLong(mixCursor.getColumnIndexOrThrow(
                        isVideo ? VideoDataSource.VIDEO_PROJECTION[2]
                                : ImageDataSource.IMAGE_PROJECTION[2]
                ));
                int imageWidth = mixCursor.getInt(mixCursor.getColumnIndexOrThrow(
                        isVideo ? VideoDataSource.VIDEO_PROJECTION[3]
                                : ImageDataSource.IMAGE_PROJECTION[3]
                ));
                int imageHeight = mixCursor.getInt(mixCursor.getColumnIndexOrThrow(
                        isVideo ? VideoDataSource.VIDEO_PROJECTION[4]
                                : ImageDataSource.IMAGE_PROJECTION[4]
                ));

                long duration = 0;
                int orientation = 0;
                if (isVideo) {
                    duration = mixCursor.getLong(mixCursor.getColumnIndexOrThrow(VideoDataSource.VIDEO_PROJECTION[5]));
                    Log.w("ImagePicker", "onLoadFinished: " + "file:" + imageName + " duration: " + duration);
                } else {
                    orientation = mixCursor.getInt(mixCursor.getColumnIndexOrThrow(ImageDataSource.IMAGE_PROJECTION[5]));
                    Log.w("ImagePicker", "onLoadFinished: " + "file:" + imageName + " orientation: " + orientation);
                }

                long imageAddTime = mixCursor.getLong(mixCursor.getColumnIndexOrThrow(
                        isVideo ? VideoDataSource.VIDEO_PROJECTION[7]
                                : ImageDataSource.IMAGE_PROJECTION[7]
                ));

                //封装实体
                ImageItem imageItem = new ImageItem();
                imageItem.name = imageName;
                imageItem.path = imagePath;
                imageItem.size = imageSize;
                imageItem.width = imageWidth;
                imageItem.height = imageHeight;
                imageItem.mimeType = imageMimeType;
                imageItem.addTime = imageAddTime;
                imageItem.duration = duration;
                imageItem.orientation = orientation;
                allImages.add(imageItem);
                //根据父路径分类存放视频
                File imageFile = new File(imagePath);
                File imageParentFile = imageFile.getParentFile();
                ImageFolder imageFolder = new ImageFolder();
                imageFolder.name = imageParentFile.getName();
                imageFolder.path = imageParentFile.getAbsolutePath();

                if (!imageFolders.contains(imageFolder)) {
                    ArrayList<ImageItem> images = new ArrayList<>();
                    images.add(imageItem);
                    imageFolder.cover = imageItem;
                    imageFolder.images = images;
                    imageFolders.add(imageFolder);
                } else {
                    imageFolders.get(imageFolders.indexOf(imageFolder)).images.add(imageItem);
                }
            }

            //防止没有视频报异常
            if (mixCursor.getCount() > 0 && allImages.size() > 0) {
                //构造所有视频的集合
                ImageFolder allImagesFolder = new ImageFolder();
                allImagesFolder.name = activity.getResources().getString(R.string.all_images);
                allImagesFolder.path = "/";
                allImagesFolder.cover = allImages.get(0);
                allImagesFolder.images = allImages;
                imageFolders.add(0, allImagesFolder);  //确保第一条是所有视频
            }
            //回调接口，通知视频数据准备完成
            if (loadedListener != null) {
                ImagePicker.getInstance().setImageFolders(imageFolders);
                loadedListener.onDatasLoaded(imageFolders);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        System.out.println("--------");
    }

}
