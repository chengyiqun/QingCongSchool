package com.example.xpb.qingcongschool.util;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.xpb.qingcongschool.BuildConfig;
import com.example.xpb.qingcongschool.app.MyApplication;
import com.example.xpb.qingcongschool.main.MainActivity;

/**
 * Created by 程义群（空灵入耳） on 2017/11/13 0013.
 */

public class Utils {
    public static int dip2px(float dip) {
        return (int) (dip * MyApplication.Companion.getScale() + 0.5f);
    }

    public static void println(String s){
        if(BuildConfig.DEBUG){
            System.out.println(s);
        }
    }

    public static void debug(final Object... contents){
        if (BuildConfig.DEBUG){
            LogUtils.d(contents);
        }
    }

    public static void inform(final Object... contents){
        if (BuildConfig.DEBUG){
            LogUtils.i(contents);
        }
    }

    public static void error(final Object... contents){
        if (BuildConfig.DEBUG){
            LogUtils.e(contents);
        }
    }
}
