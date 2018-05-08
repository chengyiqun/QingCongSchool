package com.example.xpb.qingcongschool.course;

/**
 * * Course表的业务逻辑处理
 * <p>
 * Created by xpb on 2017/3/9.
 */

import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.example.xpb.qingcongschool.RetrofitFactory;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;


public class CourseService {
    public  static final int UPLOAD_COURSE_SUCCESS=3201;
    public  static final int COURSE_EXISTED=3202;
    public  static final int USER_NOTEXIST=3003;
    public  static final int TOKEN_ERROR=3004;

    private static volatile CourseService courseService;

    private CourseService() {
    }


    public static CourseService getCourseService() {
        if (courseService == null) {
            synchronized (CourseService.class) {
                if (courseService == null)
                    courseService = new CourseService();
            }
        }
        return courseService;
    }


    /**
     * 保存一节课程
     *
     * @param course
     * @return
     */
    public boolean save(Course course) {
        return course.save();
    }

    /**
     * 查询所有课程
     *
     * @return
     */
    public List<Course> findAll() {
        return DataSupport.findAll(Course.class);
    }

    /**
     * 根据网页返回结果解析课程并保存
     *
     * @param content
     * @return
     */
    public String parseCourse(String content) {
        StringBuilder result = new StringBuilder();
        Document doc = Jsoup.parse(content);

        Elements semesters = doc.select("option[selected=selected]");
        String[] years = semesters.get(0).text().split("-");
        int startYear = Integer.parseInt(years[0]);
        int endYear = Integer.parseInt(years[1]);
        int semester = Integer.parseInt(semesters.get(1).text());
        //System.out.println(semester);
        //System.out.println(startYear + "---------" + endYear);

        Elements elements = doc.select("table#Table1");
        Element element = elements.get(0).child(0);

        int rowNum0 = element.childNodeSize();
        //System.out.println("rowNum0" + rowNum0);   //输出子节点，即有多少节课         12

        //移除一些无用数据

        element.child(0).remove();
        int rowNum1 = element.childNodeSize();
        //System.out.println("rowNum1" + rowNum1);   //输出子节点，即有多少节课         12
        element.child(0).remove();    //源代码
        // element.child(1).remove();     //修改
        element.child(0).child(0).remove();
        element.child(4).child(0).remove();
        element.child(8).child(0).remove();
        int rowNum = element.childNodeSize();
        //System.out.println("rowNum" + rowNum);   //输出子节点，即有多少节课         12
        int[][] map = new int[11][7];
        for (int i = 0; i < rowNum - 1; i++) {
            Element row = element.child(i);
            int columnNum = row.childNodeSize() - 2;
            //System.out.println(i + "   columnNum:   " + columnNum);        //输出一周有几天    8
            for (int j = 1; j < columnNum; j++) {
                Element column = row.child(j);
                int week = fillMap(column, map, i);
                //填充map，获取周几，第几节至第几节
                //作用：弥补不能获取这些数据的格式
                if (column.hasAttr("rowspan")) {
                    try {
                        //System.out.println("周" + week + " 第" + (i + 1) + "节-第" + (i + Integer.parseInt(column.attr("rowspan"))) + "节");   //输出课程
                        splitCourse(column.html(), startYear, endYear, semester, week, i + 1, i + Integer.parseInt(column.attr("rowspan")));
                        //System.out.println(i + "循环完毕！ ");
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return result.toString();
    }


    /**
     * 根据传进来的课程格式转换为对应的实体类并保存
     * @param sub
     * @param startYear
     * @param endYear
     * @param semester
     * @param week
     * @param startSection
     * @param endSection
     * @return
     */
    private Course storeCourseByResult(String sub, int startYear, int endYear, int semester, int week,
                                       int startSection, int endSection) {
        // 周二第1,2节{第4-16周} 		二,1,2,4,16,null
        // {第2-10周|3节/周} 			null,null,null,2,10,3节/周
        // 周二第1,2节{第4-16周|双周} 	二,1,2,4,16,双周
        // 周二第1节{第4-16周} 			二,1,null,4,16,null
        // 周二第1节{第4-16周|双周} 		二,1,null,4,16,双周
        // str格式如上，这里只是简单考虑每个课都只有两节课，实际上有三节和四节，模式就要改动，其他匹配模式请自行修改

        String reg = "周?(.)?第?(\\d{1,2})?,?(\\d{1,2})?节?\\{第(\\d{1,2})-(\\d{1,2})周\\|?((.*周))?\\}";

        String splitPattern = "<br>";
        String[] temp = sub.split(splitPattern);

        //System.out.println("   课程名:   " + temp[0]);
        //System.out.println("  时间:   " + temp[1]);

        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(temp[1]);  //  xpb修改的地方
        //Matcher matcher = pattern.matcher(temp[0]);
        matcher.matches();
        Course course = new Course();
        //课程开始学年
        course.setStartYear(startYear);
        //课程结束学年
        course.setEndYear(endYear);
        //课程学期
        course.setSemester(semester);

        //课程名
        course.setCourseName(temp[0]);
        //System.out.println("   课程名:   " + temp[0]);
        //课程时间，冗余字段
        course.setCourseTime(temp[1]);
        //System.out.println("  时间:   " + temp[1]);
        //教师
        course.setTeacherName(temp[2]);
        //System.out.println("   教师:   " + temp[2]);

        try {
            // 数组可能越界，即没有教室
            course.setClasssroom(temp[3]);
            //System.out.println("   教室:   " + temp[3]);
        } catch (ArrayIndexOutOfBoundsException e) {
            course.setClasssroom("无教室");
        }
        //周几，可能为空，此时使用传进来的值
        if (null != matcher.group(1)) {
            course.setDayOfWeek(getDayOfWeek(matcher.group(1)));
            //System.out.println("   周几:   " + matcher.group(1));
            //System.out.println("   成功打印周几:   ");
        } else {
            course.setDayOfWeek(getDayOfWeek(week + ""));
            //System.out.println("   未成功打印周几:   ");
        }
        //课程开始节数，可能为空，此时使用传进来的值
        if (null != matcher.group(2)) {
            course.setStartSection(Integer.parseInt(matcher.group(2)));
            //System.out.println("   开始时节数:   " + Integer.parseInt(matcher.group(2)));
        } else {
            course.setStartSection(startSection);
        }

        //课程结束时的节数，可能为空，此时使用传进来的值
        if (null != matcher.group(3)) {
            course.setEndSection(Integer.parseInt(matcher.group(3)));
            //System.out.println("   结束时节数:   " + Integer.parseInt(matcher.group(3)));
        } else {
            course.setEndSection(endSection);
        }

        //起始周
        course.setStartWeek(Integer.parseInt(matcher.group(4)));
        //System.out.println("   起始周:   " + Integer.parseInt(matcher.group(4)));
        //结束周
        course.setEndWeek(Integer.parseInt(matcher.group(5)));
        //System.out.println("   结束周:   " + Integer.parseInt(matcher.group(5)));
        //单双周
        String t = matcher.group(6);
        setEveryWeekByChinese(t, course);
        save(course);
        return course;
    }


    /**
     * 提取课程格式，可能包含多节课
     * @param str
     * @param startYear
     * @param endYear
     * @param semester
     * @param week
     * @param startSection
     * @param endSection
     * @return
     */
    private int splitCourse(String str, int startYear, int endYear, int semester, int week, int startSection,
                            int endSection) {
        // String pattern = "<br /><br />";
        String pattern = "<br>";
        String[] split = str.split(pattern);
        if (split.length > 1) {// 如果大于一节课
            for (int i = 0; i < split.length; i++) {
                //System.out.println("   从<td>中提取的:   " + split[i]);
                //  if (!(split[i].startsWith("<br />") && split[i].endsWith("<br />"))) {
                if (!(split[i].startsWith("<br>") && split[i].endsWith("<br>"))) {
                    storeCourseByResult(split[i], startYear, endYear, semester, week, startSection,
                            endSection);// 保存单节课
                } else {
                    // <br />文化地理（网络课程）<br />周日第10节{第17-17周}<br />李宏伟<br />
                    // 以上格式的特殊处理，此种格式在没有教师的情况下产生，即教室留空后<br />依旧存在
                    //int brLength = "<br />".length();
                    int brLength = "<br>".length();
                    String substring = split[i].substring(brLength,
                            split[i].length() - brLength);
                    storeCourseByResult(substring, startYear, endYear, semester, week, startSection,
                            endSection);// 保存单节课
                }
            }
            return split.length;
        } else {
            storeCourseByResult(str, startYear, endYear, semester, week, startSection, endSection);// 保存
            //System.out.println("   从<td>中提取的字符串长度没有大于1:   ");
            return 1;
        }
    }

    public void getCourseInfo(String content) {

        String[] courseName = new String[30];
        int[] courseStartSection = new int[30];    //
        int[] courseEndSection = new int[30];      //
        String[] courseWeek = new String[30];            //课程在周几上课
        String[] courseIsDouble = new String[30];        //课程是否是单双周
        int[] courseStartWeek = new int[30];      //
        int[] courseEndWeek = new int[30];     //
        String[] courseTeacher = new String[30];
        String[] courseClassroom = new String[30];

        int[] courseSameTime = new int[30];  //是否有课在同一时间段

        for (int i = 0; i < courseEndSection.length; i++) {
            courseEndSection[i] = 0;
        }

        int index = 0;
        StringBuilder result = new StringBuilder();
        Document doc = Jsoup.parse(content);

        Elements semesters = doc.select("option[selected=selected]");
        String[] years = semesters.get(0).text().split("-");
        int startYear = Integer.parseInt(years[0]);
        int endYear = Integer.parseInt(years[1]);
        int semester = Integer.parseInt(semesters.get(1).text());
        //System.out.println(semester);
        //System.out.println(startYear + "---------" + endYear);

        Elements elements = doc.select("table#Table1");
        Element element = elements.get(0).child(0);

        int rowNum0 = element.childNodeSize();
        //System.out.println("rowNum0" + rowNum0);   //输出子节点，即有多少节课         12

        //移除一些无用数据

        element.child(0).remove();
        int rowNum1 = element.childNodeSize();
        //System.out.println("rowNum1" + rowNum1);   //输出子节点，即有多少节课         12
        element.child(0).remove();    //源代码
        // element.child(1).remove();     //修改

        //Elements courseName = element.select("td");
        element.child(0).child(0).remove();
        //element.child(4).child(0).remove();
        //element.child(8).child(0).remove();
        int rowNum = element.childNodeSize();
        //System.out.println("rowNum" + rowNum);   //输出子节点，即有多少节课         12
        //  int[][] map = new int[11][7];
        for (int i = 0; i < rowNum - 1; i++) {         //奇怪的问题，明明显示有十二个子节点，但是只打印十一个，而自己数的也是十一个节点，不知道多的一个为何
            Element row = element.child(i);
            int columnNum = row.childNodeSize() - 2;           //这个减二的原因暂时也不清楚，但是减二之后是<td>的个数。不明白子节点的个数为何不等于<td>个数
            //System.out.println(i + "   columnNum:   " + columnNum);        //一周有几天 早上第一节课上课，同理，第二节。。。。

            Elements courseListInfo = row.select("td");
           /* for (Element link : courseInfo) {
                //String linkHref = link.attr("href");
                String linkText = link.get(0).text();
            }*/
            /*  for (int j = 1; j < columnNum; j++) {
                            Element column = row.child(j);
                            //Elements courseName = element.select("td");
                        }*/

            for (int j = 0; j < row.childNodeSize() - 2; j++) {
                int trueTime = 0;
                String courseListText = courseListInfo.get(j).text();
                //System.out.println(j + "--------------" + courseListText);

                String reg = "周?(.)?第?(\\d{1,2})?,?(\\d{1,2})?节?\\{第(\\d{1,2})-(\\d{1,2})周\\|?((.*周))?\\}";       //最多只能匹配连续两节课
                String reg3 = "周?(.)?第?(\\d{1,2})?,?(\\d{1,2})?,?(\\d{1,2})?节?\\{第(\\d{1,2})-(\\d{1,2})周\\|?((.*周))?\\}";   //新增匹配连续三节课的
                String splitPattern = "\\s+";
                String[] temp = courseListText.split(splitPattern);

                if (temp.length == 3) {        //有的课可能没有教师，所以要大于2

                }

                if (temp.length > 2) {        //有的课可能没有教师，所以要大于2

                    int m;
                    //System.out.println("   课程名:   " + temp[0]);
                    //  System.out.println("  时间:   " + temp[1]);
                    if (temp.length == 3) {        //有的课可能没有教师，所以要大于2
                        //System.out.println("  课程信息四项不完整");
                        m = 1;
                    } else {
                        m = ((temp.length) / 4);  //判断同一个时间段之内是否有两门课，如一个1-7周，另一个8-16周。
                        if ((temp.length) % 4 != 0) {   // 判断是否有的课没有教室
                            m++;
                        }
                        //System.out.println("  课程信息四项完整");
                    }
                    for (int k = 0; k < m; k++) {
                        // index++;   //有多少课，加上重复的。即1,2节  ，3,4节 算重复课
                        Pattern pattern = Pattern.compile(reg);
                        Matcher matcher = pattern.matcher(temp[1 + 4 * k]);  //  xpb修改的地方
                        //Matcher matcher = pattern.matcher(temp[0]);
                        boolean twoSection = matcher.matches();

                        //Course course = new Course();
                        //课程开始学年
                        //  course.setStartYear(startYear);
                        //课程结束学年
                        //    course.setEndYear(endYear);
                        //课程学期
                        //   course.setSemester(semester);

                        //课程名
                        //  course.setCourseName(temp[0]);
                        //System.out.println("   课程名:   " + temp[0 + 4 * k]);
                        courseName[index] = temp[0 + 4 * k];

                        //课程时间，冗余字段
                        //  course.setCourseTime(temp[1]);
                        //System.out.println("  时间:   " + temp[1 + 4 * k]);
                        //教师
                        //  course.setTeacher(temp[2]);
                        //System.out.println("   教师:   " + temp[2 + 4 * k]);
                        courseTeacher[index] = temp[2 + 4 * k];

                        try {
                            // 数组可能越界，即没有教室
                            //  course.setClasssroom(temp[3+4*k]);
                            //System.out.println("   教室:   " + temp[3 + 4 * k]);
                            courseClassroom[index] = temp[3 + 4 * k];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            //course.setClasssroom("无教室");
                        }
                        if (twoSection == true) {

                            //周几，可能为空，此时使用传进来的值
                            if (null != matcher.group(1)) {
                                // course.setDayOfWeek(getDayOfWeek(matcher.group(1)));
                                //System.out.println("   周几:   " + matcher.group(1));
                                courseWeek[index] = matcher.group(1);

                                //System.out.println("   成功打印周几:   ");
                            } else {
                                //  course.setDayOfWeek(getDayOfWeek(week+""));
                                //System.out.println("   未成功打印周几:   ");
                            }
                            //课程开始节数，可能为空，此时使用传进来的值
                            if (null != matcher.group(2)) {
                                //course.setStartSection(Integer.parseInt(matcher.group(2)));
                                //System.out.println("   开始时节数:   " + Integer.parseInt(matcher.group(2)));
                                courseStartSection[index] = Integer.parseInt(matcher.group(2));
                            } else {
                                //  course.setStartSection(startSection);
                            }

                            //课程结束时的节数，可能为空，此时使用传进来的值
                            if (null != matcher.group(3)) {
                                // course.setEndSection(Integer.parseInt(matcher.group(3)));
                                //System.out.println("   结束时节数:   " + Integer.parseInt(matcher.group(3)));
                                courseEndSection[index] = Integer.parseInt(matcher.group(3));
                            } else {
                                //  course.setEndSection(endSection);
                            }

                            //起始周
                            // course.setStartWeek(Integer.parseInt(matcher.group(4)));
                            //System.out.println("   起始周:   " + Integer.parseInt(matcher.group(4)));
                            courseStartWeek[index] = Integer.parseInt(matcher.group(4));
                            //结束周
                            //   course.setEndWeek(Integer.parseInt(matcher.group(5)));
                            //System.out.println("   结束周:   " + Integer.parseInt(matcher.group(5)));
                            courseEndWeek[index] = Integer.parseInt(matcher.group(5));
                            //单双周
                            String t = matcher.group(6);
                            //System.out.println("   单双周:   " + t);
                            courseIsDouble[index] = t;
                            // setEveryWeekByChinese(t, course);
                            // save(course);
                        } else {
                            Pattern pattern3 = Pattern.compile(reg3);
                            Matcher matcher3 = pattern3.matcher(temp[1 + 4 * k]);
                            boolean threeSection = matcher3.matches();
                            //System.out.println("   三节课匹配结果:   " + threeSection);
                            //周几，可能为空，此时使用传进来的值
                            if (null != matcher3.group(1)) {
                                // course.setDayOfWeek(getDayOfWeek(matcher.group(1)));
                                //System.out.println("   周几:   " + matcher3.group(1));
                                courseWeek[index] = matcher3.group(1);
                                //System.out.println("   成功打印周几:   ");
                            } else {
                                //  course.setDayOfWeek(getDayOfWeek(week+""));
                                //System.out.println("   未成功打印周几:   ");
                            }
                            //课程开始节数，可能为空，此时使用传进来的值
                            if (null != matcher3.group(2)) {
                                //course.setStartSection(Integer.parseInt(matcher.group(2)));
                                //System.out.println("   开始时节数:   " + Integer.parseInt(matcher3.group(2)));
                                courseStartSection[index] = Integer.parseInt(matcher3.group(2));
                            } else {
                                //  course.setStartSection(startSection);
                            }

                            //课程结束时的节数，可能为空，此时使用传进来的值
                            if (null != matcher3.group(4)) {
                                // course.setEndSection(Integer.parseInt(matcher.group(4)));
                                //System.out.println("   结束时节数:   " + Integer.parseInt(matcher3.group(4)));
                                courseEndSection[index] = Integer.parseInt(matcher3.group(4));
                            } else {
                                //  course.setEndSection(endSection);
                            }

                            //起始周
                            // course.setStartWeek(Integer.parseInt(matcher.group(5)));
                            //System.out.println("   起始周:   " + Integer.parseInt(matcher3.group(5)));
                            courseStartWeek[index] = Integer.parseInt(matcher3.group(5));
                            //结束周
                            //   course.setEndWeek(Integer.parseInt(matcher.group(6)));
                            //System.out.println("   结束周:   " + Integer.parseInt(matcher3.group(6)));
                            courseEndWeek[index] = Integer.parseInt(matcher3.group(6));
                            //单双周
                            String t = matcher3.group(7);
                            //System.out.println("   单双周:   " + t);
                            courseIsDouble[index] = t;
                            // setEveryWeekByChinese(t, course);
                            // save(course);

                        }
                        index++;   //有多少课，加上重复的。即1,2节  ，3,4节 算重复课
                    }
                }


                // Elements courseText = courseListInfo.select("br");
                //   for (int j = 0; j < row.childNodeSize(); j++) {
             /*   String regEx = "(第{1}[1-11]{1}节{1})|&nbsp;";                     //如果不匹配“第n节”或“&nbsp;”,那么就是课程信息，记录下来
                String regEx1 = "第{1}[1-11]{1}节{1}";
                String regEx2 = "&nbsp;";
                Pattern pattern = Pattern.compile(regEx);
                Matcher matcher = pattern.matcher(courseText);
                boolean rs = matcher.matches();
                if(rs==true){
                    trueTime++;
                    System.out.println( "  trueTime:   " + trueTime);
                    break;
                }else{
                    switch(j-trueTime){
                        case  1:
                            courseName[index]=courseText;
                            break;
                        case  2:

                            break;
                        case  3:
                            courseTeacher[index]=courseText;
                            break;
                        case  4:
                            courseClassroom[index]=courseText;
                            break;
                        default:
                            break;
                    }

                    index++;
                }
                System.out.println(rs);
            }*/

            }
        }
/*
找到重复课程，更改课程结束节数
* */
        for (int i = 0; i < courseName.length - 1; i++) {
            for (int j = i + 1; j < courseName.length; j++) {
                //System.out.println(courseName[i] + "------------------" + courseName[j]);
                if (courseName[i] != null) {
                    if (courseName[i].equals(courseName[j])) {
                        //System.out.println(i + "-----------数据重复一次-----------------");

                        if ((courseStartSection[i] > 4 && courseStartSection[j] < 4) || (courseStartSection[i] < 4 && courseStartSection[j] > 4)
                                || !(courseWeek[i].equals(courseWeek[j]))) {   //课程分开不同时间段上课
                            //System.out.println(i + "-----------数据重复一次但是在不同时间段-----------------");
                        } else {
                            courseName[j] = null;       //把重复的课程名后面一个置空，
                            if (courseEndSection[j] == 0) {
                                courseEndSection[i] = courseStartSection[j];
                                //System.out.println(j + "-----------三节课一次-----------------");
                            } else {
                                courseEndSection[i] = courseEndSection[j];
                                //System.out.println(j + "-----------四节课一次-----------------");
                            }
                        }
                    }
                }
            }

        }
        /**
         * 找到同一时间段内是否有多门课，把该门课标记
         */
        for (int i = 0; i < courseName.length - 1; i++) {
            //   for(int j=i+1;j<i+2;j++) {

            int j = i + 1;
            if (courseWeek[i] != null) {
                if (courseStartSection[i] == courseStartSection[j] && courseWeek[i].equals(courseWeek[j])) {
                    courseSameTime[i] = 1;
                    //System.out.println("-----------同时间段有课一次-----------------");
                }
            }
            //    }
        }


        //System.out.println("-----------提交数据之前-----------------");
        //System.out.println(courseName[0] + "-----------第一个数据-----------------");

/**
 * 把数据保存到数据库中
 * 再上传到服务器上
 */
        // Course course=new Course();
        List<Course> coursesList=new ArrayList<>();

        DataSupport.deleteAll(Course.class); //删除之前的数据
        for (int i = 0; i < courseName.length; i++) {
            //System.out.println("-----------提交数据之时-----------------");
            if (courseName[i] != null) {

                Course course = new Course();
                course.setSemester(semester);
                course.setStartYear(startYear);
                course.setEndYear(endYear);
                course.setCourseName(courseName[i]);
                course.setTeacherName(courseTeacher[i]);
                course.setClasssroom(courseClassroom[i]);
                course.setStartSection(courseStartSection[i]);
                course.setEndSection(courseEndSection[i]);
                course.setStartWeek(courseStartWeek[i]);
                course.setEndWeek(courseEndWeek[i]);
                course.setDayOfWeek(getDayOfWeek(courseWeek[i]));
                course.setEveryWeek(setEveryWeekByChinese1(courseIsDouble[i]));
                course.setSameTime(courseSameTime[i]);
                course.save();
                coursesList.add(course);
                if (course.save()) {
                    //System.out.println("-----------存储成功-----------------");
                } else {
                    //System.out.println("-----------存储失败-----------------");
                }
                //System.out.println(i + "次-----------提交数据完成-----------------");
            }

        }
        //System.out.println("-----------提交数据完成-----------------");
        //System.out.println("nnnnnnnnnnnnnnnnnnnnnnn");

        Gson gson = new Gson();
        String coursesJson = gson.toJson(coursesList);
        LogUtils.d("上传课表coursesList",coursesList);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),coursesJson);
        Observable<String> observableUploadCourse= RetrofitFactory.getInstance().uploadCourse(body);
        observableUploadCourse.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(String s) {
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(s);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        int result = 0;
                        try {
                            result = jsonObj.getInt("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //System.out.println("获取的返回值：" + result);
                        switch (result)
                        {
                            case UPLOAD_COURSE_SUCCESS:
                                Log.d("上传课表","result="+result+" 上传成功");
                                break;
                            case COURSE_EXISTED:
                                Log.d("上传课表","result="+result+" 课程已上传");
                                break;
                            case USER_NOTEXIST:
                                Log.d("上传课表","result="+result+" 用户不存在");
                                break;
                            case TOKEN_ERROR:
                                Log.d("上传课表","result="+result+" TokenError");
                                break;
                            default:
                                Log.d("上传课表","result="+result+" 未知错误");
                                break;

                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 去除重复元素
     */
    public String[] deleteRepeat(String[] strings) {
        //可以换种思路，把数组放到set里面（set的值不会重复）就可以去重了

        Set<String> set = new HashSet<String>();
        for (String i : strings)
            set.add(i);
      /*  for(Object j: set.toArray()){
            System.out.print(j + " ");

        }*/
        String[] strArray = new String[30];      //定义长度为6的字符串数组
        String[] toArray = (String[]) set.toArray(strArray);  //将集合转换为字符串数组形式


        return toArray;
    }


    /**
     * 填充map，获取周几，第几节课至第几节课
     * @param childColumn
     * @param map
     * @param i
     * @return 周几
     */
    public static int fillMap(Element childColumn, int map[][], int i) {
        //这个函数的作用自行领悟，总之就是返回周几，也是无意中发现的，于是就这样获取了，作用是双重保障，因为有些课事无法根据正则匹配出周几第几节到第几节
        boolean hasAttr = childColumn.hasAttr("rowspan");
        int week = 0;
        if (hasAttr) {
            for (int t = 0; t < map[0].length; t++) {
                if (map[i][t] == 0) {
                    int r = Integer.parseInt(childColumn.attr("rowspan"));
                    for (int l = 0; l < r; l++) {
                        map[i + l][t] = 1;
                    }
                    week = t + 1;
                    break;
                }
            }

        } else {
            if (childColumn.childNodes().size() > 1) {
                childColumn.attr("rowspan", "1");
            }
            for (int t = 0; t < map[0].length; t++) {
                if (map[i][t] == 0) {
                    map[i][t] = 1;
                    week = t + 1;
                    break;
                }
            }
        }
        return week;
    }

    /**
     * 设置单双周
     * @param week
     * @param course
     */
    public void setEveryWeekByChinese(String week, Course course) {
        // 1代表单周，2代表双周
        if (week != null) {
            if (week.equals("单周"))
                course.setEveryWeek(1);
            else if (week.equals("双周"))
                course.setEveryWeek(2);
        }
        // 默认值为0，代表每周
    }


    public int setEveryWeekByChinese1(String week) {
        // 1代表单周，2代表双周
        if (week != null) {
            if (week.equals("单周")) {
                // course.setEveryWeek(1);
                return 1;
            } else if (week.equals("双周")) {
                //  course.setEveryWeek(2);
                return 2;
            }
        }
        return 0;
        // 默认值为0，代表每周
    }

    /**根据中文数字一，二，三，四，五，六，日，转换为对应的阿拉伯数字
     * @param day
     * @return int
     */
    public int getDayOfWeek(String day) {
        if (day.equals("一"))
            return 1;
        else if (day.equals("二"))
            return 2;
        else if (day.equals("三"))
            return 3;
        else if (day.equals("四"))
            return 4;
        else if (day.equals("五"))
            return 5;
        else if (day.equals("六"))
            return 6;
        else if (day.equals("日"))
            return 7;
        else
            return 0;
    }
}

