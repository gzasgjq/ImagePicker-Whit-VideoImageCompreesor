package com.gjq.demo.activites;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.gjq.demo.cropvideo.tools.VideoUtils;
import com.gjq.demo.R;
import com.gjq.demo.videocompressorlib.FFmpegBridge;
import com.gjq.demo.videocompressorlib.LocalMediaCompress;
import com.gjq.demo.videocompressorlib.model.BaseMediaBitrateConfig;
import com.gjq.demo.videocompressorlib.model.LocalMediaConfig;
import com.gjq.demo.videocompressorlib.model.VBRMode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CropVideoActivity extends BaseActivity {
    private String TAG = "CropVideoActivity";

    private static final int REQUESTCODE_PICK = 12;
    private static final int REQUESTCODE_CROP = 203;

    private VideoView videoView;
    private TextView tvInfo;
    private boolean isCompressing = false;

    public static void start(Context context) {
        Intent starter = new Intent(context, CropVideoActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_video);
        findViewById(R.id.btn_crop_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("video/*");//选取所有的音乐类型 *有mp3、wav、mid等
                try {
                    startActivityForResult(intent, REQUESTCODE_PICK);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(CropVideoActivity.this, R.string.gallery_invalid, Toast.LENGTH_SHORT).show();
                } catch (SecurityException e) {
                    Log.e(TAG, "onClick: Error", e);
                }
            }
        });
        findViewById(R.id.btn_crop_select_doc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");//选取所有的音乐类型 *有mp3、wav、mid等
                try {
                    startActivityForResult(intent, REQUESTCODE_PICK);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(CropVideoActivity.this, R.string.gallery_invalid, Toast.LENGTH_SHORT).show();
                } catch (SecurityException e) {
                    Log.e(TAG, "onClick: Error", e);
                }
            }
        });
        videoView = findViewById(R.id.vv_crop_perview);
        tvInfo = findViewById(R.id.tv_crop_info);
        tvInfo.setMovementMethod(new ScrollingMovementMethod());
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                tvInfo.append("\n VideoDuration: " + mp.getDuration());
                tvInfo.append("\n VideoWidth: " + mp.getVideoWidth());
                tvInfo.append("\n VideoHeight: " + mp.getVideoHeight());
            }
        });
//        File zipFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/arm64-v8a.zip");
//        if (GetPhoneInfoUtil.getCpuArchitecture() && zipFile.exists()) {
//            File soLibs = new File(getFilesDir(), "libs");
//            try {
//                upZipFile(zipFile, soLibs.getAbsolutePath());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            FFmpegBridge.load(getFilesDir().getAbsolutePath() + "/libs/arm64-v8a");
//        } else
//            FFmpegBridge.load();
    }

    private void upZipFile(File zipFile, String folderPath) throws IOException {
        ZipFile zfile = new ZipFile(zipFile);
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                Log.d(TAG, "ze.getName() = " + ze.getName());
                String dirstr = folderPath + ze.getName();
                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                Log.d(TAG, "str = " + dirstr);
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            Log.d(TAG, "ze.getName() = " + ze.getName());
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                try {
                    substr = new String(substr.getBytes("8859_1"), "GB2312");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ret = new File(ret, substr);

            }
            Log.d(TAG, "1ret = " + ret);
            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            try {
                substr = new String(substr.getBytes("8859_1"), "GB2312");
                Log.d(TAG, "substr = " + substr);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ret = new File(ret, substr);
            Log.d(TAG, "2ret = " + ret);
            return ret;
        }
        return ret;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;
        if (requestCode == REQUESTCODE_PICK) {
            String filePath = VideoUtils.filePathFromIntent(this, data.getData());
            if (TextUtils.isEmpty(filePath)) {
                return;
            }
//            CropActivity.start(this, data.getData(), REQUESTCODE_CROP);

//            videoView.setVideoURI(data.getData());
//            videoView.start();
//            tvInfo.setText("filePath: " + filePath);

            doCompers(data);
        } else if (requestCode == REQUESTCODE_CROP) {
            videoView.setVideoPath(data.getStringExtra("path"));
            videoView.start();
//            tvInfo.setText("filePath: " + filePath);
        }
    }

    private void getMediaInfo(String path) {
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
            tvInfo.append("视频流MIME: " + mediaFormat.getString(MediaFormat.KEY_MIME) + "\n");
//            tvInfo.append("视频流FPS: " + mediaFormat.getInteger(MediaFormat.KEY_FRAME_RATE) + "\n");
            videoExtractor.selectTrack(videoTrackIndex);
        }
        videoExtractor.release();
    }

    //获取指定类型媒体文件所在轨道
    private int getMediaTrackIndex(MediaExtractor videoExtractor, String MEDIA_TYPE) {
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

    @Override
    public void onBackPressed() {
        if (!isCompressing)
            super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        FFmpegBridge.jxCMDExit();
        super.onDestroy();
    }

    private void doCompers(Intent data) {
        Uri uri = data.getData();
        String[] proj = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.MIME_TYPE, MediaStore.Video.Media.RESOLUTION, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.WIDTH};

        Cursor cursor = getContentResolver().query(uri, proj, null,
                null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int _data_num = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int mime_type_num = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE);
            String _data = cursor.getString(_data_num);
            String mime_type = cursor.getString(mime_type_num);
            if (!TextUtils.isEmpty(mime_type) && mime_type.contains("video") && !TextUtils.isEmpty(_data)) {
//                获取视频流信息
                getMediaInfo(_data);
//                int iRate = 0;
//                float fScale = 1.5f;
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(_data);
                String videoWidth = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                String videoHeight = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                float scale = Math.min(Float.parseFloat(videoWidth), Float.parseFloat(videoHeight)) / 540f;
                LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
                LocalMediaConfig config = buidler
                        .setVideoPath(_data)
                        .setFramerate(30)
                        .captureThumbnailsTime(1)
                        .doH264Compress(new VBRMode(3000, 1800).setVelocity(BaseMediaBitrateConfig.Velocity.SUPERFAST))
                        .setScale(scale)
                        .build();
                tvInfo.append(String.format("视频MIME：%s\n", mime_type));
                tvInfo.append(String.format("压缩前大小：%.2f mb\n", cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)) / 1024f / 1024f));
                tvInfo.append(String.format("压缩前分辨率：%s * %s\n", videoHeight, videoWidth));
                tvInfo.append("压缩中...\n");
                final long startTime = System.currentTimeMillis();
                isCompressing = true;
                FFmpegBridge.setLogEnable(true);
                new LocalMediaCompress(Environment.getExternalStorageDirectory() + "/demo/", config)
                        .startCompressAsyn((ret, mediaObject) -> runOnUiThread(() -> {
                            isCompressing = false;
                            tvInfo.append("压缩结束\n");
                            tvInfo.append(String.format(Locale.CHINA, "压缩后大小：%.2f mb", new File(mediaObject.getOutputTempTranscodingVideoPath()).length() / 1024f / 1024f));
                            tvInfo.append(String.format(Locale.CHINA, "耗时：%s\n", (System.currentTimeMillis() - startTime) / 1000));
                            videoView.setVideoPath(mediaObject.getOutputTempTranscodingVideoPath());
                            videoView.start();
                            tvInfo.append("filePath: " + mediaObject.getOutputTempTranscodingVideoPath() + "\n");
                        }));
//                new Thread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                tvInfo.append("压缩中...\n");
//                            }
//                        });
//                        final long startTime = System.currentTimeMillis();
//                        final OnlyCompressOverBean onlyCompressOverBean = new LocalMediaCompress(config).startCompress();
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                tvInfo.append("压缩结束\n");
//                                tvInfo.append(String.format("压缩后大小：%.2f mb", new File(onlyCompressOverBean.getVideoPath()).length() / 1024f / 1024f));
//                                tvInfo.append(String.format("耗时：%s\n", (System.currentTimeMillis() - startTime) / 1000));
//                                videoView.setVideoPath(onlyCompressOverBean.getVideoPath());
//                                videoView.start();
//                                tvInfo.append("filePath: " + onlyCompressOverBean.getVideoPath());
//                            }
//                        });
////                        Intent intent = new Intent(MainActivity.this, SendSmallVideoActivity.class);
////                        intent.putExtra(MediaRecorderActivity.VIDEO_URI, onlyCompressOverBean.getVideoPath());
////                        intent.putExtra(MediaRecorderActivity.VIDEO_SCREENSHOT, onlyCompressOverBean.getPicPath());
////                        startActivity(intent);
//                    }
//                }).start();
            } else {
                Toast.makeText(this, "选择的不是视频或者地址错误,也可能是这种方式定制神机取不到！", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
