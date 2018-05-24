package com.example.xpb.qingcongschool.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.blankj.utilcode.util.ToastUtils
import com.example.xpb.qingcongschool.R
import com.example.xpb.qingcongschool.topic.NewTopicActivity
import kotlinx.android.synthetic.main.fragment_square.*

/**
 * Created by xpb on 2016/7/23.
 */
class SquareFragment : android.support.v4.app.Fragment(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.topic_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_new_topic->{
                if(MainActivity.Companion.islogin){
                    ToastUtils.showShort("发布动态")
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
    }

}