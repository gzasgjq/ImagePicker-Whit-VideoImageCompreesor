package com.lzy.imagepicker;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.lzy.imagepicker.bean.ImageFolder;

import java.util.ArrayList;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/06/25 12:25
 * 类描述：$INTRO$
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class DataSource implements LoaderManager.LoaderCallbacks<ArrayList<ImageFolder>> {
    private static final int LOADER_ALL = 0;         //加载所有图片
    private static final int LOADER_CATEGORY = 1;    //分类加载图片

    private FragmentActivity activity;
    private OnDataLoadedListener loadedListener;                     //视频加载完成的回调接口
    private @ImagePicker.MediaType
    int mediaType;

    /**
     * @param activity       用于初始化LoaderManager，需要兼容到2.3
     * @param path           指定扫描的文件夹目录，可以为 null，表示扫描所有视频
     * @param loadedListener 视频加载完成的监听
     */
    public DataSource(FragmentActivity activity, @ImagePicker.MediaType int mediaType, String path, OnDataLoadedListener loadedListener) {
        this.activity = activity;
        this.loadedListener = loadedListener;
        this.mediaType = mediaType;

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

    @Override
    public Loader<ArrayList<ImageFolder>> onCreateLoader(int id, Bundle args) {
        MediaItemLoader loader = null;
        //扫描所有图片
        if (id == LOADER_ALL)
            loader = new MediaItemLoader(activity, mediaType, null);
        //扫描某个图片文件夹
        if (id == LOADER_CATEGORY)
            loader = new MediaItemLoader(activity, mediaType, MediaItemLoader.IMAGE_PROJECTION[1] + " like '%" + args.getString("path") + "%'");
        return loader;
    }

    // loader完成查询时调用，通常用于在查询到的cursor中提取数据
    @Override
    public void onLoadFinished(Loader<ArrayList<ImageFolder>> loader, ArrayList<ImageFolder> data) {
        //回调接口，通知视频数据准备完成
        if (loadedListener != null && data != null && !data.isEmpty()) {
            ImagePicker.getInstance().setImageFolders(data);
            loadedListener.onDatasLoaded(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ImageFolder>> loader) {
//        System.out.println("--------");
    }

}
