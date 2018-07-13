package com.lzy.imagepicker;

import com.lzy.imagepicker.bean.ImageFolder;

import java.util.List;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/06/15 17:07
 * 类描述：$INTRO$
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public interface OnDataLoadedListener {
    void onDatasLoaded(List<ImageFolder> imageFolders);
}
