package com.dinpay.trip.testdemo.adapter.SubAdapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dinpay.trip.testdemo.R;

import java.util.List;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/03/06 10:04
 * 类描述：
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class SubAdapter3 extends RecyclerView.Adapter<SubAdapter3.viewHolder> {
    private final List<String> mData;

    public SubAdapter3(List<String> data) {
        this.mData = data;
    }

    @Override
    public SubAdapter3.viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_typ3, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        holder.tv.setText(mData.get(position));
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        public final TextView tv;

        public viewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_item_sub_3);
        }
    }
}
