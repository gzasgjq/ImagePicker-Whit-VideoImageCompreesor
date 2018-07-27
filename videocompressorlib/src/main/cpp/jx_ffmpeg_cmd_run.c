/**
 * Created by jianxi on 2017/6/4..
 * https://github.com/mabeijianxi
 * mabeijianxi@gmail.com
 */
#include "jx_ffmpeg_cmd_run.h"
#include "ffmpeg.h"
#include "jx_log.h"
#include "jx_ffmpeg_thread.h"


static JavaVM *jvm = NULL;
//java虚拟机
static jclass m_clazz = NULL;//当前类(面向java)

/**
 * 回调执行Java方法
 * 参看 Jni反射+Java反射
 */
void callJavaMethod(JNIEnv *env, jclass clazz,int ret) {
    if (clazz == NULL) {
        LOGE(JNI_DEBUG, "---------------clazz isNULL---------------");
        return;
    }
    //获取方法ID (I)V指的是方法签名 通过javap -s -public FFmpegCmd 命令生成
    jmethodID methodID = (*env)->GetStaticMethodID(env, clazz, "onExecuted", "(I)V");
    if (methodID == NULL) {
        LOGE(JNI_DEBUG, "---------------methodID isNULL---------------");
        return;
    }
    //调用该java方法
    (*env)->CallStaticVoidMethod(env, clazz, methodID,ret);
}

/**
 * c语言-线程回调
 */
static void ffmpeg_callback(int ret) {
    JNIEnv *env;
    //附加到当前线程从JVM中取出JNIEnv, C/C++从子线程中直接回到Java里的方法时  必须经过这个步骤
    (*jvm)->AttachCurrentThread(jvm, (void **) &env, NULL);
    callJavaMethod(env, m_clazz,ret);

    //完毕-脱离当前线程
    (*jvm)->DetachCurrentThread(jvm);
}

JNIEXPORT jint JNICALL
Java_com_gjq_demo_videocompressorlib_FFmpegBridge_exec(JNIEnv *env, jclass clazz, jint cmdnum, jobjectArray cmdline){
    //---------------------------------C语言 反射Java 相关----------------------------------------
    //在jni的c线程中不允许使用共用的env环境变量 但JavaVM在整个jvm中是共用的 可通过保存JavaVM指针,到时候再通过JavaVM指针取出JNIEnv *env;
    //ICS之前(你可把NDK sdk版本改成低于11) 可以直接写m_clazz = clazz;直接赋值,  然而ICS(sdk11) 后便改变了这一机制,在线程中回调java时 不能直接共用变量 必须使用NewGlobalRef创建全局对象
    //官方文档正在拼命的解释这一原因,参看:http://android-developers.blogspot.jp/2011/11/jni-local-reference-changes-in-ics.html
    (*env)->GetJavaVM(env, &jvm);
    m_clazz = (*env)->NewGlobalRef(env, clazz);
    //---------------------------------C语言 反射Java 相关----------------------------------------
    //---------------------------------java 数组转C语言数组----------------------------------------
    int i = 0;//满足NDK所需的C99标准
    char **argv = NULL;//命令集 二维指针
    jstring *strr = NULL;
    if (cmdline != NULL) {
        argv = (char **) malloc(sizeof(char *) * cmdnum);
        strr = (jstring *) malloc(sizeof(jstring) * cmdnum);

        for (i = 0; i < cmdnum; ++i) {//转换
            strr[i] = (jstring)(*env)->GetObjectArrayElement(env, cmdline, i);
            argv[i] = (char *) (*env)->GetStringUTFChars(env, strr[i], 0);
        }
    }
    //---------------------------------java 数组转C语言数组----------------------------------------
    //---------------------------------执行FFmpeg命令相关----------------------------------------
    //新建线程 执行ffmpeg 命令
    ffmpeg_thread_run_cmd(cmdnum, argv);
    //注册ffmpeg命令执行完毕时的回调
    ffmpeg_thread_callback(ffmpeg_callback);
    //---------------------------------执行FFmpeg命令相关----------------------------------------
    free(strr);
    return 0;
}

/**
 * 以命令行方式运行，返回0表示成功
 */
JNIEXPORT void JNICALL
Java_com_gjq_demo_videocompressorlib_FFmpegBridge_jxCMDExit(JNIEnv *env, jclass type){
//    term_exit();
    jxExit();
//    ffmpeg_thread_exit(1);
}

JNIEXPORT jint JNICALL
Java_com_gjq_demo_videocompressorlib_FFmpegBridge_jxCMDRun(JNIEnv *env, jclass type,
        jobjectArray commands){
    int argc = (*env)->GetArrayLength(env,commands);
    char *argv[argc];
    int i;
    for (i = 0; i < argc; i++) {
        jstring js = (jstring) (*env)->GetObjectArrayElement(env,commands, i);
        argv[i] = (char *) (*env)->GetStringUTFChars(env,js, 0);
    }
    return ffmpeg_cmd_run(argc,argv);
}

int ffmpeg_cmd_run(int argc, char **argv){
    return jxRun(argc, argv);
}

char *logUrl;

/**
 * 初始化debug工具
 */
JNIEXPORT void JNICALL
Java_com_gjq_demo_videocompressorlib_FFmpegBridge_initJXFFmpeg(JNIEnv *env, jclass type,
                                                                           jboolean debug,
                                                                           jstring logUrl_) {
    JNI_DEBUG = debug;
    if (JNI_DEBUG&&logUrl_!=NULL) {
        av_log_set_callback(log_callback);
        const char* log = (*env)->GetStringUTFChars(env,logUrl_, 0);
        logUrl = (char*)malloc(strlen(log));
        strcpy(logUrl,log);
        (*env)->ReleaseStringUTFChars(env,logUrl_, log);
    }

}


void log_callback(void *ptr, int level, const char *fmt,
                  va_list vl) {
    FILE *fp = NULL;

    if (!fp)
        fp = fopen(logUrl, "a+");
    if (fp) {
        vfprintf(fp, fmt, vl);
        fflush(fp);
        fclose(fp);
    }

}
