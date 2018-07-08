package com.example.xpb.qingcongschool.util;

import com.example.xpb.qingcongschool.BuildConfig;
import com.example.xpb.qingcongschool.app.MyApplication;

import java.util.HashSet;

/**
 * Created by 程义群（空灵入耳） on 2017/11/13 0013.
 */

public class Utils {
     private static HashSet<String> chinesePunctuationHashSet =new HashSet<String>(){{
        add(",");
        add("。");
        add("、");
        add("；");
        add("？");
        add("：");
        add("！");
        add("“");
        add("”");
        add("‘");
        add("’");
        add("…");
        add("（");
        add("）");
        add("《");
        add("》");
        add("〈");
        add("〉");
        add("〔");
        add("〕");
        add("【");
        add("［");
        add("］");
        add("】");
        add("—");
        add("·");
        add("￥");
        add("．");
        add("｛");
        add("｝");
        add("｀");
    }};

    public static int dip2px(float dip) {
        return (int) (dip * MyApplication.getScale() + 0.5f);
    }

    public static void println(String s){//用于只在调试时输出
        if(BuildConfig.DEBUG){
            System.out.println(s);
        }
    }


    //如果长度大于50，那么就截取前50长度的内容加上省略号
    public static String previewComment(String s){
        int index=0;
        int lasti=getLength(s);
        System.out.println(lasti);
        if(lasti>50){
            return s.substring(0,lasti-1)+"……";
        }else {
            return s;
        }

    }


    private static int getLength(String s) {//用于计算混合中英文的字符串长度，中文为1，英文为0.5，结果向上取整进位
        double valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < s.length(); i++) {
            // 获取一个字符
            String temp = s.substring(i, i + 1);
            // 判断是否为中文字符
            if (temp.matches(chinese)|| chinesePunctuationHashSet.contains(temp)) {
                // 中文字符长度为1
                valueLength += 1;
            } else {
                // 其他字符长度为0.5
                valueLength += 0.5;
            }
            if(valueLength>51){
                return i;
            }
        }
        //进位取整
        return (int) Math.ceil(valueLength);
    }
}
