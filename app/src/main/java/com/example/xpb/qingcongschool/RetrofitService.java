package com.example.xpb.qingcongschool;


import com.jakewharton.retrofit2.adapter.rxjava2.Result;

import java.util.Map;


import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by xpb on 2017/3/3.
 */
public interface RetrofitService {

    @POST("/QingXiao/User/Register")//注册
    Observable<ResponseBody> registerUser(@Body RequestBody user);

    @POST("/QingXiao/User/Login")//登陆
    Observable<ResponseBody> login(@Body RequestBody user);

    @Multipart
    @POST("/QingXiao/User/updateAvatar")//上传头像
    Observable<String> updateAvatar1(@Part MultipartBody.Part file);


    @POST("/QingXiao/User/DownloadAvatar")//下载头像
    Observable<ResponseBody>downloadAvatar();

    @POST("/QingXiao/Course/Insert")//上传课表
    Observable<String>uploadCourse(@Body RequestBody course);

    @Multipart
    @POST("/QingXiao/CourseResource/Upload")//上传课程资源
    Call<String> upload(
            @Part("jsonStringUploadCourseResource") RequestBody description,
            @Part MultipartBody.Part file
    );

    @POST("/QingXiao/CourseResource/GetList")//获取课程资源列表
    Observable<String>getFileList(@Body RequestBody courseName);

    @GET("/QingXiao/CourseResource/Download")//下载课程资源
    Call<ResponseBody> download(@Query("resourceStoreName") String resourceStoreName);

    @POST("/QingXiao/ResourceComment/Insert")
    Observable<String>insertResourceComment(@Body RequestBody jsonStringInsertResourceComment);
}