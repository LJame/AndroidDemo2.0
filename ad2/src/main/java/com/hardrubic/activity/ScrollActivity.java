package com.hardrubic.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hardrubic.myview.ScrollLinearLayout;

import ad2.hardrubic.com.androiddemo20.R;

/**
 * 1、scrollTo/scrollBy：操作简单，适合对View的滑动
 * 2、动画：操作简单，适合没有交互的View和实现复杂动画效果
 * 3、改变布局参数：操作稍微复杂，适合有交互的View
 */
public class ScrollActivity extends TitleActivity implements View.OnClickListener {
    private Context mContext;

    private ScrollLinearLayout mScrollTarget;
    private TextView mTvAnimatorTarget;
    private TextView mTvPpTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);
        this.mContext = this;

        mScrollTarget = (ScrollLinearLayout) findViewById(R.id.tv_scroll_target);
        findViewById(R.id.tv_scroll_btn).setOnClickListener(this);
        findViewById(R.id.tv_scroll_smooth_btn).setOnClickListener(this);

        mTvAnimatorTarget = (TextView) findViewById(R.id.tv_animator_target);
        findViewById(R.id.tv_animator_btn).setOnClickListener(this);

        mTvPpTarget = (TextView) findViewById(R.id.tv_lp_target);
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
                mScrollTarget.beginSmoothScroll(0, -800, 1000);
                break;
            /**
             * 属性动画
             */
            case R.id.tv_animator_btn:
                //TODO 还有ValueAnimator、AnimatorSet等待研究
                ObjectAnimator.ofFloat(mTvAnimatorTarget, "translationY", 0, 800).setDuration(1000).start();
                break;
            /**
             *改变布局参数
             */
            case R.id.tv_lp_btn:
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mTvPpTarget.getLayoutParams();
                params.topMargin += 800;
                mTvPpTarget.requestLayout();
                mTvPpTarget.setLayoutParams(params); //或者mTvPpTarget.requestLayout();
                break;
            default:
                break;
        }
    }


}
