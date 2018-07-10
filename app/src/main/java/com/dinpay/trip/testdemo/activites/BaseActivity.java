package com.dinpay.trip.testdemo.activites;

import android.support.v7.app.AppCompatActivity;

import com.dhh.rxlifecycle2.ActivityEvent;
import com.dhh.rxlifecycle2.LifecycleTransformer;
import com.dhh.rxlifecycle2.RxLifecycle;

import io.reactivex.Observable;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/03/01 17:54
 * 类描述：
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class BaseActivity extends AppCompatActivity {

    protected  <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycle.with(this).bindToLifecycle();
    }

    protected  <T> LifecycleTransformer<T> bindOnDestroy() {
        return RxLifecycle.with(this).bindOnDestroy();
    }

    protected  <T> LifecycleTransformer<T> bindUntilEvent(ActivityEvent event) {
        return RxLifecycle.with(this).bindUntilEvent(event);
    }

    //use
    @Override
    protected void onStart() {
        super.onStart();
        Observable.just(1)
                //use
                .compose(bindToLifecycle())
                .subscribe();
    }
}
