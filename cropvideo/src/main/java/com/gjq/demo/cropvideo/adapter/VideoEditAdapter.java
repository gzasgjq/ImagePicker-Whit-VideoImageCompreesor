package com.gjq.demo.cropvideo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gjq.demo.cropvideo.R;

import java.util.ArrayList;
import java.util.List;

public class VideoEditAdapter extends RecyclerView.Adapter {
    private List<String> lists = new ArrayList<>();
    private LayoutInflater inflater;

    private int itemW;
    private Context context;

    public VideoEditAdapter(Context context, int itemW) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.itemW = itemW;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EditViewHolder(inflater.inflate(R.layout.video_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        EditViewHolder viewHolder = (EditViewHolder) holder;
        Glide.with(context)
                .load("file://" + lists.get(position))
                .apply(new RequestOptions().error(android.R.color.darker_gray))
                .into(viewHolder.img);
    }

    public void setItemCount(long count){
        for (int i = 0; i < count; i++) {
            lists.add("null");
        }
        notifyDataSetChanged();
    }

    public List<String> getData() {
        return lists;
    }

    public void updateData(int index, String data){
        if (index >= 0 && index < lists.size()) {
            lists.set(index, data);
            notifyItemChanged(index);
        }
    }

    @Override
    public int getItemCount() {
        //add by tanhaiqin
        if (lists == null || lists.isEmpty()) return 0;

        //通知@PiccutureEditAudioActivity.java enable or disable 完成 按钮状态
        if (thumbnailsCount == lists.size() && editAdapterListener != null) {
            editAdapterListener.enable(true);
        }
        //end
        return lists.size();
    }

    private final class EditViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;

        EditViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.id_image);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) img.getLayoutParams();
            layoutParams.width = itemW;
            img.setLayoutParams(layoutParams);
        }
    }

    public void addItemVideoInfo(String info) {
        lists.add(info);
        notifyItemInserted(lists.size());
    }

    //add by tanhaiqin
    private int thumbnailsCount;

    //add Listener
    public interface EditAdapterListener {
        void enable(boolean enable);
    }

    private EditAdapterListener editAdapterListener;

    public void setThumbnailsCount(int count) {
        thumbnailsCount = count;
    }

    public void setListener(EditAdapterListener listener) {
        editAdapterListener = listener;
    }
    //end
}
