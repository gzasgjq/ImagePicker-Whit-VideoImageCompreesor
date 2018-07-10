package com.dinpay.trip.testdemo.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dinpay.trip.testdemo.R;
import com.dinpay.trip.testdemo.beans.ImageItem;

import java.util.List;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/06/26 18:15
 * 类描述：$INTRO$
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class PreviewTestAdapter extends BaseQuickAdapter<ImageItem, BaseViewHolder> {

    public PreviewTestAdapter(@Nullable List<ImageItem> data) {
        super(R.layout.item_recycle_full, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ImageItem item) {
        helper.setText(R.id.tv_item_full, item.name);
        Glide.with(mContext)
                .load(item.path)
                .apply(new RequestOptions().error(null))
                .into((ImageView) helper.getView(R.id.img_item_full));
    }
}
