package com.example.xpb.qingcongschool.comment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.xpb.qingcongschool.R
import android.app.Activity
import android.view.*
import android.view.inputmethod.InputMethodManager
import io.github.rockerhieu.emojicon.EmojiconGridFragment
import io.github.rockerhieu.emojicon.EmojiconsFragment
import io.github.rockerhieu.emojicon.emoji.Emojicon
import kotlinx.android.synthetic.main.dialog_reply.*


class ReplyDialogActivity : AppCompatActivity() , EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener,View.OnClickListener{

    @Override
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
}
