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
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：加载手机图片实现类
 * 修订历史：
 * ================================================
 */
public class ImageDataSource implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER_ALL = 10;         //加载所有图片
    public static final int LOADER_CATEGORY = 11;    //分类加载图片
    public static final String[] IMAGE_PROJECTION = {     //查询图片需要的数据列
            MediaStore.Images.Media.DISPLAY_NAME,   //图片的显示名称  aaa.jpg
            MediaStore.Images.Media.DATA,           //图片的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            MediaStore.Images.Media.SIZE,           //图片的大小，long型  132492
            MediaStore.Images.Media.WIDTH,          //图片的宽度，int型  1920
            MediaStore.Images.Media.HEIGHT,         //图片的高度，int型  1080
            MediaStore.Images.Media.ORIENTATION,    //图片方向
            MediaStore.Images.Media.MIME_TYPE,      //图片的类型     image/jpeg
            MediaStore.Images.Media.DATE_ADDED};    //图片被添加的时间，long型  1450518608


    private FragmentActivity activity;
    private OnDataLoadedListener loadedListener;                     //图片加载完成的回调接口
    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks;
    private ArrayList<ImageFolder> imageFolders = new ArrayList<>();   //所有的图片文件夹

    /**
     * @param activity       用于初始化LoaderManager，需要兼容到2.3
     * @param path           指定扫描的文件夹目录，可以为 null，表示扫描所有图片
     * @param loadedListener 图片加载完成的监听
     */
    public ImageDataSource(FragmentActivity activity, String path, OnDataLoadedListener loadedListener) {
        this.activity = activity;
        this.loadedListener = loadedListener;

        LoaderManager loaderManager = activity.getSupportLoaderManager();
        if (path == null) {
            loaderManager.initLoader(LOADER_ALL, null, this);//加载所有的图片
        } else {
            //加载指定目录的图片
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
        if (this.loaderCallbacks != null) {
            loaderCallbacks.onCreateLoader(id, args);
        }
        CursorLoader cursorLoader = null;
        //扫描所有图片
        if (id == LOADER_ALL)
            cursorLoader = new CursorLoader(activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, null, null, IMAGE_PROJECTION[6] + " DESC");
        //扫描某个图片文件夹
        if (id == LOADER_CATEGORY)
            cursorLoader = new CursorLoader(activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, IMAGE_PROJECTION[1] + " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[6] + " DESC");
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
                ArrayList<ImageItem> allImages = new ArrayList<>();   //所有图片的集合,不分文件夹
                if (!data.moveToFirst()) {
                    return;
                }
                while (data.moveToNext()) {
                    //查询数据
                    // TODO: 2018/2/27 可能需要过滤MIME TYPE 服务器不支持某些格式
                    String imageName = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                    String imagePath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));

                    File file = new File(imagePath);
                    if (!file.exists() || file.length() <= 0) {
                        continue;
                    }
                    long imageSize = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                    int imageWidth = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                    int imageHeight = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                    int orientation = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));
                    String imageMimeType = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[6]));
                    long imageAddTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[7]));

                    //封装实体
                    ImageItem imageItem = new ImageItem();
                    imageItem.name = imageName;
                    imageItem.path = imagePath;
                    imageItem.size = imageSize;
                    imageItem.width = imageWidth;
                    imageItem.height = imageHeight;
                    imageItem.orientation = orientation;
                    imageItem.mimeType = imageMimeType;
                    imageItem.addTime = imageAddTime;
                    allImages.add(imageItem);
                    //根据父路径分类存放图片
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

                //防止没有图片报异常
                if (data.getCount() > 0 && allImages.size() > 0) {
                    //构造所有图片的集合
                    ImageFolder allImagesFolder = new ImageFolder();
                    allImagesFolder.name = activity.getResources().getString(R.string.all_images);
                    allImagesFolder.path = "/";
                    allImagesFolder.cover = allImages.get(0);
                    allImagesFolder.images = allImages;
                    imageFolders.add(0, allImagesFolder);  //确保第一条是所有图片
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
