package com.dinpay.trip.videocompressorlib;

import android.text.TextUtils;

import com.dinpay.trip.videocompressorlib.model.BaseMediaBitrateConfig;
import com.dinpay.trip.videocompressorlib.model.MediaObject;

import java.io.File;


/**
 * 视频录制抽象类
 */
public abstract class MediaRecorderBase {
    /**
     * 小视频高度
     */
    public static int SMALL_VIDEO_HEIGHT = 480;
    /**
     * 小视频宽度
     */
    public static int SMALL_VIDEO_WIDTH = 360;

    protected static int CAPTURE_THUMBNAILS_TIME = 1;


    protected BaseMediaBitrateConfig compressConfig;

    /**
     * 拍摄存储对象
     */
    protected MediaObject mMediaObject;

   /**
     * 视频码率
     */
    protected static int mVideoBitrate;

    private String mFrameRateCmd = "";

    public MediaRecorderBase() {

    }

    /**
     * 设置视频临时存储文件夹
     *
     * @param key  视频输出的名称，同目录下唯一，一般取系统当前时间
     * @param path 文件夹路径
     * @return 录制信息对象
     */
    public MediaObject setOutputDirectory(String key, String path) {
        if (StringUtils.isNotEmpty(path)) {
            File f = new File(path);
            if (f != null) {
                if (f.exists()) {
                    //已经存在，删除
                    if (f.isDirectory())
                        FileUtils.deleteDir(f);
                    else
                        FileUtils.deleteFile(f);
                }

                if (f.mkdirs()) {
                    mMediaObject = new MediaObject(key, path, mVideoBitrate);
                }
            }
        }
        return mMediaObject;
    }

    protected String getScaleWH() {

        return "";
    }


    protected Boolean doCompress(boolean mergeFlag) {
        if (compressConfig != null) {
            String vbr = " -vbr 4 ";
            if (compressConfig != null && compressConfig.getMode() == BaseMediaBitrateConfig.MODE.CBR) {
                vbr = "";
            }
            String scaleWH = getScaleWH();
            if (!TextUtils.isEmpty(scaleWH)) {
                scaleWH = "-s " + scaleWH;
            } else {
                scaleWH = "";
            }
            String cmd_transcoding = String.format("ffmpeg -threads 16 -i %s -c:v libx264 -profile:v high -level 3.1 -deblock 1:1 %s %s %s -c:a libfdk_aac %s %s %s %s",
                    mMediaObject.getOutputTempVideoPath(),
                    getBitrateModeCommand(compressConfig, "", false),
                    getBitrateCrfSize(compressConfig, "-crf 23", false),
                    getBitrateVelocity(compressConfig, "-preset:v fast", false),
                    vbr,
                    getFrameRateCmd(),
                    scaleWH,
                    mMediaObject.getOutputTempTranscodingVideoPath()
            );
//            FFmpegBridge.exec(cmd_transcoding, new FFmpegBridge.OnExecListener() {
//                @Override
//                public void onExecuted(int ret) {
//                    Log.e("DEMO", "onExecuted: " + ret);
//                }
//            });
            boolean transcodingFlag = FFmpegBridge.jxFFmpegCMDRun(cmd_transcoding) == 0;
            boolean captureFlag = FFMpegUtils.captureThumbnails(mMediaObject.getOutputTempTranscodingVideoPath(), mMediaObject.getOutputVideoThumbPath(), String.valueOf(CAPTURE_THUMBNAILS_TIME));
            FileUtils.deleteCacheFile(mMediaObject.getOutputDirectory());
            boolean result = mergeFlag && captureFlag && transcodingFlag;
            return result;
        } else {
            boolean captureFlag = FFMpegUtils.captureThumbnails(mMediaObject.getOutputTempVideoPath(), mMediaObject.getOutputVideoThumbPath(), String.valueOf(CAPTURE_THUMBNAILS_TIME));

            FileUtils.deleteCacheFile2TS(mMediaObject.getOutputDirectory());
            boolean result = captureFlag && mergeFlag;

            return result;
        }
    }
    protected void doCompressAsyn(boolean mergeFlag, FFmpegBridge.OnExecListener listener) {
        if (compressConfig != null) {
            String vbr = " -vbr 4 ";
            if (compressConfig.getMode() == BaseMediaBitrateConfig.MODE.CBR) {
                vbr = "";
            }
            String scaleWH = getScaleWH();
            if (!TextUtils.isEmpty(scaleWH)) {
                scaleWH = "-s " + scaleWH;
            } else {
                scaleWH = "";
            }
            String cmd_transcoding = String.format("ffmpeg -threads 16 -i %s -c:v libx264 -profile:v high -level 3.1 -deblock 1:1 %s %s %s -c:a libfdk_aac %s %s %s %s",
                    mMediaObject.getOutputTempVideoPath(),
                    getBitrateModeCommand(compressConfig, "", false),
                    getBitrateCrfSize(compressConfig, "-crf 23", false),
                    getBitrateVelocity(compressConfig, "-preset:v fast", false),
                    vbr,
                    getFrameRateCmd(),
                    scaleWH,
                    mMediaObject.getOutputTempTranscodingVideoPath()
            );
            FFmpegBridge.exec(cmd_transcoding, listener);
//            boolean transcodingFlag = FFmpegBridge.jxFFmpegCMDRun(cmd_transcoding) == 0;
//            boolean captureFlag = FFMpegUtils.captureThumbnails(mMediaObject.getOutputTempTranscodingVideoPath(), mMediaObject.getOutputVideoThumbPath(), String.valueOf(CAPTURE_THUMBNAILS_TIME));
//            FileUtils.deleteCacheFile(mMediaObject.getOutputDirectory());
//            return result;
        } else {
//            boolean captureFlag = FFMpegUtils.captureThumbnails(mMediaObject.getOutputTempVideoPath(), mMediaObject.getOutputVideoThumbPath(), String.valueOf(CAPTURE_THUMBNAILS_TIME));
            FileUtils.deleteCacheFile2TS(mMediaObject.getOutputDirectory());
        }
    }

    protected String getFrameRateCmd() {
        return mFrameRateCmd;
    }

    protected void setTranscodingFrameRate(int rate) {
        this.mFrameRateCmd = String.format(" -r %d", rate);
    }


    protected String getBitrateModeCommand(BaseMediaBitrateConfig config, String defualtCmd, boolean needSymbol) {
        String add = "";
        if (TextUtils.isEmpty(defualtCmd)) {
            defualtCmd = "";
        }
        if (config != null) {
            if (config.getMode() == BaseMediaBitrateConfig.MODE.VBR) {
                if (needSymbol) {
                    add = String.format(" -x264opts \"bitrate=%d:vbv-maxrate=%d\" ", config.getBitrate(), config.getMaxBitrate());
                } else {
                    add = String.format(" -x264opts bitrate=%d:vbv-maxrate=%d ", config.getBitrate(), config.getMaxBitrate());
                }
                return add;
            } else if (config.getMode() == BaseMediaBitrateConfig.MODE.CBR) {
                if (needSymbol) {
                    add = String.format(" -x264opts \"bitrate=%d:vbv-bufsize=%d:nal_hrd=cbr\" ", config.getBitrate(), config.getBufSize());
                } else {
                    add = String.format(" -x264opts bitrate=%d:vbv-bufsize=%d:nal_hrd=cbr ", config.getBitrate(), config.getBufSize());

                }
                return add;

            }
        }
        return defualtCmd;
    }

    protected String getBitrateCrfSize(BaseMediaBitrateConfig config, String defualtCmd, boolean nendSymbol) {
        if (TextUtils.isEmpty(defualtCmd)) {
            defualtCmd = "";
        }
        String add = "";
        if (config != null && config.getMode() == BaseMediaBitrateConfig.MODE.AUTO_VBR && config.getCrfSize() > 0) {
            if (nendSymbol) {
                add = String.format("-crf \"%d\" ", config.getCrfSize());
            } else {
                add = String.format("-crf %d ", config.getCrfSize());
            }
        } else {
            return defualtCmd;
        }
        return add;
    }

    protected String getBitrateVelocity(BaseMediaBitrateConfig config, String defualtCmd, boolean nendSymbol) {
        if (TextUtils.isEmpty(defualtCmd)) {
            defualtCmd = "";
        }
        String add = "";
        if (config != null && !TextUtils.isEmpty(config.getVelocity())) {
            if (nendSymbol) {
                add = String.format("-preset \"%s\" ", config.getVelocity());
            } else {
                add = String.format("-preset %s ", config.getVelocity());
            }
        } else {
            return defualtCmd;
        }
        return add;
    }
}
