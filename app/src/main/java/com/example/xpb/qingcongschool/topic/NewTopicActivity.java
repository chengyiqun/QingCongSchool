package com.example.xpb.qingcongschool.topic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ToastUtils;
import com.example.xpb.qingcongschool.R;
import com.example.xpb.qingcongschool.RetrofitFactory;
import com.example.xpb.qingcongschool.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class NewTopicActivity extends AppCompatActivity  {
    private TextWatcher editorDetailWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            int detailLength = s.length();
            idEditorDetailFontCount.setText(detailLength+"/140");
            if (detailLength == 141) {
                ToastUtils.showShort("字数达到上限了");
                s.delete(140, 141);
                idEditorDetailFontCount.setText("140/140");
            }
        }
    };
    public static final int INSERT_TOPIC_SUCCESS = 3411;
    int result=0;

    EditText idEditorDetail;

    TextView idEditorDetailFontCount;

    private PhotoAdapter photoAdapter;
    private ArrayList<String> selectedPhotos = new ArrayList<>();
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);
        idEditorDetail =  findViewById(R.id.id_editor_detail);
        idEditorDetail.addTextChangedListener(editorDetailWatcher);
        idEditorDetailFontCount = (TextView) findViewById(R.id.id_editor_detail_font_count);
        // 必须在setContentView()之后绑定

        setToolbar();
        initPhotoAdapter();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_topic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send_topic:
                String content= idEditorDetail.getText().toString();
                if(content.length()<5){
                    ToastUtils.showShort("动态不少于5字");
                }else {
                    System.out.println(selectedPhotos);
                    System.out.println(content);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("subjectID", "");
                    hashMap.put("dontMaskStranger", 0);
                    hashMap.put("content", content);
                    hashMap.put("topicPlace","安徽大学罄苑校区");
                    List<HashMap<String, Object>> lableList = new LinkedList<>();
                    for (int i=0 ;i<2;i++){
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("lable","lable"+i);
                        lableList.add(map);
                    }
                    JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(lableList));
                    hashMap.put("lableList",jsonArray);
                    JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(hashMap));
                    System.out.println(jsonObject);
                    uploadTopic(jsonObject.toString());
                }

                break;
            default:
                break;
        }
        return true;
    }

    private void uploadTopic(String s) {

        Map<String , RequestBody> photos = new HashMap<>();
        RequestBody topicRequestBody = RequestBody.create(MultipartBody.FORM, s);
        photos.put("jsonStringInsertTopic", topicRequestBody);//话题json字符串

        for(int i = 0;i<selectedPhotos.size();i++){
            File file = new File(selectedPhotos.get(i));
            File file1= FileUtil.getSmallBitmap2(this,file.getPath());
            String fileName = file1.getName();
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            RequestBody photoRequestBody = RequestBody.create(MediaType.parse("image/"+suffix), file1);
            //photos.put("file\"; filename=\""+file.getName()+"", photoRequestBody);//这一步是关键，拼接字符串
            photos.put("file\"; filename=\""+i+"."+suffix, photoRequestBody);//这一步是关键，拼接字符串
        }
        Observable<String> observable = RetrofitFactory.getInstance().testUpload(photos);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) { }
                    @Override
                    public void onNext(String value) {
                        System.out.println(value);
                        JSONObject jsonObject = JSONObject.parseObject(value);
                        result = jsonObject.getIntValue("result");
                        System.out.println(result);
                        if (result == INSERT_TOPIC_SUCCESS) {
                            ToastUtils.showShort("发布成功");
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onComplete() {
                        System.out.println("完毕");
                        if (result == INSERT_TOPIC_SUCCESS) {
                            finish();
                        }
                    }
                });
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar_new_topic);
        setSupportActionBar(toolbar);
        //设置是否有返回箭头
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //初始化照片选择器
    private void initPhotoAdapter() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        photoAdapter = new PhotoAdapter(this, selectedPhotos);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(photoAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                (view, position) -> {
                    if (photoAdapter.getItemViewType(position) == PhotoAdapter.TYPE_ADD) {
                        PhotoPicker.builder()
                                .setPhotoCount(PhotoAdapter.MAX_PICTURE_COUNT)
                                .setShowCamera(true)
                                .setPreviewEnabled(false)
                                .setShowGif(true)
                                .setSelected(selectedPhotos)
                                .start(NewTopicActivity.this);
                    } else {
                        PhotoPreview.builder()
                                .setPhotos(selectedPhotos)
                                .setCurrentItem(position)
                                .start(NewTopicActivity.this);
                    }
                }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {

            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            selectedPhotos.clear();

            if (photos != null) {

                selectedPhotos.addAll(photos);
            }
            photoAdapter.notifyDataSetChanged();
        }
    }

}
