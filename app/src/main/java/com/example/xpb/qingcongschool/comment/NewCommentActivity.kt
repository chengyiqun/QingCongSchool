package com.example.xpb.qingcongschool.comment

import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.example.xpb.qingcongschool.R
import com.example.xpb.qingcongschool.main.BaseActivity
import kotlinx.android.synthetic.main.activity_new_comment.*

class NewCommentActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_comment)
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
                R.id.action_send -> Snackbar.make(root_view, "send", Snackbar.LENGTH_SHORT).show()
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_send, menu)
        return true
    }

}
