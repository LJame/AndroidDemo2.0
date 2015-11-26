package com.hardrubic.activity;

import ad2.hardrubic.com.androiddemo20.R;
import android.os.Bundle;
import android.view.View;
import com.hardrubic.myview.ScrollLinearLayout;

public class ScrollActivity extends TitleActivity implements View.OnClickListener {
    private ScrollLinearLayout mScrollTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);

        mScrollTarget = (ScrollLinearLayout) findViewById(R.id.tv_scroll_target);
        findViewById(R.id.tv_scroll_btn).setOnClickListener(this);
        findViewById(R.id.tv_scroll_smooth_btn).setOnClickListener(this);

        findViewById(R.id.tv_animator_btn).setOnClickListener(this);

        findViewById(R.id.tv_lp_btn).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * scrollTo 绝对移动 scrollBy 相对移动
             */
            case R.id.tv_scroll_btn:
                //scroll直接移动
                mScrollTarget.scrollBy(0, -20);
                break;
            /**
             * 平滑移动思想：将一次大的移动分成若干次小的移动，并在一个时间段内完成
             */
            case R.id.tv_scroll_smooth_btn:
                //scroll平滑移动
                mScrollTarget.beginSmoothScroll(0, -500, 1000);
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
