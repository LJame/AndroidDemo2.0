package com.hardrubic.util.network;


import com.hardrubic.entity.response.LoginResponse;
import com.hardrubic.entity.response.ProjectListResponse;
import java.util.TreeMap;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface HttpServiceRest {

    /** 通用下载 */
    @GET
    @Streaming
    Call<ResponseBody> download(@Url String url);

    /** 2.1 登录接口 */
    String URL_LOGIN_IN = "v2/api/login";

    @FormUrlEncoded
    @POST(URL_LOGIN_IN)
    Call<LoginResponse> doLoginIn(@FieldMap() TreeMap<String, String> paramMap);

    /** 4.1项目列表 */
    String URL_PROJECT_LIST = "v2/api/mine/teams_and_project";

    @GET(URL_PROJECT_LIST)
    Call<ProjectListResponse> doGetProjectList(@QueryMap TreeMap<String, String> paramMap);
}
