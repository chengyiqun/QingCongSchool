package com.example.xpb.qingcongschool.main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.example.xpb.qingcongschool.LoginActivity;
import com.example.xpb.qingcongschool.R;
import com.example.xpb.qingcongschool.app.MyApplication;
import com.example.xpb.qingcongschool.course.Course;
import com.example.xpb.qingcongschool.course.CourseInfoActivity;
import com.example.xpb.qingcongschool.course.CourseLayout;
import com.example.xpb.qingcongschool.course.CourseService;
import com.example.xpb.qingcongschool.course.CourseView;
import com.example.xpb.qingcongschool.course.GetCourseActivity;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.example.xpb.qingcongschool.course.CourseLayout.divideWidth;
import static com.example.xpb.qingcongschool.course.CourseLayout.sectionNumber;

/**
 * Created by xpb on 2016/7/24.
 */


public class CurriculumFragment extends Fragment implements View.OnClickListener {

    View viewFragment;


    //某节课的背景图,用于随机获取
    private int[] bg = {R.drawable.kb1, R.drawable.kb2, R.drawable.kb3, R.drawable.kb4, R.drawable.kb5, R.drawable.kb6, R.drawable.kb7
    };
    private CourseService courseService;
    private CourseLayout layout;
    private LinearLayout section_Layout;
    private CourseLayout courseLayout;
    private Toolbar toolbar;
    private Spinner spinner;
    private LinearLayout layoutDl;
    private Dialog dialog;
    private Button course_button;
    private Button month_Button;

    private TextView setupTv;

    private ScrollView scrollView;


    private int monthWidth;
    private int sectionHeight;
    public static  SharedPreferences sharedPreferences;

    private int currentWeek;
    private int sameTime = 0;
    private int sameTimeCourseId = 0;
    private String[] weekNum = new String[24];

    // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

    private int getCourseState = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//fragment 有菜单
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.course_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_clear:
                //清除课表
                SharedPreferences sharedPreferencesCourse = MyApplication.getContext().getSharedPreferences("GetCourse", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedPreferencesCourse.edit();
                editor1.putInt("getCourseState", 0);
                editor1.commit();
                DataSupport.deleteAll(Course.class);
                Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                MainActivity.Companion.setFragmentNUM(1);
                startActivity(intent);
                ToastUtils.showShort("课表已清除");
                break;
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        sharedPreferences = this.getActivity().getSharedPreferences("GetCourse", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        getCourseState = sharedPreferences.getInt("getCourseState", 0);
        currentWeek = sharedPreferences.getInt("currentWeek", 1);
        if (getCourseState == 0) {
            viewFragment = inflater.inflate(R.layout.fragment_showcourse_nocourse, container, false);
            course_button = (Button) viewFragment.findViewById(R.id.course_button);
            course_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(MainActivity.Companion.getIslogin()){
                        Intent intent = new Intent(getActivity(), GetCourseActivity.class);
                        getActivity().startActivity(intent);
                    }else {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        getActivity().startActivity(intent);
                    }

                }
            });
        } else {
            //System.out.println("------------------已导入课表-----------------");
            viewFragment = inflater.inflate(R.layout.fragment_showcourse, container, false);
            toolbar = (Toolbar) viewFragment.findViewById(R.id.toolbar);
            toolbar.setTitle("当前课程");
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            scrollView = (ScrollView) viewFragment.findViewById(R.id.scrollview);
            spinner = (Spinner) viewFragment.findViewById(R.id.week_spinner);
            layout = (CourseLayout) viewFragment.findViewById(R.id.courses);

            month_Button=viewFragment.findViewById(R.id.month_Button);
            SimpleDateFormat df = new SimpleDateFormat("M");//设置日期格式
            String month = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
            //System.out.println(month);
            month_Button.setText(month+"月");

            for (int i = 0; i < 24; i++) {
                int num = i + 1;
                weekNum[i] = "第" + num + "周";
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, weekNum);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(currentWeek - 1, true); //设置spinner的默认值
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //System.out.println("------------------" + position + "-----------------");
                    currentWeek = position + 1;
                    editor.putInt("currentWeek", currentWeek);
                    editor.commit();
                    initValue();
                    initView();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            initSectionView(viewFragment);
            LayoutInflater inflaterDl = LayoutInflater.from(getActivity());
            layoutDl = (LinearLayout) inflaterDl.inflate(R.layout.repeat_course_dialog, null);
            dialog = new AlertDialog.Builder(getActivity()).create();
            initValue();
            initView();
            //LogUtils.d("ss","LogUtil");
        }


        return viewFragment;
    }


    /**
     * 需要界面重新展示时调用这个方法
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (!hidden) {

        }
    }


    /**
     * 初始化变量
     */
    private void initValue() {
        courseService = CourseService.getCourseService();
    }

    /**
     * 初始化视图
     */
    private void initView() {


        List<Course> courses = courseService.findAll();//获得数据库中的课程
        Course course = null;
        Course coursePrim = null;


        //循环遍历

        for (int i = 0; i < courses.size(); i++) {
            //System.out.println(i + "      -----------------------------");
            course = courses.get(i);

            CourseView view = new CourseView(getContext());
            view.setCourseId(course.getId());
            view.setStartSection(course.getStartSection());
            view.setEndSection(course.getEndSection());
            view.setWeek(course.getDayOfWeek());
            view.setStartWeek(course.getStartWeek());
            view.setEndWeek(course.getEndWeek());

            view.setTag(i);

            if (((course.getEveryWeek() == 0) && course.getStartWeek() <= currentWeek && currentWeek <= course.getEndWeek())
                    || ((course.getEveryWeek() == 1) && course.getStartWeek() <= currentWeek &&
                    currentWeek <= course.getEndWeek() && (currentWeek % 2 == 1))
                    || ((course.getEveryWeek() == 2) && course.getStartWeek() <= currentWeek &&
                    currentWeek <= course.getEndWeek() && (currentWeek % 2 == 0))
                    ) {  //已考虑单双周
                //------------------该课程在当前周-----------------
                if (course.getSameTime() == 1) {                //  在后面周上课的显示，
                    int tag22 = i + 50;
                    view.setTag(tag22);
                }
                if (i > 0) {
                    coursePrim = courses.get(i - 1);
                    if (coursePrim.getSameTime() == 1) {             //  在前面周上课的显示，
                        int tag22 = i + 100;
                        view.setTag(tag22);

                    }
                }
                int num = (int) (Math.random() * 6);
                int bgRes = bg[num];//随机获取背景色
                view.setBackgroundResource(bgRes);
            } else {
                if (i > 0) {
                    coursePrim = courses.get(i - 1);
                    if (coursePrim.getSameTime() == 1) {           //  在前面周上课的显示，
                        int tag33 = i + 50;
                        view.setTag(tag33);
                    }
                }
                if (course.getSameTime() == 1) {                      //  在后面周上课的没显示，
                    int tag22 = i + 100;
                    view.setTag(tag22);
                }

                view.setBackgroundResource(R.drawable.course_background);
                //------------------该课程不在当前周-----------------
                //   sameTime=0;
                if (course.getSameTime() == 1) {
                    view.setVisibility(View.INVISIBLE);
                    // sameTime=1;
                    sameTimeCourseId = course.getId();
                }
            }

            if (i > 0) {
                coursePrim = courses.get(i - 1);
                if (coursePrim.getStartWeek() <= currentWeek && currentWeek <= coursePrim.getEndWeek() &&
                        (coursePrim.getSameTime() == 1)) {
                    //------------------该课程前一课程在当前周-----------------
                    view.setVisibility(View.INVISIBLE);
                }
            }

            view.setText(course.getCourseName() + "@" + course.getClasssroom());
            view.setTextColor(Color.WHITE);
            view.setTextSize(12);
            view.setGravity(Gravity.CENTER);
            view.setOnClickListener(this);
            layout.addView(view);
        }


    }

    /**
     * 初始化节数视图
     */
    public void initSectionView(View courseFragment) {
        section_Layout = courseFragment.findViewById(R.id.section_ll);
        monthWidth = MyApplication.Companion.getScreenwidth()/15;
        sectionHeight = (MyApplication.Companion.getCourceLayoutHeightPX()-divideWidth * sectionNumber)/sectionNumber;
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                monthWidth,
                sectionHeight
        );


        for (int i = 0; i < sectionNumber; i++) {
            //System.out.println(i + "次------------------创建节数-----------------");
            final TextView textView = new TextView(this.getContext());
            String num = (i + 1) + "";
            textView.setText(num+' ');
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(16);
            int left = dip2px(2);//计算左边的坐标
            int right = 0;     //计算右边坐标
            int top = 0;//计算顶部坐标
            int bottom = dip2px(2);//计算底部坐标
            p.setMargins(left, right, top, bottom);      //距离相邻控件的距离

            textView.setLayoutParams(p);
            section_Layout.addView(textView);
        }
    }

    @Override
    public void onClick(View v) {
        int tag = (int) v.getTag();
        if (tag >= 0 && tag < 50) {
            Intent courseInfoIntent = new Intent(getActivity(), CourseInfoActivity.class);
            courseInfoIntent.putExtra("courseID", tag);
            startActivity(courseInfoIntent);
        }

        if (tag >= 50) {
            int currentCourseID1;
            int nocurrentCourseID1;
            if (tag < 100) {
                currentCourseID1 = Math.abs(tag) - 50;
                nocurrentCourseID1 = Math.abs(tag) + 1 - 50;
            } else {
                currentCourseID1 = Math.abs(tag) - 100;
                nocurrentCourseID1 = Math.abs(tag) - 1 - 100;
            }
            final int currentCourseID = currentCourseID1;
            final int nocurrentCourseID = nocurrentCourseID1;
            courseService = CourseService.getCourseService();
            List<Course> courses = courseService.findAll();//获得数据库中的课程


            Course course = courses.get(currentCourseID);
            Course course1 = courses.get(nocurrentCourseID);


            dialog.show();
            dialog.getWindow().setContentView(layoutDl);
            Button currentCourseButton = (Button) layoutDl.findViewById(R.id.current_course_button);
            Button nocurrentCourseButton = (Button) layoutDl.findViewById(R.id.nocurrent_course_button);
            currentCourseButton.setText(course.getCourseName() + "@" + course.getClasssroom());
            nocurrentCourseButton.setText(course1.getCourseName() + "@" + course1.getClasssroom());
            currentCourseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent courseInfoIntent = new Intent(getActivity(), CourseInfoActivity.class);
                    courseInfoIntent.putExtra("courseID", currentCourseID);
                    startActivity(courseInfoIntent);

                }
            });

            nocurrentCourseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent courseInfoIntent = new Intent(getActivity(), CourseInfoActivity.class);
                    courseInfoIntent.putExtra("courseID", nocurrentCourseID);
                    startActivity(courseInfoIntent);

                }
            });
        }
    }

    public int dip2px(float dip) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }
}
