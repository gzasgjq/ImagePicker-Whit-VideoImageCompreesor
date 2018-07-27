# VideoCompressorLib
基于**FFmpeg**的视频压缩库，压缩质量与微信方案相当，可自定义压缩参数，提供全平台SO支持

**！！！仅支持AVC格式的视频流！！！**
**！！！部分高帧率或4K视频可能不受支持！！！**

[ ![Download](https://api.bintray.com/packages/gzasgjq/Maven/DyVideoCompressor/images/download.svg) ](https://bintray.com/gzasgjq/Maven/DyVideoCompressor/_latestVersion)

FFmpeg版本：3.2.5

精简自@mabeijianxi编译的[FFmpeg4Android](https://github.com/mabeijianxi/FFmpeg4Android)

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

FFmpeg Log日志开关：
```java
FFmpegBridge.setLogEnable(true);
```

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

以上三种码率模式都可设置Velocity(压缩速率)，定义在{@link BaseMediaBitrateConfig#Velocity},提供以下选项：
- ULTRAFAST
- SUPERFAST
- VERYFAST
- FASTER
- FAST
- MEDIUM
- SLOW
- SLOWER
- VERYSLOW
- PLACEBO

具体差别详见：[FFmpeg官网：Preset](http://trac.ffmpeg.org/wiki/Encode/H.264)

#### 1.3    创建压缩实体对象

异步方法：

```java
/**
* @param outputPath 文件输出目录
* @param config 配置
*/
new LocalMediaCompress(outputPath, config)
                        .startCompressAsyn((ret, mediaObject) -> runOnUiThread(() -> {
                            tvInfo.append(String.format(Locale.CHINA, "压缩后大小：%.2f mb", new File(mediaObject.getOutputTempTranscodingVideoPath()).length() / 1024f / 1024f));
                            tvInfo.append(String.format(Locale.CHINA, "耗时：%s\n", (System.currentTimeMillis() - startTime) / 1000));
                            videoView.setVideoPath(mediaObject.getOutputTempTranscodingVideoPath());
                            videoView.start();
                            tvInfo.append("filePath: " + mediaObject.getOutputTempTranscodingVideoPath() + "\n");
                        }));
```

同步方法：
```java
/**
* @param outputPath 文件输出目录
* @param config 配置
*/
OnlyCompressOverBean bean = new LocalMediaCompress(outputPath, config).startCompress();
```

对接RxJava实现批量队列压缩示例：
```java
List<File> compressVideos = new ArrayList<>();
···
add VideoFiles
···
Observable.fromIterable(compressVideos)
                        .map(new Function<File, LocalMediaCompress>() {
                            @Override
                            public LocalMediaCompress apply(File file) throws Exception {
                                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                                mediaMetadataRetriever.setDataSource(file.getPath());
                                String videoWidth = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                                String videoHeight = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                                float scale = Math.min(Float.parseFloat(videoWidth), Float.parseFloat(videoHeight)) / 540f; //以宽度为540为基准缩放
                                LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
                                LocalMediaConfig config = buidler
                                        .setVideoPath(file.getPath())   //源文件路径
                                        .setFramerate(30)   //FPS
                                        .doH264Compress(new VBRMode(3000, 1800).setVelocity(BaseMediaBitrateConfig.Velocity.SUPERFAST)) //X264压缩码率设置
                                        .setScale(scale)    //分辨率缩放比例
                                        .build();
                                return new LocalMediaCompress(outputPath, config);
                            }
                        })
                        .concatMap(new Function<LocalMediaCompress, ObservableSource<MediaObject>>() {
                            @Override
                            public ObservableSource<MediaObject> apply(final LocalMediaCompress localMediaCompress) throws Exception {
                                return Observable.create(new ObservableOnSubscribe<MediaObject>() {
                                    @Override
                                    public void subscribe(final ObservableEmitter<MediaObject> observableEmitter) throws Exception {
                                        localMediaCompress.startCompressAsyn(new LocalMediaCompress.OnExecOverListener() {
                                            @Override
                                            public void onOver(int i, MediaObject mediaObject) {
                                                observableEmitter.onNext(mediaObject);
                                                observableEmitter.onComplete();
                                            }
                                        });
                                    }
                                });
                            }
                        })
                        .toList()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<List<MediaObject>>() {
                            Disposable disposable;
                            @Override
                            public void onSubscribe(Disposable disposable) {
                                this.disposable = disposable;
                                onCompressStart();
                            }

                            @Override
                            public void onSuccess(List<MediaObject> mediaObjects) {
                                onCompressStop();
                                disposable.dispose();
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                onCompressStop();
                                disposable.dispose();
                            }
                        });
```

## 2.    性能：
与微信对比测试结果大致如下
<table><tbody>
<tr>
        <th colspan="4">ARMv7</th>
        </tr>
    <tr>
        <th>视频文件</th>
        <th>视频时长</th>
        <th>压缩后大小</th>
        <th>耗时</th>
    </tr>
    <tr>
        <td>源文件(720P)</td>
        <td>5s</td>
        <td>5.55mb</td>
        <td> </td>
    </tr>
    <tr>
        <td>微信(540*960)</td>
        <td>5s</td>
        <td>1.22mb</td>
        <td>25s</td>
    </tr>
    <tr>
        <td>VideoCompressorLib(540*960)</td>
        <td>5s</td>
        <td>1.33mb</td>
        <td>29s</td>
    </tr>
</table>

## 3.    Simple
[VideoCompressorLib](/video/Final_Meizu.mp4)
[原始](/video/V80706-160626.mp4)
[微信](/video/vsg_output_1530864572212.mp4)

## 4.    Issues

为了达到微信压缩的视频质量和大小，FFmpeg压缩时添加了格式优化
```
    -profile:v high -level 3.1 -deblock 1:1
```
可能会造成某些老设备播放出现问题，手上没有设备批量测试╮(╯▽╰)╭,如果发现有问题，可以提到Issues，随缘修复(￣▽￣)／

## 5.    后期计划
1.   考虑添加动态SO加载，以缩减Apk体积
2.   添加FFmpeg命令执行接口，以扩展功能，实现添加水印等功能