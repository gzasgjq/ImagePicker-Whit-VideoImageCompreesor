package com.gjq.demo.cropvideo;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.gjq.demo.cropvideo.adapter.VideoEditAdapter;
import com.gjq.demo.cropvideo.commom.RangeSeekBar;
import com.gjq.demo.cropvideo.tools.PictureUtils;
import com.gjq.demo.cropvideo.tools.TrimVideoUtils;
import com.gjq.demo.cropvideo.tools.UIUtil;
import com.gjq.demo.cropvideo.tools.VideoThumbnailsUtils;
import com.gjq.demo.cropvideo.tools.VideoUtils;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;

public class CropActivity extends AppCompatActivity {
    private String TAG = "CropActivity";

    private static String EXTRA_VIDEO_URI = "CropActivity.videouri";

    private static final long MIN_CUT_DURATION = 1000L;// 最小剪辑时间1s
    private static final int MAX_CUT_DURATION = 20 * 1000; // 最长20s
    private static final int MAX_COUNT_RANGE = 10;//seekBar的区域内一共有多少张图片

    private int mVideoWidth, mVideoHeight;// 压缩用
    private long mThumbnailCount; // 总截图数量（根据总时长和seekBar区域图片数计算
    private long mVideoDuration; // 原视频时长
    private float mPxTime; // 每Px时长
    private long mStartTime; // 开始时间
    private long mEndTime; // 结束时间
    private long leftProgress; //左侧进度（裁剪开始进度
    private long rightProgress; //右侧进度（裁剪结束进度
    private int mMaxCuterWidth; // 剪辑框最大宽度（用于计算像素时间
    boolean isSeeking; // 是否正在滑动
    long scrollPos; // 滑动距离进度（视频时间
    private Uri uri;
    private String mOrignPath;
    private VideoView mVideoView;
    private RecyclerView mRecycleView;
    private VideoEditAdapter mAdapter;
    private RangeSeekBar rangeSeekBar;
    private LinearLayout seekBarLayout;

    public static void start(Activity context, Uri uri, int requestCode) {
        Intent starter = new Intent(context, CropActivity.class);
        starter.putExtra(EXTRA_VIDEO_URI, uri);
        context.startActivityForResult(starter, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        mMaxCuterWidth = UIUtil.getScreenWidth(this) - UIUtil.dip2px(this, 70);

        uri = getIntent().getParcelableExtra(EXTRA_VIDEO_URI);
        mOrignPath = VideoUtils.filePathFromIntent(this, uri);
        mRecycleView = findViewById(R.id.recycleView);
        seekBarLayout = findViewById(R.id.id_seekBarLayout);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecycleView.setAdapter(mAdapter = new VideoEditAdapter(this, mMaxCuterWidth / MAX_COUNT_RANGE));
        mRecycleView.addOnScrollListener(onScrollListener);

        initThumbnails();
        initVideo();
    }

    private void initThumbnails() {
        mVideoDuration = Long.parseLong(VideoUtils.getVideoLength(this, uri));
        boolean isOverMax;
        long rangeWidth;
        // 初始剪辑开始位置为0
        leftProgress = 0;
        if (mVideoDuration > MAX_CUT_DURATION){
            // 如果大于最大剪辑时长
            isOverMax = true;
            // 截图总数= 视频时长 / (最大剪辑时长/最大裁剪框图片数):每张截图时长
            mThumbnailCount = mVideoDuration / (MAX_CUT_DURATION / MAX_COUNT_RANGE);
            // 总时间线宽度= (最大裁剪框宽度 / 裁剪框图片数):单截图宽度 * 总截图数量
            rangeWidth = mMaxCuterWidth / MAX_COUNT_RANGE * mThumbnailCount;
            // 初始剪辑结束位置为最大裁剪时长
            rightProgress = MAX_CUT_DURATION;
        }else {
            // 如果小于最大剪辑时长
            isOverMax = false;
            // 截图总数=裁剪框图片数
            mThumbnailCount = MAX_COUNT_RANGE;
            // 总时间线宽度=最大裁剪框宽度
            rangeWidth = mMaxCuterWidth;
            // 初始剪辑结束位置为视频时长
            rightProgress = mVideoDuration;
        }
        // 单位像素所占时长
        mPxTime = mVideoDuration * 1.0f / rangeWidth * 1.0f;
        // 占位
        mAdapter.setItemCount(mThumbnailCount);
        // --------裁剪框---------
        if (isOverMax) {
            // 视频时长超过剪辑最大时长限制，要设置SeekBar范围在最大时长限制内
            rangeSeekBar = new RangeSeekBar(this, 0L, MAX_CUT_DURATION);
            rangeSeekBar.setSelectedMinValue(0L);
            rangeSeekBar.setSelectedMaxValue(MAX_CUT_DURATION);
        } else {
            // 视频时长不足剪辑最大时长限制，设置SeekBar范围在视频时长内
            rangeSeekBar = new RangeSeekBar(this, 0L, mVideoDuration);
            rangeSeekBar.setSelectedMinValue(0L);
            rangeSeekBar.setSelectedMaxValue(mVideoDuration);
        }
        rangeSeekBar.setMin_cut_time(MIN_CUT_DURATION);//设置最小裁剪时间
        rangeSeekBar.setNotifyWhileDragging(true);
        rangeSeekBar.setOnRangeSeekBarChangeListener(mOnRangeSeekBarChangeListener);
        seekBarLayout.addView(rangeSeekBar);
        // 获取截图Job
        String mOutPutFileDirPath = PictureUtils.getSaveEditThumbnailDir(this);
        VideoThumbnailsUtils.getInstance().getVideoThumbnailsInfoForEditRx(
                this
                , uri // 视频Uri
                , mOutPutFileDirPath // 输出路径
                , 0 // 开始截图位置
                , mVideoDuration // 结束截图位置
                , mThumbnailCount // 需要截图的数量
        )
                .subscribe(new ResourceSubscriber<String>() {
                    public String TAG = "VideoThumbnailsUtils";
                    private int lastestIndex;

                    @Override
                    public void onNext(String s) {
                        // 因为有占位，所以Update
                        mAdapter.updateData(lastestIndex++, s);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "onError: ", t);
                        onComplete();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void initVideo() {
        mVideoView = findViewById(R.id.vv_edit_perview);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        mVideoView.start();
                    }
                });
                mVideoDuration = mp.getDuration();
                mVideoWidth = mp.getVideoWidth();
                mVideoHeight = mp.getVideoHeight();
            }
        });
        mVideoView.setVideoURI(uri);
        mVideoView.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        crop_video();
        return true;
    }

    /**
     * 传统方法，使用isomp4parser 处理裁剪，然后 ffmpeg压缩
     * 需要根据 裁剪后的文件大小，判断是否需要进一步压缩
     */
    private void crop_video() {
        // 保存的路径
        String OutMoviePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mio/cache/video/" + System.currentTimeMillis() + ".mp4";
//        OutMoviePath = VIDEO_CROP_TEMP_FILE  + System.currentTimeMillis() + ".mp4";
        // ==
        final File file = new File(mOrignPath); // 视频文件地址
        final File trimFile = new File(OutMoviePath);// 裁剪文件保存地址


        final int startS = (int) leftProgress / 1000; // 获取开始时间(单位秒
        final int endS = (int) rightProgress / 1000; // 获取结束时间(单位秒

        //showPleaseDialog("处理中...");
        // 进行裁剪
        TrimVideoUtils.getInstance().startTrimRx(true, startS, endS, file, trimFile)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(new ResourceSubscriber<TrimVideoUtils.TrimResult>() {
                    @Override
                    public void onNext(TrimVideoUtils.TrimResult o) {
                        /**
                         * 裁剪回调
                         * @param isNew 是否新剪辑
                         * @param starts 开始时间(秒)
                         * @param ends 结束时间(秒)
                         * @param vTime 视频长度
                         * @param file 需要裁剪的文件路径
                         * @param trimFile 裁剪后保存的文件路径
                         */
                        // ===========
                        Log.i(TAG, "isNew : " + o.isNew);
                        Log.i(TAG, "startS : " + o.startS);
                        Log.i(TAG, "endS : " + o.endS);
                        Log.i(TAG, "vTotal : " + o.vTotal);
                        Log.i(TAG, "file : " + o.file.getAbsolutePath());
                        Log.i(TAG, "trimFile : " + o.trimFile.getAbsolutePath());

                        Intent intent = new Intent();
                        intent.putExtra("path", o.trimFile.getAbsolutePath());
                        setResult(RESULT_OK, intent);  //改变了储存目录
                        finish();
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 水平滑动了多少px
     * 是根据RecycleView显示最左侧的Item计算，所以是以一个截图时间为步进
     * @return int px
     */
    private int getScrollXDistance() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecycleView.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisibleChildView = layoutManager.findViewByPosition(position);
        int itemWidth = firstVisibleChildView.getWidth();
        return (position) * itemWidth - firstVisibleChildView.getLeft();
    }

    private RangeSeekBar.OnRangeSeekBarChangeListener mOnRangeSeekBarChangeListener = new RangeSeekBar.OnRangeSeekBarChangeListener() {
        @Override
        public void onRangeSeekBarValuesChanged(RangeSeekBar bar, long minValue, long maxValue, int action, boolean isMin, RangeSeekBar.Thumb pressedThumb) {
            // minValue,maxValue（时长，是你画的时候设置的
            Log.w(TAG, "onRangeSeekBarValuesChanged: minValue= " + minValue + " maxValue= " + maxValue + " action= " + action + " isMin= " + isMin);
            leftProgress = minValue + scrollPos;
            rightProgress = maxValue + scrollPos;
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    isSeeking = false;
//                    videoPause();
                    break;
                case MotionEvent.ACTION_MOVE:
                    isSeeking = true;
                    mVideoView.seekTo((int) (pressedThumb == RangeSeekBar.Thumb.MIN ?
                            leftProgress : rightProgress));
                    break;
                case MotionEvent.ACTION_UP:
                    isSeeking = false;
                    //从minValue开始播
                    mVideoView.seekTo((int) leftProgress);
//                    videoStart();
                    break;
                default:
                    break;
            }
        }
    };

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        private boolean isOverScaledTouchSlop;
        private int lastScrollX;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                isSeeking = false;
//                videoStart();
            } else {
                isSeeking = true;
                if (isOverScaledTouchSlop && mVideoView != null && mVideoView.isPlaying()) {
//                    videoPause();
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            isSeeking = false;
            int scrollX = getScrollXDistance();
            //达不到滑动的距离
            if (Math.abs(lastScrollX - scrollX) < ViewConfiguration.get(CropActivity.this).getScaledTouchSlop()) {
                isOverScaledTouchSlop = false;
                return;
            }
            isOverScaledTouchSlop = true;
            Log.e(TAG, "onScrolled: scrollX:>>>>>" + scrollX);
                // why 在这里处理一下,因为onScrollStateChanged早于onScrolled回调
                if (mVideoView != null && mVideoView.isPlaying()) {
//                    videoPause();
                }
                isSeeking = true;
                scrollPos = (long) (mPxTime * scrollX);
                Log.e(TAG, "onScrolled: scrollPos:>>>>>" + scrollPos);
                // 获取当前裁剪框左边位置+滑动进度
                leftProgress = rangeSeekBar.getSelectedMinValue() + scrollPos;
                // 获取当前裁剪框右边位置+滑动进度
                rightProgress = rangeSeekBar.getSelectedMaxValue() + scrollPos;
                Log.e(TAG, "onScrolled: leftProgress:>>>>>" + leftProgress);
//                mVideoView.seekTo((int) leftProgress);
            lastScrollX = scrollX;
        }
    };
}
