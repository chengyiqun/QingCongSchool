package com.example.xpb.qingcongschool.main

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.example.xpb.qingcongschool.R
import com.example.xpb.qingcongschool.RetrofitFactory
import com.example.xpb.qingcongschool.topic.NewTopicActivity
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_square.*

/**
 * Created by xpb on 2016/7/23.
 */
class SquareFragment : android.support.v4.app.Fragment(){
    private var myDataset : List<HashMap<*,*>>?=null
    private var mAdapter: TopicAdapter? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var lastVisibleItem:Int=0
    private var currentPageCount:Int=1;

    companion object {
        const val GET_TOPIC_SUCCESS=3421
        const val TOKEN_ERROR = 3004
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        //设置该方法为true后，可以让fragment在activity被重建时保持实例不变。
        //https://www.jianshu.com/p/fb14480e47fb
        retainInstance = true;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if(MainActivity.islogin){
            inflater?.inflate(R.menu.topic_menu,menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_new_topic->{
                if(MainActivity.islogin){
                    val intent = Intent(activity, NewTopicActivity::class.java)
                    startActivity(intent)
                }else{
                    ToastUtils.showShort("登陆才能发布动态")
                }
                return true
            }
        }
        return false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savebundle: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_square, container, false)
    }
    private fun init() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar_square)
        recycler_view_topic.setHasFixedSize(true)
        mLayoutManager=LinearLayoutManager(activity)
        recycler_view_topic.layoutManager=mLayoutManager

        ////
        val list= mutableListOf<Topic>()
        mAdapter=TopicAdapter(list,context)
        recycler_view_topic.adapter=mAdapter

        //下拉刷新
        swipeRefreshLayout_topic.setOnRefreshListener(object :SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                mAdapter!!.clearList()
                currentPageCount=1
                val observable = RetrofitFactory.getInstance().getTopicList(currentPageCount)
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Observer<String>{
                            override fun onComplete() {
                                swipeRefreshLayout_topic.isRefreshing = false
                            }
                            override fun onSubscribe(d: Disposable?) {
                            }
                            override fun onNext(s: String?) {
                                val jsonObject = JSONObject.parseObject(s)
                                val result = jsonObject.getInteger("result")
                                LogUtils.d(result)
                                when(result){
                                    GET_TOPIC_SUCCESS->{
                                        val resultString = jsonObject.getString("resultString")
                                        val list0= JSON.parseObject(resultString,object :TypeReference<MutableList<Topic>>(){})
                                        mAdapter!!.refreshList(list0)
                                        mAdapter!!.printList()
                                        ToastUtils.showShort("刷新成功")
                                    }
                                    TOKEN_ERROR->{
                                        ToastUtils.showShort("请重新登陆")
                                    }
                                }
                            }
                            override fun onError(e: Throwable?) {
                                e?.printStackTrace()
                                swipeRefreshLayout_topic.isRefreshing = false
                                ToastUtils.showShort("刷新失败")
                            }
                        })
            }

        })

        //上拉加载
        recycler_view_topic.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mAdapter!!.itemCount) {
                    progressBar.visibility=View.VISIBLE
                    val observable = RetrofitFactory.getInstance().getTopicList(currentPageCount+1)
                    observable.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : Observer<String>{
                                override fun onComplete() {
                                    progressBar.visibility=View.GONE
                                    currentPageCount++
                                }
                                override fun onSubscribe(d: Disposable?) {
                                }
                                override fun onNext(value: String?) {
                                    val jsonObject = JSONObject.parseObject(value)
                                    val result = jsonObject.getInteger("result")
                                    LogUtils.d(result)
                                    when(result){
                                        GET_TOPIC_SUCCESS->{
                                            val resultString = jsonObject.getString("resultString")
                                            val list0= JSON.parseObject(resultString,object :TypeReference<MutableList<Topic>>(){})
                                            mAdapter!!.appendList(list0)
                                            mAdapter!!.printList()
                                            if(list0.size==0){
                                                ToastUtils.showShort("没有更多了")
                                            }else{
                                                ToastUtils.showShort("刷新成功")
                                            }
                                        }
                                        TOKEN_ERROR->{
                                            ToastUtils.showShort("请重新登陆")
                                        }
                                    }
                                }
                                override fun onError(e: Throwable?) {
                                    e?.printStackTrace()
                                }
                            })
                }
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                lastVisibleItem= mLayoutManager!!.findLastVisibleItemPosition();
            }
        })


    }


}