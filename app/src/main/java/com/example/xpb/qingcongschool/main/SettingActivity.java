package com.example.xpb.qingcongschool.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.xpb.qingcongschool.R;
import com.example.xpb.qingcongschool.course.Course;
import com.example.xpb.qingcongschool.util.DataCleanManager;

import org.litepal.crud.DataSupport;

/**
 * @program: QingCongSchool
 * @description: ${description}
 * @author: 程义群
 * @create: 2018-07-22 21:43
 **/
public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton settingsback;
    private LinearLayout linearLayout_loginout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_me_activitysetting);
        settingsback = (ImageButton) findViewById(R.id.settings_back);
        settingsback.setOnClickListener(this);
        linearLayout_loginout = (LinearLayout) findViewById(R.id.loginout);
        linearLayout_loginout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_back:
                this.finish();
                break;
            case R.id.loginout:
                //清除登陆状态
                MainActivity.islogin = false;
                MainActivity.phoneNum = "";
                MainActivity.accessToken = "";
                MainActivity.userName = "";
                SharedPreferences myLoginSharedPreferences = getSharedPreferences("myLoginSharedPreferences",
                        Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = myLoginSharedPreferences.edit();
                editor.putString("phoneNum", MainActivity.phoneNum);
                editor.putString("accessToken", MainActivity.accessToken);
                editor.putBoolean("islogin", MainActivity.islogin);
                editor.putString("userName", MainActivity.userName);
                editor.commit();
                //清除课表
                SharedPreferences sharedPreferencesCourse = getSharedPreferences("GetCourse", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedPreferencesCourse.edit();
                editor1.putInt("getCourseState", 0);
                editor1.commit();
                DataSupport.deleteAll(Course.class);

                //清除应用缓存
                DataCleanManager.cleanExternalCache(this);

                Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            default :
                break;
        }
    }
}

