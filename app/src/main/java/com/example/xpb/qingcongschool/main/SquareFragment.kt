package com.example.xpb.qingcongschool.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.xpb.qingcongschool.comment.CommentActivity
import com.example.xpb.qingcongschool.R
import kotlinx.android.synthetic.main.fragment_square.*

/**
 * Created by xpb on 2016/7/23.
 */
class SquareFragment : android.support.v4.app.Fragment() {
    private var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null;
    @SuppressLint("CommitPrefEdits")
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savebundle: Bundle?): View? {

        sharedPreferences = this.getActivity().getSharedPreferences("GetCourse", Context.MODE_PRIVATE)
        editor = sharedPreferences!!.edit()
        return inflater?.inflate(R.layout.fragment_square, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bt_ActivityComment.setOnClickListener {
            val intent = Intent(getActivity(), CommentActivity::class.java)
            startActivity(intent)
        }

    }
}