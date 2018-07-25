# ImagePicker
本地媒体选择器，修改自@jeasonlzy的<https://github.com/jeasonlzy/ImagePicker>

添加了本地视频选择及[视频压缩](../videocompressorlib)支持

## 1.   使用

#### 1.1    参数配置
```java
ImagePicker imagePicker = ImagePicker.getInstance();
imagePicker.setImageLoader(new GlideLoader());  //设置图片加载器
imagePicker.setSaveRectangle(false);    //是否按矩形区域保存
imagePicker.setStyle(CropImageView.Style.RECTANGLE);    //裁剪框的形状
imagePicker.setShowCamera(false);   //显示拍照按钮
imagePicker.setMultiMode(true); //是否可以多选
imagePicker.setFocusWidth(screenWidth - Utils.dip2px(context, 30)); //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
imagePicker.setFocusHeight((int) ((screenWidth - Utils.dip2px(context, 30)) * 0.618));  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
imagePicker.setOutPutX(screenWidth);    //保存文件的宽度。单位像素
imagePicker.setOutPutY((int) (screenWidth * 0.618));    //保存文件的高度。单位像素
imagePicker.setNeedCompress(true, getCachePath());  //需要压缩（默认false），压缩输出文件保存目录
imagePicker.setMediaMode(ImagePicker.MediaType.MEDIA_IMAGE);    //媒体选择模式
imagePicker.setSelectLimit(mPicsLimit - mPicsCount);    //数量限制
imagePicker.setCrop(false); //需要裁剪(仅支持图片)
imagePicker.setMaxVideoDuration(MAX_VIDEO_DURATION);    //视频最大时长限制
// 启动
Intent intent = new Intent(context, ImageGridActivity.class);
startActivityForResult(intent, REQUEST_LOCAL_MEDIA);
```
其中MediaType包含：

- MEDIA_IMAGE：图片模式
- MEDIA_VIDEO：视频模式
- MEDIA_MIX：混合模式（参考微信）

#### 1.2    数据处理回调

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (data != null && resultCode == ImagePicker.RESULT_CODE_ITEMS && requestCode == REQUEST_LOCAL_MEDIA) {
        List<ImageItem> list = (List<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
        ···
        processMedia(list);
        ···
    }
    super.onActivityResult(requestCode, resultCode, data);
}
```

#### 1.3    回调数据结构

回调使用的是ImageItem类，包含参数如下：

| 参数  |   描述  |   可能为null    |   备注  |
| :--------: | :--------:| :--: | :--: |
| path   |     路径 |   false    |   |
| mimeType   |     类型 |   false    |   |
| name   |     名字 |   true    |   |
| size   |     大小 |   true    |   |
| width   |     宽度    |   true    |   |
| height   |     高度   |   true    |   |
| addTime   |     创建时间    |   true    |   |
| duration   |     视频时长   |   true    |  只有没压缩的源视频文件有此参数，并且可能为null |
| orientation   |     方向  |   true    |   Only Image file whit degrees 0, 90, 180, 270 will work. |


## 4.    Issues
由于项目采用Loader加载媒体文件，受系统ContentProvider的刷新问题的限制，某些机型新增的媒体文件可能不会及时加载出来，目前项目采取的临时解决方案是在启动ImagePicker的时候执行
```java
MediaScannerConnection.scanFile()
```
方法扫描系统媒体文件，扫描完成会自动刷新页面数据，但是设备图片较多的情况下，不保证能及时更新。

## 5.    后期计划
1.   添加视频裁剪和视频编辑功能
2.   优化媒体扫描器，解决新增媒体刷新延时问题