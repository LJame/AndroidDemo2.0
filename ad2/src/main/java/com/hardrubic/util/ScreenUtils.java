package com.hardrubic.util;

import android.content.Context;

/**
 * 获取屏幕宽高;
 * px和dp之间转换;
 * @author chengkai
 *
 */
public class ScreenUtils {

    private ScreenUtils() {
        throw new AssertionError();
    }

    /**
     * get the width(in px) of screen
     */
    public static int getScreenWidthPixels(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * get the height(in px) of screen
     */
    public static int getScreenHeightPixels(Context context){
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static float dpToPx(Context context, float dp) {
        if (context == null) {
            return -1;
        }
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float pxToDp(Context context, float px) {
        if (context == null) {
            return -1;
        }
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static int dpToPxInt(Context context, float dp) {
        return (int)(dpToPx(context, dp) + 0.5f);
    }

    public static int pxToDpInt(Context context, float px) {
        return (int)(pxToDp(context, px) + 0.5f);
    }

}
