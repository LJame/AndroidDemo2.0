package com.hardrubic.activity;

import ad2.hardrubic.com.androiddemo20.R;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;

import com.hardrubic.Constants;
import com.hardrubic.entity.db.Project;
import com.hardrubic.entity.db.Team;
import com.hardrubic.entity.response.LoginResponse;
import com.hardrubic.entity.response.ProjectListResponse;
import com.hardrubic.util.LogUtils;
import com.hardrubic.util.ToastUtil;
import com.hardrubic.util.network.HttpDownloadResult;
import com.hardrubic.util.network.HttpException;
import com.hardrubic.util.network.HttpManager;
import com.hardrubic.util.network.HttpService;
import com.hardrubic.util.network.SyncExecutorServiceUtil;
import com.hardrubic.util.network.entity.HttpDownloadInfo;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class NetworkActivity extends TitleActivity {
    private Context mContext;
    private String token;

    /**
     * 同步项目接口
     */
    public static final String ERROR_CODE_E001 = "E001";
    /**
     * 同步用户与角色接口
     */
    public static final String ERROR_CODE_E002 = "E002";
    /**
     * 同步项目检查项接口
     */
    public static final String ERROR_CODE_E003 = "E003";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        mContext = this;
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.tv_login_in)
    void clickLoginIn() {
        String username = "htwy";
        String password = "haitou123";

        HttpService.applyLoginIn(username, password, new HttpManager.HttpServiceCallback() {
            @Override
            public <T> void onNext(T result) {
                LoginResponse response = (LoginResponse) result;
                if (response.getResult() == Constants.RESPOND_RESULT_OK) {
                    token = response.getData().getToken();
                    ToastUtil.longShow(mContext, "登陆成功");
                } else {
                    ToastUtil.longShow(mContext, response.getMessage());
                }
            }

            @Override
            public void onFailure(HttpException e) {
                ToastUtil.longShow(mContext, e.getMessage());
            }
        });
    }

    @OnClick(R.id.tv_sync_all)
    void clickSyncAll() {
        if (TextUtils.isEmpty(token)) {
            ToastUtil.longShow(mContext, "请先登陆");
            return;
        }

        LogUtils.d("主线程:" + Thread.currentThread().getId());

        pullProjectList();
    }

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
     * project列表
     */
    private void pullProjectList() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                LogUtils.d("project列表线程:" + Thread.currentThread().getId());

                List<Long> idList = new ArrayList<>();
                try {
                    ProjectListResponse response = HttpService.applyProjectList(token);
                    if (response.getResult() != Constants.RESPOND_RESULT_OK) {
                        throw new HttpException(ERROR_CODE_E001);
                    }

                    List<Project> projectList = response.getData().getProjects();
                    List<Team> teamList = response.getData().getTeams();
                    int projectSize = projectList == null ? 0 : projectList.size();
                    int teamSize = teamList == null ? 0 : teamList.size();
                    LogUtils.d("同步项目成功，数量：projectList_" + projectSize + " teamList_" + teamSize);

                    for (Project project : projectList) {
                        idList.add(project.getId());
                    }
                } catch (HttpException e) {
                    subscriber.onError(e);
                }

                subscriber.onNext(TextUtils.join(",", idList));
            }
        })
                .subscribeOn(Schedulers.from(SyncExecutorServiceUtil.getFixedThreadPool()))
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String o) {
                        LogUtils.d("onNext");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

}
