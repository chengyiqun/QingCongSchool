package com.example.xpb.qingcongschool.topic.comment.reply

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.xpb.qingcongschool.R
import com.example.xpb.qingcongschool.main.Topic

class TopicCommentReplyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_comment_reply)

        println(intent.getSerializableExtra("comment") as Topic.TopicCommentListBean)
    }
}
