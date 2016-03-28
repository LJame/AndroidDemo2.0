package com.hardrubic.activity;

import ad2.hardrubic.com.androiddemo20.R;
import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.gson.Gson;
import com.hardrubic.util.LogUtils;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkActivity extends TitleActivity {

    private final static boolean isOkHttp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.tv_get)
    void applyGet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isOkHttp) {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request request = new Request.Builder().url("http://www.baidu.com").build();
                    Response response;
                    try {
                        response = okHttpClient.newCall(request).execute();
                        LogUtils.d(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        }).start();
    }

    @OnClick(R.id.tv_post)
    void applyPost() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isOkHttp) {
                    MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                    OkHttpClient okHttpClient = new OkHttpClient();

                    Login login = new Login();
                    login.setUsername("htwy");
                    login.setPassword("haitou123");

                    RequestBody body = RequestBody.create(mediaType, new Gson().toJson(login));
                    Request request = new Request.Builder()
                            .url("http://zj.buildingqm.com/v2/api/login")
                            .post(body).build();
                    Response response = null;
                    try {
                        response = okHttpClient.newCall(request).execute();
                        LogUtils.d(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        }).start();
    }

    class Login {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
