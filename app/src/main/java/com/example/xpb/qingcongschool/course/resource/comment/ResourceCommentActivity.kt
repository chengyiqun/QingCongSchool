package com.example.xpb.qingcongschool.course.resource.comment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.blankj.utilcode.util.EmptyUtils
import com.example.xpb.qingcongschool.R
import com.example.xpb.qingcongschool.RegisterActivity
import com.example.xpb.qingcongschool.RetrofitFactory
import com.example.xpb.qingcongschool.main.BaseActivity
import com.example.xpb.qingcongschool.util.NetworkUtil
import com.example.xpb.qingcongschool.util.Utils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.rockerhieu.emojicon.EmojiconGridFragment
import io.github.rockerhieu.emojicon.EmojiconsFragment
import io.github.rockerhieu.emojicon.emoji.Emojicon
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.internal.util.NotificationLite.accept
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_resource_comment.*
import kotlinx.android.synthetic.main.dialog_reply.*
import okhttp3.RequestBody
import java.util.*

class ResourceCommentActivity : BaseActivity() , EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener, View.OnClickListener {
    var courseResourceID:String = ""
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
        when(v?.id){
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
            R.id.imageButton_send->{
                if(!emg_editText.text.toString().equals("")){
                    val resourceComment = HashMap<String, Any>()
                    resourceComment.put("courseResourceID",courseResourceID)
                    resourceComment.put("content",emg_editText.text.toString())
                    resourceComment.put("commentType",0)
                    val jsonStr:String = Gson().toJson(resourceComment)
                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonStr)
                    val observableInsertResourceComment = RetrofitFactory.getInstance().insertResourceComment(body)
                    observableInsertResourceComment.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe()
                    Utils.println("发送")
                    emg_editText.setText("")
                }
            }
        }
    }

    private var myDataset:Array<String>?=null
    private var mAdapter: MyAdapter? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resource_comment)

        courseResourceID=intent.getStringExtra("courseResourceID")

        setEmojiconFragment(false)
        emojicons.layoutParams.height=600//设置高度，最好和输入法一样高
        emojicons.visibility=View.GONE
        iv_chose_emoji.setOnClickListener(this)
        emg_editText.setOnClickListener(this)
        imageButton_send.setOnClickListener(this)
        initToolbar();
        init()
    }
    private fun initToolbar(){
        toolbar_resource_comment.title="资源评论"
        setSupportActionBar(toolbar_resource_comment)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_resource_comment.setNavigationOnClickListener { finish() }
    }
    private fun init() {
        //myDataset= arrayOf()
        myDataset = arrayOf("\ue32d111\uE32D", "\uE32D222\uE32D", "\uE32D333\uE32D", "\uE32D444\uE32D", "\uE32D555\uE32D", "\uE32D666\uE32D", "\uE32D777\uE32D")
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        resource_comment_recycylerView.setHasFixedSize(true)
        // use a linear layout manager
        mLayoutManager = LinearLayoutManager(this)
        resource_comment_recycylerView.layoutManager = mLayoutManager
        // specify an adapter (see also next example)

        mAdapter = MyAdapter(myDataset!!)
        resource_comment_recycylerView.adapter = mAdapter
    }

    override fun onResume() {
        super.onResume()
        //避免一上来EditTexit获得焦点
        recource_comment_rootview.isFocusable=true
        recource_comment_rootview.isFocusableInTouchMode=true
        recource_comment_rootview.requestFocus()

        if(emojicons.visibility==View.VISIBLE){
            emojicons.visibility=View.GONE
        }
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
