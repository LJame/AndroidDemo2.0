package com.hardrubic.activity;

import ad2.hardrubic.com.androiddemo20.R;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import com.hardrubic.util.ToastUtil;

public class NdkActivity extends TitleActivity {
    private Context mContext;

    static {
        System.loadLibrary("jni-test");//导入生成的链接库文件
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ndk);
        mContext = this;

        findViewById(R.id.tv_call_hello_world).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_call_hello_world:
                String msg = sayHello();
                ToastUtil.longShow(mContext,msg);
                break;
            default:
                break;
        }
    }

    public native String sayHello();
}
