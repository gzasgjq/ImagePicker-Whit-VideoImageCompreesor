package com.gjq.demo.commom;

import android.support.annotation.IntDef;

import static com.gjq.demo.commom.MediaType.MEDIA_IMAGE;
import static com.gjq.demo.commom.MediaType.MEDIA_MIX;
import static com.gjq.demo.commom.MediaType.MEDIA_VIDEO;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/06/26 18:23
 * 类描述：$INTRO$
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
@IntDef(value = {MEDIA_MIX, MEDIA_VIDEO, MEDIA_IMAGE})
public @interface MediaType {
    int MEDIA_MIX = 0;
    int MEDIA_VIDEO = 1;
    int MEDIA_IMAGE = 2;
}