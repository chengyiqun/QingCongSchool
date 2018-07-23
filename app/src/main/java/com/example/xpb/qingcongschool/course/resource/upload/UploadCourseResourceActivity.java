package com.example.xpb.qingcongschool.course.resource.upload;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.example.xpb.qingcongschool.R;
import com.example.xpb.qingcongschool.RetrofitFactory;
import com.example.xpb.qingcongschool.main.BaseActivity;
import com.google.gson.Gson;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Response;

public class UploadCourseResourceActivity extends BaseActivity implements View.OnClickListener,EasyPermissions.PermissionCallbacks{
    public static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final int REQUESTCODE_WRITE_EXTERNAL_STORAGE=4000;

    public  static final int UPLOAD_COURSE_RESOURCE_SUCCESS=3301;
    public  static final int COURSE_NOT_EXIST=3223;
    public  static final int USER_NOT_EXIST=3003;
    public  static final int TOKEN_ERROR=3004;

    public static final int REQUESTCODE_FROM_ACTIVITY=2000;

    private Context context=this;

    private Spinner spinner;
    private EditText etCourseName;
    private EditText etResourceType;
    private TextView tvFilePath;
    private Button btFilePicker;
    private Button btUploadFIle;
    private boolean fileSelected=false;

    Call<String> callUploadFile;//上传调用

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_course_resource);
        init();
        etCourseName.setText(getIntent().getStringExtra("courseName"));

        dialog= new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setIcon(R.drawable.ic_launcher_24dp);// 设置提示的title的图标，默认是没有的
        dialog.setTitle("上传文件");
        dialog.setMax(100);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callUploadFile.cancel();
                    }
                });
    }

    private void init() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_upload_resource);
        setSupportActionBar(mToolbar);
        //设置是否有返回箭头
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        spinner= (Spinner) findViewById(R.id.spinner_resourceDescription);
        etCourseName= (EditText) findViewById(R.id.editText_courseName);
        etResourceType= (EditText) findViewById(R.id.editText_ResourceType);
        tvFilePath= (TextView) findViewById(R.id.textView_uploadFilePath);
        btFilePicker= (Button) findViewById(R.id.button_filePicker);
        btFilePicker.setOnClickListener(this);
        btUploadFIle= (Button) findViewById(R.id.button_uploadFile);
        btUploadFIle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button_filePicker:
                //调出上传文件页面
                //https://github.com/leonHua/LFilePicker
                Log.v("filepicker","BT_OnClick");
                selectFile();
                break;
            case R.id.button_uploadFile:
                if(fileSelected)
                    uploadFileToServer();
                break;
        }
    }

    void selectFile() {
        if(EasyPermissions.hasPermissions(this,WRITE_EXTERNAL_STORAGE)){
            new LFilePicker()
                    .withActivity(this)
                    .withMutilyMode(false)
                    .withRequestCode(REQUESTCODE_FROM_ACTIVITY)
                    .withTitle("文件选择")
                    .withBackgroundColor("#00c853")
                    .withIconStyle(Constant.ICON_STYLE_GREEN)
                    .withTitleColor("#FFFFFF")
                    .withBackIcon(Constant.BACKICON_STYLETHREE)
                    .start();
        }else {
            EasyPermissions.requestPermissions(this,getString(R.string.permission_write_external_storage),REQUESTCODE_WRITE_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    //成功
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        if(requestCode==REQUESTCODE_WRITE_EXTERNAL_STORAGE){
            selectFile();
        }
    }

    //失败
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        ToastUtils.showLong("未授予读写存储权限\n可能会在部分机型无法展示文件\n");
    }



    private void uploadFileToServer() {
        System.out.println(spinner.getSelectedItem().toString());
        System.out.println(etCourseName.getText().toString());
        System.out.println(etResourceType.getText().toString());
        System.out.println(tvFilePath.getText().toString());
        HashMap<String,Object> fileInfoMap=new HashMap<>();
        fileInfoMap.put("resourceDescribe",spinner.getSelectedItem().toString());
        fileInfoMap.put("courseName",etCourseName.getText().toString());
        fileInfoMap.put("resourceType",etResourceType.getText().toString());

        String fileDescription=new Gson().toJson(fileInfoMap);
        RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, fileDescription);
        File file=new File(tvFilePath.getText().toString());
        RequestBody resquestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        //通过该行代码将RequestBody转换成特定的FileRequestBody
        FileRequestBody<String> frbody = new FileRequestBody<>(resquestBody, callback);
        MultipartBody.Part mbody = MultipartBody.Part.createFormData("documents",file.getName(),frbody);
        callUploadFile =RetrofitFactory.getInstance().upload(description,mbody);
        dialog.show();
        callUploadFile.enqueue(callback);

    }

    @Override
    //filePicker的返回
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //文件选择器LFilePicker返回
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_FROM_ACTIVITY) {
                List<String> list = data.getStringArrayListExtra("paths");
                //Toast.makeText(this, "选中了" + list.size() + "个文件", Toast.LENGTH_SHORT).show();
                for(String s:list){
                    tvFilePath.setText(s);
                    fileSelected=true;
                }
            }
        }
    }

    RetrofitCallback< String > callback = new RetrofitCallback<String>() {
        @Override
        public void onSuccess(Call< String > call, Response< String > response) {
            //runOnUIThread(this, response.body().toString());
            //进度更新结束
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(response.body());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int result = 0;
            try {
                result = jsonObj.getInt("result");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            switch (result)
            {
                case UPLOAD_COURSE_RESOURCE_SUCCESS:
                    Log.d("上传文件" ,"UPLOAD_COURSE_RESOURCE_SUCCESS");
                    dialog.cancel();
                    Toast.makeText(context,"上传成功",Toast.LENGTH_SHORT).show();
                    fileSelected=false;
                    break;
                case COURSE_NOT_EXIST:
                    Log.d("上传文件","COURSE_NOT_EXIST");
                    Toast.makeText(context,"课程不存在",Toast.LENGTH_SHORT).show();
                    break;
                case USER_NOT_EXIST:
                    Log.d("上传文件","USER_NOT_EXIST");
                    Toast.makeText(context,"用户不存在",Toast.LENGTH_SHORT).show();
                    break;
                case TOKEN_ERROR:
                    Log.d("上传文件","TOKEN_ERROR");
                    Toast.makeText(context,"请重新登陆",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Log.d("上传文件","未知错误 "+result);
                    Toast.makeText(context,"未知错误 "+result,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        @Override
        public void onFailure(Call< String > call, Throwable t) {
            //runOnUIThread(this, t.getMessage());
            //进度更新结束
            System.out.println("上传文件异常");
            Toast.makeText(getApplicationContext(),"上传失败",Toast.LENGTH_LONG).show();
            t.printStackTrace();
        }
        @Override
        public void onLoading(long total, long progress) {
            dialog.setProgress((int) (progress*100/total));
            //Log.w("onLoading",progress+"/"+total);
        }
    };
}
