package com.lzy.imagepicker.adapter;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.R;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageBaseActivity;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.util.MediaUtil;
import com.lzy.imagepicker.util.Utils;
import com.lzy.imagepicker.view.SuperCheckBox;

import java.util.ArrayList;
import java.util.Locale;

/**
 * 加载相册图片的RecyclerView适配器
 * <p>
 * 用于替换原项目的GridView，使用局部刷新解决选中照片出现闪动问题
 * <p>
 * 替换为RecyclerView后只是不再会导致全局刷新，
 * <p>
 * 但还是会出现明显的重新加载图片，可能是picasso图片加载框架的问题
 * <p>
 * Author: nanchen
 * Email: liushilin520@foxmail.com
 * Date: 2017-04-05  10:04
 */

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {


    private static final int ITEM_TYPE_CAMERA = 0;  //第一个条目是相机
    private static final int ITEM_TYPE_NORMAL = 1;  //第一个条目不是相机
    private ImagePicker imagePicker;
    private Activity mActivity;
    private ArrayList<ImageItem> images;       //当前需要显示的所有的图片数据
    private ArrayList<ImageItem> mSelectedImages; //全局保存的已经选中的图片数据
    private boolean isShowCamera;         //是否显示拍照按钮
    private int mImageSize;               //每个条目的大小
    private LayoutInflater mInflater;
    private OnImageItemClickListener listener;   //图片被点击的监听

    public void setOnImageItemClickListener(OnImageItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnImageItemClickListener {
        void onImageItemClick(View view, ImageItem imageItem, int position);
    }

    public void refreshData(ArrayList<ImageItem> images) {
        if (images == null || images.size() == 0) this.images = new ArrayList<>();
        else this.images = images;
        notifyDataSetChanged();
    }

    /**
     * 构造方法
     */
    public ImageRecyclerAdapter(Activity activity, ArrayList<ImageItem> images) {
        this.mActivity = activity;
        if (images == null || images.size() == 0) this.images = new ArrayList<>();
        else this.images = images;

        mImageSize = Utils.getImageItemWidth(mActivity);
        imagePicker = ImagePicker.getInstance();
        isShowCamera = imagePicker.isShowCamera();
        mSelectedImages = imagePicker.getSelectedImages();
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CAMERA) {
            return new CameraViewHolder(mInflater.inflate(R.layout.adapter_camera_item, parent, false));
        }
        return new ImageViewHolder(mInflater.inflate(R.layout.adapter_image_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof CameraViewHolder) {
            ((CameraViewHolder) holder).bindCamera();
        } else if (holder instanceof ImageViewHolder) {
            ((ImageViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowCamera) return position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_NORMAL;
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return isShowCamera ? images.size() + 1 : images.size();
    }

    public ImageItem getItem(int position) {
        if (isShowCamera) {
            if (position == 0) return null;
            return images.get(position - 1);
        } else {
            return images.get(position);
        }
    }

    private class ImageViewHolder extends ViewHolder {

        View rootView, videoRoot;
        ImageView ivThumb;
        View mask;
        SuperCheckBox cbCheck;
        TextView tvDuration;

        ImageViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            videoRoot = itemView.findViewById(R.id.ll_video);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            ivThumb = (ImageView) itemView.findViewById(R.id.iv_thumb);
            mask = itemView.findViewById(R.id.mask);
            cbCheck = (SuperCheckBox) itemView.findViewById(R.id.cb_check);
            itemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize)); //让图片是个正方形
        }

        void bind(final int position) {
            final ImageItem imageItem = getItem(position);
            ivThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onImageItemClick(rootView, imageItem, position);
                }
            });
            cbCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MediaUtil.isFileValid(mActivity, cbCheck.isChecked(), imageItem, mSelectedImages)) {
                        imagePicker.addSelectedImageItem(position, imageItem, cbCheck.isChecked());
                        mask.setVisibility(View.VISIBLE);
                    } else {
                        cbCheck.setChecked(false);
                        mask.setVisibility(View.GONE);
                    }
//                        if (imageItem.mimeType.startsWith("video")) {
//                            int selectLimit = imagePicker.getSelectVideoLimit();
//                            if (cbCheck.isChecked() && mSelectedImages.size() >= selectLimit) {
//                                Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.selectvideo_limit, selectLimit), Toast.LENGTH_SHORT).show();
//                                cbCheck.setChecked(false);
//                                mask.setVisibility(View.GONE);
//                            } else {
//                                if (!new File(imageItem.path).exists()) {
//                                    Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.invalid_video), Toast.LENGTH_SHORT).show();
//                                    cbCheck.setChecked(false);
//                                    mask.setVisibility(View.GONE);
//                                } else {
//                                    if (imagePicker.getSelectImageCount() > 0 && !imagePicker.isSelectedImageContainsVideo()) {
//                                        cbCheck.setChecked(false);
//                                        return;
//                                    }
//                                    imagePicker.addSelectedImageItem(position, imageItem, cbCheck.isChecked());
//                                    mask.setVisibility(View.VISIBLE);
//                                }
//                            }
//                        } else {
//                            int selectLimit = imagePicker.getSelectLimit();
//                            if (cbCheck.isChecked() && mSelectedImages.size() >= selectLimit) {
//                                Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.select_limit, selectLimit), Toast.LENGTH_SHORT).show();
//                                cbCheck.setChecked(false);
//                                mask.setVisibility(View.GONE);
//                            } else {
//                                if (!new File(imageItem.path).exists()) {
//                                    Toast.makeText(mActivity.getApplicationContext(), mActivity.getString(R.string.invalid), Toast.LENGTH_SHORT).show();
//                                    cbCheck.setChecked(false);
//                                    mask.setVisibility(View.GONE);
//                                } else {
//                                    if (imagePicker.getSelectImageCount() > 0 && imagePicker.isSelectedImageContainsVideo()) {
//                                        cbCheck.setChecked(false);
//                                        return;
//                                    }
//                                    imagePicker.addSelectedImageItem(position, imageItem, cbCheck.isChecked());
//                                    mask.setVisibility(View.VISIBLE);
//                                }
//                            }
//                        }
                }
            });
            //根据是否多选，显示或隐藏checkbox
            if (imagePicker.isMultiMode()) {
                cbCheck.setVisibility(View.VISIBLE);
                boolean checked = mSelectedImages.contains(imageItem);
                if (checked) {
                    mask.setVisibility(View.VISIBLE);
                    cbCheck.setChecked(true);
                } else {
                    mask.setVisibility(View.GONE);
                    cbCheck.setChecked(false);
                }
            } else {
                cbCheck.setVisibility(View.GONE);
            }
            if (imageItem.mimeType.startsWith("video")) {
                videoRoot.setVisibility(View.VISIBLE);
                cbCheck.setVisibility(View.GONE);
                tvDuration.setText(String.format(Locale.CHINA, "%.0f:%.0f", imageItem.duration / 1000f / 60f, Math.ceil(imageItem.duration / 1000f)));
                if (ImagePicker.getInstance().getMaxVideoDuration() > 0 && imageItem.duration > ImagePicker.getInstance().getMaxVideoDuration()) {
                    mask.setVisibility(View.VISIBLE);
                    mask.setBackgroundColor(mActivity.getResources().getColor(R.color.mask_disable));
                } else {
                    mask.setBackgroundColor(mActivity.getResources().getColor(R.color.mask_selected));
                }
            } else {
                videoRoot.setVisibility(View.GONE);
            }
            if (imagePicker.getImageLoader() != null) {
                imagePicker.getImageLoader().displayImage(mActivity, imageItem.path, ivThumb, mImageSize, mImageSize); //显示图片
            }
        }

    }

    private class CameraViewHolder extends ViewHolder {

        View mItemView;

        CameraViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
        }

        void bindCamera() {
            mItemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize)); //让图片是个正方形
            mItemView.setTag(null);
            mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!((ImageBaseActivity) mActivity).checkPermission(Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA}, ImageGridActivity.REQUEST_PERMISSION_CAMERA);
                    } else {
                        imagePicker.takePicture(mActivity, ImagePicker.REQUEST_CODE_TAKE);
                    }
                }
            });
        }
    }
}
