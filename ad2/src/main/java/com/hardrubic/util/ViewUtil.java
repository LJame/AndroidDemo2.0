package com.hardrubic.util;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ViewUtil {

	/**
	 * 设置一个view的宽度
	 * @param view
	 * @param width
	 */
	public static void setWidth(View view, int width) {
		LayoutParams params = view.getLayoutParams();
		params.width = width;
		view.setLayoutParams(params);
	}

	/**
	 * 设置一个view的高度
	 * @param view
	 * @param height
	 */
	public static void setHeight(View view, int height) {
		LayoutParams params = view.getLayoutParams();
		params.height = height;
		view.setLayoutParams(params);
	}

	/**
	 * 设置一个view的宽高
	 * @param view
	 * @param width
	 * @param height
	 */
	public static void setWidthAndHeight(View view,int width, int height) {
		LayoutParams params = view.getLayoutParams();
		params.width = width;
		params.height = height;
		view.setLayoutParams(params);
	}

    /**
     * 获取ListView高度
     * @param listView
     * @return
     */
	public static int getTotalHeightOfListView(ListView listView) {
		ListAdapter mAdapter = listView.getAdapter();
		if (mAdapter == null) {
			return 0;
		}
		int totalHeight = 0;
		for (int i = 0; i < mAdapter.getCount(); i++) {
			View mView = mAdapter.getView(i, null, listView);
			mView.measure(
					View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
					View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
			totalHeight += mView.getMeasuredHeight();
		}
		return totalHeight;
	}

    /**
     * 判断点击点是否在指定View上
     */
    public static boolean inRangeOfView(View view, MotionEvent ev){
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if(ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y || ev.getY() > (y + view.getHeight())){
            return false;
        }
        return true;
    }

}
