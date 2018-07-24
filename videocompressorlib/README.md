# VideoCompressorLib
基于**FFmpeg**的视频压缩库，压缩质量与微信方案相当，可自定义压缩参数，提供全平台SO支持

[ ![Download](https://api.bintray.com/packages/gzasgjq/Maven/DyVideoCompressor/images/download.svg) ](https://bintray.com/gzasgjq/Maven/DyVideoCompressor/_latestVersion)

[TOC]

## 1. 使用：

#### 1.1    添加依赖：

确保项目有jcenter仓库

```groovy
    com.dinpay.trip.videocompressorlib:videocompressorlib:0.3
```

SO库包含了:

```groovy
        ndk {
            abiFilters "armeabi-v7a", "arm64-v8a", "x86"
        }
```

可根据app module的ndk配置自动过滤不需要的abi

#### 1.2    配置参数：

```java
LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
LocalMediaConfig config = buidler
        .setVideoPath(_data)    // 原视频文件路径
        .setFramerate(30)   // 输出比特率
        .captureThumbnailsTime(1)   //捕获缩略图   
        .doH264Compress(    //X264压缩配置
                new VBRMode(3000, 1800) //X264码率模式 see:{@link BaseMediaBitrateConfig}
                .setVelocity(BaseMediaBitrateConfig.Velocity.SUPERFAST) //压缩等级设置
            )
        .setScale(scale)    //输出分辨率缩放比例
        .build();
```

X264码率模式包含：

- 固定码率模式(CBR)
```java
    /**
     * @param bufSize
     * @param bitrate 固定码率值
     */
    new CBRMode(bufSize, bitrate)
```
- 额定最大码率模式(VBR)
```java
    /**
    * @param maxBitrate 最大码率
    * @param bitrate 额定码率
    */
    new VBRMode(maxBitrate, bitrate)
```
- 预设码率自适应模式(AutoVBR)
```java
    /**
    * @param crfSize 压缩等级，0~51，值越大约模糊，视频越小，建议18~28.
    */
    new AutoVBRMode(crfSize)
```

为了达到微信压缩的视频质量和大小，FFmpeg压缩时添加了格式优化
```
    -profile:v high -level 3.1 -deblock 1:1
```
可能会造成某些老设备播放出现问题，手上没有设备批量测试╮(╯▽╰)╭

#### 1.3    性能：
与微信对比测试结果大致如下
<table><tbody>
<tr>
        <th colspan="4">ARM64</th>
        </tr>
    <tr>
        <th>视频文件</th>
        <th>视频时长</th>
        <th>压缩后大小</th>
        <th>耗时</th>
    </tr>
    <tr>
        <td>源文件</td>
        <td>23s</td>
        <td>47.6mb</td>
        <td> </td>
    </tr>
    <tr>
        <td>微信</td>
        <td>10s</td>
        <td>2.01mb</td>
        <td>56s</td>
    </tr>
    <tr>
        <td>VideoCompressorLib</td>
        <td>23s</td>
        <td>5.44mb</td>
        <td>76s</td>
    </tr>
</table>
同等质量和压缩体积的情况下，速度会比微信的慢，后期再慢慢优化吧 :disappointed:

#### 1.4    Simple
[VideoCompressorLib](videocompressorlib/video/Final_Meizu.mp4)
[原始](videocompressorlib/video/V80706-160626.mp4)
[微信](videocompressorlib/video/vsg_output_1530864572212.mp4)