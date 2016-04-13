package com.hardrubic.activity;

import ad2.hardrubic.com.androiddemo20.R;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
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
import com.hardrubic.util.network.HttpService;
import com.hardrubic.util.network.PreferencesUtils;
import com.hardrubic.util.network.SyncExecutorServiceUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class NetworkActivity extends TitleActivity {
    private Context mContext;
    private String token;
    private String auth;
    private String msg = "";
    private String downloadFilePath;

    @Bind(R.id.tv_token)
    TextView tv_token;
    @Bind(R.id.pb_download)
    ProgressBar pb_download;

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
     * 多文件下载 三线程,每线程1000图片,196.125s;每个线程一张,193.564s
     */
    @OnClick(R.id.tv_download_more_file)
    void clickDownloadMoreFile() {
        final String path = Constants.APP_IMG_FILE_PATH;
        final int num = 1;

        final ExecutorService executorService = SyncExecutorServiceUtil.getFixedThreadPool();
        final CountDownLatch latch = new CountDownLatch(num);
        long time1 = System.currentTimeMillis();

        /*
        final List<HttpDownloadInfo> infoList = new ArrayList<>();
        for (int i = 0; i < 600; i++) {
            infoList.add(new HttpDownloadInfo("http://7xrnwo.com2.z0.glb.qiniucdn.com/pictures/8c1e3a8f45076c68a9b9ff3e6c0ff75d..jpg", path));
            infoList.add(new HttpDownloadInfo("http://7xrnwo.com2.z0.glb.qiniucdn.com/pictures/db6cbb49672b9e0fd3336ca34c96b30d..jpg", path));
            infoList.add(new HttpDownloadInfo("http://7xrnwo.com2.z0.glb.qiniucdn.com/pictures/90cd56facb610f6039daf45923f2698d..jpg", path));
            infoList.add(new HttpDownloadInfo("http://7xrnwo.com2.z0.glb.qiniucdn.com/pictures/11e7b19623e41fd198500610403b64f1..jpg", path));
            infoList.add(new HttpDownloadInfo("http://7xrnwo.com2.z0.glb.qiniucdn.com/pictures/4ceb013c7ceec05842a58617d77a7030..jpg", path));
        }
        */

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < num; i++) {
                    HttpService.applyDownloadPhoto("http://7xrnwo.com2.z0.glb.qiniucdn.com/pictures/4ceb013c7ceec05842a58617d77a7030..jp", path, executorService).subscribe(new Action1<String>() {
                        @Override
                        public void call(String path) {
                            downloadFilePath = path;
                            latch.countDown();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            //ToastUtil.longShow(mContext, throwable.getMessage());
                            LogUtils.w(throwable.getMessage());
                            latch.countDown();
                        }
                    });
                }
            }
        }).start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LogUtils.d("完成所有下载");
        LogUtils.d((System.currentTimeMillis() - time1) + "ms");
    }

    /**
     * 大文件下载
     */
    @OnClick(R.id.tv_download_large_file)
    void clickDownloadLargeFile() {
        String downloadUrl = "http://14.215.93.23/apk.r1.market.hiapk.com/data/upload/apkres/2016/3_1/11/com.ilovephone.dxzs.baidu_112704.apk";

        /*
        Call<ResponseBody> call = service.download(downloadUrl);
        retrofit2.Response<ResponseBody> retrofitResponse = call.execute();
        if (retrofitResponse.isSuccessful()) {
            //下载成功
            result.setSavePath(HttpUtil.saveFileAtDisk(retrofitResponse.body(), path));
            resultList.add(result);
            result.setResult(true);
        } else {
            //ResponseBody errorBody = retrofitResponse.errorBody();
            throw new Exception(retrofitResponse.message());
        }
        */
    }

    /**
     * 文件上传
     */
    @OnClick(R.id.tv_upload_file)
    void clickUploadFile() {
        if (TextUtils.isEmpty(downloadFilePath)) {
            ToastUtil.longShow(mContext, "没有待上传文件");
            return;
        }

        if (TextUtils.isEmpty(token)) {
            ToastUtil.longShow(mContext, "请先登陆");
            return;
        }

        final File file = new File(downloadFilePath);
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

    /**
     * 同步
     */
    @OnClick(R.id.tv_sync_all)
    void clickSyncAll() {
        if (TextUtils.isEmpty(token)) {
            ToastUtil.longShow(mContext, "请先登陆");
            return;
        }

        //LogUtils.d("主线程:" + Thread.currentThread().getId());

        //pullProjectList();

        createData();
    }

    class Data {
        List<Integer> numList;
        List<Character> letterList;
    }

    private void createData() {
        LogUtils.d("主线程:" + Thread.currentThread().getId());
        Observable.create(new Observable.OnSubscribe<Data>() {

            @Override
            public void call(Subscriber<? super Data> subscriber) {
                LogUtils.d("生成数据线程-->" + Thread.currentThread().getId());
                List<Integer> numList = new ArrayList<>();
                for (int i = 0; i < 30; i++) {
                    numList.add(i);
                }
                List<Character> letterList = new ArrayList<>();
                char letter = 'A';
                for (int i = 0; i < 26; i++) {
                    letterList.add((char) (letter + i));
                }
                Data data = new Data();
                data.numList = numList;
                data.letterList = letterList;
                subscriber.onNext(data);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Data>() {
                    @Override
                    public void call(Data data) {
                        LogUtils.d("生成数据成功-->" + Thread.currentThread().getId());
                        runAtConcurrence(data);
                    }
                }).subscribe();
    }

    private void runAtConcurrence(final Data data) {
        Observable.create(new Observable.OnSubscribe<Data>() {

            @Override
            public void call(Subscriber<? super Data> subscriber) {
                LogUtils.d("并发管理线程-->" + Thread.currentThread().getId());
                CountDownLatch latch = new CountDownLatch(2);
                printNum(latch, data.numList);
                printLetter(latch, data.letterList);
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    subscriber.onError(e);
                }
                subscriber.onNext(data);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Data>() {
                    @Override
                    public void call(Data data) {
                        LogUtils.d("完成字母数字并发打印-->" + Thread.currentThread().getId());
                        printEnd();
                    }
                });
    }

    private void printNum(final CountDownLatch latch, List<Integer> numList) {
        Observable.from(numList)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.immediate())
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        LogUtils.d("打印:" + integer + " 线程-->" + Thread.currentThread().getId());
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.d("完成数字打印:" + Thread.currentThread().getId());
                        latch.countDown();
                    }
                }).subscribe();
    }

    private void printLetter(final CountDownLatch latch, List<Character> charList) {
        Observable.from(charList)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.immediate())
                .doOnNext(new Action1<Character>() {
                    @Override
                    public void call(Character character) {
                        LogUtils.d("打印:" + character + " 线程-->" + Thread.currentThread().getId());
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        LogUtils.d("完成字母打印:" + Thread.currentThread().getId());
                        latch.countDown();
                    }
                }).subscribe();
    }

    private void printEnd() {
        Observable.just("结束啦")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        LogUtils.d(s + "-->" + Thread.currentThread().getId());
                    }
                });
    }

}
