package com.example.xpb.qingcongschool.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.blankj.utilcode.util.ToastUtils
import com.example.xpb.qingcongschool.R
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
        inflater?.inflate(R.menu.subject_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_new_subject->{
                if(MainActivity.Companion.islogin){
                    ToastUtils.showShort("发布动态")
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