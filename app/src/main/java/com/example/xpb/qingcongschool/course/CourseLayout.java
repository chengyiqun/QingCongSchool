package com.example.xpb.qingcongschool.course;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.xpb.qingcongschool.app.MyApplication;
import com.example.xpb.qingcongschool.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xpb on 2017/3/9.
 */
public class CourseLayout extends ViewGroup {
    private List<CourseView> courses = new ArrayList<CourseView>();
    private int width;//布局宽度
    private int height;//布局高度
    public  int sectionHeight;//每节课高度
    public  int sectionWidth;//每节课宽度
    private int monthWidth;//月份和节数宽度
    public static final int sectionNumber = 12;//一天的节数
    public static final int dayNumber = 7;//一周的天数
    public static int divideWidth = 2;//分隔线宽度,dp
    public static int divideHeight = 2;//分隔线高度,dp


    public CourseLayout(Context context) {
        this(context, null);
    }

    public CourseLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CourseLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //默认宽度全屏
        width = getScreenWidth();
        //默认高度600dp 转换成PX
        height = MyApplication.getCourceLayoutHeightPX();
        //默认分隔线宽度2dp
        divideWidth = Utils.dip2px(2);
        //默认分隔线高度2dp
        divideHeight = Utils.dip2px(2);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        courses.clear();//清除

        //System.out.println("============屏幕宽度"+getMeasuredWidth()+"============");
        sectionHeight = (getMeasuredHeight() - divideWidth * sectionNumber)/ sectionNumber;//计算每节课高度
        sectionWidth =2* (getMeasuredWidth() - divideWidth * dayNumber)/( dayNumber*2+1);//计算每节课宽度
        //sectionWidth=sectionWidth*2;
        monthWidth=sectionWidth/2;
        //System.out.println("============课程宽度"+sectionWidth+"============");
        //System.out.println("============节数宽度"+monthWidth+"============");
        int count = getChildCount();//获得子控件个数
        for (int i = 0; i < count; i++) {
            CourseView child = (CourseView) getChildAt(i);
            courses.add(child);//增加到list中


            int week = child.getWeek();//获得周几
            int startSection = child.getStartSection();//开始节数
            int endSection = child.getEndSection();//结束节数

            //int left = sectionWidth * (week - 1) + (week) * divideWidth+monthWidth;//计算左边的坐标,不能加这个monthwidth

            int left = sectionWidth * (week - 1) + (week+1) * divideWidth;//计算左边的坐标
            int right = left + sectionWidth;//计算右边坐标
            int top = sectionHeight * (startSection - 1) + (startSection) * divideHeight;//计算顶部坐标
            int bottom = top + (endSection - startSection + 1) * sectionHeight+ (endSection - startSection) * divideHeight;//计算底部坐标
            //System.out.println("============左边坐标"+left+"============");
            //System.out.println("============右边坐标"+right+"============");
            child.layout(left, top, right, bottom);
        }


    }

    public int getScreenWidth() {
        WindowManager manager = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }


}