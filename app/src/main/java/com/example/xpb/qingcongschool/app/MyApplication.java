package com.example.xpb.qingcongschool.app;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.blankj.utilcode.util.Utils;
import com.example.xpb.qingcongschool.util.NetworkUtil;

import org.litepal.LitePalApplication;

/**
 * @program: QingCongSchool
 * @description: application初始化，
 * @author: 程义群
 * @create: 2018-07-08 10:21
 **/

public class MyApplication extends LitePalApplication {//继承了那个数据库的 application，以添加自己的初始化操作，然后定义到manifests
    public static final String PACKAGE_NAME = "com.example.xpb.qingcongschool";
    public static final float COURCE_LAYOUT_HEIGHT_DPI = 600f;
    private static int screenwidth = -1;
    private static float scale = 0f;
    private static int courceLayoutHeightPX = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化工具类
        Utils.init(this);
        NetworkUtil.init(this);
        scale = setScale();
        screenwidth = setWidth();
        courceLayoutHeightPX = com.example.xpb.qingcongschool.util.Utils.dip2px(COURCE_LAYOUT_HEIGHT_DPI);
    }

    private int setWidth() {
        Resources resources = this.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    private float setScale() {
        return this.getResources().getDisplayMetrics().density;
    }

    public static int getScreenwidth() {
        return screenwidth;
    }

    public static float getScale() {
        return scale;
    }

    public static int getCourceLayoutHeightPX() {
        return courceLayoutHeightPX;
    }
}
