package com.gjq.demo.commom;

import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v4.os.OperationCanceledException;
import android.text.TextUtils;

import com.gjq.demo.R;
import com.gjq.demo.beans.ImageFolder;
import com.gjq.demo.beans.ImageItem;

import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/06/25 11:39
 * 类描述：$INTRO$
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class MediaItemLoader extends AsyncTaskLoader<ArrayList<ImageFolder>> {
    static final String[] IMAGE_PROJECTION = {     //查询图片需要的数据列
            MediaStore.Images.Media.DISPLAY_NAME,   //图片的显示名称  aaa.jpg
            MediaStore.Images.Media.DATA,           //图片的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            MediaStore.Images.Media.SIZE,           //图片的大小，long型  132492
            MediaStore.Images.Media.WIDTH,          //图片的宽度，int型  1920
            MediaStore.Images.Media.HEIGHT,         //图片的高度，int型  1080
            MediaStore.Images.Media.ORIENTATION,    //图片方向
            MediaStore.Images.Media.MIME_TYPE,      //图片的类型     image/jpeg
            MediaStore.Images.Media.DATE_ADDED};    //图片被添加的时间，long型  1450518608

    private static final String[] VIDEO_PROJECTION = {     //查询视频需要的数据列
            MediaStore.Video.Media.DISPLAY_NAME,   //视频的显示名称  aaa.jpg
            MediaStore.Video.Media.DATA,           //视频的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            MediaStore.Video.Media.SIZE,           //视频的大小，long型  132492
            MediaStore.Video.Media.WIDTH,          //视频的宽度，int型  1920
            MediaStore.Video.Media.HEIGHT,         //视频的高度，int型  1080
            MediaStore.Video.Media.DURATION,       //视频的时长，long型
            MediaStore.Video.Media.MIME_TYPE,      //视频的类型     video/mp4
            MediaStore.Video.Media.DATE_ADDED};    //视频被添加的时间，long型  1450518608

    private @MediaType
    int mMediaType;
    String mSelection;
    private ArrayList<ImageFolder> mDatas;
    private CancellationSignal mCancellationSignal;

    /* Runs on a worker thread */
    @Override
    public ArrayList<ImageFolder> loadInBackground() {
        synchronized (this) {
            if (isLoadInBackgroundCanceled()) {
                throw new OperationCanceledException();
            }
            mCancellationSignal = new CancellationSignal();
        }
        try {
            ArrayList<ImageFolder> datas;
            switch (mMediaType) {
                case MediaType.MEDIA_MIX:
                    datas = getMixData();
                    break;
                case MediaType.MEDIA_VIDEO:
                    datas = getVideoData();
                    break;
                case MediaType.MEDIA_IMAGE:
                default:
                    datas = getImageData();
                    break;
            }
            return datas;
        } finally {
            synchronized (this) {
                mCancellationSignal = null;
            }
        }
    }

    private ArrayList<ImageFolder> getImageData() {
        Cursor mImageCursor = getCursor(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION);
        if (mImageCursor != null) {
            ArrayList<ImageItem> allImages = new ArrayList<>();   //所有图片的集合,不分文件夹
            if (!mImageCursor.moveToFirst()) {
                return null;
            }
            ArrayList<ImageFolder> imageFolders = new ArrayList<>();
            while (mImageCursor.moveToNext()) {
                //查询数据
                String imageName = mImageCursor.getString(mImageCursor.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                String imagePath = mImageCursor.getString(mImageCursor.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                File file = new File(imagePath);
                if (!file.exists() || file.length() <= 0) {
                    continue;
                }
                long imageSize = mImageCursor.getLong(mImageCursor.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                int imageWidth = mImageCursor.getInt(mImageCursor.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                int imageHeight = mImageCursor.getInt(mImageCursor.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                int orientation = mImageCursor.getInt(mImageCursor.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));
                String imageMimeType = mImageCursor.getString(mImageCursor.getColumnIndexOrThrow(IMAGE_PROJECTION[6]));
                if (TextUtils.isEmpty(imageMimeType)) continue;
                long imageAddTime = mImageCursor.getLong(mImageCursor.getColumnIndexOrThrow(IMAGE_PROJECTION[7]));
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
            if (mImageCursor.getCount() > 0 && allImages.size() > 0) {
                //构造所有图片的集合
                ImageFolder allImagesFolder = new ImageFolder();
                allImagesFolder.name = getContext().getResources().getString(R.string.all_images);
                allImagesFolder.path = "/";
                allImagesFolder.cover = allImages.get(0);
                allImagesFolder.images = allImages;
                imageFolders.add(0, allImagesFolder);  //确保第一条是所有图片
            }
            mImageCursor.close();
            return imageFolders;
        }
        return null;
    }

    private ArrayList<ImageFolder> getVideoData() {
        Cursor mVideoCursor = getCursor(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION);
        if (mVideoCursor != null) {
            ArrayList<ImageItem> allImages = new ArrayList<>();   //所有视频的集合,不分文件夹
            if (!mVideoCursor.moveToFirst()) {
                return null;
            }
            ArrayList<ImageFolder> imageFolders = new ArrayList<>();
            while (mVideoCursor.moveToNext()) {
                //查询数据
                String imageName = mVideoCursor.getString(mVideoCursor.getColumnIndexOrThrow(VIDEO_PROJECTION[0]));
                String imagePath = mVideoCursor.getString(mVideoCursor.getColumnIndexOrThrow(VIDEO_PROJECTION[1]));

                File file = new File(imagePath);
                if (!file.exists() || file.length() <= 0) {
                    continue;
                }
                long imageSize = mVideoCursor.getLong(mVideoCursor.getColumnIndexOrThrow(VIDEO_PROJECTION[2]));
                int imageWidth = mVideoCursor.getInt(mVideoCursor.getColumnIndexOrThrow(VIDEO_PROJECTION[3]));
                int imageHeight = mVideoCursor.getInt(mVideoCursor.getColumnIndexOrThrow(VIDEO_PROJECTION[4]));
                long duration = mVideoCursor.getLong(mVideoCursor.getColumnIndexOrThrow(VIDEO_PROJECTION[5]));
                String imageMimeType = mVideoCursor.getString(mVideoCursor.getColumnIndexOrThrow(VIDEO_PROJECTION[6]));
                if (TextUtils.isEmpty(imageMimeType)) continue;
                long imageAddTime = mVideoCursor.getLong(mVideoCursor.getColumnIndexOrThrow(VIDEO_PROJECTION[7]));
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
            if (mVideoCursor.getCount() > 0 && allImages.size() > 0) {
                //构造所有视频的集合
                ImageFolder allImagesFolder = new ImageFolder();
                allImagesFolder.name = getContext().getResources().getString(R.string.all_videos);
                allImagesFolder.path = "/";
                allImagesFolder.cover = allImages.get(0);
                allImagesFolder.images = allImages;
                imageFolders.add(0, allImagesFolder);  //确保第一条是所有视频
            }
            mVideoCursor.close();
            return imageFolders;
        }
        return null;
    }

    private ArrayList<ImageFolder> getMixData() {
        Cursor mVideoCursor = getCursor(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION);
        Cursor mImageCursor = getCursor(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION);
        if (mVideoCursor != null && mImageCursor != null) {
            if (!mVideoCursor.moveToFirst()) return null;
            if (!mImageCursor.moveToFirst()) return null;

            ArrayList<ImageFolder> imageFolders = new ArrayList<>();

            MergeCursor mergeCursor = new MergeCursor(new Cursor[]{mVideoCursor, mImageCursor});
            SortCursor mixCursor = new SortCursor(mergeCursor, MediaStore.MediaColumns.DATE_ADDED);
            mixCursor.moveToFirst();
            imageFolders.clear();
            ArrayList<ImageItem> allImages = new ArrayList<>();   //所有视频的集合,不分文件夹
            if (!mixCursor.moveToFirst()) {
                return null;
            }
            while (mixCursor.moveToNext()) {
                String imageMimeType = mixCursor.getString(mixCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE));
                if (imageMimeType == null) continue;
                boolean isVideo = imageMimeType.startsWith("video");
                //查询数据
                String imageName = mixCursor.getString(mixCursor.getColumnIndexOrThrow(
                        isVideo ? VIDEO_PROJECTION[0]
                                : IMAGE_PROJECTION[0]
                ));
                String imagePath = mixCursor.getString(mixCursor.getColumnIndexOrThrow(
                        isVideo ? VIDEO_PROJECTION[1]
                                : IMAGE_PROJECTION[1]
                ));

                File file = new File(imagePath);
                if (!file.exists() || file.length() <= 0) {
                    continue;
                }
                long imageSize = mixCursor.getLong(mixCursor.getColumnIndexOrThrow(
                        isVideo ? VIDEO_PROJECTION[2]
                                : IMAGE_PROJECTION[2]
                ));
                int imageWidth = mixCursor.getInt(mixCursor.getColumnIndexOrThrow(
                        isVideo ? VIDEO_PROJECTION[3]
                                : IMAGE_PROJECTION[3]
                ));
                int imageHeight = mixCursor.getInt(mixCursor.getColumnIndexOrThrow(
                        isVideo ? VIDEO_PROJECTION[4]
                                : IMAGE_PROJECTION[4]
                ));

                long duration = 0;
                int orientation = 0;
                if (isVideo) {
                    duration = mixCursor.getLong(mixCursor.getColumnIndexOrThrow(VIDEO_PROJECTION[5]));
                } else {
                    orientation = mixCursor.getInt(mixCursor.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));
                }

                long imageAddTime = mixCursor.getLong(mixCursor.getColumnIndexOrThrow(
                        isVideo ? VIDEO_PROJECTION[7]
                                : IMAGE_PROJECTION[7]
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
                allImagesFolder.name = getContext().getResources().getString(R.string.all_media);
                allImagesFolder.path = "/";
                allImagesFolder.cover = allImages.get(0);
                allImagesFolder.images = allImages;
                imageFolders.add(0, allImagesFolder);  //确保第一条是所有视频
            }
            mVideoCursor.close();
            mImageCursor.close();
            mergeCursor.close();
            mixCursor.close();
            return imageFolders;
        }
        return null;
    }

    private Cursor getCursor(Uri externalContentUri, String[] imageProjection) {
        boolean isVideo = imageProjection == IMAGE_PROJECTION;
        Cursor cursor = ContentResolverCompat.query(getContext().getContentResolver(),
                externalContentUri, imageProjection, mSelection, null,
                String.format("%s DESC", isVideo ? VIDEO_PROJECTION[VIDEO_PROJECTION.length - 1] : IMAGE_PROJECTION[IMAGE_PROJECTION.length - 1]),
                mCancellationSignal);
        if (cursor != null) {
            try {
                // Ensure the cursor window is filled.
                cursor.getCount();
            } catch (RuntimeException ex) {
                cursor.close();
                throw ex;
            }
        }
        return cursor;
    }

    @Override
    public void cancelLoadInBackground() {
        super.cancelLoadInBackground();

        synchronized (this) {
            if (mCancellationSignal != null) {
                mCancellationSignal.cancel();
            }
        }
    }

    /* Runs on the UI thread */
    @Override
    public void deliverResult(ArrayList<ImageFolder> data) {
        if (isReset()) {
            // An async query came in while the loader is stopped
//            if (data != null) {
//                data.close();
//            }
            return;
        }
//        Cursor oldCursor = mCursor;
//        mCursor = data;
        mDatas = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

//        if (oldCursor != null && oldCursor != data && !oldCursor.isClosed()) {
//            oldCursor.close();
//        }
    }

    public MediaItemLoader(@NonNull Context context) {
        super(context);
    }

    public MediaItemLoader(@NonNull Context context, @MediaType int mediaType,
                           @Nullable String selection) {
        super(context);
        this.mMediaType = mediaType;
        mSelection = selection;
    }

    /**
     * Starts an asynchronous load of the contacts list data. When the result is ready the callbacks
     * will be called on the UI thread. If a previous load has been completed and is still valid
     * the result may be passed to the callbacks immediately.
     * <p>
     * Must be called from the UI thread
     */
    @Override
    protected void onStartLoading() {
        if (mDatas != null) {
            deliverResult(mDatas);
        }
        if (takeContentChanged() || mDatas == null) {
            forceLoad();
        }
    }

    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }
//
//    @Override
//    public void onCanceled(ArrayList<ImageFolder> data) {
//        if (data != null) {
//            data.close();
//        }
//    }


    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        mDatas = null;
    }


    public @MediaType
    int getmMediaType() {
        return mMediaType;
    }

    public void setmMediaType(@MediaType int mMediaType) {
        this.mMediaType = mMediaType;
    }

    public String getmSelection() {
        return mSelection;
    }

    public void setmSelection(String mSelection) {
        this.mSelection = mSelection;
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        writer.print(prefix);
        writer.print("mMediaType=");
        writer.println(mMediaType);
//        writer.print(prefix); writer.print("mUri="); writer.println(mUri);
        writer.print(prefix);
//        writer.print("mProjection=");
//        writer.println(Arrays.toString(mProjection));
        writer.print(prefix);
        writer.print("mSelection=");
        writer.println(mSelection);
        writer.print(prefix);
//        writer.print("mSelectionArgs=");
//        writer.println(Arrays.toString(mSelectionArgs));
        writer.print(prefix);
//        writer.print("mSortOrder=");
//        writer.println(mSortOrder);
        writer.print(prefix);
        writer.print("mDatas=");
        writer.println(mDatas);
//        writer.print(prefix); writer.print("mContentChanged="); writer.println(mContentChanged);
    }
}
