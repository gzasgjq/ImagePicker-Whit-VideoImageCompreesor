package com.dinpay.trip.testdemo.activites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dinpay.trip.testdemo.R;

public class GetPhoneInfoActivity extends BaseActivity {
    private String TAG = "GetPhoneInfoActivity";

    public static void start(Context context) {
        Intent starter = new Intent(context, GetPhoneInfoActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_video);
    }

}
