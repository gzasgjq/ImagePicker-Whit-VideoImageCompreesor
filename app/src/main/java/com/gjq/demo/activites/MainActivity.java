package com.gjq.demo.activites;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gjq.demo.R;
import com.gjq.demo.commom.GetPhoneInfoUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends BaseActivity {

    // Used to load the 'native-lib' library on application startup.
//    static {
//        System.loadLibrary("native-lib");
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
//        TextView tv = (TextView) findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());
        findViewById(R.id.btn_home_recycle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RecycleViewFullActivity.class));
            }
        });
        findViewById(R.id.btn_home_websocket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebSocketActivity.start(MainActivity.this);
            }
        });
        findViewById(R.id.btn_home_crop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropVideoActivity.start(MainActivity.this);
            }
        });
        findViewById(R.id.btn_home_pllax).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestHomeActivity.start(MainActivity.this);
            }
        });
        findViewById(R.id.btn_home_scroll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScrollingActivity.start(MainActivity.this);
            }
        });
        findViewById(R.id.btn_get_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("guo", "getPhoneInfo = " + GetPhoneInfoUtil.getInstance().getPhoneInfo());
                Log.i("guo", "getDeviceBrand = " + GetPhoneInfoUtil.getInstance().getDeviceBrand());
                Log.i("guo", "getHandSetInfo = " + GetPhoneInfoUtil.getInstance().getHandSetInfo());
                Log.i("guo", "getPhoneModel = " + GetPhoneInfoUtil.getInstance().getPhoneModel());
                Log.i("guo", "getRomInfo = " + GetPhoneInfoUtil.getInstance().getRomInfo());
                Log.i("guo", "getSystemVersion = " + GetPhoneInfoUtil.getInstance().getSystemVersion());
                Log.i("guo", "getVersion = " + GetPhoneInfoUtil.getInstance().getVersion(MainActivity.this));
            }
        });
        findViewById(R.id.btn_face).setOnClickListener(v ->
            FaceActivity.start(MainActivity.this)
        );
        findViewById(R.id.btn_wechat_preview).setOnClickListener(v ->
            WeChatPreviewActivity.start(MainActivity.this)
        );
        TextView viewById = (TextView) findViewById(R.id.phone_info);
        viewById.setText(String.format("is64Bit : %s", GetPhoneInfoUtil.getCpuArchitecture()));
        viewById.append(GetPhoneInfoUtil.getInstance().getPhoneInfo());
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
//    public native String stringFromJNI();
}
