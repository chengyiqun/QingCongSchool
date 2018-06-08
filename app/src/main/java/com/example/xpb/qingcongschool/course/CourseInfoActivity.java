package com.example.xpb.qingcongschool.course;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.xpb.qingcongschool.R;
import com.example.xpb.qingcongschool.comment.CommentActivity;
import com.example.xpb.qingcongschool.course.resource.download.GetResourceListActivity;
import com.example.xpb.qingcongschool.course.resource.upload.UploadCourseResourceActivity;

import java.util.List;

/**
 * Created by xpb on 2017/3/18.
 */
public class CourseInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mToolbar;
    private Button btComment;
    private Button btUploadDocuments;
    private Button btGetResourceList;
    private TextView tvTime;
    private TextView tvClassroom;
    private TextView tvSection;
    private TextView tvWeek;
    private TextView tvTeacher;
    private TextView tvCourseName;
    private Course course;
    private CourseService courseService;
    private String teachID;

    int courseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_courseinfo);
        Intent intent = getIntent();
        courseID = intent.getIntExtra("courseID", 0);
        System.out.println("courseID:"+courseID);
        inits(courseID);
    }

    public void inits(int courseID) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_courseinfo);
        mToolbar.setTitle("课程信息");
        setSupportActionBar(mToolbar);
        //设置是否有返回箭头
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        courseService = CourseService.getCourseService();
        List<Course> courses = courseService.findAll();//获得数据库中的课程
        course = courses.get(courseID);
        btComment = (Button) findViewById(R.id.button_comment);
        btComment.setOnClickListener(this);
        btUploadDocuments = (Button) findViewById(R.id.button_upload_documents);
        btUploadDocuments.setOnClickListener(this);
        btGetResourceList = (Button) findViewById(R.id.button_get_resource_list);
        btGetResourceList.setOnClickListener(this);
        tvTime = (TextView) findViewById(R.id.time_tv);
        tvClassroom = (TextView) findViewById(R.id.classroom_tv);
        tvSection = (TextView) findViewById(R.id.section_tv);
        tvTeacher = (TextView) findViewById(R.id.teacher_tv);
        tvWeek = (TextView) findViewById(R.id.weeks_tv);
        tvCourseName = (TextView) findViewById(R.id.coursename_tv);
        System.out.println(course.getCourseTime());//没有课程时间这一项
        tvTime.setText(course.getCourseTime());
        tvClassroom.setText(course.getClasssroom());
        tvSection.setText(course.getStartSection() + "--" + course.getEndSection() + "节");
        tvTeacher.setText(course.getTeacherName());
        tvWeek.setText(course.getStartWeek() + "--" + course.getEndWeek() + "周");
        tvCourseName.setText(course.getCourseName());
        System.out.println("----------------教室" + course.getClasssroom() + "----------------");
        teachID=course.getTeachID();
        System.out.println("topicID:"+teachID);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_comment://实现点击评论，调出评论页面
                Intent intent0=new Intent(this, CommentActivity.class);
                intent0.putExtra("teachID", teachID);
                startActivity(intent0);

                break;
            case R.id.button_upload_documents://上传文档
                Intent intent = new Intent(this, UploadCourseResourceActivity.class);
                intent.putExtra("courseName", tvCourseName.getText().toString());
                startActivity(intent);
                break;

            case R.id.button_get_resource_list://获取资源列表
                Intent intent1 = new Intent(this, GetResourceListActivity.class);
                intent1.putExtra("courseName1",tvCourseName.getText().toString());
                startActivity(intent1);
                break;
            default:
                break;

        }
    }


}
