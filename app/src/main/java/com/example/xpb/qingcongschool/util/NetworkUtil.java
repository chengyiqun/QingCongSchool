package com.example.xpb.qingcongschool.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.webkit.WebSettings;

import com.blankj.utilcode.BuildConfig;
import com.blankj.utilcode.util.Utils;

/**
 * Created by xpb on 2017/3/3.
 */
public class NetworkUtil {
    private static Application sApplication;
    public static void init(@NonNull final Application app) {
        NetworkUtil.sApplication = app;
    }

    /**
    * @Description: 判断网络状态是否可用
    * @Param: ApplicationContext/ActivityContext
    * @return: boolean
    * @Author: 程义群
    * @Date: 2018年6月8日22点03分
    */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
    * @Description: 获取用户代理字符串
    * @Param: null
    * @return: String
    * @Author: 程义群
    * @Date: 2018年6月8日 22点01分
    */
    public static String getUserAgent() {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(sApplication.getApplicationContext());
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        sb.append(";QingXiao/");//加上青校的版本信息
        sb.append(BuildConfig.VERSION_NAME);
        return sb.toString();
    }
}

