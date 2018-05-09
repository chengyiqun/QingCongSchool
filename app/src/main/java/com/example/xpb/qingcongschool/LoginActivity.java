package com.example.xpb.qingcongschool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DebugUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.example.xpb.qingcongschool.main.BaseActivity;
import com.example.xpb.qingcongschool.main.MainActivity;
import com.example.xpb.qingcongschool.util.FileUtil;
import com.example.xpb.qingcongschool.util.NetworkUtil;
import com.example.xpb.qingcongschool.util.Utils;
import com.google.gson.Gson;
import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private Toolbar mToolbar;
    private LinearLayout root_linearLayout;

    private Button registerButton;
    private Button loginButton;
    private Button resetPasswordButton;
    private EditText userNameEditText;
    private EditText passwordEditText;
    Dialog dialog;
    // 填写从短信SDK应用后台注册得到的APPKEY
    private static final String APPKEY = "10085a6d32d89";
    // 填写从短信SDK应用后台注册得到的APPSECRET
    private static final String APPSECRET = "193dd6346affefdbf56831b200e96651";

    public  static final int LOGIN_SUCCESS=3001;
    public  static final int PASSWORD_ERROR=3002;
    public  static final int USER_NOTEXIST=3003;


    public Observable<ResponseBody> observablelogin;   //登录


    private String phoneNum = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        MobSDK.init(this, APPKEY, APPSECRET);
        registerButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        resetPasswordButton.setOnClickListener(this);

    }
    public void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_loggin);
        mToolbar.setTitle("用户登陆");
        setSupportActionBar(mToolbar);
        //设置是否有返回箭头
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(v -> finish());
        root_linearLayout = (LinearLayout) findViewById(R.id.root_linearlayout);
        registerButton = (Button) findViewById(R.id.register_button);
        loginButton = (Button) findViewById(R.id.login_button);
        resetPasswordButton = (Button) findViewById(R.id.reset_button);
        userNameEditText = (EditText) findViewById(R.id.username_editText);
        passwordEditText = (EditText) findViewById(R.id.password_editText);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                phoneNum = userNameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                HashMap<String, Object> userInfo = new HashMap<String, Object>();
                userInfo.put("phoneNum", phoneNum);
                userInfo.put("password", password);
                Gson gson = new Gson();
                String userJson = gson.toJson(userInfo);
                Utils.println("开始登陆");
                if (checkEdit()) {//检查注册信息
                    //检查网络
                    if (NetworkUtil.isNetworkAvailable(getApplicationContext()))
                        if (userNameEditText.getText().toString().equals(""))
                            Snackbar.make(root_linearLayout, "请输入账号", Snackbar.LENGTH_SHORT).show();
                        else {
                            //启动登录Thread
                            dialog = new Dialog(LoginActivity.this);
                            dialog.setTitle("正在登陆，请稍后...");
                            dialog.setCancelable(false);
                            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), userJson);
                            observablelogin = RetrofitFactory.getInstanceLogin().login(body);
                            observablelogin.subscribeOn(Schedulers.io())
                                    .doOnSubscribe(disposable -> {
                                        Utils.println("doOnScribe,showDiaglog");
                                        if(dialog!=null&&!dialog.isShowing())
                                        {
                                            dialog.show();
                                        }
                                    })
                                    .map(responseBody -> {
                                        Utils.println("正在登录！");
                                        Utils.println("请求返回数据为：" + responseBody);
                                        InputStream inputStream = null;
                                        inputStream = responseBody.byteStream();
                                        BufferedReader br = null;

                                        br = new BufferedReader(new InputStreamReader(inputStream));
                                        String string = null;
                                        StringBuilder sb = new StringBuilder();
                                        String line = null;
                                        try {
                                            while ((line = br.readLine()) != null) {
                                                sb.append(line + "\n");
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
                                        Utils.println("请求返回数据为：" + string);
                                        return string;
                                    })
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<String>() {
                                        @Override
                                        public void onSubscribe(Disposable disposable) {

                                        }

                                        @SuppressLint("ApplySharedPref")
                                        @Override
                                        public void onNext(String s) {

                                            //dialog.cancel();
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
                                            Utils.println("获取的返回值：" + result);
                                            if (result == LOGIN_SUCCESS) {
                                                //登陆成功后把本地缓存的头像删了。
                                                File file = new File(FileUtil.getCachePath(getApplicationContext()), "user-avatar.jpg");
                                                if(file.exists())
                                                {
                                                    file.delete();
                                                }
                                                Utils.println("跳到主界面！");
                                                MainActivity.Companion.setIslogin(true);
                                                MainActivity.Companion.setPhoneNum(phoneNum);
                                                try {
                                                    MainActivity.Companion.setUserName(jsonObj.getString("userName"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                SharedPreferences myLoginSharedPreferences= getSharedPreferences("myLoginSharedPreferences",
                                                        Activity.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = myLoginSharedPreferences.edit();
                                                editor.putString("phoneNum", MainActivity.Companion.getPhoneNum());
                                                editor.putBoolean("islogin", MainActivity.Companion.getIslogin());
                                                editor.putString("accessToken", MainActivity.Companion.getAccessToken());
                                                editor.putString("userName", MainActivity.Companion.getUserName());
                                                editor.commit();

                                                Observable<ResponseBody> observableDownloadAvatar=RetrofitFactory.getInstance().downloadAvatar();
                                                observableDownloadAvatar.subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(new Observer<ResponseBody>() {
                                                            @Override
                                                            public void onSubscribe(Disposable disposable) {

                                                            }

                                                            @Override
                                                            public void onNext(ResponseBody responseBody) {
                                                                Utils.println("这一步");
                                                                boolean writtenToDisk = writeResponseBodyToDisk(responseBody);
                                                                if(BuildConfig.DEBUG){
                                                                    LogUtils.dTag("下载","file download was a success? " + writtenToDisk);
                                                                }
                                                                dialog.cancel();
                                                                finish();
                                                            }

                                                            @Override
                                                            public void onError(Throwable throwable) {
                                                                throwable.printStackTrace();
                                                                dialog.cancel();
                                                                if(BuildConfig.DEBUG){
                                                                    LogUtils.dTag("下载文件", "没有头像");
                                                                }
                                                                finish();
                                                            }

                                                            @Override
                                                            public void onComplete() {

                                                            }
                                                        });
                                                Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_LONG).show();
                                            } else if (result == USER_NOTEXIST) {
                                                Snackbar.make(root_linearLayout, "用户不存在", Snackbar.LENGTH_SHORT).show();
                                            } else if (result == PASSWORD_ERROR) {
                                                passwordEditText.setText("");
                                                Snackbar.make(root_linearLayout, "密码错误", Snackbar.LENGTH_SHORT).show();
                                            } else {
                                                Snackbar.make(root_linearLayout, "登录失败 "+result, Snackbar.LENGTH_SHORT).show();
                                            }
                                            dialog.cancel();
                                        }

                                        @Override
                                        public void onError(Throwable throwable) {
                                            dialog.cancel();
                                            throwable.printStackTrace();
                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });
                        }
                    else {
                        Snackbar.make(root_linearLayout, "网络未连接", Snackbar.LENGTH_SHORT).show();
                    }
                }

                break;
            case R.id.register_button:
                Utils.println("跳到注册界面！");
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                Utils.println("正跳到注册界面！");
                break;
            case R.id.reset_button:
                break;
            default:
                break;
        }
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File userAvatarFile = new File(FileUtil.getCachePath(getApplicationContext()), "user-avatar.jpg");
            if (userAvatarFile.exists())
            {
                userAvatarFile.delete();
            }

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(userAvatarFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    if(BuildConfig.DEBUG){
                        LogUtils.dTag("下载文件", "file download: " + fileSizeDownloaded + " of " + fileSize);
                    }
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }


    //检查登陆信息
    public boolean checkEdit() {
        if (passwordEditText.getText().toString().equals("")) {
            Snackbar.make(root_linearLayout, "密码不能为空", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (userNameEditText.getText().toString().equals("")) {
            Snackbar.make(root_linearLayout, "用户名不能为空", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void finish() {//关闭这个Activity时重新加载整个应用，用来刷新登陆状态
        //MainActivity.islogin=true;
        MainActivity.Companion.setFragmentNUM(2);
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        super.finish();
    }
}
