package com.hardrubic.util.network;

import android.text.TextUtils;
import com.hardrubic.Constants;
import com.hardrubic.util.LogUtils;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TreeMap;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit;
    private static HttpServiceRest service;

    /** 请求url不能为空 */
    public static final String ERROR_CODE_H100 = "H100";
    /** 无法找到URL对应的REST方法 */
    public static final String ERROR_CODE_H101 = "H101";
    /** 非法url */
    public static final String ERROR_CODE_H102 = "H102";
    /** 同步请求错误 */
    public static final String ERROR_CODE_H103 = "H103";
    /** 异步请求错误 */
    public static final String ERROR_CODE_H104 = "H104";

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
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.HOST)
                .addConverterFactory(GsonConverterFactory.create())
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
     * 请求响应日志信息
     */
    public static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            LogUtils.d(String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            LogUtils.d(String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));

            return response;
        }
    }

}
