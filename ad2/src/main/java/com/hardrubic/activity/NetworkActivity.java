package com.hardrubic.activity;

import ad2.hardrubic.com.androiddemo20.R;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hardrubic.Constants;
import com.hardrubic.entity.db.Project;
import com.hardrubic.entity.db.Team;
import com.hardrubic.entity.response.LoginResponse;
import com.hardrubic.entity.response.ProjectListResponse;
import com.hardrubic.entity.response.UploadAuthResponse;
import com.hardrubic.entity.response.UploadPhotoResponse;
import com.hardrubic.util.LogUtils;
import com.hardrubic.util.MD5Utils;
import com.hardrubic.util.ToastUtil;
import com.hardrubic.util.network.HttpException;
import com.hardrubic.util.network.HttpManager;
import com.hardrubic.util.network.HttpService;
import com.hardrubic.util.network.PreferencesUtils;
import com.hardrubic.util.network.entity.HttpDownloadInfo;
import com.hardrubic.util.network.entity.HttpDownloadResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import rx.functions.Action1;

public class NetworkActivity extends TitleActivity {
    private Context mContext;
    private String token;
    private String auth;
    private String msg = "";
    private HttpDownloadResult downloadFileInfo;

    private TextView tv_token;

    /**
     * 同步项目接口
     */
    public static final String ERROR_CODE_E001 = "E001";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        mContext = this;
        ButterKnife.bind(this);

        tv_token = (TextView) findViewById(R.id.tv_token);
        token = PreferencesUtils.getInstance().getString("token");
        if (!TextUtils.isEmpty(token)) {
            tv_token.setText(token);
            tv_token.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    /**
     * 登陆
     */
    @OnClick(R.id.tv_login_in)
    void clickLoginIn() {
        String username = "htwy";
        String password = "haitou123";

        HttpService.applyLoginIn(username, password).subscribe(new Action1<LoginResponse>() {
            @Override
            public void call(LoginResponse response) {
                if (response.getResult() == Constants.RESPOND_RESULT_OK) {
                    token = response.getData().getToken();
                    tv_token.setText(token);
                    tv_token.setVisibility(View.VISIBLE);
                    PreferencesUtils.getInstance().putString("token", token);
                    ToastUtil.longShow(mContext, "登陆成功");
                } else {
                    ToastUtil.longShow(mContext, response.getMessage());
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                ToastUtil.longShow(mContext, throwable.getMessage());
            }
        });
    }

    /**
     * 同步
     */
    @OnClick(R.id.tv_sync_all)
    void clickSyncAll() {
        if (TextUtils.isEmpty(token)) {
            ToastUtil.longShow(mContext, "请先登陆");
            return;
        }

        LogUtils.d("主线程:" + Thread.currentThread().getId());

        pullProjectList();
    }

    /**
     * 文件下载
     */
    @OnClick(R.id.tv_download_file)
    void clickDownloadFile() {
        final List<HttpDownloadInfo> infoList = new ArrayList<>();
        String path = Constants.APP_IMG_FILE_PATH;
        infoList.add(new HttpDownloadInfo("http://7xrnwo.com2.z0.glb.qiniucdn.com/pictures/8c1e3a8f45076c68a9b9ff3e6c0ff75d..jpg", path));
        infoList.add(new HttpDownloadInfo("http://7xrnwo.com2.z0.glb.qiniucdn.com/pictures/db6cbb49672b9e0fd3336ca34c96b30d..jpg", path));
        infoList.add(new HttpDownloadInfo("http://7xrnwo.com2.z0.glb.qiniucdn.com/pictures/90cd56facb610f6039daf45923f2698d..jpg", path));
        infoList.add(new HttpDownloadInfo("http://7xrnwo.com2.z0.glb.qiniucdn.com/pictures/11e7b19623e41fd198500610403b64f1..jpg", path));
        infoList.add(new HttpDownloadInfo("http://7xrnwo.com2.z0.glb.qiniucdn.com/pictures/4ceb013c7ceec05842a58617d77a7030..jpg", path));

        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtils.d("开始下载");
                List<HttpDownloadResult> resultList = null;
                try {
                    resultList = HttpManager.getInstance().download(infoList);
                } catch (HttpException e) {
                    e.printStackTrace();
                }

                int successSize = 0;
                int failSize = 0;
                for (HttpDownloadResult result : resultList) {
                    if (result.getResult()) {
                        successSize++;
                        downloadFileInfo = result;
                    } else {
                        failSize++;
                        LogUtils.w(result.getUrl() + " ------ " + result.getException().getMessage());
                    }
                }

                final int finalSuccessSize = successSize;
                final int finalFailSize = failSize;
                NetworkActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.longShow(mContext, "下载完毕,成功" + finalSuccessSize + ",失败" + finalFailSize);
                    }
                });
            }
        }).start();
    }

    /**
     * 文件上传
     */
    @OnClick(R.id.tv_upload_file)
    void clickUploadFile() {
        if (downloadFileInfo == null) {
            ToastUtil.longShow(mContext, "没有待上传文件");
            return;
        }

        if (TextUtils.isEmpty(token)) {
            ToastUtil.longShow(mContext, "请先登陆");
            return;
        }

        final File file = new File(downloadFileInfo.getSavePath());
        final String md5 = MD5Utils.calculate(file);
        final Long projectId = 12l;
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取上传凭证
                HttpService.applyUploadAuth(token).subscribe(new Action1<UploadAuthResponse>() {
                    @Override
                    public void call(UploadAuthResponse response) {
                        if (response.getResult() == Constants.RESPOND_RESULT_OK) {
                            auth = response.getData().getUpload_auth();
                        } else {
                            msg = response.getMessage();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        e.printStackTrace();
                        msg = e.getMessage();
                    }
                });

                //上传图片
                if (!TextUtils.isEmpty(auth)) {
                    HttpService.applyUploadPhoto(token, auth, md5, projectId, file).subscribe(new Action1<UploadPhotoResponse>() {
                        @Override
                        public void call(UploadPhotoResponse response) {
                            if (response.getResult() == Constants.RESPOND_RESULT_OK) {
                                msg = "上传文件成功";
                            } else {
                                msg = response.getMessage();
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable e) {
                            e.printStackTrace();
                            msg = e.getMessage();
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.longShow(mContext, msg);
                    }
                });
            }
        }).start();
    }

    /**
     * project列表
     */
    private void pullProjectList() {
        HttpService.applyProjectList(token).subscribe(new Action1<ProjectListResponse>() {
            @Override
            public void call(ProjectListResponse response) {
                if (response.getResult() == Constants.RESPOND_RESULT_OK) {
                    List<Project> projectList = response.getData().getProjects();
                    List<Team> teamList = response.getData().getTeams();
                    int projectSize = projectList == null ? 0 : projectList.size();
                    int teamSize = teamList == null ? 0 : teamList.size();
                    LogUtils.d("同步项目成功，数量：projectList_" + projectSize + " teamList_" + teamSize);
                } else {
                    ToastUtil.longShow(mContext, response.getMessage());
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                ToastUtil.longShow(mContext, throwable.getMessage());
            }
        });
    }

}
