package com.example.xpb.qingcongschool.course;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.example.xpb.qingcongschool.R;
import com.example.xpb.qingcongschool.app.MyApplication;
import com.example.xpb.qingcongschool.main.BaseActivity;
import com.example.xpb.qingcongschool.main.MainActivity;
import com.example.xpb.qingcongschool.util.NetworkUtil;
import com.example.xpb.qingcongschool.util.Utils;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by xpb on 2017/3/4.
 */
public class GetCourseActivity extends BaseActivity implements View.OnClickListener {

    // private TextView textView;
    Context context=this;

    public Observable<ResponseBody> observable;   //登录之前获取验证码
    public Observable<ResponseBody> observableing;   //登录进行时
    public Observable<ResponseBody> observableing2;   //登录进行时,重定向一次
    public Observer observer;

    public Context mContext;


    private Button loginButton;
    private Button reIdentifyButton;
    private EditText studentCodeEditText;
    private EditText passwordEditText;
    private EditText identifyCodeEditText;
    private ImageView codeImage;

    private Bitmap codeBitmap;

    // Host地址
    public static final String HOST = "xk2.ahu.cn";
    // 基础地址
    public static final String URL_BASE = "http://***.***.***.***/";
    //登录首页面地址，
    public static final String URL_BEFORE_LOGIN = "http://xk2.ahu.cn/";
    // 验证码地址
    public static final String URL_CODE = "http://xk2.ahu.cn/";
    // 登陆进系统地址
    public static final String URL_LOGIN = "http://xk2.ahu.cn/default2.aspx/";

    // 登录成功的首页
    public static String URL_MAIN = "http://xk2.ahu.cn/";
    // 请求地址
    public static String URL_QUERY = "http://***.***.***.***/QUERY";

    public static String URL_BAIDU = "http://image.baidu.com/search/down?tn=download&word=download&ie=utf8&" +
            "fr=detail&url=http%3A%2F%2Fpic.58pic.com%2F58pic%2F14%2F54%2F14%2F09E58PICUpb_1024.jpg&" +
            "thumburl=http%3A%2F%2Fimg1.imgtn.bdimg.com%2Fit%2Fu%3D3611450414%2C2684460387%26fm%3D23%26gp%3D0.jpg/";


    private String __VIEWSTATE = "/wEPDwUJODk4OTczODQxZGSd5+vukryCS8lHzdVXKTT0u4iBCQ==";

    private String txtUserName = "E11414081";
    private String TextBox2 = "123456b";
    private String txtSecretCode = "";
    private String RadioButtonList1 = "学生";
    private String Button1 = "";
    private String lbLanguage = "";
    private String hidPdrs = "";
    private String hidsc = "";
    private String __EVENTVALIDATION = "/wEWDgLzoKm3DwKl1bKzCQLs0fbZDAKEs66uBwK/wuqQDgKAqenNDQLN7c0VAuaMg+INAveMotMNAoznisYGArursYYIAt+RzN8IApObsvIHArWNqOoPaI2efK7Edblvk63PR91f855AKWE=";


    private static String xh = "E11414081";
    private String xm = "%D0%EC%C5%F4%B0%EF";
    private String gnmkdm = "N121603";
    //http://xk2.ahu.cn/xskbcx.aspx?xh=E11414083&xm=%D6%EC%D6%BE%CE%C4&gnmkdm=N121603
    public static SharedPreferences sharedPreferences;
    //SharedPreferences  pref = GetCourseActivity.this.getSharedPreferences("xxcookie",MODE_PRIVATE);


    private static String cookiesone = "";
    private static HashSet<String> cookies = new HashSet<>();

    public interface RetrofitServiceBeforeSchool {
        @GET("CheckCode.aspx")
        Observable<ResponseBody> loginSchoolBefore();
    }

    public interface RetrofitServiceSchool {

        @Headers({
                "Host: xk2.ahu.cn",
                "Referer: http://xk2.ahu.cn/default2.aspx",
                "User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36"
        })
        @POST("default2.aspx")
        @FormUrlEncoded
        Observable<ResponseBody> loginSchool(@FieldMap(encoded = true) Map<String, String> reviews);   // encoded =true 至关重要,表示不进行URL编码
    }

    public interface RetrofitServiceSchool2 {
        //http://xk2.ahu.cn/xskbcx.aspx?xh=E11414081&xm=%D0%EC%C5%F4%B0%EF&gnmkdm=N121603
        //http://xk2.ahu.cn/xskbcx.aspx?xh=E11414083&xm=%D6%EC%D6%BE%CE%C4&gnmkdm=N121603
        @Headers({
                "Host: xk2.ahu.cn",
                "User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36"})
        @GET("xskbcx.aspx")
        Observable<ResponseBody> loginSchool2(@Query("xh") String xh, @Query("xm") String xm, @Query("gnmkdm") String gnmkdm);
    }



    /*private static Retrofit create() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.connectTimeout(9, TimeUnit.SECONDS);

        return new Retrofit.Builder().baseUrl(URL_CODE)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }*/

    private static OkHttpClient httpClient = new OkHttpClient.Builder()
            .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request.Builder builder = chain.request().newBuilder();
                    builder.addHeader("cookie", cookiesone);
                    builder.addHeader("Referer","http://xk2.ahu.cn/xs_main.aspx?xh="+xh);
                    return chain.proceed(builder.build());
                }
            }).connectTimeout(120,TimeUnit.SECONDS)
            .build();


    private static OkHttpClient getNewClient() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();

        return client.addNetworkInterceptor(
                new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request.Builder builder = chain.request().newBuilder();
                        builder.addHeader("cookie", cookiesone);
                        return chain.proceed(builder.build());
                    }
                })
                .connectTimeout(120, TimeUnit.SECONDS)
                .build();
    }

    private static OkHttpClient getNewClient2() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                //System.out.println(" 555555555555555555555555555555555");
                if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                    for (String header : originalResponse.headers("Set-Cookie")) {
                        cookies.add(header);
                        cookiesone = header;
                        //System.out.println(cookiesone + "      hhhh");
                    }
                }
                return originalResponse;
            }
        });

        return client.addNetworkInterceptor(
                new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
    }


    RetrofitServiceBeforeSchool retrofitServiceBeforeSchool = new Retrofit.Builder()
            .baseUrl(URL_CODE)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(getNewClient2())
            .build()
            .create(RetrofitServiceBeforeSchool.class);

    private static RetrofitServiceSchool retrofitServiceSchool = new Retrofit.Builder()
            .baseUrl(URL_MAIN)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(getNewClient())
            .build()
            .create(RetrofitServiceSchool.class);

    private static RetrofitServiceSchool2 retrofitServiceSchool2 = new Retrofit.Builder()
            .baseUrl(URL_MAIN)
            // .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient)
            .build()
            .create(RetrofitServiceSchool2.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getcourse);

        init();
        //System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");


        observableing2 = retrofitServiceSchool2.loginSchool2(xh, xm, gnmkdm);

        reIdentifyButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        //System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
    }

    private boolean identifyCodeEditTextClickFirst=true;
    public void init() {
        loginButton = (Button) findViewById(R.id.login_button);
        reIdentifyButton = (Button) findViewById(R.id.getCode_button);
        studentCodeEditText = (EditText) findViewById(R.id.studentname_editText);
        passwordEditText = (EditText) findViewById(R.id.password_editText);
        identifyCodeEditText = (EditText) findViewById(R.id.identifyCode_editText);
        identifyCodeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus&&identifyCodeEditTextClickFirst)
                {
                    if(NetworkUtil.isNetworkAvailable(context)){
                        identifyCodeEditText.setText("");
                        observable = retrofitServiceBeforeSchool.loginSchoolBefore();
                        observable.subscribeOn(Schedulers.io())                     //登录进教务系统之前，获取验证码

                                .map(new Function<ResponseBody, Bitmap>() {
                                    @Override
                                    public Bitmap apply(ResponseBody responseBody) {
                                        InputStream inputStream = null;
                                        inputStream = responseBody.byteStream();
                                        //System.out.println();
                                        if (inputStream != null) {
                                            return BitmapFactory.decodeStream(inputStream);
                                        } else {
                                            return null;
                                        }
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
                                        identifyCodeEditTextClickFirst=false;
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        identifyCodeEditTextClickFirst=false;
                                        codeImage.setContentDescription("刷新验证码");
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onComplete() {
                                        identifyCodeEditTextClickFirst=false;
                                    }
                                });
                    }else {
                        Toast.makeText(context,"网络不可用",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        codeImage = (ImageView) findViewById(R.id.codeImage);
    }


    @Override
    public void onClick(View v) {
        if(NetworkUtil.isNetworkAvailable(context)){
            switch (v.getId()) {
                case R.id.getCode_button:
                    //System.out.println("OnClick getCode_button");
                    identifyCodeEditText.setText("");
                    //System.out.println("wwwwwwwwwwwwwwwwww");
                    observable = retrofitServiceBeforeSchool.loginSchoolBefore();
                    //System.out.println("oooooooooooooooooooooooooooooo");
                    observable.subscribeOn(Schedulers.io())                     //登录进教务系统之前，获取验证码

                            .map(new Function<ResponseBody, Bitmap>() {
                                @Override
                                public Bitmap apply(ResponseBody responseBody) {
                                    InputStream inputStream = null;
                                    inputStream = responseBody.byteStream();
                                    //System.out.println("4444444466666");
                                    //System.out.println();
                                    if (inputStream != null) {
                                        //System.out.println("tutututu");
                                        return BitmapFactory.decodeStream(inputStream);
                                    } else {
                                        //System.out.println("null!");
                                        return null;
                                    }
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

                                }

                                @Override
                                public void onComplete() {

                                }
                            });


                    break;

                case R.id.login_button:

                    //System.out.println("333333333333333333333333");
                    int n = cookiesone.length() - 18;
                    //System.out.println(n + "      lllllllllllllllllllllllll");
                    cookiesone = cookiesone.substring(0, n);
                    //System.out.println(cookiesone + "llllllllllllllllllllllllll");
                    txtSecretCode = identifyCodeEditText.getText().toString();

                    txtUserName=studentCodeEditText.getText().toString();
                    TextBox2=passwordEditText.getText().toString();
                    xh = txtUserName;
                    //System.out.println(txtUserName);
                    //System.out.println(TextBox2);
                    Map<String, String> reviewMap = new HashMap<String, String>();

                    //System.out.println("ggggggggggggggghhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
                    try {
                        RadioButtonList1 = URLEncoder.encode("学生", "gb2312");
                        __VIEWSTATE = URLEncoder.encode(__VIEWSTATE, "gb2312");
                        __EVENTVALIDATION = URLEncoder.encode(__EVENTVALIDATION, "gb2312");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //System.out.println(txtSecretCode);
                    //System.out.println(TextBox2);
                    //System.out.println(RadioButtonList1);
                    //System.out.println(hidsc);
                    //System.out.println(__VIEWSTATE);
                    //System.out.println(__EVENTVALIDATION);

                    reviewMap.put("__VIEWSTATE", __VIEWSTATE);
                    reviewMap.put("txtUserName", txtUserName);
                    reviewMap.put("TextBox2", TextBox2);
                    reviewMap.put("txtSecretCode", txtSecretCode);
                    reviewMap.put("RadioButtonList1", RadioButtonList1);
                    reviewMap.put("Button1", Button1);
                    reviewMap.put("lbLanguage", lbLanguage);
                    reviewMap.put("hidPdrs", hidPdrs);
                    reviewMap.put("hidsc", hidsc);
                    reviewMap.put("__EVENTVALIDATION", __EVENTVALIDATION);   // 参考资料没有此项,要有，应该是系统不同的问题

                    observableing = retrofitServiceSchool.loginSchool(reviewMap);
                    //System.out.println(txtSecretCode);
                    observableing.subscribeOn(Schedulers.io())                     //登录进教务系统
                            .flatMap(new Function<ResponseBody, Observable<ResponseBody>>() {
                                @Override
                                public Observable<ResponseBody> apply(ResponseBody responseBody) throws Exception {
                                    //判断登录是否错误，如密码错，用户名错，提取系统反馈的信息
                                    String responsebody = responseBody2String(responseBody);
                                    if(responsebody.contains("敏感字符"))
                                    {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(MyApplication.getContext(),"你的ip已被记录\n请不要使用敏感字符！！",Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                    String studentName = isLogin(responsebody);
                                    if (studentName!=null&&!studentName.equals("")) {
                                        //  Toast.makeText(getApplicationContext(), stringtext,Toast.LENGTH_SHORT).show();
                                        xm = studentName.substring(0, studentName.length() - 2);
                                    }
                                    final String errorType = errorType(responsebody);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MyApplication.getContext(), errorType,Toast.LENGTH_LONG).show();
                                            if(errorType.contains("验证码")){
                                                identifyCodeEditText.setText("");
                                                reIdentifyButton.performClick();
                                            }
                                        }
                                    });
                                    return retrofitServiceSchool2.loginSchool2(xh, xm, gnmkdm);
                                }
                            })
                            .map(new Function<ResponseBody, String>() {

                                @Override
                                public String apply(ResponseBody responseBody) throws Exception {
                                    String string = responseBody2String(responseBody);
                                    if(string.contains("敏感字符"))
                                    {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(MyApplication.getContext(),"你的ip已被记录\n请不要使用敏感字符！！",Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                    //System.out.println("eeeeeee111111111111111");
                                    CourseService courseService = CourseService.getCourseService();
                                    //System.out.println("eeeeeee22222222222222222");
                                    courseService.getCourseInfo(string);
                                    //System.out.println("eeeeeee333333333333333333333333333");
                                    if (string!=null&&!string.equals("")) {
                                        sharedPreferences = getApplicationContext().getSharedPreferences("GetCourse", Context.MODE_PRIVATE);
                                        final SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putInt("getCourseState", 1);             //   0:不成功，1：成功
                                        editor.commit();
                                        //System.out.println("课表字符串不为空");

                                    }
                                    return string;
                                }
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new io.reactivex.Observer<String>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(String value) {
                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onComplete() {
                                    finish();
                                }
                            })
                    ;
                    break;
                default:
                    //System.out.println("0000000000");
                    break;
            }
        }else {
            Toast.makeText(context,"网络不可用",Toast.LENGTH_SHORT).show();
        }
    }

    /*
    判断是否登录成功，通过页面中是否能提取出“XX同学”判断。
     */
    public String isLogin(String content) {
        Document doc = Jsoup.parse(content, "UTF-8");
        Elements elements = doc.select("span#xhxm");
        try {
            Element element = elements.get(0);
            return element.text();
        } catch (IndexOutOfBoundsException e) {
            //e.printStackTrace();
        }
        return null;
    }

    /*
查找登录出错原因，通过提取页面中“<script language='javascript' defer>alert('用户名不能为空！！');”的信息判断。
 */
    public String errorType(String content) {
        String errorType3 = "";
        Document doc = Jsoup.parse(content, "UTF-8");
        Elements script = doc.select("script");
        int j = 0;
        for (Element element : script) {
            if (j == 2) {
                errorType3 = element.html();
            }
            j++;
        }
        int firstLeft = errorType3.indexOf("(");
        int firstRight = errorType3.indexOf(")");

        return errorType3.substring(firstLeft + 2, firstRight - 1);
    }

    /*
    把ResponseBody类型转换成String类型
     */
    public String responseBody2String(ResponseBody responseBody) {
        InputStream inputStream = null;
        inputStream = responseBody.byteStream();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream, "GB2312"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
        return string;
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
