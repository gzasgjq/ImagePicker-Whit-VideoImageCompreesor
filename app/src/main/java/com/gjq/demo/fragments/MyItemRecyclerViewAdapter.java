package com.gjq.demo.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import com.gjq.demo.R;
import com.gjq.demo.adapter.SubAdapter.SubAdapter1;
import com.gjq.demo.adapter.SubAdapter.SubAdapter2;
import com.gjq.demo.adapter.SubAdapter.SubAdapter3;
import com.gjq.demo.commom.FullyLinearLayoutManager;
import com.gjq.demo.fragments.dummy.DummyContent;
import com.gjq.demo.fragments.dummy.DummyContent.DummyItem;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<DummyItem> mValues;

    public MyItemRecyclerViewAdapter(List<DummyItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        String content = holder.mItem.content;
        holder.mTitle.setText(content);
        if (content.equals(DummyContent.CONTENT_TYPE_1)){
            holder.bindAdapter(new SubAdapter1(holder.mItem.details));
        }else if (content.equals(DummyContent.CONTENT_TYPE_2)){
            holder.bindAdapter(new SubAdapter2(holder.mItem.details));
        }else if (content.equals(DummyContent.CONTENT_TYPE_3)){
            holder.bindAdapter(new SubAdapter3(holder.mItem.details));
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final RecyclerView mRecyclerView;
        public final TextView mTitle;
        public DummyItem mItem;
        private RecyclerView.Adapter mAdapter;

        public ViewHolder(View view) {
            super(view);
            mRecyclerView = view.findViewById(R.id.recycle_item_list);
            mTitle = view.findViewById(R.id.tv_item_title);
            mRecyclerView.setLayoutManager(new FullyLinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false){
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
        }

        public void bindAdapter(RecyclerView.Adapter subAdapter){
            mRecyclerView.setAdapter(mAdapter = subAdapter);
        }
    }
}
