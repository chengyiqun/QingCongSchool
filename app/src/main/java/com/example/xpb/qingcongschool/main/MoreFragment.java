package com.example.xpb.qingcongschool.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xpb.qingcongschool.R;
import com.example.xpb.qingcongschool.comment.CommentActivity;
import com.example.xpb.qingcongschool.course.Course;
import com.example.xpb.qingcongschool.course.resource.comment.ResourceCommentActivity;

import org.litepal.crud.DataSupport;

import java.util.UUID;

/**
 * @program: QingCongSchool
 * @description: ${description}
 * @author: 程义群
 * @create: 2018-07-22 22:21
 **/
public class MoreFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.bt_ActivityComment).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CommentActivity.class);
            startActivity(intent);
        });
        view.findViewById(R.id.gotoRC).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ResourceCommentActivity.class);
            intent.putExtra("courseResourceID", UUID.randomUUID().toString().replace("-",""));
            startActivity(intent);
        });
        view.findViewById(R.id.button_clear_course).setOnClickListener(v -> {
            //清除课表
            SharedPreferences sharedPreferencesCourse = getActivity().getSharedPreferences("GetCourse", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor1 = sharedPreferencesCourse.edit();
            editor1.putInt("getCourseState", 0);
            editor1.commit();
            DataSupport.deleteAll(Course.class);

            Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }
    
}
