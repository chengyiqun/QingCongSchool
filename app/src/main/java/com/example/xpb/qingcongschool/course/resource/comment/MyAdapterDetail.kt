package com.example.xpb.qingcongschool.course.resource.comment

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blankj.utilcode.util.LogUtils
import com.example.xpb.qingcongschool.BuildConfig

import com.example.xpb.qingcongschool.R
import com.example.xpb.qingcongschool.comment.ReplyDialogActivity
import com.example.xpb.qingcongschool.util.Utils
import com.facebook.drawee.view.SimpleDraweeView

/**
 * Created by 程义群（空灵入耳） on 2018/01/03 0003.
 */

class MyAdapterDetail// Provide a suitable constructor (depends on the kind of dataset)
(myDataset: Array<String>) : RecyclerView.Adapter<MyAdapterDetail.MViewHolder>() {
    init {
        mDataset = myDataset
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class MViewHolder (v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

        public val root_layout:RelativeLayout

        public val iv_user_avatar: SimpleDraweeView
        public val tv_username: TextView

        public val tv_thumbDown_count_comment:TextView
        public val iv_thumbDown_comment:ImageView
        public val tv_thumbUp_count_comment:TextView
        public val iv_thumbUp_comment:ImageView
        public val tv_comment:TextView
        public val tv_publish_time:TextView
        public val tv_reply:TextView
        public val tv_share:TextView


        init {
            root_layout = v.findViewById(R.id.root_layout)

            iv_user_avatar = v.findViewById(R.id.iv_user_avatar)
            tv_username = v.findViewById(R.id.tv_username)
            tv_thumbDown_count_comment = v.findViewById(R.id.tv_thumbDown_count_comment)
            iv_thumbDown_comment = v.findViewById(R.id.iv_thumbDown_comment)
            tv_thumbUp_count_comment = v.findViewById(R.id.tv_thumbUp_count_comment)
            iv_thumbUp_comment = v.findViewById(R.id.iv_thumbUp_comment)
            tv_comment = v.findViewById(R.id.tv_comment)
            tv_publish_time = v.findViewById(R.id.tv_publish_time)
            tv_reply = v.findViewById(R.id.tv_reply)
            tv_share = v.findViewById(R.id.tv_share)


            root_layout.setOnClickListener(this)
            iv_user_avatar.setOnClickListener(this)
            tv_username.setOnClickListener(this)

            tv_thumbDown_count_comment.setOnClickListener(this)
            iv_thumbDown_comment.setOnClickListener(this)
            tv_thumbUp_count_comment.setOnClickListener(this)
            iv_thumbUp_comment.setOnClickListener(this)

            tv_comment.setOnClickListener(this)
            tv_publish_time.setOnClickListener(this)
            tv_reply.setOnClickListener(this)
            tv_share.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val i = v.tag as Int
            if(BuildConfig.DEBUG)
                LogUtils.dTag("position ", i.toString())
            when (v.id) {
                R.id.root_layout -> Utils.println("单击root_layout")
                R.id.iv_user_avatar -> {
                    Utils.println("单击用户头像")
                }
                R.id.tv_username -> {
                    Utils.println("单击用户名")
                }
                R.id.iv_thumbDown_comment -> {
                    iv_thumbDown_comment.isSelected = true
                    iv_thumbUp_comment.isSelected = false
                }
                R.id.iv_thumbUp_comment -> {
                    iv_thumbUp_comment.isSelected = true
                    iv_thumbDown_comment.isSelected = false
                }
                R.id.tv_comment -> {
                    Utils.println("单击评论")
                    //new ReplyDialog(v.getContext()).show();
                    //改成跳转到新的页面呢而不是回复框
                    val intent = Intent(v.context, ReplyDialogActivity::class.java)
                    v.context.startActivity(intent)
                }
                R.id.tv_publish_time -> {
                    Utils.println("单击发布时间")
                }
                R.id.tv_reply->{Utils.println("单击回复")}
                R.id.tv_share->{Utils.println("单击分享")}
            }
        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyAdapterDetail.MViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.comment_viewholder2, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return MyAdapterDetail.MViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyAdapterDetail.MViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.tv_username.text = mDataset[position]

        holder.root_layout.tag=position
        holder.iv_user_avatar.tag=position
        holder.tv_username.tag=position
        holder.tv_thumbDown_count_comment.tag=position
        holder.iv_thumbDown_comment.tag=position
        holder.tv_thumbUp_count_comment.tag=position
        holder.iv_thumbUp_comment.tag=position

        holder.tv_comment.tag = position
        holder.tv_publish_time.tag = position
        holder.tv_reply.tag = position
        holder.tv_share.tag = position
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return mDataset.size
    }

    companion object {
        lateinit var mDataset: Array<String>
    }
}