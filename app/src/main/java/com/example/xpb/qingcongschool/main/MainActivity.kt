package com.example.xpb.qingcongschool.main

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.ashokvarma.bottomnavigation.BottomNavigationItem
import com.example.xpb.qingcongschool.util.DataCleanManager
import com.example.xpb.qingcongschool.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var mViewPager: NoScrollViewPager? = null
    //双击返回
    //记录用户首次点击返回键的时间
    private var firstTime: Long = 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - firstTime > 2000) {
                Snackbar.make(root_layout, "再按一次退出程序", Snackbar.LENGTH_LONG).setAction("退出") {
                    finish()
                    System.exit(0)
                }.show()
                firstTime = System.currentTimeMillis()
            } else {
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logginState()
        mViewPager = findViewById(R.id.m_viewPage)
        initsview()
    }

    private fun initsview() {
        val list = ArrayList<Fragment>()
        list.add(SquareFragment())
        list.add(CurriculumFragment())
        list.add(UserFragment())
        list.add(MoreFragment())
        mViewPager?.adapter = MyFragmentAdapter(supportFragmentManager, list)
        mViewPager?.offscreenPageLimit = 3

        bottom_navigation
                .setMode(BottomNavigationBar.MODE_FIXED)
                .addItem(BottomNavigationItem(R.drawable.bottom_menu_square, "广场"))
                .addItem(BottomNavigationItem(R.drawable.bottom_menu_curriculum, "课表"))
                .addItem(BottomNavigationItem(R.drawable.bottom_menu_user, "用户"))
                .addItem(BottomNavigationItem(R.drawable.bottom_menu_user, "更多"))
                .initialise()

        bottom_navigation.setTabSelectedListener(object : BottomNavigationBar.OnTabSelectedListener {
            override fun onTabReselected(position: Int) {
            }

            override fun onTabUnselected(position: Int) {
            }

            override fun onTabSelected(position: Int) {
                when (position) {
                    0 -> mViewPager?.setCurrentItem(0, false)
                    1 -> mViewPager?.setCurrentItem(1, false)
                    2 -> mViewPager?.setCurrentItem(2, false)
                    3 -> mViewPager?.setCurrentItem(3, false)
                }
            }

        })
    }


    private fun logginState() {
        val myLoginSharedPreferences = getSharedPreferences("myLoginSharedPreferences",
                Activity.MODE_PRIVATE)
        phoneNum = myLoginSharedPreferences.getString("phoneNum", "")
        islogin = myLoginSharedPreferences.getBoolean("islogin", false)
        accessToken = myLoginSharedPreferences.getString("accessToken", "")
        userName = myLoginSharedPreferences.getString("userName", "")
    }


    override fun onResume() {
        super.onResume()
        if (islogin) {
            if (fragmentNUM == 2) {
                bottom_navigation.selectTab(2)
                //bottom_navigation.selectedItemId = R.id.action_user
                fragmentNUM = 3
            } else if (fragmentNUM == 1) {
                bottom_navigation.selectTab(1)
                //bottom_navigation.selectedItemId = R.id.action_curriculum
                fragmentNUM = 3
            }
        } else {
            bottom_navigation.selectTab(0)
            //bottom_navigation.selectedItemId = R.id.action_square
        }

    }

    override fun finish() {
        //清除缓存啦
        DataCleanManager.cleanExternalCacheExceptAvatar(this)
        super.finish()
    }

    companion object {
        var accessToken: String? = ""
        var phoneNum: String? = ""
        var islogin = false//////////////////////默认值为false，true是调试用的
        var userName: String? = ""//用户昵称
        var fragmentNUM = 2
    }
}