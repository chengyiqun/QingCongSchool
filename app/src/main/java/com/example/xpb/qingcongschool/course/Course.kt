package com.example.xpb.qingcongschool.course

import android.support.annotation.Keep

import org.litepal.crud.DataSupport

/**
 * * 课程实体类
 * Created by xpb on 2017/3/9.
 */
@Keep
class Course : DataSupport() {
    var id: Int = 0//主鍵，自增
    var startYear: Int = 0//学年开始年
    var endYear: Int = 0//学年结束年
    var semester: Int = 0//学期
    var courseName: String? = null//课程名
    var courseTime: String? = null//课程时间，冗余字段
    var classsroom: String? = null//教室
    var teacherName: String? = null//老师
    var dayOfWeek: Int = 0//星期几
    var startSection: Int = 0//第几节课开始
    var endSection: Int = 0//第几节课结束
    var startWeek: Int = 0//开始周
    var endWeek: Int = 0//结束周
    var everyWeek: Int = 0//标记是否是单双周，0为每周,1单周，2双周
    var sameTime: Int = 0//标记同一时间段是否有不同的课

    override fun toString(): String {
        return ("Course [id=" + id + ", startYear=" + startYear + ", endYear="
                + endYear + ", semester=" + semester + ", courseName="
                + courseName + ", courseTime=" + courseTime + ", classsroom="
                + classsroom + ", teacher=" + teacherName + ", dayOfWeek="
                + dayOfWeek + ", startSection=" + startSection
                + ", endSection=" + endSection + ", startWeek=" + startWeek
                + ", endWeek=" + endWeek + ", everyWeek=" + everyWeek +
                ", sameTime=" + sameTime + "]")
    }


}