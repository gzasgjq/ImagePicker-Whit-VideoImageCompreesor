package com.dinpay.trip.testdemo.activites;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dinpay.trip.testdemo.R;
import com.dinpay.trip.testdemo.adapter.BaseAdapterTest;
import com.dinpay.trip.testdemo.adapter.SubAdapter.SubAdapter1;
import com.dinpay.trip.testdemo.commom.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class TestHomeActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final String TAG = "TestHome";
    private RefreshLayout recycleView;
    private TextView mTvTitle2;
    private Button mSub1,mSub2;
    private AppBarLayout mAppBar;
    float scale = 0.4f;
    private float orginSize;

    public static void start(Context context) {
        Intent starter = new Intent(context, TestHomeActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_home);
        recycleView = findViewById(R.id.recycleView);
//        recycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            data.add("" + i);
        }
        recycleView.setAdapterAndLoadMoreListener(new BaseAdapterTest(data), new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

            }
        });
        mAppBar = findViewById(R.id.app_bar);
        mTvTitle2 = findViewById(R.id.title2);
        mSub1 = findViewById(R.id.imageButton1);
        mSub2 = findViewById(R.id.imageButton2);
        mAppBar.addOnOffsetChangedListener(this);
        mTvTitle2.setPivotX(0);
        orginSize = mSub1.getTextSize();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        float per = (float) Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange();
        Log.e(TAG, "Per: " + per + " onOffsetChanged: " + verticalOffset + " appBarLayoutHeigt: " + appBarLayout.getTotalScrollRange());

        mTvTitle2.setPivotY(mTvTitle2.getMeasuredHeight());
        mTvTitle2.setScaleX(scale + ((1 - scale) * (1 - per)));
        mTvTitle2.setScaleY(scale + ((1 - scale) * (1 - per)));

        mSub1.setTextSize(TypedValue.COMPLEX_UNIT_PX, orginSize * (1 - per));
        mSub2.setTextSize(TypedValue.COMPLEX_UNIT_PX, orginSize * (1 - per));
        mSub1.setCompoundDrawablePadding(Math.round(-50f * per));
        mSub2.setCompoundDrawablePadding(Math.round(-50f * per));
    }
}
