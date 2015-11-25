package com.hardrubic.activity;

import ad2.hardrubic.com.androiddemo20.R;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class ScrollActivity extends TitleActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);

        findViewById(R.id.tv_scroll_btn).setOnClickListener(this);
        findViewById(R.id.tv_animator_btn).setOnClickListener(this);
        findViewById(R.id.tv_lp_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_scroll_btn:
                LinearLayout tvScrollTarget = (LinearLayout) findViewById(R.id.tv_scroll_target);
                tvScrollTarget.scrollBy(0, 100);
                break;
            case R.id.tv_animator_btn:
                break;
            case R.id.tv_lp_btn:
                break;
            default:
                break;
        }
    }
}
