package com.gjq.demo.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gjq.demo.R;

import java.util.Locale;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/01/22 15:35
 * 类描述：
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.viewHolde>{
    private int count;

    public TestAdapter(int count) {
        this.count = count;
    }

    @Override
    public viewHolde onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_full, parent, false);
        return new viewHolde(cardView);
    }

    @Override
    public void onBindViewHolder(viewHolde holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return count;
    }

    public class viewHolde extends RecyclerView.ViewHolder{
        TextView text;

        public viewHolde(View itemView) {
            super(itemView);
            this.text = itemView.findViewById(R.id.tv_item_full);
        }

        public void bind(int position) {
            text.setText(String.format(Locale.CHINA,"%d", position));
        }
    }
}
