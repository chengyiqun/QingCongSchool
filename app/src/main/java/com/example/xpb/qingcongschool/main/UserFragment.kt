package com.example.xpb.qingcongschool.main
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.blankj.utilcode.util.ToastUtils

import com.example.xpb.qingcongschool.LoginActivity
import com.example.xpb.qingcongschool.R
import com.example.xpb.qingcongschool.RetrofitFactory
import com.example.xpb.qingcongschool.app.MyApplication
import com.example.xpb.qingcongschool.util.CropUtils
import com.example.xpb.qingcongschool.util.DialogPermission
import com.example.xpb.qingcongschool.util.FileUtil
import com.example.xpb.qingcongschool.util.PermissionUtil
import com.example.xpb.qingcongschool.util.SharedPreferenceMark
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.generic.RoundingParams
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import org.json.JSONException
import org.json.JSONObject
import java.io.File
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import kotlinx.android.synthetic.main.fragment_me_after_entry.*
import kotlinx.android.synthetic.main.fragment_me_before_entry.*
/**
 * Created by xpb on 2016/7/24.
 */
class UserFragment : Fragment(), View.OnClickListener {
    private var uri: Uri? = null
    private var uriPhoto: Uri? = null
    private var file: File? = null//头像
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        if (MainActivity.islogin) {
            return inflater!!.inflate(R.layout.fragment_me_after_entry, container, false)
        } else {
            return inflater!!.inflate(R.layout.fragment_me_before_entry, container, false)
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (MainActivity.islogin) {
            init1()
        } else {
            login_textview.setOnClickListener(this)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun init1() {
        file = File(FileUtil.getCachePath(activity), "user-avatar.jpg")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file)
        } else {
            //通过FileProvider创建一个content类型的Uri(android 7.0需要这样的方法跨应用访问)
            uri = FileProvider.getUriForFile(activity, MyApplication.packagename, file)
        }
        (activity as AppCompatActivity).setSupportActionBar(toolbar_islogin)
        ib_settings.setOnClickListener(this)
        ib_logininfo.setOnClickListener(this)
        tv_name1.text = "昵称：" + MainActivity.userName
        tv_num.text = "手机号：" + MainActivity.phoneNum
        if (file!!.length() > 1024) {//文件大小大于1kb，证明图片存在的。
            println(file!!.length())
            uploadAvatarFromPhoto()
        }
        simpleDraweeView_user_avatar.setOnClickListener {
            val items = arrayOf("拍照", "来自相册")
            val listDialog = AlertDialog.Builder(activity)
            listDialog.setTitle("设置头像")
            listDialog.setItems(items) { _, which ->
                when (which) {
                    0//拍照
                    -> {
                        //Log.e("TAG", "单击拍照")
                        if (PermissionUtil.hasCameraPermission(activity)) {
                            uploadAvatarFromPhotoRequest()
                        }
                    }
                    1//来自相册
                    -> {
                        //Log.d("TAG", "单击选取照片")
                        uploadAvatarFromAlbumRequest()
                    }
                    else -> {
                    }
                }
            }
            listDialog.show()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ib_settings -> {
                val intent = Intent(activity, SettingActivity::class.java)
                startActivity(intent)
            }
            R.id.login_textview -> {
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }


    /**
     * photo
     */
    private fun uploadAvatarFromPhotoRequest() {
        val file = File(FileUtil.getCachePath(activity), "photo.jpg")
        if (file.exists()) {
            file.delete()
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uriPhoto = Uri.fromFile(file)
        } else {
            //通过FileProvider创建一个content类型的Uri(android 7.0需要这样的方法跨应用访问)
            uriPhoto = FileProvider.getUriForFile(activity, MyApplication.packagename, file)
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPhoto)
        startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO)
    }

    /**
     * album
     */
    private fun uploadAvatarFromAlbumRequest() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, REQUEST_CODE_ALBUM)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != -1) {
            return
        }
        if (requestCode == REQUEST_CODE_ALBUM && data != null) {
            val newUri: Uri?
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                newUri = Uri.parse("file:///" + CropUtils.getPath(activity, data.data)!!)
            } else {
                newUri = data.data
            }
            if (newUri != null) {
                if (file!!.exists()) {
                    file!!.delete()
                }
                startPhotoZoom(newUri)
            } else {
                Toast.makeText(activity, "没有得到相册图片", Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
            if (file!!.exists()) {
                file!!.delete()
            }
            startPhotoZoom(uriPhoto)
        } else if (requestCode == REQUEST_CODE_CROUP_PHOTO) {
            uploadAvatarFromPhoto()
            uploadAvatarToServer()
        }
    }

    //上传图片到服务器的代码
    private fun uploadAvatarToServer() {
        println("上传图片")
        val requestbody = RequestBody.create(MediaType.parse("image/jpg"), file!!)
        val body = MultipartBody.Part.createFormData("userAvatar", file!!.name, requestbody)
        val observableUploadAvator = RetrofitFactory.getInstance().updateAvatar1(body)
        observableUploadAvator.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<String> {
                    override fun onSubscribe(disposable: Disposable) {

                    }

                    override fun onNext(s: String) {
                        println("上传头像返回 " + s)
                        var jsonObj: JSONObject? = null
                        try {
                            jsonObj = JSONObject(s)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        var result = 0
                        try {
                            result = jsonObj!!.getInt("result")
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        println("获取的返回值：" + result)
                        when (result) {
                            UPDATE_AVATAR_SUCCESS -> Toast.makeText(activity, "上传成功", Toast.LENGTH_SHORT).show()
                            TOKEN_ERROR -> Toast.makeText(activity, "tokenError", Toast.LENGTH_SHORT).show()
                            else -> Toast.makeText(activity, "未知错误 " + result, Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onError(throwable: Throwable) {
                        println("上传出错")
                        ToastUtils.showShort("上传失败")
                        throwable.printStackTrace()
                    }

                    override fun onComplete() {

                    }
                })
    }

    private fun uploadAvatarFromPhoto() {
        compressAndUploadAvatar(file!!.path)

    }

    private fun compressAndUploadAvatar(fileSrc: String) {
        val cover = FileUtil.getSmallBitmap(activity, fileSrc)
        //Fresco设置圆形头像
        val builder = GenericDraweeHierarchyBuilder(resources)
        val hierarchy = builder
                .setDesiredAspectRatio(1.0f)
                .setFailureImage(R.drawable.ic_launcher_24dp)
                .setRoundingParams(RoundingParams.fromCornersRadius(100f))
                .build()

        //加载本地图片
        val uri = Uri.fromFile(cover)
        val controller = Fresco.newDraweeControllerBuilder()
                .setOldController(simpleDraweeView_user_avatar.controller)
                .setUri(uri)
                .build()
        simpleDraweeView_user_avatar.hierarchy = hierarchy
        simpleDraweeView_user_avatar.controller = controller

    }


    /**
     * 裁剪拍照裁剪
     *
     * @param uri
     */
    fun startPhotoZoom(uri: Uri?) {
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(uri, "image/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra("crop", "true")// crop=true 有这句才能出来最后的裁剪页面.
        intent.putExtra("aspectX", 1)// 这两项为裁剪框的比例.
        intent.putExtra("aspectY", 1)// x:y=1:1
        intent.putExtra("outputX", 200)//图片输出大小
        intent.putExtra("outputY", 200)
        intent.putExtra("output", Uri.fromFile(file))
        intent.putExtra("outputFormat", "JPEG")// 返回格式
        startActivityForResult(intent, REQUEST_CODE_CROUP_PHOTO)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {

            PermissionUtil.REQUEST_SHOWCAMERA -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                uploadAvatarFromPhotoRequest()

            } else {
                if (!SharedPreferenceMark.getHasShowCamera()) {
                    SharedPreferenceMark.setHasShowCamera(true)
                    DialogPermission(activity, "关闭摄像头权限影响扫描功能")

                } else {
                    Toast.makeText(activity, "未获取摄像头权限", Toast.LENGTH_SHORT)
                            .show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    companion object {
        //startActivityForResult
        private val REQUEST_CODE_TAKE_PHOTO = 1
        private val REQUEST_CODE_ALBUM = 2
        private val REQUEST_CODE_CROUP_PHOTO = 3

        //上传头像返回值
        val TOKEN_ERROR = 3004
        val UPDATE_AVATAR_SUCCESS = 3005
    }
}
