package com.example.xpb.qingcongschool;


import android.support.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.example.xpb.qingcongschool.course.resource.download.FileResponseBody;
import com.example.xpb.qingcongschool.course.resource.upload.RetrofitCallback;
import com.example.xpb.qingcongschool.main.MainActivity;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;

/**
 * Created by 程义群（空灵入耳） on 2017/3/3.
 */
public class RetrofitFactory {
    public static final String baseUrl = BuildConfig.DEBUG? "http://192.168.183.1:8080":"http://60.205.218.103:80";
    //private static String baseUrl ="http://60.205.218.103:80";
    private RetrofitFactory(String BaseUrl){};



    //登陆以后所有的要验证token的操作
    private static OkHttpClient  httpClient = new OkHttpClient.Builder().
            addInterceptor(chain -> {
                Request.Builder  builder = chain.request().newBuilder();
                builder.addHeader("accessToken", MainActivity.Companion.getAccessToken());
                String encodedUserName= URLEncoder.encode(MainActivity.Companion.getUserName(),"UTF-8");
                if(BuildConfig.DEBUG){
                    LogUtils.d("encodedUserName",encodedUserName);
                }
                builder.addHeader("userName",encodedUserName);
                /*Request request= builder.build();
                System.out.println(request);*/
                //新增的响应拦截by程义群
                return chain.proceed(builder.build());
            }).connectTimeout(30, TimeUnit.SECONDS).
            readTimeout(30,TimeUnit.SECONDS)
            .build();
    private static RetrofitService retrofitService =  new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient)
            .build()
            .create(RetrofitService.class);


    //登陆以后，带token的
    public static RetrofitService getInstance() {
        return retrofitService;
    }

    //登陆
    private static OkHttpClient  httpClientlogin = new OkHttpClient.Builder().
            addInterceptor(chain -> {
                Request.Builder  builder = chain.request().newBuilder();
                //新增的响应拦截by程义群
                Response response=chain.proceed(builder.build());
                MainActivity.Companion.setAccessToken(response.header("accessToken", "NoaccessToken"));
                return response;
            }).connectTimeout(30, TimeUnit.SECONDS)/*本来是60*/
            .readTimeout(30,TimeUnit.SECONDS)
            .build();

    private static RetrofitService retrofitServicelogin =  new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClientlogin)
            .build()
            .create(RetrofitService.class);
    static RetrofitService getInstanceLogin() {
        return retrofitServicelogin;
    }


    //下载资源
    public static <T> RetrofitService getRetrofitService(final RetrofitCallback<T> callback) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(chain -> {
            Request.Builder  builder = chain.request().newBuilder();
            builder.addHeader("accessToken", MainActivity.Companion.getAccessToken());
            String encodedUserName= URLEncoder.encode(MainActivity.Companion.getUserName(),"UTF-8");
            builder.addHeader("userName",encodedUserName);

            Response response = chain.proceed(builder.build());
            //将ResponseBody转换成我们需要的FileResponseBody
            return response.newBuilder().body(new FileResponseBody<T>(response.body(), callback)).build();
        });
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(RetrofitService.class);
    }//通过上面的设置后，我们需要在回调RetrofitCallback中实现onLoading方法来进行进度的更新操作，与上传文件的方法相同
}
