package com.dinpay.trip.videocompressorlib;


import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by jianxi on 2017/5/12.
 * https://github.com/mabeijianxi
 * mabeijianxi@gmail.com
 */

public class FFmpegBridge {

    private static final String TAG = "FFmpegBridge";

    static {
        System.loadLibrary("avutil");
        System.loadLibrary("fdk-aac");
        System.loadLibrary("avcodec");
        System.loadLibrary("avformat");
        System.loadLibrary("swscale");
        System.loadLibrary("swresample");
        System.loadLibrary("avfilter");
        System.loadLibrary("jx_ffmpeg_jni");
        Log.w(TAG, "loadInterLibSuccess: ");
    }

    public static void load(String path) {
        System.load(path + "/libavutil.so");
        System.load(path + "/libfdk-aac.so");
        System.load(path + "/libavcodec.so");
        System.load(path + "/libavformat.so");
        System.load(path + "/libswscale.so");
        System.load(path + "/libswresample.so");
        System.load(path + "/libavfilter.so");
        System.load(path + "/libjx_ffmpeg_jni.so");
        Log.w(TAG, "loadRawLibSuccess: ");
    }

    /**
     * 结束录制并且转码保存完成
     */
    public static final int ALL_RECORD_END = 1;


    public final static int ROTATE_0_CROP_LF = 0;
    /**
     * 旋转90度剪裁左上
     */
    public final static int ROTATE_90_CROP_LT = 1;
    /**
     * 暂时没处理
     */
    public final static int ROTATE_180 = 2;
    /**
     * 旋转270(-90)裁剪左上，左右镜像
     */
    public final static int ROTATE_270_CROP_LT_MIRROR_LR = 3;


    /**
     * @return 返回ffmpeg的编译信息
     */
    public static native String getFFmpegConfig();

    public static native String getFFmpegCodevConfig();

    /**
     * 命令形式运行ffmpeg
     *
     * @param cmd
     * @return 返回0表示成功
     */
    public static native int jxCMDRun(String cmd[]);

    /**
     * 编码一帧视频，暂时只能编码yv12视频
     *
     * @param data
     * @return
     */
    public static native int encodeFrame2H264(byte[] data);


    /**
     * 编码一帧音频,暂时只能编码pcm音频
     *
     * @param data
     * @return
     */
    public static native int encodeFrame2AAC(byte[] data);

    /**
     * 录制结束
     *
     * @return
     */
    public static native int recordEnd();

    /**
     * 初始化
     *
     * @param debug
     * @param logUrl
     */
    public static native void initJXFFmpeg(boolean debug, String logUrl);


    public static native void nativeRelease();

    public static native void jxCMDExit();

    /**
     * @param mediaBasePath 视频存放目录
     * @param mediaName     视频名称
     * @param filter        旋转镜像剪切处理
     * @param in_width      输入视频宽度
     * @param in_height     输入视频高度
     * @param out_height    输出视频高度
     * @param out_width     输出视频宽度
     * @param frameRate     视频帧率
     * @param bit_rate      视频比特率
     * @return
     */
    public static native int prepareJXFFmpegEncoder(String mediaBasePath, String mediaName, int filter, int in_width, int in_height, int out_width, int out_height, int frameRate, long bit_rate);


    /**
     * 命令形式执行
     *
     * @param cmd
     */
    public static int jxFFmpegCMDRun(String cmd) {
        String regulation = "[ \\t]+";
        final String[] split = cmd.split(regulation);
        String logUrl = Environment.getExternalStorageDirectory().getAbsolutePath() + "/log.txt";
        File file = new File(logUrl);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        initJXFFmpeg(true, file.getAbsolutePath());
        return jxCMDRun(split);
    }

    private static OnExecListener listener;
    /**
     * 调用底层执行
     * @param argc
     * @param argv
     * @return
     */
    public static native int exec(int argc, String[] argv);

    public static void onExecuted(int ret) {
        if (listener != null) {
            listener.onExecuted(ret);
        }
    }

    /**
     * 执行ffmoeg命令
     * @param cmds
     * @param listener
     */
    public static void exec(String cmds, OnExecListener listener) {
        String regulation = "[ \\t]+";
        final String[] split = cmds.split(regulation);
        FFmpegBridge.listener = listener;
        exec(split.length, split);
    }

    /**
     * 执行完成/错误 时的回调接口
     */
    public interface OnExecListener {
        void onExecuted(int ret);
    }
}
