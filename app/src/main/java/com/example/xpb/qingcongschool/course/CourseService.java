package com.example.xpb.qingcongschool.course;

/**
 * * Course表的业务逻辑处理
 * <p>
 * Created by xpb on 2017/3/9.
 */

import org.litepal.crud.DataSupport;

import java.util.List;


public class CourseService {
    public static final int UPLOAD_COURSE_SUCCESS = 3201;
    public static final int COURSE_EXISTED = 3202;
    public static final int USER_NOTEXIST = 3003;
    public static final int TOKEN_ERROR = 3004;

    private static volatile CourseService courseService;

    private CourseService() {
    }


    public static CourseService getCourseService() {
        if (courseService == null) {
            synchronized (CourseService.class) {
                if (courseService == null) {
                    courseService = new CourseService();
                }
            }
        }
        return courseService;
    }

    /**
     * 查询所有课程
     *
     * @return
     */
    public List<Course> findAll() {
        return DataSupport.findAll(Course.class);
    }

    public void saveCourseInfo(List<Course> list) {

        /*把数据保存到数据库中*/


        DataSupport.deleteAll(Course.class); //删除之前的数据
        for (int i = 0; i < list.size(); i++) {
            System.out.println("-----------提交数据之时-----------------");


            if (list.get(i).save()) {
                System.out.println("-----------存储成功-----------------" + i);
            } else {
                System.out.println("-----------存储失败-----------------" + i);
            }
            System.out.println(i + "次-----------提交数据完成-----------------");
        }
    }

}

