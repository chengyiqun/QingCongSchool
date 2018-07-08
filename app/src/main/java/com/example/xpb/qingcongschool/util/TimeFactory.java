package com.example.xpb.qingcongschool.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author 程义群
 */
public class TimeFactory {


    public static  String getCurrentTime(){
        //获取当前时间
        String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format( new Date());
        System.out.println("当前时间："+current);
        return current;
    }

    public static String second2TimeStrapString(long second){
        Date dt = new Date();
        dt.setTime(second);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");//设置日期格式
        return df.format(dt);
    }

    /*
 获取用户注册的时间,即当前时刻系统的时间。
 */
    public static String getCurrentTimeSecond(){
        String time="";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        System.out.println(df.format(new Date()));// new Date()为获取当前系统时间

        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        System.out.println(year + "/" + month + "/" + date + " " +hour + ":" +minute + ":" + second);
        return time;
    }

    /*
   获取用户注册的时间,即当前时刻系统的时间。
   */
    public static Date getCurrentTimeDate(){
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
        return currentDate;
    }

}