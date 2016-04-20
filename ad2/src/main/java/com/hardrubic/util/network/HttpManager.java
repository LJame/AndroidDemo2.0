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
import java.util.Collection;
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
import rx.functions.Action0;
import rx.functions.Func1;
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
            throw new HttpException(HttpSupport.ERROR_CODE_H100);
        }

        URL url = null;
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

    /**
     * 普通请求(默认使用io线程进行异步)
     */
    public <T> Observable<T> send(final String urlStr, final TreeMap<String, String> paramMap) {
        return this.send(urlStr, paramMap, Schedulers.io());
    }

    /**
     * 普通请求(可同步)
     */
    public <T> Observable<T> send(final String urlStr, final TreeMap<String, String> paramMap, boolean isSync) {
        if (isSync) {
            return this.send(urlStr, paramMap, Schedulers.immediate(), Schedulers.immediate());
        } else {
            return this.send(urlStr, paramMap);
        }
    }

    /**
     * 普通请求(可指定执行异步的线程,回到主线程)
     */
    public <T> Observable<T> send(final String urlStr, final TreeMap<String, String> paramMap, Scheduler subscribeScheduler) {
        return this.send(urlStr, paramMap, subscribeScheduler, AndroidSchedulers.mainThread());
    }

    /**
     * 普通请求
     */
    private <T> Observable<T> send(final String urlStr, final TreeMap<String, String> paramMap, Scheduler subscribeScheduler, Scheduler observeScheduler) {
        return Observable.create(new Observable.OnSubscribe<HttpResponse<T>>() {
            @Override
            public void call(Subscriber<? super HttpResponse<T>> subscriber) {
                try {
                    URL url = createUrl(urlStr);
                    Method method = createMethod(url);
                    Call<HttpResponse<T>> call = (Call<HttpResponse<T>>) method.invoke(service, paramMap);
                    retrofit2.Response<HttpResponse<T>> response = call.execute();
                    subscriber.onNext(response.body());
                } catch (HttpException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(new HttpException(e, HttpSupport.ERROR_CODE_H104));
                }
            }
        }).map(new HttpResponseFunc<T>())
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler);
    }

    /**
     * 文件下载请求
     */
    public Observable<HttpDownloadResult> download(final String urlStr, final String savePath) {
        return this.download(urlStr, savePath, Schedulers.io());
    }

    /**
     * 文件下载请求(同步)
     */
    public Observable<HttpDownloadResult> download(final String urlStr, final String savePath, boolean isSync) {
        if (isSync) {
            return this.download(urlStr, savePath, Schedulers.immediate(), Schedulers.immediate());
        } else {
            return this.download(urlStr, savePath);
        }
    }

    /**
     * 文件下载请求
     */
    public Observable<HttpDownloadResult> download(final String urlStr, final String savePath, Scheduler subscribeScheduler) {
        return this.download(urlStr, savePath, subscribeScheduler, AndroidSchedulers.mainThread());
    }

    /**
     * 文件下载请求
     */
    public Observable<HttpDownloadResult> download(final String urlStr, final String savePath, Scheduler subscribeScheduler, Scheduler observeScheduler) {
        return Observable.create(new Observable.OnSubscribe<HttpDownloadResult>() {
            @Override
            public void call(final Subscriber<? super HttpDownloadResult> subscriber) {
                if (TextUtils.isEmpty(urlStr)) {
                    subscriber.onError(new HttpException(HttpSupport.ERROR_CODE_H100));
                }
                if (TextUtils.isEmpty(savePath)) {
                    subscriber.onError(new HttpException(HttpSupport.ERROR_CODE_H105));
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
                        subscriber.onError(new Exception(urlStr + "下载错误:" + retrofitResponse.message()));
                    }
                    result.setDiskPath(diskPath);
                    subscriber.onNext(result);
                } catch (Exception e) {
                    //下载失败
                    //e.printStackTrace();
                    subscriber.onError(new HttpException(e, HttpSupport.ERROR_CODE_H108));
                }
            }
        }).subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler);
    }

    /**
     * 上传文件请求
     */
    public <T> Observable<T> upload(final String urlStr, final TreeMap<String, File> fileMap, final TreeMap<String, String> paramMap, boolean isSync) {
        if (isSync) {
            return this.upload(urlStr, fileMap, paramMap, Schedulers.immediate(), Schedulers.immediate());
        } else {
            return this.upload(urlStr, fileMap, paramMap, Schedulers.io());
        }
    }

    /**
     * 上传文件请求
     */
    public <T> Observable<T> upload(final String urlStr, final TreeMap<String, File> fileMap, final TreeMap<String, String> paramMap, Scheduler subscribeScheduler) {
        return this.upload(urlStr, fileMap, paramMap, subscribeScheduler, AndroidSchedulers.mainThread());
    }

    /**
     * 上传文件请求
     */
    public <T> Observable<T> upload(final String urlStr, final TreeMap<String, File> fileMap, final TreeMap<String, String> paramMap, Scheduler subscribeScheduler, Scheduler observeScheduler) {
        return Observable.create(new Observable.OnSubscribe<HttpResponse<T>>() {
            @Override
            public void call(Subscriber<? super HttpResponse<T>> subscriber) {
                //判断文件是否存在
                Collection<File> fileList = fileMap.values();
                if (fileList == null || fileList.isEmpty()) {
                    subscriber.onError(new HttpException(HttpSupport.ERROR_CODE_H107));
                }
                for (File file : fileList) {
                    if (!file.exists()) {
                        subscriber.onError(new HttpException(HttpSupport.ERROR_CODE_H107));
                    }
                }

                //file --> requestBody
                TreeMap<String, RequestBody> requestBodyTreeMap = new TreeMap<>();
                for (Map.Entry<String, File> entry : fileMap.entrySet()) {
                    MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");    //默认上传图片
                    RequestBody requestBody = RequestBody.create(MEDIA_TYPE_PNG, entry.getValue());
                    requestBodyTreeMap.put(entry.getKey(), requestBody);
                }

                try {
                    URL url = createUrl(urlStr);
                    Method method = createMethod(url);
                    Call<HttpResponse<T>> call = (Call<HttpResponse<T>>) method.invoke(service, paramMap, requestBodyTreeMap);
                    retrofit2.Response<HttpResponse<T>> response = call.execute();
                    subscriber.onNext(response.body());
                } catch (HttpException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(new HttpException(e, HttpSupport.ERROR_CODE_H106));
                }
            }
        }).map(new HttpResponseFunc<T>())
                .subscribeOn(subscribeScheduler)
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
                LogUtils.d(String.format("HTTP发送请求 %s %s", request.url(), request.method()));
            }

            Response response = chain.proceed(request);

            if (PRINT_HTTP_LOG) {
                long t2 = System.nanoTime();
                LogUtils.d(String.format("HTTP响应请求 %s %.1fms", response.request().url(), (t2 - t1) / 1e6d));
                //LogUtils.d(String.format("收到响应请求 %s %.1fms%n%s",
                //        response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            }

            return response;
        }
    }

    /**
     * 统一处理Http的resultCode,并将Data部分剥离出来返回给subscriber
     *
     * @param <T>
     */
    private class HttpResponseFunc<T> implements Func1<HttpResponse<T>, T> {

        @Override
        public T call(HttpResponse<T> httpResponse) {
            if (httpResponse.getResult() != Constants.RESPOND_RESULT_OK) {
                throw new HttpException(HttpSupport.ERROR_CODE_H100, httpResponse.getMessage());
            }
            return httpResponse.getData();
        }
    }
}
