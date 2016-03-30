package com.hardrubic.util.network;


import android.text.TextUtils;
import java.lang.reflect.Method;
import java.net.URL;
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
        String urlPath = path.deleteCharAt(0).toString();

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
}
