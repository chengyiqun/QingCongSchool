package com.example.xpb.qingcongschool.course.resource.comment

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.ToastUtils
import com.example.xpb.qingcongschool.R
import com.example.xpb.qingcongschool.RetrofitFactory
import com.example.xpb.qingcongschool.main.BaseActivity
import com.example.xpb.qingcongschool.util.NetworkUtil
import com.example.xpb.qingcongschool.util.TimeFactory
import com.example.xpb.qingcongschool.util.Utils
import com.google.gson.Gson
import io.github.rockerhieu.emojicon.EmojiconGridFragment
import io.github.rockerhieu.emojicon.EmojiconsFragment
import io.github.rockerhieu.emojicon.emoji.Emojicon
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_resource_comment.*
import kotlinx.android.synthetic.main.dialog_reply.*
import okhttp3.RequestBody
import kotlin.collections.HashMap

class ResourceCommentActivity : BaseActivity(), EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener, View.OnClickListener {
    var dialog:Dialog? = null
    val UPLOAD__RESOURCE_COMMENT_SUCCESS = 3351
    val COURSE_RESOURCE_NOTEXIST = 3352
    val TOKEN_ERROR = 3004
    var result: Int? = 0

    var courseResourceID: String = ""
    override fun onEmojiconClicked(emojicon: Emojicon?) {
        EmojiconsFragment.input(emg_editText, emojicon);
    }

    override fun onEmojiconBackspaceClicked(v: View?) {
        EmojiconsFragment.backspace(emg_editText);
    }

    private fun setEmojiconFragment(useSystemDefault: Boolean) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_chose_emoji -> {
                if (emojicons.visibility == View.GONE) {
                    hideSoftKeyboard()
                    emojicons.visibility = View.VISIBLE
                    iv_chose_emoji.isSelected = true
                } else {
                    showSoftKeyboard()
                    emojicons.visibility = View.GONE
                    iv_chose_emoji.isSelected = false
                }
            }
            R.id.emg_editText -> {
                if (emojicons.visibility == View.VISIBLE) {
                    emojicons.visibility = View.GONE
                    iv_chose_emoji.isSelected = false
                }
            }
            R.id.imageButton_send -> {
                if (!emg_editText.text.toString().equals("")) {
                    val resourceComment = HashMap<String, Any>()
                    resourceComment.put("courseResourceID", courseResourceID)
                    resourceComment.put("content", emg_editText.text.toString())
                    resourceComment.put("commentType", 0)
                    val jsonStr: String = Gson().toJson(resourceComment)
                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonStr)
                    val observableInsertResourceComment = RetrofitFactory.getInstance().insertResourceComment(body)
                    observableInsertResourceComment.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : Observer<String> {
                                override fun onComplete() {
                                    if (result == UPLOAD__RESOURCE_COMMENT_SUCCESS) {

                                    }
                                }

                                override fun onSubscribe(d: Disposable?) {}
                                override fun onNext(s: String?) {
                                    val jsonObject = JSONObject.parseObject(s)
                                    result = jsonObject.getInteger("result")
                                    when (result) {
                                        UPLOAD__RESOURCE_COMMENT_SUCCESS -> {
                                            ToastUtils.showShort("发送成功")
                                            result = UPLOAD__RESOURCE_COMMENT_SUCCESS
                                        }
                                        COURSE_RESOURCE_NOTEXIST -> {
                                            ToastUtils.showShort("教学活动不存在")
                                        }
                                        TOKEN_ERROR -> {
                                            ToastUtils.showShort("请重新登陆")
                                        }
                                    }
                                }

                                override fun onError(e: Throwable?) {
                                    ToastUtils.showLong("服务器通信异常")
                                    e?.printStackTrace()
                                }

                            })
                    Utils.println("发送")
                    emg_editText.setText("")
                }
            }
        }
    }

    private lateinit var myDataset: List<HashMap<*,*>>
    private var mAdapter: MyAdapter? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resource_comment)

        courseResourceID = intent.getStringExtra("courseResourceID")

        setEmojiconFragment(false)
        emojicons.layoutParams.height = 600//设置高度，最好和输入法一样高
        emojicons.visibility = View.GONE
        iv_chose_emoji.setOnClickListener(this)
        emg_editText.setOnClickListener(this)
        imageButton_send.setOnClickListener(this)
        initToolbar();
    }

    private fun initToolbar() {
        toolbar_resource_comment.title = "资源评论"
        setSupportActionBar(toolbar_resource_comment)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_resource_comment.setNavigationOnClickListener { finish() }
    }



    override fun onResume() {
        super.onResume()
        //避免一上来EditTexit获得焦点
        recource_comment_rootview.isFocusable = true
        recource_comment_rootview.isFocusableInTouchMode = true
        recource_comment_rootview.requestFocus()

        if (emojicons.visibility == View.VISIBLE) {
            emojicons.visibility = View.GONE
        }

        getCommentList()
    }

    private fun getCommentList() {
        val hashMap = HashMap<String, Any>()
        hashMap.put("courseResourceID", courseResourceID)
        hashMap.put("nowTime", TimeFactory.getCurrentTime())
        hashMap.put("sinceTime", "2018-05-13 17:51:39.004")
        val gson = Gson()
        val jsonString = gson.toJson(hashMap)
        Utils.println("获取课程评论列表")

        //检查网络
        if (NetworkUtil.isNetworkAvailable(applicationContext)) {
            dialog = Dialog(this@ResourceCommentActivity)
            dialog!!.setTitle("正在刷新，请稍后...")
            dialog!!.setCancelable(true)
            val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonString)
            val observablelogin = RetrofitFactory.getInstance().getResourceCommentList(body)
            observablelogin.subscribeOn(Schedulers.io())
                    .doOnSubscribe({ _ ->
                        Utils.println("doOnScribe,showDialog")
                        if (dialog != null && !dialog!!.isShowing) {
                            dialog!!.show()
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<String> {
                        override fun onSubscribe(disposable: Disposable) {}
                        override fun onNext(s: String) {
                            println("onNext")
                            val jsonObject = JSONObject.parseObject(s)
                            println(jsonObject)
                            result = jsonObject.getInteger("result")
                            if (result == 3004) {
                                ToastUtils.showShort("tokenError")
                            } else {
                                println("commentList:"+jsonObject.getJSONArray("resultString"))
                                val listHashMap = JSONArray.parseArray(jsonObject.getJSONArray("resultString").toString(), HashMap::class.java)
                                //https://www.cnblogs.com/pcheng/p/5336903.html
                                //listHashMap 循环遍历删除
                                val iterator = listHashMap.iterator()
                                while(iterator.hasNext()){
                                    val map = iterator.next()
                                    if(map.get("comment_type")==1){
                                        iterator.remove()
                                    }
                                }
                                myDataset=listHashMap
                                for (map in listHashMap)
                                    println(map.toString()+"listHashMap map")
                            }
                        }

                        override fun onError(throwable: Throwable) {
                            dialog!!.cancel()
                            throwable.printStackTrace()
                        }

                        override fun onComplete() {
                            println("onComplete")
                            dialog!!.cancel()
                            initData()
                        }
                    })
        } else {
            Snackbar.make(recource_comment_rootview, "网络未连接", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun initData() {
        resource_comment_recycylerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(this)
        resource_comment_recycylerView.layoutManager = mLayoutManager
        mAdapter = MyAdapter(myDataset)
        resource_comment_recycylerView.adapter = mAdapter
    }

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
}
