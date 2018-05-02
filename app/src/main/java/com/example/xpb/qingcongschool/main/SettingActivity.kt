package com.example.xpb.qingcongschool.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout

import com.example.xpb.qingcongschool.util.DataCleanManager
import com.example.xpb.qingcongschool.R
import com.example.xpb.qingcongschool.course.Course
import org.litepal.crud.DataSupport

class SettingActivity : AppCompatActivity(), View.OnClickListener {

    private var settingsback: ImageButton? = null
    private var linearLayout_loginout: LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_me_activitysetting)
        settingsback = findViewById<View>(R.id.settings_back) as ImageButton
        settingsback!!.setOnClickListener(this)
        linearLayout_loginout = findViewById<View>(R.id.loginout) as LinearLayout
        linearLayout_loginout!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.settings_back -> this.finish()
            R.id.loginout -> {
                //清除登陆状态
                MainActivity.islogin = false
                MainActivity.phoneNum = ""
                MainActivity.accessToken = ""
                MainActivity.userName = ""
                val myLoginSharedPreferences = getSharedPreferences("myLoginSharedPreferences",
                        Activity.MODE_PRIVATE)
                val editor = myLoginSharedPreferences.edit()

                editor.putString("phoneNum", MainActivity.phoneNum)
                editor.putString("accessToken", MainActivity.accessToken)
                editor.putBoolean("islogin", MainActivity.islogin)
                editor.putString("userName", MainActivity.userName)
                editor.commit()

                //清除课表
                val sharedPreferencesCourse = getSharedPreferences("GetCourse", Context.MODE_PRIVATE)
                val editor1 = sharedPreferencesCourse.edit()
                editor1.putInt("getCourseState", 0)
                editor1.commit()
                DataSupport.deleteAll(Course::class.java)

                //清除应用缓存
                DataCleanManager.cleanExternalCache(this)

                val intent = packageManager.getLaunchIntentForPackage(packageName)
                intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
    }

}
