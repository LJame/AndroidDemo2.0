package com.hardrubic.util.network;


import com.hardrubic.Constants;
import com.hardrubic.entity.response.LoginResponse;
import com.hardrubic.entity.response.ProjectListResponse;
import java.util.TreeMap;
import retrofit2.Response;

public class HttpService {

    /** 2.1 登录接口 */
    public static LoginResponse applyLoginIn(String username, String password) throws HttpException {
        TreeMap<String, String> treeMap = new TreeMap<>();
        treeMap.put("username", username);
        treeMap.put("password", password);
        Response<LoginResponse> response = HttpManager.getInstance().send(Constants.HOST + HttpServiceRest.URL_LOGIN_IN, treeMap);
        return response.body();
    }

    /** 2.1 登录接口 */
    public static void applyLoginIn(String username, String password, HttpManager.HttpServiceCallback callback){
        TreeMap<String, String> treeMap = new TreeMap<>();
        treeMap.put("username", username);
        treeMap.put("password", password);
        HttpManager.getInstance().send(Constants.HOST + HttpServiceRest.URL_LOGIN_IN, treeMap, callback);
    }

    /** 4.1项目列表 */
    public static ProjectListResponse applyProjectList(String token) throws HttpException {
        final TreeMap<String, String> treeMap = new TreeMap<>();
        treeMap.put("token", token);
        treeMap.put("timestamp", "0");

        ProjectListResponse response = HttpManager.getInstance().send(Constants.HOST + HttpServiceRest.URL_PROJECT_LIST, treeMap);
        return response;
    }
}
