package com.hardrubic.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Toast工具类
 *
 * @author Sunny
 */
public class ToastUtil {

    public static void shortShow(Context context, String text) {
        if (StringUtil.isEmpty(text)) {
            return;
        }
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.setText(text);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void shortShow(Context context, int resId) {
        Toast toast = Toast.makeText(context, resId, Toast.LENGTH_LONG);
        toast.setText(resId);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void longShow(Context context, String text) {
        if (StringUtil.isEmpty(text)) {
            return;
        }
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.setText(text);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void longShow(Context context, int resId) {
        Toast toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        toast.setText(resId);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
