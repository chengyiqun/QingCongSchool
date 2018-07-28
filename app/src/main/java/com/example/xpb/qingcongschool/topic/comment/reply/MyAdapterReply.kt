package com.example.xpb.qingcongschool.topic.comment.reply


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.blankj.utilcode.util.LogUtils
import com.example.xpb.qingcongschool.R
import com.example.xpb.qingcongschool.RetrofitFactory
import com.example.xpb.qingcongschool.util.GlideApp
import com.example.xpb.qingcongschool.util.GlideCircleTransform


/**
 * Created by lenovo on 2017/10/15 0015.
 */

class MyAdapterReply(myDataset: MutableList<TopicCommentReply>, mTopicID: String ,context: Context) : RecyclerView.Adapter<MyAdapterReply.MViewHolder>() {
    companion object {
        lateinit var mDataset: MutableList<TopicCommentReply>
        lateinit var topicID: String
    }
    var context :Context?=null
    init {
        this.context=context
        mDataset = myDataset
        topicID = mTopicID
    }
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return mDataset.size
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class MViewHolder (v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

        val root_layout_viewholder: RelativeLayout
        val iv_user_avatar2: ImageView
        val tv_username: TextView
        val iv_thumbUp_comment: ImageView
        val tv_thumbUp_count_comment: TextView
        val tv_comment: TextView
        val tv_publish_time: TextView
        val tv_reply: TextView

        val tv_share: TextView

        init {
            root_layout_viewholder = v.findViewById(R.id.root_layout_viewholder)
            iv_user_avatar2 = v.findViewById(R.id.iv_user_avatar2)
            tv_username = v.findViewById(R.id.tv_username)//
            //Emojix.wrap(tv_username.getContext());
            tv_publish_time = v.findViewById(R.id.tv_publish_time)

            tv_comment = v.findViewById(R.id.tv_comment)

            iv_thumbUp_comment = v.findViewById(R.id.iv_thumbUp_comment)
            tv_thumbUp_count_comment = v.findViewById(R.id.tv_thumbUp_count_comment)
            tv_share = v.findViewById(R.id.tv_share)
            tv_reply = v.findViewById(R.id.tv_reply)//
            //Emojix.wrap(tv_comment.getContext());

            root_layout_viewholder.setOnClickListener(this)
            iv_user_avatar2.setOnClickListener(this)
            tv_username.setOnClickListener(this)
            iv_thumbUp_comment.setOnClickListener(this)
            tv_share.setOnClickListener(this)
            tv_reply.setOnClickListener(this)
            tv_share.setBackgroundResource(R.drawable.radius_textview_bg)
            tv_reply.setBackgroundResource(R.drawable.radius_textview_bg)
        }

        override fun onClick(v: View) {
            val i = v.tag as Int
            Log.d("position ", i.toString())
            when (v.id) {
                R.id.root_layout_viewholder -> println("单击rootview")
                R.id.iv_user_avatar2 -> println("单击头像")
                R.id.tv_user_name -> println("单击用户名")
                R.id.iv_thumbUp_comment -> {
                    iv_thumbUp_comment.isSelected = true
                    println("点赞")
                }
                R.id.tv_share -> println("分享")
                R.id.tv_reply -> {
                    println("回复")
                    val hashMap = HashMap<String, Any>()
                    hashMap.put("topicID",topicID)
                    hashMap.put("objectID", mDataset[i].getCommentID())
                    hashMap.put("toUserID", mDataset[i].getUserID())
                    LogUtils.d(hashMap)
                    val intent = Intent(v.context,TopicCommentReplyDialogActivity::class.java)
                    intent.putExtra("commentReplyInfo",hashMap)
                    v.context.startActivity(intent)
                }
            }
        }
    }



    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.comment_viewholder2, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return MViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MViewHolder, position: Int) {//设置数据
        holder.itemView.tag = position
        holder.root_layout_viewholder.tag = position
        holder.iv_user_avatar2.tag = position
        holder.tv_username.tag = position
        holder.iv_thumbUp_comment.tag = position
        holder.tv_share.tag = position
        holder.tv_reply.tag = position
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.tv_username.text = mDataset[position].getUserName() as CharSequence
        if(mDataset[position].toUserName==null||mDataset[position].toUserName.equals("")){
            System.out.println(position.toString() + " 回复的是评论，不是回复")
            holder.tv_comment.text = mDataset[position].getContent()
        }else{
            holder.tv_comment.text = ("@"+mDataset[position].getToUserName()+"："+mDataset[position].getContent())
        }

        holder.tv_publish_time.text = mDataset[position].getCommentTime()
        holder.tv_thumbUp_count_comment.text = (mDataset[position].getLikeTimes()).toString()
        val uri = Uri.parse(RetrofitFactory.baseUrl + "/QingXiao/avatar/" + mDataset[position].getAvatar_store_name())
        println(uri)
        GlideApp.with(context!!)
                .load(uri)
                .transform(GlideCircleTransform())
                .into(holder.iv_user_avatar2)

    }




}