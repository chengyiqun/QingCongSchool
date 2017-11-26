package com.example.xpb.qingcongschool;


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
 * Created by xpb on 2017/3/3.
 */
public class RetrofitFactory {
    private static   String baseUrl = "http://60.205.218.103:80";
    private RetrofitFactory(String baseUrl){

    }

    private static OkHttpClient  httpClientlogin = new OkHttpClient.Builder().
            addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request.Builder  builder = chain.request().newBuilder();
                    //新增的响应拦截by程义群
                    Response response=chain.proceed(builder.build());
                    MainActivity.Companion.setAccessToken(response.header("accessToken", "NoaccessToken"));
                    return response;
                }
            }).connectTimeout(60, TimeUnit.SECONDS).
            readTimeout(60,TimeUnit.SECONDS)
            .build();

    private static OkHttpClient  httpClient = new OkHttpClient.Builder().
            addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder  builder = chain.request().newBuilder();
            builder.addHeader("accessToken", MainActivity.Companion.getAccessToken());
            String encodedUserName= URLEncoder.encode(MainActivity.Companion.getUserName(),"UTF-8");
            builder.addHeader("userName",encodedUserName);
            /*Request request= builder.build();
            System.out.println(request);*/
            //新增的响应拦截by程义群
            return chain.proceed(builder.build());
        }
    }).connectTimeout(60, TimeUnit.SECONDS).
            readTimeout(60,TimeUnit.SECONDS)
            .build();


    private static RetrofitService retrofitServicelogin =  new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClientlogin)
            .build()
            .create(RetrofitService.class);


    private static RetrofitService retrofitService =  new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient)
            .build()
            .create(RetrofitService.class);


    public static RetrofitService getInstance() {
        return retrofitService;
    }

    public static RetrofitService getInstanceLogin() {
        return retrofitServicelogin;
    }

    public static <T> RetrofitService getRetrofitService(final RetrofitCallback<T> callback) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request.Builder  builder = chain.request().newBuilder();
                builder.addHeader("accessToken", MainActivity.Companion.getAccessToken());
                String encodedUserName= URLEncoder.encode(MainActivity.Companion.getUserName(),"UTF-8");
                builder.addHeader("userName",encodedUserName);

                Response response = chain.proceed(builder.build());
                //将ResponseBody转换成我们需要的FileResponseBody
                return response.newBuilder().body(new FileResponseBody<T>(response.body(), callback)).build();
            }
        });
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService service = retrofit.create(RetrofitService.class);
        return service ;
    }//通过上面的设置后，我们需要在回调RetrofitCallback中实现onLoading方法来进行进度的更新操作，与上传文件的方法相同
}
