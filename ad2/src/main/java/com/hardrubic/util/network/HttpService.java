package com.hardrubic.util.network;


import com.hardrubic.Constants;
import com.hardrubic.entity.response.LoginResponse;
import com.hardrubic.entity.response.UploadAuthResponse;
import com.hardrubic.entity.response.UploadPhotoResponse;
import java.io.File;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import rx.Observable;
import rx.schedulers.Schedulers;

public class HttpService {

    public static Observable<HttpDownloadResult> applyDownloadPhoto(String urlStr, String savePath, ExecutorService executorService) {
        return HttpManager.getInstance().download(urlStr, savePath, Schedulers.from(executorService));
    }

    /**
     * 2.1 登录接口
     */
    public static Observable<LoginResponse> applyLoginIn(String username, String password) {
        TreeMap<String, String> treeMap = new TreeMap<>();
        treeMap.put("username", username);
        treeMap.put("password", password);
        return HttpManager.getInstance().send(Constants.HOST + HttpServiceRest.URL_LOGIN_IN, treeMap, Schedulers.io());
    }

    /**
     * 4.5 获取上传凭证
     */
    public static UploadAuthResponse applyUploadAuth(String token) {
        final TreeMap<String, String> treeMap = new TreeMap<>();
        treeMap.put("token", token);

        return HttpManager.getInstance().send(Constants.HOST + HttpServiceRest.URL_GET_UPLOAD_AUTH, treeMap);
    }

    /**
     * 4.6 上传图片文件
     */
    public static Observable<UploadPhotoResponse> applyUploadPhoto(String token, String auth, String md5, Long projectId, File file) {
        final TreeMap<String, String> treeMap = new TreeMap<>();
        treeMap.put("token", token);
        treeMap.put("auth", auth);
        treeMap.put("md5", md5);
        treeMap.put("project_id", projectId.toString());

        TreeMap<String, File> fileMap = new TreeMap<>();
        fileMap.put("userfile", file);

        return HttpManager.getInstance().send(Constants.HOST + HttpServiceRest.URL_UPLOAD_IMAGE, treeMap, fileMap, Schedulers.io());
    }
}
