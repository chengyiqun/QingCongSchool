package com.example.xpb.qingcongschool.course.resource.download;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.example.xpb.qingcongschool.R;
import com.example.xpb.qingcongschool.RetrofitFactory;
import com.example.xpb.qingcongschool.util.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;

public class GetResourceListActivity extends AppCompatActivity {
    public static final String FOUNDING_TIME="2017-10-28 15:30:00";

    public static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final int REQUESTCODE_WRITE_EXTERNAL_STORAGE=4000;

    private String courseName;
    Dialog dialog;
    public Activity context=this;
    private View rootLayout;

    private RecyclerView mRecyclerView;
    private ResourceListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_resource_list);
        rootLayout= findViewById(R.id.root_layout_getResource);
        courseName=getIntent().getStringExtra("courseName1");
        initToolber();
        init();
        if(NetworkUtil.isNetworkAvailable(context))
        {
            getFileListFromServer();
        }else {
            Snackbar.make(rootLayout,"请检查网络",Snackbar.LENGTH_LONG).show();
        }

    }

    private void getFileListFromServer() {
        Map<String,Object> map=new HashMap<>();
        map.put("courseName",courseName);
        map.put("sinceTime",FOUNDING_TIME);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");//设置日期格式
        String datetime = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
        map.put("nowTime", datetime);

        String jsonString=new Gson().toJson(map);
        //System.out.println("jsonString "+jsonString);
        RequestBody body=RequestBody.create(MediaType.parse("application/json; charset=utf-8"),jsonString);
        Observable<String> observable = RetrofitFactory.getInstance().getFileList(body);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        System.out.println("doOnSubscribe");
                        dialog=new Dialog(context);
                        dialog.setTitle("正在获取文件列表。。。");
                        dialog.setCancelable(false);
                        dialog.show();
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        System.out.println("onSubscribe");
                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println("onNext "+s);
                        if(s!=null&&!s.equals("")&&!s.equals("null")){
                            Gson gson=new Gson();
                            //LogUtils.d(s);
                            List<ResourceInfo> list=gson.fromJson(s,new TypeToken<List<ResourceInfo>>(){}.getType());
                            /*for(int i = 0; i < list.size() ; i++)
                            {
                                ResourceInfo p = list.get(i);
                                System.out.println(p.toString());
                            }*/
                            mAdapter.setData(list);
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("onError");
                        dialog.cancel();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        dialog.cancel();
                        EasyPermissions.requestPermissions(context,getString(R.string.permission_write_external_storage),REQUESTCODE_WRITE_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void init() {
        ArrayList<ResourceInfo> list=new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.resource_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        mAdapter = new ResourceListAdapter(list);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initToolber() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_resource_list);
        mToolbar.setTitle(courseName);
        setSupportActionBar(mToolbar);
        //设置是否有返回箭头
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
