package com.hardrubic.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Bundle;

import com.hardrubic.util.LogUtils;

import ad2.hardrubic.com.androiddemo20.R;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.HandlerScheduler;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class RxJavaActivity extends TitleActivity {
    private Context mContext;
    private Handler managerThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java);
        ButterKnife.bind(this);
        mContext = this;
    }

    @OnClick(R.id.tv_test)
    void test() {
        LogUtils.d("主线程：" + Thread.currentThread().getId());

        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtils.d("管理线程：" + Thread.currentThread().getId());
                managerThreadHandler = new Handler();

                doing();

                while (true) {

                }
            }
        }).start();
    }

    private void doing(){
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                LogUtils.d("线程2：" + Thread.currentThread().getId());
                subscriber.onNext("hello");
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(HandlerScheduler.from(managerThreadHandler))
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        LogUtils.d("线程3：" + Thread.currentThread().getId());
                        LogUtils.d(s);
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this);
    }
}
