package com.example.xpb.qingcongschool.course.resource.comment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.xpb.qingcongschool.R
import com.example.xpb.qingcongschool.main.BaseActivity
import com.example.xpb.qingcongschool.util.Utils
import io.github.rockerhieu.emojicon.EmojiconGridFragment
import io.github.rockerhieu.emojicon.EmojiconsFragment
import io.github.rockerhieu.emojicon.emoji.Emojicon
import kotlinx.android.synthetic.main.activity_comment_detaill.*
import kotlinx.android.synthetic.main.dialog_reply.*

class CommentDetaillActivity : BaseActivity(), EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener, View.OnClickListener  {
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
                Utils.println("发送")
            }
        }
    }

    private var myDataset:Array<String>?=null
    private var mAdapter: MyAdapterDetail? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_detaill)
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
        toolbar_detail.title="详情"
        setSupportActionBar(toolbar_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_detail.setNavigationOnClickListener { finish() }
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

        mAdapter = MyAdapterDetail(myDataset!!)
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
