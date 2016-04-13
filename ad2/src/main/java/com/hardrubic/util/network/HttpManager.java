package com.hardrubic.util.network;

import android.text.TextUtils;
import com.hardrubic.Constants;
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
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
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
     * 普通请求
     */
    public <T> Observable<T> send(final String urlStr, final TreeMap<String, String> paramMap, boolean isSync) {
        if (isSync) {
            return this.send(urlStr, paramMap, Schedulers.immediate(), Schedulers.immediate());
        } else {
            return this.send(urlStr, paramMap, Schedulers.io());
        }
    }

    /**
     * 普通请求
     */
    public <T> Observable<T> send(final String urlStr, final TreeMap<String, String> paramMap, Scheduler subscribeScheduler) {
        return this.send(urlStr, paramMap, subscribeScheduler, AndroidSchedulers.mainThread());
    }

    /**
     * 普通请求
     */
    private <T> Observable<T> send(final String urlStr, final TreeMap<String, String> paramMap, Scheduler subscribeScheduler, Scheduler observeScheduler) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    URL url = createUrl(urlStr);
                    Method method = createMethod(url);
                    Call<T> call = (Call<T>) method.invoke(service, paramMap);
                    retrofit2.Response<T> response = call.execute();
                    subscriber.onNext(response.body());
                } catch (HttpException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(new HttpException(e, HttpSupport.ERROR_CODE_H104));
                }
            }
        }).subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler);
    }

    /**
     * 文件下载请求(同步)
     */
    public Observable<String> download(final String urlStr, final String savePath, boolean isSync) {
        if (isSync) {
            return this.download(urlStr, savePath, Schedulers.immediate(), Schedulers.immediate());
        } else {
            return this.download(urlStr, savePath, Schedulers.io());
        }
    }

    /**
     * 文件下载请求
     */
    public Observable<String> download(final String urlStr, final String savePath, Scheduler subscribeScheduler) {
        return this.download(urlStr, savePath, subscribeScheduler, AndroidSchedulers.mainThread());
    }

    /**
     * 文件下载请求
     */
    public Observable<String> download(final String urlStr, final String savePath, Scheduler subscribeScheduler, Scheduler observeScheduler) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
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
                    if (retrofitResponse.isSuccessful()) {
                        //下载成功
                        diskPath = HttpUtil.saveFileAtDisk(retrofitResponse.body(), savePath);
                    } else {
                        //ResponseBody errorBody = retrofitResponse.errorBody();
                        subscriber.onError(new Exception(urlStr + "下载错误:" + retrofitResponse.message()));
                    }
                    subscriber.onNext(diskPath);
                } catch (Exception e) {
                    //下载失败
                    //e.printStackTrace();
                    subscriber.onError(new HttpException(e, HttpSupport.ERROR_CODE_H108));
                }
            }
        }).subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler);
    }

    /*
    public Observable<String> downloadLarge(final String urlStr, final String savePath, Scheduler subscribeScheduler, Scheduler observeScheduler) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (TextUtils.isEmpty(urlStr)) {
                    subscriber.onError(new HttpException(HttpSupport.ERROR_CODE_H100));
                }
                if (TextUtils.isEmpty(savePath)) {
                    subscriber.onError(new HttpException(HttpSupport.ERROR_CODE_H105));
                }

                try {
                    URL url = createUrl(urlStr);
                    Call<ResponseBody> call = service.download(urlStr);
                    retrofit2.Response<ResponseBody> retrofitResponse = call.execute();
                    if (retrofitResponse.isSuccessful()) {
                        //下载成功
                        result.setResult(true);
                        result.setSavePath(HttpUtil.saveFileAtDisk(retrofitResponse.body(), savePath));
                    } else {
                        result.setResult(false);
                        result.setException(new Exception(retrofitResponse.message()));
                        //ResponseBody errorBody = retrofitResponse.errorBody();
                    }
                    //subscriber.onNext(result);
                } catch (Exception e) {
                    //下载失败
                    e.printStackTrace();
                    subscriber.onError(new HttpException(e, HttpSupport.ERROR_CODE_H108));
                }
            }
        }).subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler);
    }
    */

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
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
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
                    Call<T> call = (Call<T>) method.invoke(service, paramMap, requestBodyTreeMap);
                    retrofit2.Response<T> response = call.execute();
                    subscriber.onNext(response.body());
                } catch (HttpException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                } catch (Exception e) {
                    e.printStackTrace();
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
                //LogUtils.d(String.format("发送请求 %s 结果 %s", request.url(), request.headers()));
            }

            Response response = chain.proceed(request);

            if (PRINT_HTTP_LOG) {
                long t2 = System.nanoTime();
                LogUtils.d(String.format("响应请求 %s %.1fms", response.request().url(), (t2 - t1) / 1e6d));
                //LogUtils.d(String.format("收到响应请求 %s %.1fms%n%s",
                //        response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            }

            return response;
        }
    }
}
