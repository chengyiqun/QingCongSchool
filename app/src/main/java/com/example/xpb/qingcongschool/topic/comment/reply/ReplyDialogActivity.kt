package com.example.xpb.qingcongschool.topic.comment.reply

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.xpb.qingcongschool.R
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.support.design.widget.Snackbar
import android.view.*
import android.view.inputmethod.InputMethodManager
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.ToastUtils
import com.example.xpb.qingcongschool.RetrofitFactory
import com.example.xpb.qingcongschool.util.NetworkUtil
import com.example.xpb.qingcongschool.util.Utils
import com.google.gson.Gson
import io.github.rockerhieu.emojicon.EmojiconGridFragment
import io.github.rockerhieu.emojicon.EmojiconsFragment
import io.github.rockerhieu.emojicon.emoji.Emojicon
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_reply.*
import okhttp3.RequestBody


class ReplyDialogActivity : AppCompatActivity() , EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener,View.OnClickListener{

    private var teachID: String? = null
    private var objectID:String ? = null
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

    @Override
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.imageButton_send->{
                println("发送")
                if(emg_editText.text.toString().length<2){
                    showSnackBar("不可少于2字")
                }else{
                    val commentInfoMap = HashMap<String,Any>()
                    commentInfoMap.put("topicID",teachID!!)
                    commentInfoMap.put("commentType",1)
                    commentInfoMap.put("objectID", objectID!!)
                    commentInfoMap.put("content",emg_editText.text.toString())
                    commentInfoMap.put("isAnonymous",0)
                    val commentInfo:String = Gson().toJson(commentInfoMap)
                    println(commentInfo)
                    insertTeachComment(commentInfo)
                }
            }
            R.id.iv_chose_emoji->{
                if(emojicons.visibility==View.GONE){
                    hideSoftKeyboard()
                    emojicons.visibility=View.VISIBLE
                    iv_chose_emoji.isSelected=true
                }else{
                    showSoftKeyboard()
                    emojicons.visibility=View.GONE
                    iv_chose_emoji.isSelected=false
                }
            }
            R.id.emg_editText->{
                if(emojicons.visibility==View.VISIBLE)
                {
                    emojicons.visibility=View.GONE
                    iv_chose_emoji.isSelected=false
                }
            }
        }
    }


    //https://github.com/rockerhieu/emojicon
    @Override
    override fun onEmojiconBackspaceClicked(v: View?) {
        EmojiconsFragment.backspace(emg_editText);
    }

    @Override
    override fun onEmojiconClicked(emojicon: Emojicon?) {
        EmojiconsFragment.input(emg_editText, emojicon);
    }

    private fun setEmojiconFragment(useSystemDefault: Boolean) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit()
    }

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_reply)
        setEmojiconFragment(false)//设置emoji选择器Fragment//false是苹果风格，true是安卓风格
        emojicons.layoutParams.height=600//设置高度，最好和输入法一样高
        emojicons.visibility=View.GONE
        val dialogWindow = this.window
        dialogWindow.setGravity(Gravity.BOTTOM)
        dialogWindow.setBackgroundDrawable(ColorDrawable(Color.WHITE))//dialog设置这货才能横向满屏，不过此处只是设个背景而已
        dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        //dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);//防止软键盘不弹出
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)//自动弹出软键盘

        iv_chose_emoji.setOnClickListener(this)
        emg_editText.setOnClickListener(this)


        val hashMap = intent.getSerializableExtra("commentReplyInfo") as HashMap<*,*>
        println(hashMap)
        teachID=(hashMap["topicID"]).toString()
        objectID=(hashMap["objectID"]).toString()


        imageButton_send.setOnClickListener(this)
    }

    @Override
    override fun onTouchEvent(event: MotionEvent): Boolean {//单击外部空白自动关闭
        return if (event.action == MotionEvent.ACTION_DOWN && isOutOfBounds(this, event)) {
            finish()
            true
        } else super.onTouchEvent(event)
    }
    private fun isOutOfBounds(context: Activity, event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        val slop = ViewConfiguration.get(context).scaledWindowTouchSlop
        val decorView = context.window.decorView
        return x < -slop || y < -slop || x > decorView.width + slop || y > decorView.height + slop
    }//判断是否点击了外部


    /**
     * Shows the soft input for its input text, by  reflection mechanism
     */
    fun showSoftKeyboard() {
        var o: Any? = null
        try {
            val threadClazz = Class.forName("android.view.inputmethod.InputMethodManager")

            val method = threadClazz.getDeclaredMethod("peekInstance")//return sInstance
            //val methods = threadClazz.declaredMethods

            method.isAccessible = true
            o = method.invoke(null)
            if (o == null) {
                return
            }
            val inputMethodManager = o as InputMethodManager?
            if (inputMethodManager != null) {
                emg_editText.requestFocus()
                inputMethodManager.showSoftInput(emg_editText, 0)
            }
        } catch (e: Exception) {
        }

    }

    /**
     * Hides the soft input if it is active for the input text, by  reflection mechanism
     */
    fun hideSoftKeyboard() {
        var o: Any? = null
        try {
            val threadClazz = Class.forName("android.view.inputmethod.InputMethodManager")

            val method = threadClazz.getDeclaredMethod("peekInstance")//return sInstance
            //val methods = threadClazz.declaredMethods

            method.isAccessible = true
            o = method.invoke(null)
            if (o == null) {
                return
            }
            val inputMethodManager = o as InputMethodManager?
            if (inputMethodManager != null && inputMethodManager.isActive(emg_editText)) {
                inputMethodManager.hideSoftInputFromWindow(emg_editText.getWindowToken(), 0)
            }
        } catch (e: Exception) {
        }

    }

    override fun onResume() {
        super.onResume()
        if(emojicons.visibility==View.VISIBLE){
            emojicons.visibility=View.GONE
        }
    }

    fun showSnackBar(text:String) {
        Snackbar.make(new_sub_comment_rootView, text, Snackbar.LENGTH_SHORT).show()
    }

    private fun insertTeachComment(commentInfo: String) {
        if(NetworkUtil.isNetworkAvailable(this@ReplyDialogActivity)) {
            dialog = Dialog(this@ReplyDialogActivity)
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
                    .subscribe(object : Observer<String> {
                        override fun onComplete() {
                            dialog!!.cancel()
                            if(result== INSERT_COMMENT_SUCCESS){
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
}
