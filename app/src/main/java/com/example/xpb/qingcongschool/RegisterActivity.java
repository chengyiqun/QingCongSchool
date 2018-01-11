package com.example.xpb.qingcongschool;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.example.xpb.qingcongschool.main.BaseActivity;
import com.example.xpb.qingcongschool.util.NetworkUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import cn.smssdk.SMSSDK;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by xpb on 2017/3/2.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout root_linearLayout;

    private Button getIdentifyCodeButton;
    private Button registerButton;
    private EditText phoneNumEditText;
    private EditText identifyCodeEditText;

    ProgressDialog progressDialog;    //  待定
    public boolean showLoading = true;

    private EditText passwordEditText;
    private String phoneNum;
    private String password;
    private String identifyCode;

    public  static int REGISTER_SUCCESS=2001;
    public  static int IDENTIFYCODE_ERROR=2002;
    public  static int USER_EXISTED=2003;
    public  static int USER_ILLEGAL=2004;

    int recLen=60;

    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler() {

        public void handleMessage(Message msg) {         // handle message
            switch (msg.what) {
                case 1:
                    recLen--;
                    getIdentifyCodeButton.setText(String.valueOf(recLen));
                    getIdentifyCodeButton.setTextSize(20);

                    if (recLen > 0) {
                        Message message = handler.obtainMessage(1);	//obtainmessage（）是从消息池中拿来一个msg 不需要另开辟空间new，
																	//new需要重新申请，效率低，obtianmessage可以循环利用；
																	//new需要重新申请，效率低，obtianmessage可以循环利用；
                        handler.sendMessageDelayed(message, 1000);      // send message
                    } else {
                        getIdentifyCodeButton.setEnabled(true);
                        getIdentifyCodeButton.setText("获取验证码");
                        recLen=60;//重置计数
                        getIdentifyCodeButton.setTextSize(12);
                    }
            }

            super.handleMessage(msg);
        }
    };

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        System.out.println("已跳到注册界面！可以开始注册了！");
        getIdentifyCodeButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);

    }

    @SuppressLint("ClickableViewAccessibility")
    public void init() {
        progressDialog = new ProgressDialog(RegisterActivity.this);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_register);
        mToolbar.setTitle("新用户注册");
        setSupportActionBar(mToolbar);
        //设置是否有返回箭头
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        root_linearLayout = (LinearLayout) findViewById(R.id.root_linearlayout);

        getIdentifyCodeButton = (Button) findViewById(R.id.get_identify_button);
        registerButton = (Button) findViewById(R.id.register_button);

        phoneNumEditText = (EditText) findViewById(R.id.phoneNum_editText);
        identifyCodeEditText = (EditText) findViewById(R.id.identifyCode_editText);
        passwordEditText = (EditText) findViewById(R.id.password_editText);
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    Toast.makeText(RegisterActivity.this,"6-16个字符；不能包含空格\n不能是9位以下纯数字",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_identify_button:  //获取验证码
                if (!TextUtils.isEmpty(phoneNumEditText.getText().toString())) {
                    SMSSDK.getVerificationCode("86", phoneNumEditText.getText()
                            .toString());
                    phoneNum = phoneNumEditText.getText().toString();
                    getIdentifyCodeButton.setEnabled(false);//设置不可点
                    Message message = handler.obtainMessage(1);     // Message
                    handler.sendMessageDelayed(message, 1000);
                } else {
                    Snackbar.make(root_linearLayout, "电话不能为空", Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.register_button:
                identifyCode = identifyCodeEditText.getText().toString();
                password = passwordEditText.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    Snackbar.make(root_linearLayout, "密码不能为空", Snackbar.LENGTH_SHORT).show();
                } else {
                    if (!TextUtils.isEmpty(identifyCode)) {
                        if(passwordMatchs(password)) {
                            registerUser();
                        }else {
                            Toast.makeText(RegisterActivity.this,"6-16个字符；不能包含空格\n不能是9位以下纯数字",Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Snackbar.make(root_linearLayout, "验证码不能为空", Snackbar.LENGTH_SHORT).show();
                    }
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

    public void registerUser() {
        HashMap<String, Object> userInfo = new HashMap<String, Object>();
        userInfo.put("phoneNum", phoneNum);
        userInfo.put("password", password);
        userInfo.put("identifyCode", identifyCode);
        userInfo.put("zone","86");

        Gson gson = new Gson();
        String jsonStr = gson.toJson(userInfo);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonStr);
        Observable<ResponseBody> observable = RetrofitFactory.getInstance().registerUser(body);
        observable.subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    if (NetworkUtil.isNetworkAvailable(RegisterActivity.this)) {
                        if (showLoading) {
                            if (progressDialog != null && !progressDialog.isShowing()) {
                                progressDialog.show();
                            }
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "网络连接异常，请检查网络", Toast.LENGTH_LONG).show();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ResponseBody, String>() {
                    @Override
                    public String apply(ResponseBody responseBody) {
                        System.out.println("正在注册！");
                        System.out.println("请求返回数据为：" + responseBody);
                        InputStream inputStream = null;
                        inputStream = responseBody.byteStream();
                        BufferedReader br = null;

                        br = new BufferedReader(new InputStreamReader(inputStream));
                        String string = null;
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        try {
                            while ((line = br.readLine()) != null) {
                                sb.append(line).append("\n");
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        string = sb.toString();
                        System.out.println("请求返回数据为：" + string);
                        return string;
                    }


                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(String s) {
                        progressDialog.cancel();
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(s);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        int result = 0;
                        try {
                            result = jsonObj.getInt("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("获取的返回值：" + result);
                        if (result == REGISTER_SUCCESS) {
                            System.out.println("注册OK！");
                            finish();
                            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_LONG).show();
                        } else if (result == IDENTIFYCODE_ERROR) {
                            System.out.println("验证码有误");
                            identifyCodeEditText.setText("");
                            Snackbar.make(root_linearLayout, "验证码有误", Snackbar.LENGTH_SHORT).show();
                        } else if (result == USER_EXISTED) {
                            System.out.println("用户已存在！");
                            Snackbar.make(root_linearLayout, "用户已存在", Snackbar.LENGTH_SHORT).show();
                        } else if (result == USER_ILLEGAL){
                            System.out.println("用户已被拉黑");
                            Snackbar.make(root_linearLayout, "此用户已被拉黑", Snackbar.LENGTH_SHORT).show();
                        }else
                        {
                            Toast.makeText(getApplicationContext(), "未知错误："+result, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        progressDialog.cancel();
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public boolean passwordMatchs(String str) {
        return str.matches(".{6,16}") && str.matches("\\S*") && str.matches("(.*\\D.*){1,8}|.{9,}");
    }
}
