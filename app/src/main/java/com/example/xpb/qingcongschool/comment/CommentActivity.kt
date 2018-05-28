package com.example.xpb.qingcongschool.comment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_comment.*
import okhttp3.RequestBody

class CommentActivity : BaseActivity() {
    private var myDataset : List<HashMap<*,*>>?=null
    private var mAdapter: MyAdapter? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var teachID: String? = null
    private var dialog: Dialog? = null
    private var result:Int ? = 0
    private  var comment_recycler_view : RecyclerView? = null
    companion object {
        const val INSERT_COMMENT_SUCCESS = 3221
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        initView()
        teachID = intent.getStringExtra("topicID")
        println(teachID)
        getCommentList()
    }


    private fun initView() {
        toolbar_comment.title = "课程评论"
        setSupportActionBar(toolbar_comment)
        //设置是否有返回箭头
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_comment.setNavigationOnClickListener { finish() }
        comment_recycler_view=findViewById(R.id.comment_recycler_view)
    }



    private fun getCommentList() {
        val hashMap = HashMap<String, Any>()
        hashMap.put("topicID", teachID!!)
        hashMap.put("nowTime", TimeFactory.getCurrentTime())
        hashMap.put("sinceTime", "2018-05-13 17:51:39.004")
        val gson = Gson()
        val jsonString = gson.toJson(hashMap)
        Utils.println("获取课程评论列表")

        //检查网络
        if (NetworkUtil.isNetworkAvailable(applicationContext)) {
            dialog = Dialog(this@CommentActivity)
            dialog!!.setTitle("正在刷新，请稍后...")
            dialog!!.setCancelable(true)
            val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonString)
            val observablelogin = RetrofitFactory.getInstance().getCommentList(body)
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
                                println("commentList:"+jsonObject.getJSONArray("commentList"))
                                val listHashMap = JSONArray.parseArray(jsonObject.getJSONArray("commentList").toString(), HashMap::class.java)
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
            Snackbar.make(root_layout, "网络未连接", Snackbar.LENGTH_SHORT).show()
        }

    }


    private fun initData() {
        println("initData")
        //myDataset = arrayOf("\ue32d111\uE32D", "\uE32D222\uE32D", "\uE32D333\uE32D", "\uE32D444\uE32D", "\uE32D555\uE32D", "\uE32D666\uE32D", "\uE32D777\uE32D")
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        comment_recycler_view!!.setHasFixedSize(true)
        // use a linear layout manager
        mLayoutManager = LinearLayoutManager(this@CommentActivity)
        comment_recycler_view!!.layoutManager = mLayoutManager
        // specify an adapter (see also next example)

        mAdapter = MyAdapter(myDataset,teachID)
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
            val intent = Intent(this, NewCommentActivity::class.java)
            intent.putExtra("topicID",teachID)
            startActivityForResult(intent,0)
        }
    }

    private fun getCommentListonActivityResult() {
        val hashMap = HashMap<String, Any>()
        hashMap.put("topicID", teachID!!)
        hashMap.put("nowTime", TimeFactory.getCurrentTime())
        hashMap.put("sinceTime", "2018-05-13 17:51:39.004")
        val gson = Gson()
        val jsonString = gson.toJson(hashMap)
        Utils.println("获取课程评论列表")

        //检查网络
        if (NetworkUtil.isNetworkAvailable(applicationContext)) {
            dialog = Dialog(this@CommentActivity)
            dialog!!.setTitle("正在刷新，请稍后...")
            dialog!!.setCancelable(true)
            val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonString)
            val observablelogin = RetrofitFactory.getInstance().getCommentList(body)
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
                                println("commentList:"+jsonObject.getJSONArray("commentList"))
                                val listHashMap = JSONArray.parseArray(jsonObject.getJSONArray("commentList").toString(), HashMap::class.java)
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
                            mAdapter = MyAdapter(myDataset,teachID)
                            comment_recycler_view!!.adapter = mAdapter
                            mAdapter!!.notifyDataSetChanged()
                        }
                    })
        } else {
            Snackbar.make(root_layout, "网络未连接", Snackbar.LENGTH_SHORT).show()
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {///在onCreate()之外
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == 0 && resultCode == INSERT_COMMENT_SUCCESS) {
            println("onActivityResult")
            getCommentListonActivityResult()
        }
    }
}