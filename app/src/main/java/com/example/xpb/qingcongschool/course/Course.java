package com.example.xpb.qingcongschool.course;

import android.support.annotation.Keep;

import org.jetbrains.annotations.Nullable;
import org.litepal.crud.DataSupport;

@Keep
public class Course extends DataSupport {
    private int id;
    @Nullable
    private String courseID;
    @Nullable
    private String teachID;
    private int startYear;
    private int endYear;
    private int semester;
    @Nullable
    private String courseName;
    @Nullable
    private String courseTime;
    @Nullable
    private String classsroom;
    @Nullable
    private String teacherName;
    private int dayOfWeek;
    private int startSection;
    private int endSection;
    private int startWeek;
    private int endWeek;
    private int everyWeek;
    private int sameTime;

    public int getId() {
        return id;
    }

    public void setId(int  id) {
        this.id = id;
    }

    @Nullable
    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(@Nullable String courseID) {
        this.courseID = courseID;
    }

    @Nullable
    public String getTeachID() {
        return teachID;
    }

    public void setTeachID(@Nullable String teachID) {
        this.teachID = teachID;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    @Nullable
    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(@Nullable String courseName) {
        this.courseName = courseName;
    }

    @Nullable
    public String getCourseTime() {
        return courseTime;
    }

    public void setCourseTime(@Nullable String courseTime) {
        this.courseTime = courseTime;
    }

    @Nullable
    public String getClasssroom() {
        return classsroom;
    }

    public void setClasssroom(@Nullable String classsroom) {
        this.classsroom = classsroom;
    }

    @Nullable
    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(@Nullable String teacherName) {
        this.teacherName = teacherName;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
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

    public int getStartWeek() {
        return startWeek;
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

    public int getEveryWeek() {
        return everyWeek;
    }

    public void setEveryWeek(int everyWeek) {
        this.everyWeek = everyWeek;
    }

    public int getSameTime() {
        return sameTime;
    }

    public void setSameTime(int sameTime) {
        this.sameTime = sameTime;
    }
}
