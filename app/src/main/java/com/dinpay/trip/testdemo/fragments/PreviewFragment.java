package com.dinpay.trip.testdemo.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.ChangeImageTransform;
import android.support.transition.Explode;
import android.support.transition.Transition;
import android.support.transition.TransitionValues;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.ViewDragHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.dinpay.trip.testdemo.R;
import com.dinpay.trip.testdemo.beans.ImageItem;

import static android.media.MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;

public class PreviewFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private ImageItem mParam1;

    private float fromX, fromY;
    private float fromWidth, fromHeight;

    public PreviewFragment() {
        // Required empty public constructor
    }

    public static PreviewFragment newInstance(ImageItem imageItem) {
        PreviewFragment fragment = new PreviewFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, imageItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (ImageItem) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        VideoView videoView = view.findViewById(R.id.preview_vv_video);
        videoView.setOnPreparedListener(mp -> {
            mp.setVideoScalingMode(VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            mp.start();
        });
        videoView.setOnCompletionListener(MediaPlayer::start);
        videoView.setVideoPath(mParam1.path);
        ViewDragHelper viewDragHelper = ViewDragHelper.create((ViewGroup) view, new DragCallBack());
    }

    public void show(FragmentManager supportFragmentManager, View view) {
        fromX = view.getX();
        fromY = view.getY();
        fromWidth = view.getMeasuredWidth();
        fromHeight = view.getMeasuredHeight();
        setEnterTransition(new Explode());
        supportFragmentManager.beginTransaction()
                .add(R.id.preview_rl_content, this)
                .commit();
    }

    class DragCallBack extends ViewDragHelper.Callback{

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return false;
        }
    }
}
