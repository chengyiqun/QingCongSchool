package com.example.xpb.qingcongschool.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.xpb.qingcongschool.R
import com.example.xpb.qingcongschool.comment.CommentActivity
import com.example.xpb.qingcongschool.course.resource.comment.ResourceCommentActivity
import kotlinx.android.synthetic.main.fragment_more.*
import java.util.*


class MoreFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bt_ActivityComment.setOnClickListener {
            val intent = Intent(activity, CommentActivity::class.java)
            startActivity(intent)
        }

        gotoRC.setOnClickListener{
            val intent = Intent(activity, ResourceCommentActivity::class.java);
            intent.putExtra("courseResourceID", UUID.randomUUID().toString().replace("-",""))
            startActivity(intent)
        }
    }
    companion object {}
}