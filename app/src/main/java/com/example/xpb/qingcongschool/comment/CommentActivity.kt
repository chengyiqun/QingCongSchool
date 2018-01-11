package com.example.xpb.qingcongschool.comment

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.ActionBar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View

import com.example.xpb.qingcongschool.R
import com.example.xpb.qingcongschool.main.BaseActivity
import kotlinx.android.synthetic.main.activity_comment.*

class CommentActivity : BaseActivity() {
    private var myDataset:Array<String>?=null
    private var mAdapter: MyAdapter? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        initToolbar()
        init()

    }

    private fun initToolbar() {
        toolbar_comment.title = "课程评论"
        setSupportActionBar(toolbar_comment)
        //设置是否有返回箭头
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_comment.setNavigationOnClickListener { finish() }
    }

    private fun init() {
        myDataset = arrayOf("\ue32d111\uE32D", "\uE32D222\uE32D", "\uE32D333\uE32D", "\uE32D444\uE32D", "\uE32D555\uE32D", "\uE32D666\uE32D", "\uE32D777\uE32D")
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        comment_recycler_view.setHasFixedSize(true)
        // use a linear layout manager
        mLayoutManager = LinearLayoutManager(this)
        comment_recycler_view.layoutManager = mLayoutManager
        // specify an adapter (see also next example)

        mAdapter = MyAdapter(myDataset)
        comment_recycler_view.adapter = mAdapter
        comment_recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    bt_new_comment.hide()
                } else {
                    bt_new_comment.show()
                }
            }
        })

        bt_new_comment.setOnClickListener {
            val intent = Intent(this, NewCommentActivity::class.java)
            startActivity(intent)
        }
    }


}