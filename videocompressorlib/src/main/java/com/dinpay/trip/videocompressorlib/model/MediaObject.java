package com.dinpay.trip.videocompressorlib.model;


import com.dinpay.trip.videocompressorlib.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;


@SuppressWarnings("serial")
public class MediaObject implements Serializable {

    /**
     * 拍摄
     */
    public final static int MEDIA_PART_TYPE_RECORD = 0;
    /**
     * 导入视频
     */
    public final static int MEDIA_PART_TYPE_IMPORT_VIDEO = 1;
    /**
     * 导入图片
     */
    public final static int MEDIA_PART_TYPE_IMPORT_IMAGE = 2;
    /**
     * 使用系统拍摄mp4
     */
    public final static int MEDIA_PART_TYPE_RECORD_MP4 = 3;
    /**
     * 默认最大时长
     */
    public final static int DEFAULT_MAX_DURATION = 10 * 1000;
    /**
     * 默认码率
     */
    public final static int DEFAULT_VIDEO_BITRATE = 800;

    /**
     * 视频最大时长，默认10秒
     */
    private int mMaxDuration;
    /**
     * 视频目录
     */
    private String mOutputDirectory;
    /**
     * 对象文件
     */
    private String mOutputObjectPath;
    /**
     * 视频码率
     */
    private int mVideoBitrate;
    /**
     * 最终视频输出路径
     */
    private String mOutputVideoPath;
    /**
     * 最终视频截图输出路径
     */
    private String mOutputVideoThumbPath;
    /**
     * 文件夹、文件名
     */
    private String mKey;

    private String outputTempVideoPath;

    public MediaObject(String key, String path) {
        this(key, path, DEFAULT_VIDEO_BITRATE);
    }

    public MediaObject(String key, String path, int videoBitrate) {
        this.mKey = key;
        this.mOutputDirectory = path;
        this.mVideoBitrate = videoBitrate;
        this.mOutputObjectPath = mOutputDirectory + File.separator + mKey + ".obj";
        this.mOutputVideoPath = mOutputDirectory + ".mp4";
        this.mOutputVideoThumbPath = mOutputDirectory + File.separator + mKey + ".jpg";
        this.mMaxDuration = DEFAULT_MAX_DURATION;
        this.outputTempVideoPath = mOutputDirectory + File.separator + mKey + "_temp.mp4";
    }


    public String getBaseName(){
        return mKey;
    }
    /**
     * 获取视频码率
     */
    public int getVideoBitrate() {
        return mVideoBitrate;
    }

    /**
     * 获取视频最大长度
     */
    public int getMaxDuration() {
        return mMaxDuration;
    }

    /**
     * 设置最大时长，必须大于1秒
     */
    public void setMaxDuration(int duration) {
        if (duration >= 1000) {
            mMaxDuration = duration;
        }
    }

    /**
     * 获取视频临时文件夹
     */
    public String getOutputDirectory() {
        return mOutputDirectory;
    }

    /**
     * 获取视频临时输出播放
     */
    public String getOutputTempVideoPath() {
        return outputTempVideoPath;
    }

    public void setOutputTempVideoPath(String path) {
        this.outputTempVideoPath = path;
    }

    public String getOutputTempTranscodingVideoPath() {
        return mOutputDirectory +
                File.separator + mKey + ".mp4";
    }


    /**
     * 获取视频信息春促路径
     */
    public String getObjectFilePath() {
//        if (StringUtils.isEmpty(mOutputObjectPath)) {
            File f = new File(mOutputVideoPath);
            String obj = mOutputDirectory + File.separator + f.getName() + ".obj";
            mOutputObjectPath = obj;
//        }
        return mOutputObjectPath;
    }

    /**
     * 获取视频最终输出地址
     */
    public String getOutputVideoPath() {
        return mOutputVideoPath;
    }

    /**
     * 获取视频截图最终输出地址
     */
    public String getOutputVideoThumbPath() {
        return mOutputVideoThumbPath;
    }


    public static class MediaPart implements Serializable {

        /**
         * 索引
         */
        public int index;
        /**
         * 视频路径
         */
        public String mediaPath;
        /**
         * 音频路径
         */
        public String audioPath;
        /**
         * 临时视频路径
         */
        public String tempMediaPath;
        /**
         * 临时音频路径
         */
        public String tempAudioPath;
        /**
         * 截图路径
         */
        public String thumbPath;
        /**
         * 存放导入的视频和图片
         */
        public String tempPath;
        /**
         * 类型
         */
        public int type = MEDIA_PART_TYPE_RECORD;
        /**
         * 剪切视频（开始时间）
         */
        public int cutStartTime;
        /**
         * 剪切视频（结束时间）
         */
        public int cutEndTime;
        /**
         * 分段长度
         */
        public int duration;
        /**
         * 总时长中的具体位置
         */
        public int position;
        /**
         * 0.2倍速-3倍速（取值2~30）
         */
        public int speed = 10;
        /**
         * 摄像头
         */
        public int cameraId;
        /**
         * 视频尺寸
         */
        public int yuvWidth;
        /**
         * 视频高度
         */
        public int yuvHeight;
        public transient boolean remove;
        public transient long startTime;
        public transient long endTime;
        public transient FileOutputStream mCurrentOutputVideo;
        public transient FileOutputStream mCurrentOutputAudio;
        public transient volatile boolean recording;

        public MediaPart() {

        }

        public void delete() {
            FileUtils.deleteFile(mediaPath);
            FileUtils.deleteFile(audioPath);
            FileUtils.deleteFile(thumbPath);
            FileUtils.deleteFile(tempMediaPath);
            FileUtils.deleteFile(tempAudioPath);
        }

        /**
         * 写入音频数据
         */
        public void writeAudioData(byte[] buffer) throws IOException {
            if (mCurrentOutputAudio != null)
                mCurrentOutputAudio.write(buffer);
        }

        /**
         * 写入视频数据
         */
        public void writeVideoData(byte[] buffer) throws IOException {
            if (mCurrentOutputVideo != null)
                mCurrentOutputVideo.write(buffer);
        }

        public void prepare() {
            try {
                mCurrentOutputVideo = new FileOutputStream(mediaPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            prepareAudio();
        }

        public void prepareAudio() {
            try {
                mCurrentOutputAudio = new FileOutputStream(audioPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public int getDuration() {
            return duration > 0 ? duration : (int) (System.currentTimeMillis() - startTime);
        }

        public void stop() {
            if (mCurrentOutputVideo != null) {
                try {
                    mCurrentOutputVideo.flush();
                    mCurrentOutputVideo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCurrentOutputVideo = null;
            }

            if (mCurrentOutputAudio != null) {
                try {
                    mCurrentOutputAudio.flush();
                    mCurrentOutputAudio.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mCurrentOutputAudio = null;
            }
        }

    }

}
