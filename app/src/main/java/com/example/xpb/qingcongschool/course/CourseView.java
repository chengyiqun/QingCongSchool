package com.example.xpb.qingcongschool.course;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import com.example.xpb.qingcongschool.R;


/**
 * Created by xpb on 2017/3/9.
 */
public class CourseView extends AppCompatButton {
    private int courseId;
    private int startSection;
    private int endSection;
    private int weekDay;
    private int startWeek;
    private int endWeek;

    public CourseView(Context context) {
        this(context,null);
    }

    public CourseView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public CourseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.CourseView);
        courseId = array.getInt(R.styleable.CourseView_startSection, 0);;
        startSection=array.getInt(R.styleable.CourseView_startSection, 0);
        endSection=array.getInt(R.styleable.CourseView_endSection, 0);
        weekDay=array.getInt(R.styleable.CourseView_weekDay, 0);
        array.recycle();
    }
    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getStartSection() {
        return startSection;
    }

    public void setStartSection(int startSection) {
        this.startSection = startSection;
    }

    public int getEndSection() {
        return endSection;
    }

    public void setEndSection(int endSection) {
        this.endSection = endSection;
    }
    public int getWeek() {
        return weekDay;
    }

    public void setWeek(int week) {
        this.weekDay = week;
    }

    public int getStartWeek(){
        return  startWeek;
    }

    public void setStartWeek(int startWeek) {
        this.startWeek = startWeek;
    }

    public int getEndWeek() {
        return endWeek;
    }

    public void setEndWeek(int endWeek) {
        this.endWeek = endWeek;
    }
}