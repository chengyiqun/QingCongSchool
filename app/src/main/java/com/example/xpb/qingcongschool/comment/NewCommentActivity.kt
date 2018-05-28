package com.example.xpb.qingcongschool.comment

import android.app.Dialog
import android.content.Intent
import android.support.design.widget.Snackbar
import android.os.Bundle
import android.view.Menu
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.ToastUtils
import com.example.xpb.qingcongschool.R
import com.example.xpb.qingcongschool.RetrofitFactory
import com.example.xpb.qingcongschool.main.BaseActivity
import com.example.xpb.qingcongschool.util.NetworkUtil
import com.example.xpb.qingcongschool.util.Utils
import com.google.gson.Gson
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_new_comment.*
import okhttp3.RequestBody

class NewCommentActivity : BaseActivity() {
    private var teachID: String? = null
    private var dialog: Dialog? = null
    private var result:Int? =0
    companion object {
        const val INSERT_COMMENT_SUCCESS = 3221
        const val TEACH_NOTEXIST = 3222
        const val COURSE_NOTEXIST = 3223
        const val TEACHER_NOTEXIST = 3224
        const val STUDENTCOURSE_NOTEXIST = 3225
        const val TOKEN_ERROR = 3004
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_comment)
        teachID = intent.getStringExtra("topicID")
        println(teachID)
        val intent = Intent()
        setResult(0,intent)
        init()
    }

    private fun init() {
        toolbar_new_comment.title = "发布新评论"
        setSupportActionBar(toolbar_new_comment)
        //设置是否有返回箭头
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_new_comment.setNavigationOnClickListener { finish() }
        toolbar_new_comment.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_send -> {
                    if(comment_editText.text.toString().length<2){
                        showSnackBar("评论不可少于2字")
                    }else{
                        val commentInfoMap = HashMap<String,Any>()
                        commentInfoMap.put("topicID",teachID!!)
                        commentInfoMap.put("commentType",0)
                        commentInfoMap.put("score", (ratingBar_teachRating.rating).toInt())
                        commentInfoMap.put("content",comment_editText.text.toString())
                        commentInfoMap.put("isAnonymous",0)
                        val commentInfo:String = Gson().toJson(commentInfoMap)
                        println(commentInfo)
                        insertTeachComment(commentInfo)
                    }
                }
            }
            true
        }
    }

    private fun insertTeachComment(commentInfo: String) {
        if(NetworkUtil.isNetworkAvailable(this@NewCommentActivity)) {
            dialog = Dialog(this@NewCommentActivity)
            dialog!!.setTitle("正在刷新，请稍后...")
            dialog!!.setCancelable(true)
            val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), commentInfo)
            val observableInsertTeachComment = RetrofitFactory.getInstance().insertTeachComment(body)
            observableInsertTeachComment.subscribeOn(Schedulers.io())
                    .doOnSubscribe({_->
                        Utils.println("doOnScribe,showDialog")
                        if (dialog != null && !dialog!!.isShowing) {
                            dialog!!.show()
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object :Observer<String>{
                        override fun onComplete() {
                            dialog!!.cancel()
                            if(result== INSERT_COMMENT_SUCCESS){
                                val intent = Intent()
                                setResult(INSERT_COMMENT_SUCCESS,intent)
                                finish()
                            }
                        }
                        override fun onSubscribe(d: Disposable?) {}
                        override fun onNext(s: String?) {
                            val jsonObject = JSONObject.parseObject(s)
                            result = jsonObject.getInteger("result")
                            when(result){
                                INSERT_COMMENT_SUCCESS->{
                                    ToastUtils.showShort("发送成功")
                                    result= INSERT_COMMENT_SUCCESS
                                }
                                TEACH_NOTEXIST->{ToastUtils.showShort("教学活动不存在")}
                                COURSE_NOTEXIST->{ToastUtils.showShort("课程不存在")}
                                TEACHER_NOTEXIST->{ToastUtils.showShort("教师不存在")}
                                STUDENTCOURSE_NOTEXIST->{ToastUtils.showShort("选课信息不在数据库")}
                                TOKEN_ERROR->{ToastUtils.showShort("请重新登陆")}
                            }
                        }
                        override fun onError(e: Throwable?) {
                            dialog!!.cancel()
                            ToastUtils.showLong("服务器通信异常")
                            e?.printStackTrace()
                        }

                    })
        }else{
            showSnackBar("网络未连接")
        }
    }

    fun showSnackBar(text:String) {
        Snackbar.make(root_view, text, Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_send, menu)
        return true
    }

}
