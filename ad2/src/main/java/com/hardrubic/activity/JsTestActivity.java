package com.hardrubic.activity;

import ad2.hardrubic.com.androiddemo20.R;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.google.gson.Gson;
import com.hardrubic.util.LogUtils;
import com.hardrubic.util.StringUtil;
import com.hardrubic.util.ToastUtil;

public class JsTestActivity extends TitleActivity {
    private Context mContext;

    private WebView mCommonWebView;
    private BridgeWebView mBridgeWebView;

    private Handler mHandler = new Handler();

    int RESULT_CODE = 0;
    ValueCallback<Uri> mUploadMessage;

    static class Location {
        String address;
    }

    static class User {
        String name;
        Location location;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mContext = this;
        setContentView(R.layout.activity_js_test);


        /**
         * 普通WebView
         */
        mCommonWebView = (WebView) findViewById(R.id.common_web_view);
        final ProgressBar progressBar1 = (ProgressBar) findViewById(R.id.pb_progress_1);

        WebSettings webSettings = mCommonWebView.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);

        mCommonWebView.setWebChromeClient(new WebChromeClient() {

            /**
             * 重写js alert显示方式
             */
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                LogUtils.d(url + " " + message);
                result.confirm();

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("计算结果");
                builder.setMessage(message);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return true;
            }

            /**
             * 关联进度条
             */
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar1.setVisibility(View.INVISIBLE);
                } else {
                    if (View.INVISIBLE == progressBar1.getVisibility()) {
                        progressBar1.setVisibility(View.VISIBLE);
                    }
                    progressBar1.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        //
        mCommonWebView.addJavascriptInterface(new CommonWebViewDemoJavaScriptInterface(), "cal");

        //加载计算用的js
        mCommonWebView.loadUrl("file:///android_asset/js_calculator.html");


        /**
         * BridgeWebView ----------------------------------------------------------------------------------------》
         */
        mBridgeWebView = (BridgeWebView) findViewById(R.id.bridge_web_view);
        mBridgeWebView.setWebChromeClient(new WebChromeClient() {

            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType, String capture) {
                this.openFileChooser(uploadMsg);
            }

            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType) {
                this.openFileChooser(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                pickFile();
            }
        });
        mBridgeWebView.loadUrl("file:///android_asset/jsbridge_demo.html");

        //默认处理的handler，js调用send方法，不用指定handleName
        mBridgeWebView.setDefaultHandler(new DefaultHandler());
        //注册java方法，js可以调用
        mBridgeWebView.registerHandler("submitFromWeb", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                LogUtils.d("handler = submitFromWeb, data from web = " + data);
                function.onCallBack("submitFromWeb exe, response data 中文 from Java");
            }

        });

        //Java调用Js
        findViewById(R.id.button).setOnClickListener(this);

        //向Js init方法发送信息
        mBridgeWebView.send("hello");
    }

    /**
     * 在Html中被调用的Java方法
     */
    public class CommonWebViewDemoJavaScriptInterface {

        /**
         * This is not called on the UI thread. Post a runnable to invoke loadUrl on the UI thread.
         */
        @JavascriptInterface
        public void calAdd() {
            LogUtils.d("calAdd");

            AppCompatEditText etNum1 = (AppCompatEditText) findViewById(R.id.et_num_1);
            AppCompatEditText etNum2 = (AppCompatEditText) findViewById(R.id.et_num_2);
            final String num1 = etNum1.getText().toString().trim();
            final String num2 = etNum2.getText().toString().trim();
            if (StringUtil.isEmpty(num1) || StringUtil.isEmpty(num2)) {
                ToastUtil.longShow(mContext, "请输入数字");
                return;
            }

            mHandler.post(new Runnable() {
                public void run() {
                    //Java调用Js方法
                    mCommonWebView.loadUrl("javascript:add(" + num1 + "," + num2 + ")");
                }
            });

        }
    }

    public void pickFile() {
        Intent chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooserIntent.setType("image/*");
        startActivityForResult(chooserIntent, RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RESULT_CODE) {
            if (null == mUploadMessage) {
                return;
            }
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }

    @Override
    public void onClick(View v) {
        mBridgeWebView.callHandler("functionInJs", "data from Java", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                LogUtils.d("response data from js " + data);
            }

        });

        User user = new User();
        Location location = new Location();
        location.address = "SDU";
        user.location = location;
        user.name = "大头鬼";
        mBridgeWebView.callHandler("functionInJs", new Gson().toJson(user), new CallBackFunction() {
            @Override
            public void onCallBack(String data) {

            }
        });
    }
}
