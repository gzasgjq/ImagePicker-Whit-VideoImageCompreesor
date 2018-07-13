package com.lzy.imagepicker;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.lzy.imagepicker.bean.ImageFolder;
import com.lzy.imagepicker.bean.ImageItem;

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
public class VideoDataSource implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER_ALL = 0x0;         //加载所有视频
    public static final int LOADER_CATEGORY = 0x1;    //分类加载视频
    public static final String[] VIDEO_PROJECTION = {     //查询视频需要的数据列
            MediaStore.Video.Media.DISPLAY_NAME,   //视频的显示名称  aaa.jpg
            MediaStore.Video.Media.DATA,           //视频的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            MediaStore.Video.Media.SIZE,           //视频的大小，long型  132492
            MediaStore.Video.Media.WIDTH,          //视频的宽度，int型  1920
            MediaStore.Video.Media.HEIGHT,         //视频的高度，int型  1080
            MediaStore.Video.Media.DURATION,       //视频的时长，long型
            MediaStore.Video.Media.MIME_TYPE,      //视频的类型     video/mp4
            MediaStore.Video.Media.DATE_ADDED};    //视频被添加的时间，long型  1450518608


    private FragmentActivity activity;
    private OnDataLoadedListener loadedListener;                     //视频加载完成的回调接口
    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks;
    private ArrayList<ImageFolder> imageFolders = new ArrayList<>();   //所有的视频文件夹

    /**
     * @param activity       用于初始化LoaderManager，需要兼容到2.3
     * @param path           指定扫描的文件夹目录，可以为 null，表示扫描所有视频
     * @param loadedListener 视频加载完成的监听
     */
    public VideoDataSource(FragmentActivity activity, String path, OnDataLoadedListener loadedListener) {
        this.activity = activity;
        this.loadedListener = loadedListener;

        LoaderManager loaderManager = activity.getSupportLoaderManager();
        if (path == null) {
            loaderManager.initLoader(LOADER_ALL, null, this);//加载所有的视频
        } else {
            //加载指定目录的视频
            Bundle bundle = new Bundle();
            bundle.putString("path", path);
            loaderManager.initLoader(LOADER_CATEGORY, bundle, this);
        }
    }

    public void setLoaderCallbacks(LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks) {
        this.loaderCallbacks = loaderCallbacks;
    }

    // 创建一个可查询ContentProvider的loader

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (this.loaderCallbacks != null)
            loaderCallbacks.onCreateLoader(id, args);
        CursorLoader cursorLoader = null;
        //扫描所有视频
        if (id == LOADER_ALL)
            cursorLoader = new CursorLoader(activity, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION, null, null, VIDEO_PROJECTION[6] + " DESC");
        //扫描某个视频文件夹
        if (id == LOADER_CATEGORY)
            cursorLoader = new CursorLoader(activity, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION, VIDEO_PROJECTION[1] + " like '%" + args.getString("path") + "%'", null, VIDEO_PROJECTION[6] + " DESC");
        return cursorLoader;
    }

    // loader完成查询时调用，通常用于在查询到的cursor中提取数据
    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        if (this.loaderCallbacks != null) {
            loaderCallbacks.onLoadFinished(loader, data);
        } else {
            imageFolders.clear();
            if (data != null) {
                ArrayList<ImageItem> allImages = new ArrayList<>();   //所有视频的集合,不分文件夹
                if (!data.moveToFirst()) {
                    return;
                }
                while (data.moveToNext()) {
                    //查询数据
                    String imageName = data.getString(data.getColumnIndexOrThrow(VIDEO_PROJECTION[0]));
                    String imagePath = data.getString(data.getColumnIndexOrThrow(VIDEO_PROJECTION[1]));

                    File file = new File(imagePath);
                    if (!file.exists() || file.length() <= 0) {
                        continue;
                    }
                    long imageSize = data.getLong(data.getColumnIndexOrThrow(VIDEO_PROJECTION[2]));
                    int imageWidth = data.getInt(data.getColumnIndexOrThrow(VIDEO_PROJECTION[3]));
                    int imageHeight = data.getInt(data.getColumnIndexOrThrow(VIDEO_PROJECTION[4]));
                    long duration = data.getLong(data.getColumnIndexOrThrow(VIDEO_PROJECTION[5]));
                    String imageMimeType = data.getString(data.getColumnIndexOrThrow(VIDEO_PROJECTION[6]));
                    long imageAddTime = data.getLong(data.getColumnIndexOrThrow(VIDEO_PROJECTION[7]));

                    //封装实体
                    ImageItem imageItem = new ImageItem();
                    imageItem.name = imageName;
                    imageItem.path = imagePath;
                    imageItem.size = imageSize;
                    imageItem.width = imageWidth;
                    imageItem.height = imageHeight;
                    imageItem.duration = duration;
                    imageItem.mimeType = imageMimeType;
                    imageItem.addTime = imageAddTime;
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
                if (data.getCount() > 0 && allImages.size() > 0) {
                    //构造所有视频的集合
                    ImageFolder allImagesFolder = new ImageFolder();
                    allImagesFolder.name = activity.getResources().getString(R.string.all_images);
                    allImagesFolder.path = "/";
                    allImagesFolder.cover = allImages.get(0);
                    allImagesFolder.images = allImages;
                    imageFolders.add(0, allImagesFolder);  //确保第一条是所有视频
                }
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
        if (this.loaderCallbacks != null) {
            loaderCallbacks.onLoaderReset(loader);
        }
    }

}
