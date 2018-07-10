package com.dinpay.trip.testdemo.commom;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class GetPhoneInfoUtil {
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_EMUI_VERSION_CODE = "ro.build.hw_emui_api_level";
    private static final String KEY_FLYME_ID_FALG_KEY = "ro.build.display.id";
    private static final String KEY_FLYME_ICON_FALG = "persist.sys.use.flyme.icon";
    private static final String KEY_FLYME_SETUP_FALG = "ro.meizu.setupwizard.flyme";
    private static final String KEY_FLYME_PUBLISH_FALG = "ro.flyme.published";
    public static final int INT = 18;
    private Properties mProper;

    public static GetPhoneInfoUtil mPhoneInfoUtil;

    public enum ROM_TYPE {

        MIUI_ROM,

        FLYME_ROM,

        EMUI_ROM,

        OTHER_ROM

    }

    private GetPhoneInfoUtil() {
        if (mProper == null) {
            mProper = new Properties();
        }
        try {
            mProper.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * [获取cpu类型和架构]
     *
     * @return 三个参数类型的数组，第一个参数标识是不是ARM架构，第二个参数标识是V6还是V7架构，第三个参数标识是不是neon指令集
     */
    public static Boolean getCpuArchitecture() {
        boolean is64bit = false;
        try {
            InputStream is = new FileInputStream("/proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(ir);
            String line;
            while ((line = br.readLine()) != null) {
                line = line.toLowerCase();
                if (line.contains("processor") && line.contains("64")){
                    is64bit = true;
                }
                if (line.contains("model name") && line.contains("armv8")){
                    is64bit = true;
                }
            }
            br.close();
            ir.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return is64bit;
    }

    public static GetPhoneInfoUtil getInstance() {
        if (mPhoneInfoUtil == null) {
            synchronized (GetPhoneInfoUtil.class) {
                if (mPhoneInfoUtil == null) {
                    mPhoneInfoUtil = new GetPhoneInfoUtil();
                }
            }
        }
        return mPhoneInfoUtil;
    }


    /**
     * 获取手机厂商
     *
     * @return
     */
    public String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机型号
     *
     * @return
     */
    public String getPhoneModel() {
        return android.os.Build.MODEL;
    }


    /**
     * 获取系统版本号
     *
     * @return
     */
    public String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }


    /**
     * 获取当前应用版本
     *
     * @return
     */
    public String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 判断ROM是否为MIUI
     *
     * @return
     */
    public boolean isMIUI() {

        return mProper.containsKey(KEY_MIUI_VERSION_CODE) || mProper.containsKey(KEY_MIUI_VERSION_NAME);
    }

    /**
     * 判断ROM是否为EMUI
     *
     * @return
     */
    public boolean isEMUI() {

        return mProper.containsKey(KEY_EMUI_VERSION_CODE);
    }


    /**
     * 判断ROM是否为Flyme
     *
     * @return
     */
    public boolean isFlyme() {
        return mProper.containsKey(KEY_FLYME_ICON_FALG) || mProper.containsKey(KEY_FLYME_SETUP_FALG) || mProper.containsKey(KEY_FLYME_PUBLISH_FALG);
    }


    /**
     * 获取ROM版本信息
     *
     * @return
     */
    public String getRomInfo() {
        if (isMIUI()) {
            return ROM_TYPE.MIUI_ROM + " " + mProper.getProperty(KEY_MIUI_VERSION_NAME);
        } else if (isFlyme()) {
            return ROM_TYPE.FLYME_ROM + " " + mProper.getProperty(KEY_FLYME_ID_FALG_KEY);
        } else if (isEMUI()) {
            return ROM_TYPE.EMUI_ROM + " " + mProper.getProperty(KEY_EMUI_VERSION_CODE);
        } else {
            return ROM_TYPE.OTHER_ROM + "";
        }
    }

    /**
     * 华为－huawei
     * 魅族－meizu
     * 小米－xiaomi
     * oppo－oppo
     * vivo－vivo
     * 三星－samsung
     * 锤子－smartisan
     * LG－lg
     * 乐视－letv
     * 中兴－zte
     * 酷派－yulong
     * 联想－lenovo
     * 索尼－sony
     *
     * @return
     */
    public String getHandSetInfo() {
        String handSetInfo = "手机型号:" + android.os.Build.MODEL
                + "\n系统版本:" + android.os.Build.VERSION.RELEASE
                + "\n产品型号:" + android.os.Build.PRODUCT
                + "\n版本显示:" + android.os.Build.DISPLAY
//                + "\n基带版本:" + reflect()
                + "\n系统定制商:" + android.os.Build.BRAND
                + "\n设备参数:" + android.os.Build.DEVICE
                + "\n开发代号:" + android.os.Build.VERSION.CODENAME
                + "\nSDK版本号:" + android.os.Build.VERSION.SDK_INT
                + "\nCPU类型:" + android.os.Build.CPU_ABI
                + "\n硬件类型:" + android.os.Build.HARDWARE
                + "\n主机:" + android.os.Build.HOST
                + "\n生产ID:" + android.os.Build.ID
                + "\nROM制造商:" + android.os.Build.MANUFACTURER // 这行返回的是rom定制商的名称
                ;
        return handSetInfo;
    }

    /**
     * Android.os.Build类中。包括了这样的一些信息。我们可以直接调用 而不需要添加任何的权限和方法。
     * <p>
     * android.os.Build.BOARD：获取设备基板名称
     * android.os.Build.BOOTLOADER:获取设备引导程序版本号
     * android.os.Build.BRAND：获取设备品牌
     * <p>
     * android.os.Build.CPU_ABI：获取设备指令集名称（CPU的类型）
     * <p>
     * android.os.Build.CPU_ABI2：获取第二个指令集名称
     * <p>
     * android.os.Build.DEVICE：获取设备驱动名称
     * android.os.Build.DISPLAY：获取设备显示的版本包（在系统设置中显示为版本号）和ID一样
     * android.os.Build.FINGERPRINT：设备的唯一标识。由设备的多个信息拼接合成。
     * <p>
     * android.os.Build.HARDWARE：设备硬件名称,一般和基板名称一样（BOARD）
     * <p>
     * android.os.Build.HOST：设备主机地址
     * android.os.Build.ID:设备版本号。
     * <p>
     * android.os.Build.MODEL ：获取手机的型号 设备名称。
     * <p>
     * android.os.Build.MANUFACTURER:获取设备制造商
     * <p>
     * android:os.Build.PRODUCT：整个产品的名称
     * <p>
     * android:os.Build.RADIO：无线电固件版本号，通常是不可用的 显示unknown
     * android.os.Build.TAGS：设备标签。如release-keys 或测试的 test-keys
     * <p>
     * android.os.Build.TIME：时间
     * <p>
     * android.os.Build.TYPE:设备版本类型 主要为”user” 或”eng”.
     * <p>
     * android.os.Build.USER:设备用户名 基本上都为android-build
     * <p>
     * android.os.Build.VERSION.RELEASE：获取系统版本字符串。如4.1.2 或2.2 或2.3等
     * <p>
     * android.os.Build.VERSION.CODENAME：设备当前的系统开发代号，一般使用REL代替
     * android.os.Build.VERSION.INCREMENTAL：系统源代码控制值，一个数字或者Git hash值
     * <p>
     * android.os.Build.VERSION.SDK：系统的API级别 一般使用下面大的SDK_INT 来查看
     * <p>
     * android.os.Build.VERSION.SDK_INT：系统的API级别 数字表示
     * <p>
     * android.os.Build.VERSION_CODES类 中有所有的已公布的Android版本号。全部是Int常亮。可用于与SDK_INT进行比较来判断当前的系统版本
     */
    public String getPhoneInfo() {
        //BOARD 主板
        String phoneInfo = "BOARD: " + android.os.Build.BOARD;
        phoneInfo += "\n BOOTLOADER: " + android.os.Build.BOOTLOADER;
        //BRAND 运营商
        phoneInfo += "\n BRAND: " + android.os.Build.BRAND;
        phoneInfo += "\n CPU_ABI: " + android.os.Build.CPU_ABI;
        phoneInfo += "\n CPU_ABI2: " + android.os.Build.CPU_ABI2;

        //DEVICE 驱动
        phoneInfo += "\n DEVICE: " + android.os.Build.DEVICE;
        //DISPLAY Rom的名字 例如 Flyme 1.1.2（魅族rom） &nbsp;JWR66V（Android nexus系列原生4.3rom）
        phoneInfo += "\n DISPLAY: " + android.os.Build.DISPLAY;
        //指纹
        phoneInfo += "\n FINGERPRINT: " + android.os.Build.FINGERPRINT;
        //HARDWARE 硬件
        phoneInfo += "\n HARDWARE: " + android.os.Build.HARDWARE;
        phoneInfo += "\n HOST: " + android.os.Build.HOST;
        phoneInfo += "\n ID: " + android.os.Build.ID;
        //MANUFACTURER 生产厂家
        phoneInfo += "\n MANUFACTURER: " + android.os.Build.MANUFACTURER;
        //MODEL 机型
        phoneInfo += "\n MODEL: " + android.os.Build.MODEL;
        phoneInfo += "\n PRODUCT: " + android.os.Build.PRODUCT;
        phoneInfo += "\n RADIO: " + android.os.Build.RADIO;
        phoneInfo += "\n RADITAGSO: " + android.os.Build.TAGS;
        phoneInfo += "\n TIME: " + android.os.Build.TIME;
        phoneInfo += "\n TYPE: " + android.os.Build.TYPE;
        phoneInfo += "\n USER: " + android.os.Build.USER;
        //VERSION.RELEASE 固件版本
        phoneInfo += "\n VERSION.RELEASE: " + android.os.Build.VERSION.RELEASE;
        phoneInfo += "\n VERSION.CODENAME: " + android.os.Build.VERSION.CODENAME;
        //VERSION.INCREMENTAL 基带版本
        phoneInfo += "\n VERSION.INCREMENTAL: " + android.os.Build.VERSION.INCREMENTAL;
        //VERSION.SDK SDK版本
        phoneInfo += "\n VERSION.SDK: " + android.os.Build.VERSION.SDK;
        phoneInfo += "\n VERSION.SDK_INT: " + android.os.Build.VERSION.SDK_INT;
        return phoneInfo;
    }

}

