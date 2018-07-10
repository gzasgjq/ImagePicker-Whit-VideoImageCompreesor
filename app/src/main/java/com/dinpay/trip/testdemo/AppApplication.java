package com.dinpay.trip.testdemo;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import com.dhh.rxlifecycle2.RxLifecycle;
import com.dhh.websocket.RxWebSocketUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/03/01 17:37
 * 类描述：
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initOkhttp();
        RxLifecycle.injectRxLifecycle(this);
    }

    private void initOkhttp() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor("OkGo");
            interceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        interceptor.setColorLevel(Level.INFO);
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        builder.addInterceptor(interceptor);
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        builder.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier);
        builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));

        OkHttpClient client = builder.build();
        OkGo.getInstance()
                .setRetryCount(0)
                .setOkHttpClient(client)
                .init(this);

        //wss support
//        RxWebSocketUtil.getInstance().setSSLSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
//        RxWebSocketUtil.getInstance().getWebSocket("wss://...");
        //if you want to use your okhttpClient
        RxWebSocketUtil.getInstance().setClient(client);
        // show log,default false
        RxWebSocketUtil.getInstance().setShowLog(true);

        XGPushConfig.enableDebug(this,true);
        XGPushManager.registerPush(this,  new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                //token在设备卸载重装的时候有可能会变
                Log.d("TPush", "注册成功，设备token为：" + data);
                XGPushManager.appendAccount(getApplicationContext(), "XINGE", new XGIOperateCallback() {
                    @Override
                    public void onSuccess(Object o, int i) {
                        Log.d("TPush", "账号注册成功，设备token为：" + o);
                    }

                    @Override
                    public void onFail(Object o, int i, String s) {
                        Log.d("TPush", "账号注册失败，错误码：" + i + ",错误信息：" + s);
                    }
                });
                XGPushManager.setTag(getApplicationContext(), "App");
            }
            @Override
            public void onFail(Object data, int errCode, String msg) {
                Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
            }
        });
    }
}
