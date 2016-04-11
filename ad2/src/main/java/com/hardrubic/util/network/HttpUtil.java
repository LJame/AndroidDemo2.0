package com.hardrubic.util.network;


import android.text.TextUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class HttpUtil {

    /**
     * 获取URL对应的执行方法
     */
    public static Method getUrlMethod(URL url) {
        StringBuilder path = new StringBuilder(url.getPath());
        if (!TextUtils.isEmpty(url.getQuery())) {
            path.append("?");
            path.append(url.getQuery());
        }
        String urlPath = path.toString();

        Method[] methodArray = HttpServiceRest.class.getMethods();
        Method retMethod = null;
        for (Method method : methodArray) {
            GET getAnnotation = method.getAnnotation(GET.class);
            if (getAnnotation != null && urlPath.equals(getAnnotation.value())) {
                retMethod = method;
                break;
            }
            POST postAnnotation = method.getAnnotation(POST.class);
            if (postAnnotation != null && urlPath.equals(postAnnotation.value())) {
                retMethod = method;
                break;
            }
        }
        return retMethod;
    }

    /**
     * 获取下载文件的文件名
     */
    public static String createFileName() {
        //用“时间加随机数”生成文件名
        int randomNum = new Random().nextInt(1000) + 9000;
        String dateStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        StringBuilder newFileName = new StringBuilder();
        newFileName.append(dateStr);
        newFileName.append(String.valueOf(randomNum));
        return newFileName.toString();
    }

    /**
     * 从response保存文件到磁盘
     */
    public static String saveFileAtDisk(ResponseBody responseBody, String fileSavePath)
            throws Exception {
        File targetFile;

        if (fileSavePath.endsWith("/")) {
            // 目标路径是文件夹
            File dir = new File(fileSavePath);
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }
            // 创建文件名
            String newFileName = createFileName();
            targetFile = new File(dir, newFileName);
        } else {
            // 目标路径是文件
            targetFile = new File(fileSavePath);
            if (!targetFile.exists()) {
                File dir = targetFile.getParentFile();
                if (dir.exists() || dir.mkdirs()) {
                    targetFile.createNewFile();
                }
            }
        }

        //写文件
        FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
        InputStream inputStream = responseBody.byteStream();
        byte[] buffer = new byte[1024 * 100];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            fileOutputStream.write(buffer, 0, length);
        }

        //close
        if (null != inputStream) {
            inputStream.close();
        }
        if (null != fileOutputStream) {
            fileOutputStream.close();
        }
        return targetFile.getAbsolutePath();
    }
}
