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
     * 命令形式运行ffmpeg
     *
     * @param cmd
     * @return 返回0表示成功
     */
    public static native int jxCMDRun(String cmd[]);

    /**
     * 初始化
     *
     * @param debug
     * @param logUrl
     */
    public static native void initJXFFmpeg(boolean debug, String logUrl);

    public static native void jxCMDExit();

    /**
     * 命令形式执行
     *
     * @param cmd
     */
    public static int jxFFmpegCMDRun(String cmd) {
        String regulation = "[ \\t]+";
        final String[] split = cmd.split(regulation);
        initLog();
        return jxCMDRun(split);
    }

    private static void initLog() {
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
        initLog();
        exec(split.length, split);
    }

    /**
     * 执行完成/错误 时的回调接口
     */
    public interface OnExecListener {
        void onExecuted(int ret);
    }
}
