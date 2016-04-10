package com.hardrubic.util.network;

import android.text.TextUtils;

import com.hardrubic.Constants;
import com.hardrubic.util.LogUtils;
import com.hardrubic.util.network.entity.HttpDownloadInfo;
import com.hardrubic.util.network.entity.HttpDownloadResult;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class HttpManager {

    private static HttpManager mInstance;
    private static Retrofit retrofit;
    private static HttpServiceRest service;

    /**
     * 是否打印HTTP日志
     */
    private static final boolean PRINT_HTTP_LOG = true;
    /**
     * HTTP超时
     */
    private static final int HTTP_CONNECT_TIMEOUT = 1000;
    private static final int HTTP_READ_TIMEOUT = 1000 * 3;
    private static final int HTTP_WRITE_TIMEOUT = 1000 * 3;

    /**
     * 请求url不能为空
     */
    public static final String ERROR_CODE_H100 = "H100";
    /**
     * 无法找到URL对应的REST方法
     */
    public static final String ERROR_CODE_H101 = "H101";
    /**
     * 非法url
     */
    public static final String ERROR_CODE_H102 = "H102";
    /**
     * 同步请求错误
     */
    public static final String ERROR_CODE_H103 = "H103";
    /**
     * 异步请求错误
     */
    public static final String ERROR_CODE_H104 = "H104";
    /**
     * 文件保存路径不能为空
     */
    public static final String ERROR_CODE_H105 = "H105";
    /**
     * 缺少上传下载信息
     */
    public static final String ERROR_CODE_H106 = "H106";
    /**
     * 文件不存在
     */
    public static final String ERROR_CODE_H107 = "H107";
    /**
     * 同步上传文件错误
     */
    public static final String ERROR_CODE_H108 = "H108";

    public static abstract class HttpServiceCallback {
        public abstract <T> void onNext(T result);

        public abstract void onFailure(HttpException e);
    }

    private HttpManager() {
        init();
    }

    public static HttpManager getInstance() {
        if (mInstance == null) {
            mInstance = new HttpManager();
        }
        return mInstance;
    }

    private void init() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(HTTP_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                //.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        service = retrofit.create(HttpServiceRest.class);
    }

    /**
     * url对象创建
     */
    private URL createUrl(String urlStr) throws HttpException {
        if (TextUtils.isEmpty(urlStr)) {
            throw new HttpException(ERROR_CODE_H100);
        }

        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            throw new HttpException(e, ERROR_CODE_H102);
        }

        return url;
    }

    /**
     * 获取Http对应的方法
     */
    private Method createMethod(URL url) throws HttpException {
        Method method = HttpUtil.getUrlMethod(url);
        if (method == null) {
            throw new HttpException(ERROR_CODE_H101);
        }
        return method;
    }

    /**
     * 同步请求
     */
    public <T> T send(String urlStr, TreeMap<String, String> paramMap) throws HttpException {
        URL url = createUrl(urlStr);
        Method method = createMethod(url);

        Call<T> call;
        retrofit2.Response<T> response;
        try {
            call = (Call<T>) method.invoke(service, paramMap);
            response = call.execute();
        } catch (Exception e) {
            throw new HttpException(e, ERROR_CODE_H103, e.getMessage());
        }

        return response.body();
    }

    /**
     * 异步请求
     */
    public <T> void send(String urlStr, final TreeMap<String, String> paramMap, final HttpServiceCallback callback) {
        //
        Method method = null;
        try {
            URL url = createUrl(urlStr);
            method = createMethod(url);
        } catch (HttpException e) {
            callback.onFailure(e);
        }

        final Method finalMethod = method;
        Observable<T> observable = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    Call<T> call = (Call<T>) finalMethod.invoke(service, paramMap);
                    retrofit2.Response<T> response = call.execute();
                    subscriber.onNext(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onFailure(new HttpException(e, ERROR_CODE_H104));
                }
            }
        }).subscribeOn(Schedulers.io())    //网络请求运行在io线程
                .observeOn(AndroidSchedulers.mainThread());   //运行完毕返回主线程
        observable.subscribe(new Action1<T>() {
            @Override
            public void call(T t) {
                LogUtils.d("onNext");
                callback.onNext(t);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                callback.onFailure(new HttpException(throwable, ERROR_CODE_H104));
            }
        }, new Action0() {
            @Override
            public void call() {
                LogUtils.d("onComplete");
                //callback.onComplete();
            }
        });
    }

    /**
     * 同步下载文件请求
     */
    public List<HttpDownloadResult> download(String url, String savePath) throws HttpException {
        List<HttpDownloadInfo> downloadInfoList = new ArrayList<>();
        HttpDownloadInfo httpDownloadInfo = new HttpDownloadInfo(url, savePath);
        downloadInfoList.add(httpDownloadInfo);
        return download(downloadInfoList);
    }

    /**
     * 同步下载文件请求
     */
    public List<HttpDownloadResult> download(final List<HttpDownloadInfo> downloadInfoList) throws HttpException {
        //TODO 校验存储空间是否足够

        //
        if (downloadInfoList == null || downloadInfoList.isEmpty()) {
            throw new HttpException(ERROR_CODE_H106);
        }

        for (HttpDownloadInfo info : downloadInfoList) {
            if (TextUtils.isEmpty(info.getUrl())) {
                throw new HttpException(ERROR_CODE_H106);
            }
            if (TextUtils.isEmpty(info.getTargetPath())) {
                throw new HttpException(ERROR_CODE_H105);
            }
        }

        List<HttpDownloadResult> resultList = new ArrayList<>();
        for (HttpDownloadInfo httpDownloadInfo : downloadInfoList) {
            String urlStr = httpDownloadInfo.getUrl();
            String path = httpDownloadInfo.getTargetPath();
            HttpDownloadResult result = new HttpDownloadResult();
            result.setUrl(urlStr);
            result.setTargetPath(path);
            try {
                URL url = createUrl(urlStr);
                Call<ResponseBody> call = service.download(urlStr);
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
            } catch (Exception e) {
                //下载失败
                result.setResult(false);
                result.setException(e);
                resultList.add(result);
            }
        }

        return resultList;
    }

    /**
     * 同步上传文件请求
     */
    public <T> T upload(String urlStr, TreeMap<String, File> fileMap, TreeMap<String, String> paramMap) throws HttpException {
        //check file exist
        Collection<File> fileList = fileMap.values();
        if (fileList == null || fileList.isEmpty()) {
            throw new HttpException(ERROR_CODE_H107);
        }
        for (File file : fileList) {
            if (!file.exists()) {
                throw new HttpException(ERROR_CODE_H107);
            }
        }

        //file --> requestBody
        TreeMap<String, RequestBody> requestBodyTreeMap = new TreeMap<>();
        for (Map.Entry<String, File> entry : fileMap.entrySet()) {
            MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");    //默认上传图片
            RequestBody requestBody = RequestBody.create(MEDIA_TYPE_PNG, entry.getValue());
            requestBodyTreeMap.put(entry.getKey(), requestBody);
        }

        URL url = createUrl(urlStr);
        Method method = createMethod(url);
        Call<T> call;
        retrofit2.Response<T> response;
        try {
            call = (Call<T>) method.invoke(service, paramMap, requestBodyTreeMap);
            response = call.execute();
        } catch (Exception e) {
            throw new HttpException(e, ERROR_CODE_H108, e.getMessage());
        }
        return response.body();
    }

    /**
     * 拦截的HTTP的日志信息
     */
    public static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            if (PRINT_HTTP_LOG) {
                 //LogUtils.d(String.format("发送请求 %s 结果 %s", request.url(), request.headers()));
            }

            Response response = chain.proceed(request);

            if (PRINT_HTTP_LOG) {
                long t2 = System.nanoTime();
                LogUtils.d(String.format("收到响应请求 %s %.1fms", response.request().url(), (t2 - t1) / 1e6d));
                //LogUtils.d(String.format("收到响应请求 %s %.1fms%n%s",
                //        response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            }

            return response;
        }
    }
}
