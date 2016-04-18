package com.hardrubic.util.network;


import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;

import com.hardrubic.application.AppApplication;
import com.hardrubic.util.LogUtils;
import com.hardrubic.util.ToastUtil;
import com.hardrubic.util.network.progress.ProgressCancelListener;
import com.hardrubic.util.network.progress.ProgressDialogHandler;

import rx.Subscriber;

/**
 * 支持进度条的下载观察者
 */
public class DownloadProgressSubscriber extends Subscriber<HttpDownloadResult> implements ProgressCancelListener {

    Context context;
    SubscriberOnNextListener subscriberOnNextListener;
    ProgressDialogHandler progressDialogHandler;

    public DownloadProgressSubscriber(Context context, SubscriberOnNextListener subscriberOnNextListener) {
        this.context = context;
        this.subscriberOnNextListener = subscriberOnNextListener;
        progressDialogHandler = new ProgressDialogHandler(context, this, true);
    }

    private void showProgressDialog() {
        if (progressDialogHandler != null) {
            progressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    private void refreshProgressDialog(double per) {
        if (progressDialogHandler != null) {
            Message message = new Message();
            message.what = ProgressDialogHandler.REFRESH_PROGRESS_DIALOG;
            message.obj = per;
            progressDialogHandler.sendMessage(message);
        }
    }

    private void dismissProgressDialog() {
        if (progressDialogHandler != null) {
            progressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            progressDialogHandler = null;
        }
    }

    @Override
    public void onStart() {
        LogUtils.d(Thread.currentThread().getId() + "");
        showProgressDialog();
    }

    @Override
    public void onNext(HttpDownloadResult result) {
        LogUtils.d(result.toString());
        refreshProgressDialog(result.getPercent());

        if (subscriberOnNextListener != null) {
            subscriberOnNextListener.onNext(result);
        }
    }

    @Override
    public void onCompleted() {
        dismissProgressDialog();
        ToastUtil.longShow(context, "下载成功");
        LogUtils.d("下载成功");
    }

    @Override
    public void onError(Throwable e) {
        dismissProgressDialog();
        ToastUtil.longShow(context, e.getMessage());
        LogUtils.d(e.getMessage());
    }

    @Override
    public void onCancelProgress() {
        //中止请求
        LogUtils.d("中止请求");
        if (!isUnsubscribed()) {
            unsubscribe();
        }
    }
}
