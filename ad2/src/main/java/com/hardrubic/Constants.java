package com.hardrubic;

import com.hardrubic.application.AppApplication;
import com.hardrubic.util.DeviceUtils;
import java.io.File;

public class Constants {
    public static String HOST = "http://test.admin.buildingqm.com:80";
    public static final int RESPOND_RESULT_OK = 0;

    /** app文件保存路径 */
    public static String APP_BASE_FILE_PATH = File.separator + "ad2";
    /** app图片保存路径 */
    public static String APP_IMG_FILE_PATH = DeviceUtils.getSystemBaseDir(AppApplication.getContext()) + APP_BASE_FILE_PATH + File.separator + "img" + File.separator;
}
