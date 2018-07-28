package com.example.xpb.qingcongschool.topic.comment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.example.xpb.qingcongschool.R
import com.example.xpb.qingcongschool.RetrofitFactory
import com.example.xpb.qingcongschool.main.Topic
import com.example.xpb.qingcongschool.util.NetworkUtil
import com.example.xpb.qingcongschool.util.TimeFactory
import com.example.xpb.qingcongschool.util.Utils
import com.google.gson.Gson
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_topic_comment.*
import okhttp3.RequestBody

class TopicCommentActivity : AppCompatActivity() {

    private var myDataset : MutableList<Topic.TopicCommentListBean>?=null
    private var mAdapter: TopicCommentAdapter? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var topicID: String? = null
    private var toUserID:String? = null
    private var dialog: Dialog? = null
    private var result:Int ? = 0
    private  var comment_recycler_view : RecyclerView? = null
    companion object {
        const val GET_TOPICCOMMENT_SUCCESS=3451;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_comment)
        topicID = intent.getStringExtra("topicID")
        toUserID = intent.getStringExtra("toUserID")
        println(topicID)
        println(toUserID)
        initView()
    }

    override fun onResume() {
        super.onResume()
        getCommentList()
    }

    private fun getCommentList() {
        val hashMap = HashMap<String, Any>()
        hashMap.put("topicID", topicID!!)
        hashMap.put("nowTime", TimeFactory.getCurrentTime())
        hashMap.put("sinceTime", "2018-05-13 17:51:39.004")
        val gson = Gson()
        val jsonString = gson.toJson(hashMap)
        Utils.println("获取topic评论列表")

        //检查网络
        if (NetworkUtil.isNetworkAvailable(applicationContext)) {
            dialog = Dialog(this@TopicCommentActivity)
            dialog!!.setTitle("正在刷新，请稍后...")
            dialog!!.setCancelable(true)
            val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonString)
            val observablelogin = RetrofitFactory.getInstance().getTopicCommentList(body)
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
                            } else if (result == GET_TOPICCOMMENT_SUCCESS){
                                println(result!!)
                                println("commentList:"+jsonObject.getJSONArray("commentList"))
                                val topicCommentList = JSONArray.parseObject(jsonObject.getJSONArray("commentList").toString(), object :TypeReference<MutableList<Topic.TopicCommentListBean>>(){})

                                myDataset=topicCommentList
                                for (bean in topicCommentList)
                                    println(bean.toString()+":topicComment")
                                ToastUtils.showShort("刷新成功")
                            } else{
                                ToastUtils.showLong("服务器异常")
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
            Snackbar.make(root_layout, "网络未连接", Snackbar.LENGTH_SHORT).show()
        }

    }


    private fun initData() {
        println("initData")
        mAdapter!!.refreshAdapter(myDataset,topicID)
    }

    private fun initView() {
        toolbar_comment.title = "动态评论"
        setSupportActionBar(toolbar_comment)
        //设置是否有返回箭头
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_comment.setNavigationOnClickListener { finish() }
        comment_recycler_view=findViewById(R.id.comment_recycler_view)

        comment_recycler_view!!.setHasFixedSize(true)
        // use a linear layout manager
        mLayoutManager = LinearLayoutManager(this@TopicCommentActivity)
        comment_recycler_view!!.layoutManager = mLayoutManager
        // specify an adapter (see also next example)

        val mDataSet = mutableListOf<Topic.TopicCommentListBean>()
        mAdapter = TopicCommentAdapter(mDataSet, topicID,this@TopicCommentActivity)
        comment_recycler_view!!.adapter = mAdapter
        comment_recycler_view!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
            val intent = Intent(this,NewCommentDialogActivity::class.java)
            intent.putExtra("topicID",topicID)
            intent.putExtra("toUserID",toUserID)
            LogUtils.dTag("topicID toUserID",topicID,toUserID)
            startActivity(intent)
        }
    }
}
