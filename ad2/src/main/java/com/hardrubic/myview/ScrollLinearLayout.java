package com.hardrubic.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * 可平滑移动的LinearLayout
 */
public class ScrollLinearLayout extends LinearLayout {
    private Context mContext;
    private Scroller mScroller;

    public ScrollLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public ScrollLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        mScroller = new Scroller(mContext);
    }

    /**
     * 开始平滑移动
     */
    public void beginSmoothScroll(int destX, int destY, int duration) {
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        mScroller.startScroll(scrollX, scrollY, destX - scrollX, destY - scrollY, duration);
        invalidate();
    }

    @Override
    public void computeScroll() {
        //返回true滑动未结束
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }
}
