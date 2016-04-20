package com.hardrubic.util.network;

import android.text.TextUtils;
import com.hardrubic.Constants;
import com.hardrubic.entity.response.HttpResponse;
import com.hardrubic.util.LogUtils;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
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
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

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
    private static final int HTTP_CONNECT_TIMEOUT = 10;
    //private static final int HTTP_READ_TIMEOUT = 10;
    //private static final int HTTP_WRITE_TIMEOUT = 10;

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
                .connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                //.readTimeout(HTTP_READ_TIMEOUT, TimeUnit.SECONDS)
                //.writeTimeout(HTTP_WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        service = retrofit.create(HttpServiceRest.class);
    }

    /**
     * url对象创建
     */
    private URL createUrl(String urlStr) throws HttpException {
        if (TextUtils.isEmpty(urlStr)) {
            throw new HttpException(HttpSupport.ERROR_CODE_H102);
        }

        URL url;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            throw new HttpException(e, HttpSupport.ERROR_CODE_H102);
        }

        return url;
    }

    /**
     * 获取Http对应的方法
     */
    private Method createMethod(URL url) throws HttpException {
        Method method = HttpUtil.getUrlMethod(url);
        if (method == null) {
            throw new HttpException(HttpSupport.ERROR_CODE_H101);
        }
        return method;
    }

    private <T> HttpResponse<T> doSend(final String urlStr, final TreeMap<String, String> paramMap, final TreeMap<String, File> fileMap) throws HttpException {

        TreeMap<String, RequestBody> requestBodyTreeMap = null;
        if (fileMap != null && !fileMap.isEmpty()) {
            //待上传文件检查
            for (File file : fileMap.values()) {
                if (!file.exists()) {
                    throw new HttpException(HttpSupport.ERROR_CODE_H105);
                }
            }

            //file --> requestBody
            requestBodyTreeMap = new TreeMap<>();
            for (Map.Entry<String, File> entry : fileMap.entrySet()) {
                MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");    //默认上传图片
                RequestBody requestBody = RequestBody.create(MEDIA_TYPE_PNG, entry.getValue());
                requestBodyTreeMap.put(entry.getKey(), requestBody);
            }
        }

        try {
            URL url = createUrl(urlStr);
            Method method = createMethod(url);
            Call<HttpResponse<T>> call;
            if (requestBodyTreeMap == null) {
                call = (Call<HttpResponse<T>>) method.invoke(service, paramMap);
            } else {
                call = (Call<HttpResponse<T>>) method.invoke(service, paramMap, requestBodyTreeMap);
            }
            retrofit2.Response<HttpResponse<T>> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                throw new HttpException(HttpSupport.ERROR_CODE_H103, response.message());
            }
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            throw new HttpException(e, HttpSupport.ERROR_CODE_H103);
        }
    }

    /**
     * 普通请求(同步)
     */
    public <T> T send(String urlStr, TreeMap<String, String> paramMap) throws HttpException {
        HttpResponse<T> httpResponse = doSend(urlStr, paramMap, null);
        return dealHttpResponse(httpResponse);
    }

    /**
     * 普通请求(同步-上传)
     */
    public <T> T send(String urlStr, TreeMap<String, String> paramMap, TreeMap<String, File> fileMap) throws HttpException {
        HttpResponse<T> httpResponse = doSend(urlStr, paramMap, fileMap);
        return dealHttpResponse(httpResponse);
    }

    /**
     * 普通请求(异步-指定单向线程)
     */
    public <T> Observable<T> send(String urlStr, TreeMap<String, String> paramMap, Scheduler subscribeScheduler) {
        return this.send(urlStr, paramMap, null, subscribeScheduler, AndroidSchedulers.mainThread());
    }

    /**
     * 普通请求(异步-指定单向线程-上传)
     */
    public <T> Observable<T> send(String urlStr, TreeMap<String, String> paramMap, TreeMap<String, File> fileMap, Scheduler subscribeScheduler) {
        return this.send(urlStr, paramMap, fileMap, subscribeScheduler, AndroidSchedulers.mainThread());
    }

    /**
     * 普通请求(异步-指定双向线程)
     */
    public <T> Observable<T> send(String urlStr, TreeMap<String, String> paramMap, Scheduler subscribeScheduler, Scheduler observeScheduler) {
        return this.send(urlStr, paramMap, null, subscribeScheduler, observeScheduler);
    }

    /**
     * 普通请求(异步-指定双向线程-上传)
     */
    public <T> Observable<T> send(final String urlStr, final TreeMap<String, String> paramMap, final TreeMap<String, File> fileMap, Scheduler subscribeScheduler, Scheduler observeScheduler) {
        return Observable.create(new Observable.OnSubscribe<HttpResponse<T>>() {
            @Override
            public void call(Subscriber<? super HttpResponse<T>> subscriber) {
                HttpResponse<T> httpResponse = doSend(urlStr, paramMap, fileMap);
                subscriber.onNext(httpResponse);
            }
        }).map(new Func1<HttpResponse<T>, T>() {
            @Override
            public T call(HttpResponse<T> httpResponse) {
                return dealHttpResponse(httpResponse);
            }
        }).subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler);

    }

    /**
     * 文件下载请求(异步-单向线程)
     */
    public Observable<HttpDownloadResult> download(final String urlStr, final String savePath, Scheduler subscribeScheduler) {
        return this.download(urlStr, savePath, subscribeScheduler, AndroidSchedulers.mainThread());
    }

    /**
     * 文件下载请求(异步-双向线程)
     */
    public Observable<HttpDownloadResult> download(final String urlStr, final String savePath, Scheduler subscribeScheduler, Scheduler observeScheduler) {
        return Observable.create(new Observable.OnSubscribe<HttpDownloadResult>() {
            @Override
            public void call(final Subscriber<? super HttpDownloadResult> subscriber) {
                if (TextUtils.isEmpty(savePath)) {
                    subscriber.onError(new HttpException(HttpSupport.ERROR_CODE_H104));
                }

                try {
                    String diskPath = "";
                    URL url = createUrl(urlStr);
                    Call<ResponseBody> call = service.download(urlStr);
                    retrofit2.Response<ResponseBody> retrofitResponse = call.execute();

                    final HttpDownloadResult result = new HttpDownloadResult();
                    if (retrofitResponse.isSuccessful()) {
                        //下载成功
                        diskPath = HttpUtil.saveFileAtDisk(retrofitResponse.body(), savePath, new HttpUtil.IFileDownloadListener() {
                            @Override
                            public void onRefreshProgress(double per) {
                                result.setPercent(per);
                                result.setFinish(false);
                                subscriber.onNext(result);
                            }

                            @Override
                            public void onFinish() {
                                result.setFinish(true);
                            }
                        });
                    } else {
                        //ResponseBody errorBody = retrofitResponse.errorBody();
                        subscriber.onError(new Exception(urlStr + ":" + retrofitResponse.message()));
                    }
                    result.setDiskPath(diskPath);
                    subscriber.onNext(result);
                } catch (Exception e) {
                    //下载失败
                    subscriber.onError(new HttpException(e, HttpSupport.ERROR_CODE_H106));
                }
            }
        }).subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler);
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
                //LogUtils.d(String.format("HTTP发送请求 %s %s", request.url(), request.method()));
            }

            Response response = chain.proceed(request);

            if (PRINT_HTTP_LOG) {
                long t2 = System.nanoTime();
                double time = (t2 - t1) / 1e6d;
                String msg = String.format("HTTP响应请求 %s %s %.1fms", response.request().url(), response.request().method(), time);
                if (time > 1000) {
                    LogUtils.w(msg);
                } else {
                    LogUtils.d(msg);
                }
            }

            return response;
        }
    }

    /**
     * 统一处理Http的resultCode,将Data部分剥离出来
     */
    private <T> T dealHttpResponse(HttpResponse<T> httpResponse) {
        if (httpResponse.getResult() != Constants.RESPOND_RESULT_OK) {
            throw new HttpException(HttpSupport.ERROR_CODE_H100, httpResponse.getMessage());
        }
        return httpResponse.getData();
    }
}
