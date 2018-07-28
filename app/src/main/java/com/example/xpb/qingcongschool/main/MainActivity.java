package com.example.xpb.qingcongschool.main;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.xpb.qingcongschool.R;
import com.example.xpb.qingcongschool.util.DataCleanManager;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: QingCongSchool
 * @description: ${description}
 * @author: 程义群
 * @create: 2018-07-22 21:49
 **/
public class MainActivity extends AppCompatActivity {
    public static String accessToken = "";
    public static String phoneNum = "";
    public static boolean islogin = false;//////////////////////默认值为false，true是调试用的
    public static String userName = "";//用户昵称
    public static int fragmentNUM = 2;
    private NoScrollViewPager mViewPager;
    BottomNavigationBar bottomNavigationBar;
    View rootView;
    //双击返回
    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - firstTime > 2000) {
                Snackbar.make(rootView, "再按一次退出程序", Snackbar.LENGTH_LONG).setAction("退出", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        System.exit(0);
                    }
                }).show();
                firstTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logginState();
        mViewPager = findViewById(R.id.m_viewPage);
        initsview();
    }


    private void initsview() {
        rootView = findViewById(R.id.root_layout);
        List<Fragment> list = new ArrayList<>();
        list.add(new SquareFragment());
        list.add(new CurriculumFragment());
        list.add(new UserFragment());
        list.add(new MoreFragment());
        mViewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), list));
        mViewPager.setOffscreenPageLimit(3);

        bottomNavigationBar = findViewById(R.id.bottom_navigation);
        bottomNavigationBar
                .setMode(BottomNavigationBar.MODE_FIXED)
                .addItem(new BottomNavigationItem(R.drawable.bottom_menu_square, "广场"))
                .addItem(new BottomNavigationItem(R.drawable.bottom_menu_curriculum, "课表"))
                .addItem(new BottomNavigationItem(R.drawable.bottom_menu_user, "用户"))
                .addItem(new BottomNavigationItem(R.drawable.bottom_menu_user, "更多"))
                .initialise();

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener(){
            @Override
            public void onTabSelected(int position) {
                switch (position) {
                    case 0:
                        mViewPager.setCurrentItem(0, false);
                        break;
                    case 1:
                        mViewPager.setCurrentItem(1, false);
                        break;
                    case 2:
                        mViewPager.setCurrentItem(2, false);
                        break;
                    case 3:
                        mViewPager.setCurrentItem(3, false);
                        break;
                    default :
                        break;
                }
            }
            @Override
            public void onTabUnselected(int position) {
                // TODO 切换到其他页时，触发，position为之前的页
            }
            @Override
            public void onTabReselected(int position) {
                // TODO 当第二次点击同一个Item时，才会触发
            }
        });
    }


    private void logginState() {
        SharedPreferences myLoginSharedPreferences = getSharedPreferences("myLoginSharedPreferences",
                Activity.MODE_PRIVATE);
        phoneNum = myLoginSharedPreferences.getString("phoneNum", "");
        //islogin = myLoginSharedPreferences.getBoolean("islogin", false);
        islogin = true;
        accessToken = myLoginSharedPreferences.getString("accessToken", "");
        userName = myLoginSharedPreferences.getString("userName", "");
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (islogin) {
            if (fragmentNUM == 2) {
                bottomNavigationBar.selectTab(2);
                //bottom_navigation.selectedItemId = R.id.action_user
                fragmentNUM = 3;
            } else if (fragmentNUM == 1) {
                bottomNavigationBar.selectTab(1);
                //bottom_navigation.selectedItemId = R.id.action_curriculum
                fragmentNUM = 3;
            }
        } else {
            bottomNavigationBar.selectTab(0);
            //bottom_navigation.selectedItemId = R.id.action_square
        }
    }

    @Override
    public void finish() {
        DataCleanManager.cleanExternalCacheExceptAvatar(this);
        super.finish();
    }
}
