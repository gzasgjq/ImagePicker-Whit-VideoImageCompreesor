package com.lzy.imagepicker.bean;

import java.io.Serializable;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧 Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：图片信息
 * 修订历史：
 * ================================================
 */
public class ImageItem implements Serializable {

    public String name;       //名字
    public String path;       //路径
    public long size;         //大小
    public int width;         //宽度
    public int height;        //高度
    public String mimeType;   //类型
    public long addTime;      //创建时间
    public long duration;     //视频时长
    public int orientation;   //方向 Only Image file whit degrees 0, 90, 180, 270 will work.

    /** 图片的路径和创建时间相同就认为是同一张图片 */
    @Override
    public boolean equals(Object o) {
        if (o instanceof ImageItem) {
            ImageItem item = (ImageItem) o;
            return this.path.equalsIgnoreCase(item.path) && this.addTime == item.addTime;
        }

        return super.equals(o);
    }
}
