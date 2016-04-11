package com.hardrubic.util.network;


import com.hardrubic.entity.response.LoginResponse;
import com.hardrubic.entity.response.ProjectListResponse;
import com.hardrubic.entity.response.UploadAuthResponse;
import com.hardrubic.entity.response.UploadPhotoResponse;
import java.util.TreeMap;
import okhttp3.RequestBody;
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

    /**
     * 通用下载
     */
    @GET
    @Streaming
    Call<ResponseBody> download(@Url String url);

    /**
     * 2.1 登录接口
     */
    String URL_LOGIN_IN = "/v2/api/login";

    @FormUrlEncoded
    @POST(URL_LOGIN_IN)
    //Call<LoginResponse> doLoginIn(@FieldMap() TreeMap<String, String> paramMap);
    Call<LoginResponse> doLoginIn(@FieldMap() TreeMap<String, String> paramMap);

    /**
     * 4.1项目列表
     */
    String URL_PROJECT_LIST = "/v2/api/mine/teams_and_project";

    @GET(URL_PROJECT_LIST)
    Call<ProjectListResponse> doGetProjectList(@QueryMap TreeMap<String, String> paramMap);

    /**
     * 4.5获取上传凭证
     */
    String URL_GET_UPLOAD_AUTH = "/v2/api/project/upload_auth";

    @GET(URL_GET_UPLOAD_AUTH)
    Call<UploadAuthResponse> doGetUploadAuth(@QueryMap TreeMap<String, String> paramMap);

    /**
     * 4.6上传项目图片文件
     */
    String URL_UPLOAD_IMAGE = "/v2/api/project/report_image";

    @FormUrlEncoded
    @POST(URL_UPLOAD_IMAGE)
    Call<UploadPhotoResponse> doUploadImage(@FieldMap TreeMap<String, String> paramMap, @FieldMap TreeMap<String, RequestBody> fileMap);
}
