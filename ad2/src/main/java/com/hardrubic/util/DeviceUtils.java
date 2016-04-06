package com.hardrubic.util;

import android.content.Context;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public class DeviceUtils {

    /**
     * 获取设备宽度
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    /**
     * 获取设备高度
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    /**
     * 判断是否有外部存储
     */
    public static boolean isHadExternalStorage(Context context) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            ToastUtil.longShow(context, "无外部存储");
            return false;
        }
        return true;
    }

    /**
     * 获取文件系统的前置路径，默认保存在SD卡
     */
    public static String getSystemBaseDir(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return context.getFilesDir().getPath();
        }
    }

    /**
     * 隐藏软键盘
     */
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        IBinder token = view.getWindowToken();
        if (token != null) {
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示软键盘
     */
    public static void showKeyboard(EditText editText) {
        InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }

    /**
     * 返回剩余的空间容量（M）
     */
    public static float getSdAvailableSizeByM(Context context) {
        String dir = getSystemBaseDir(context);
        StatFs sf = new StatFs(dir);
        long blockSize = sf.getBlockSize();
        long availableBlocks = sf.getAvailableBlocks();
        return (float) blockSize * availableBlocks / 1024 / 1024;
    }
}
