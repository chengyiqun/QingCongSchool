package com.example.xpb.qingcongschool.course;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.xpb.qingcongschool.R;
import com.example.xpb.qingcongschool.RetrofitFactory;
import com.example.xpb.qingcongschool.main.BaseActivity;
import com.example.xpb.qingcongschool.main.MainActivity;
import com.example.xpb.qingcongschool.util.NetworkUtil;
import com.google.gson.Gson;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by xpb on 2017/3/4.
 */
public class GetCourseActivity extends BaseActivity implements View.OnClickListener {
    Context context=this;

    public Observable<ResponseBody> observableGetIdentifyCode;   //登录之前获取验证码
    public Observable<String> observableGetCourse;   //获取课表

    private Button loginButton;
    private Button reIdentifyButton;
    private EditText studentCodeEditText;
    private EditText passwordEditText;
    private EditText identifyCodeEditText;
    private ImageView codeImage;

    private boolean getCoursesSuccessed=false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getcourse);
        init();
        reIdentifyButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
    }
    public void init() {
        loginButton = (Button) findViewById(R.id.login_button);
        reIdentifyButton = (Button) findViewById(R.id.getCode_button);
        studentCodeEditText = (EditText) findViewById(R.id.studentname_editText);
        passwordEditText = (EditText) findViewById(R.id.password_editText);
        identifyCodeEditText = (EditText) findViewById(R.id.identifyCode_editText);
        if(NetworkUtil.isNetworkAvailable(context)){
            identifyCodeEditText.setText("");
            observableGetIdentifyCode = RetrofitFactory.getInstance().getIdentifyCode();
            observableGetIdentifyCode.subscribeOn(Schedulers.io())                     //登录进教务系统之前，获取验证码

                    .map(responseBody -> {
                        InputStream inputStream = null;
                        inputStream = responseBody.byteStream();
                        if (inputStream != null) {
                            return BitmapFactory.decodeStream(inputStream);
                        } else {
                            return null;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new io.reactivex.Observer<Bitmap>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Bitmap bitmap) {
                            codeImage.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onError(Throwable e) {
                            codeImage.setContentDescription("刷新验证码");
                            ToastUtils.showShort("教务处异常");
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }else {
            Toast.makeText(context,"网络不可用",Toast.LENGTH_SHORT).show();
        }
        codeImage = (ImageView) findViewById(R.id.codeImage);
    }


    @Override
    public void onClick(View v) {
        if(NetworkUtil.isNetworkAvailable(context)){
            switch (v.getId()) {
                case R.id.getCode_button:
                    identifyCodeEditText.setText("");
                    observableGetIdentifyCode = RetrofitFactory.getInstance().getIdentifyCode();
                    observableGetIdentifyCode.subscribeOn(Schedulers.io())                     //登录进教务系统之前，获取验证码
                            .map(responseBody -> {
                                InputStream inputStream = null;
                                inputStream = responseBody.byteStream();
                                if (inputStream != null) {
                                    return BitmapFactory.decodeStream(inputStream);
                                } else {
                                    return null;
                                }
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new io.reactivex.Observer<Bitmap>() {
                                @Override
                                public void onSubscribe(Disposable d) { }
                                @Override
                                public void onNext(Bitmap bitmap) {
                                    codeImage.setImageBitmap(bitmap);
                                }
                                @Override
                                public void onError(Throwable e) {
                                    codeImage.setContentDescription("刷新验证码");
                                    e.printStackTrace();
                                }

                                @Override
                                public void onComplete() {}
                            });

                    break;

                case R.id.login_button:
                    String txtUserName = studentCodeEditText.getText().toString();
                    String TextBox2 = passwordEditText.getText().toString();
                    String txtSecretCode = identifyCodeEditText.getText().toString();
                    if(txtUserName.equals("")||TextBox2.equals("")){
                        ToastUtils.showShort("学号或密码为空");
                    }else {
                        if (txtSecretCode.equals("")){
                            ToastUtils.showShort("请输入验证码");
                        }
                        else {
                            HashMap<String, Object> userInfo = new HashMap<String, Object>();
                            userInfo.put("txtUserName",txtUserName);//学号
                            userInfo.put("TextBox2",TextBox2);//密码
                            userInfo.put("txtSecretCode",txtSecretCode);//验证码
                            Gson gson=new Gson();
                            String userJson= gson.toJson(userInfo);
                            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), userJson);
                            observableGetCourse=RetrofitFactory.getInstance().getCourse(body);
                            observableGetCourse.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<String>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {}

                                        @Override
                                        public void onNext(String value) {
                                            System.out.println("课表"+value);
                                            JSONObject jsonObject = JSONObject.parseObject(value);
                                            int result = jsonObject.getInteger("result");
                                            if (result == 3004)
                                            {
                                                ToastUtils.showShort("tokenError");
                                            }else if (result == 3005){
                                                ToastUtils.showShort("登陆错误");
                                            }else if (result==3006){
                                                ToastUtils.showShort("验证码错误");
                                            }else {
                                                ToastUtils.showShort(String.valueOf(result));
                                                getCoursesSuccessed=true;
                                                List<Course> list = JSONArray.parseArray(jsonObject.getJSONArray("courses").toString(),Course.class);
                                                CourseService courseService = CourseService.getCourseService();
                                                courseService.saveCourseInfo(list);
                                            }

                                        }
                                        @Override
                                        public void onError(Throwable e) {
                                            LogUtils.e("获取课表","失败");
                                            ToastUtils.showShort("登陆信息有误");
                                        }
                                        @Override
                                        public void onComplete() {
                                            if(getCoursesSuccessed){
                                                LogUtils.i("获取课表","完毕");
                                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("GetCourse", Context.MODE_PRIVATE);
                                                final SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putInt("getCourseState", 1);             //   0:不成功，1：成功
                                                editor.commit();
                                                finish();
                                            }

                                        }
                                    });
                        }
                    }
                    break;
                default:
                    break;
            }
        }else {
            Toast.makeText(context,"网络不可用",Toast.LENGTH_SHORT).show();
        }
    }





    @Override
    public void finish() {
        MainActivity.Companion.setFragmentNUM(1);
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        super.finish();
    }
}
