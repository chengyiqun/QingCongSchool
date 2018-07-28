package com.example.xpb.qingcongschool.topic;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.xpb.qingcongschool.R;

import me.relex.photodraweeview.PhotoDraweeView;

public class ImageViewActivity extends Activity {
    private PhotoDraweeView mPhotoDraweeView;
    private String img_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        // FIXME
        //mPhotoDraweeView = (PhotoDraweeView) findViewById(R.id.photoView);
    }

    private void initData() {
        img_url = getIntent().getStringExtra("img_url");
        System.out.println(img_url);
        if (!TextUtils.isEmpty(img_url)) {//FIXME 未验证
            /*GlideApp.with(this)
                    .load(img_url)
                    .into(mPhotoDraweeView);*/

        } else {
            Toast.makeText(this, "图片获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void initEvent() {
        //添加点击事件 FIXME
       /* mPhotoDraweeView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finish();
            }
        });*/
    }
}

