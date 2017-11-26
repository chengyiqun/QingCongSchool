package com.example.xpb.qingcongschool.util;

import com.example.xpb.qingcongschool.app.MyApplication;
import com.example.xpb.qingcongschool.main.MainActivity;

/**
 * Created by 程义群（空灵入耳） on 2017/11/13 0013.
 */

public class Utils {
    public static int dip2px(float dip) {
        return (int) (dip * MyApplication.Companion.getScale() + 0.5f);
    }
}
