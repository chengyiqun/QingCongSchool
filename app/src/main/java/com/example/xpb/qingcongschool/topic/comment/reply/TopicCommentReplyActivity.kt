package com.example.xpb.qingcongschool.topic.comment.reply

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.example.xpb.qingcongschool.R
import com.example.xpb.qingcongschool.RetrofitFactory
import com.example.xpb.qingcongschool.main.Topic
import com.example.xpb.qingcongschool.util.*
import com.google.gson.Gson
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_topic_comment_reply.*
import kotlinx.android.synthetic.main.comment_viewholder2.*
import okhttp3.RequestBody

class TopicCommentReplyActivity : AppCompatActivity(), View.OnClickListener {
    private var commentBean:Topic.TopicCommentListBean?=null
    private var myDataset: MutableList<TopicCommentReply>? = null
    private var mAdapter: MyAdapterReply? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var dialog: Dialog? = null
    private var result: Int? = 0
    private var comment_recycler_view: RecyclerView? = null

    companion object {
        const val GET_TOPICCOMMENTREPLY_SUCCESS=3461
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_user_avatar2 -> {
                println("头像")
            }
            R.id.tv_username -> {
                println("名字")
            }
            R.id.iv_thumbUp_comment -> {
                println("点赞")
            }
            R.id.tv_reply -> {
                println("回复")
                val hashMap = HashMap<String, Any>()
                hashMap.put("objectID", commentBean!!.commentID)
                hashMap.put("topicID", commentBean!!.topicID)
                LogUtils.d(hashMap)
                val intent = Intent(this, TopicCommentReplyDialogActivity::class.java)
                intent.putExtra("commentReplyInfo", hashMap)
                startActivity(intent)
            }
            R.id.tv_share -> {
                println("分享")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_comment_reply)
        initView()
        init()
    }

    override fun onResume() {
        super.onResume()
        getCommentReplyList()
    }



    private fun initView() {
        toolbar_detail.title = "课程评论回复"
        setSupportActionBar(toolbar_detail)
        //设置是否有返回箭头
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_detail.setNavigationOnClickListener { finish() }
        comment_recycler_view=this.findViewById(R.id.teach_commentReply_recycylerView)
        iv_user_avatar2.setOnClickListener(this)
        tv_username.setOnClickListener(this)
        iv_thumbUp_comment.setOnClickListener(this)
        tv_reply.setOnClickListener(this)
        tv_share.setOnClickListener(this)
        tv_reply.setBackgroundResource(R.drawable.radius_textview_bg)
        tv_share.setBackgroundResource(R.drawable.radius_textview_bg)
    }

    private fun init() {
        commentBean=intent.getSerializableExtra("comment") as Topic.TopicCommentListBean
        LogUtils.d("commentBean",commentBean)
        val uri = Uri.parse(RetrofitFactory.baseUrl + "/QingXiao/avatar/" + commentBean!!.avatarStoreName)
        println(uri)
        GlideApp.with(this)
                .load(uri)
                .transform(GlideCircleTransform())
                .into(iv_user_avatar2)
        tv_username.text = (commentBean!!.userName).toString()
        tv_comment.text = (commentBean!!.content).toString()
        tv_thumbUp_count_comment.text = (commentBean!!.likeTimes).toString()
        tv_publish_time.text = (commentBean!!.commentTime).toString()

    }


    private fun getCommentReplyList() {
        val hashMap = HashMap<String, Any>()
        hashMap.put("commentID", commentBean!!.commentID)
        hashMap.put("nowTime", TimeFactory.getCurrentTime())
        hashMap.put("sinceTime", "2018-05-13 17:51:39.004")
        val gson = Gson()
        val jsonString = gson.toJson(hashMap)
        Utils.println("获取动态评论回复列表")

        //检查网络
        if (NetworkUtil.isNetworkAvailable(applicationContext)) {
            dialog = Dialog(this@TopicCommentReplyActivity)
            dialog!!.setTitle("正在刷新，请稍后...")
            dialog!!.setCancelable(true)
            val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonString)
            val observablelogin = RetrofitFactory.getInstance().getTopicCommentReplyList(body)
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
                                val listCommentReply = JSONArray.parseObject(jsonObject.getJSONArray("resultString").toString(), object :TypeReference<MutableList<TopicCommentReply>>(){})
                                myDataset=listCommentReply
                                for (bean in listCommentReply)
                                    println(bean.toString()+"listCommentReply bean")
                            }
                        }
                        override fun onError(throwable: Throwable) {
                            dialog!!.cancel()
                            throwable.printStackTrace()
                        }

                        override fun onComplete() {
                            println("onComplete")
                            dialog!!.cancel()
                            if (result == GET_TOPICCOMMENTREPLY_SUCCESS) {
                                ToastUtils.showShort("刷新成功")
                            }
                            initData()
                        }
                    })
        } else {
            Snackbar.make(recource_comment_rootview, "网络未连接", Snackbar.LENGTH_SHORT).show()
        }

    }

    private fun initData() {
        println("initData")
        //myDataset = arrayOf("\ue32d111\uE32D", "\uE32D222\uE32D", "\uE32D333\uE32D", "\uE32D444\uE32D", "\uE32D555\uE32D", "\uE32D666\uE32D", "\uE32D777\uE32D")
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        comment_recycler_view!!.setHasFixedSize(true)
        // use a linear layout manager
        mLayoutManager = LinearLayoutManager(this@TopicCommentReplyActivity)
        comment_recycler_view!!.layoutManager = mLayoutManager
        // specify an adapter (see also next example)

        mAdapter = MyAdapterReply(myDataset!!, commentBean!!.topicID,this@TopicCommentReplyActivity)
        comment_recycler_view!!.adapter = mAdapter
        comment_recycler_view!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    println("下滑")
                } else {
                    println("上啦")
                }
            }
        })
    }
}
