package com.gjq.demo.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gjq.demo.R;

import java.util.List;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/05/05 10:25
 * 类描述：$INTRO$
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class BaseAdapterTest extends BaseQuickAdapter<String, BaseViewHolder> {
    public BaseAdapterTest(@Nullable List<String> data) {
        super(R.layout.item_sub_typ1, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_item_sub_1, item);
    }
}
