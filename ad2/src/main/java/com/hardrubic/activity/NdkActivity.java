package com.hardrubic.activity;

import ad2.hardrubic.com.androiddemo20.R;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

public class NdkActivity extends TitleActivity {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ndk);
        mContext = this;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_call_hello_world:
                //String msg = getHelloWorldFromNative();
                //ToastUtil.longShow(mContext,msg);
                break;
            default:
                break;
        }
    }

    static {
        //System.loadLibrary("MyJni");//导入生成的链接库文件
    }

    //public native String getStringFromNative();

}
