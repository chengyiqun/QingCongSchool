package com.example.xpb.qingcongschool.comment;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xpb.qingcongschool.R;
import com.example.xpb.qingcongschool.RetrofitFactory;
import com.example.xpb.qingcongschool.util.TimeFactory;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lenovo on 2017/10/15 0015.
 */

public class MyAdapterReply extends RecyclerView.Adapter<MyAdapterReply.MViewHolder> {

    public static List<HashMap> mDataset;
    public static String teachID;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RelativeLayout root_layout_viewholder;
        private SimpleDraweeView iv_user_avatar2;
        private TextView tv_username;
        private ImageView iv_thumbUp_comment;
        private TextView tv_thumbUp_count_comment;
        private TextView tv_comment;
        private TextView tv_publish_time;
        private TextView tv_reply;

        private TextView tv_share;
        private MViewHolder(View v) {
            super(v);
            root_layout_viewholder = v.findViewById(R.id.root_layout_viewholder);
            iv_user_avatar2 =v.findViewById(R.id.iv_user_avatar2);
            iv_user_avatar2.setHierarchy(new GenericDraweeHierarchyBuilder(v.getResources()).setDesiredAspectRatio(1.0f)
                    .setFailureImage(R.drawable.ic_launcher_24dp)
                    .setRoundingParams(RoundingParams.fromCornersRadius(100f))
                    .build());
            tv_username = v.findViewById(R.id.tv_username);//
            //Emojix.wrap(tv_username.getContext());
            tv_publish_time=v.findViewById(R.id.tv_publish_time);

            tv_comment = v.findViewById(R.id.tv_comment);

            iv_thumbUp_comment=v.findViewById(R.id.iv_thumbUp_comment);
            tv_thumbUp_count_comment=v.findViewById(R.id.tv_thumbUp_count_comment);
            tv_share =v.findViewById(R.id.tv_share);
            tv_reply=v.findViewById(R.id.tv_reply);//
            //Emojix.wrap(tv_comment.getContext());

            root_layout_viewholder.setOnClickListener(this);
            iv_user_avatar2.setOnClickListener(this);
            tv_username.setOnClickListener(this);
            iv_thumbUp_comment.setOnClickListener(this);
            tv_share.setOnClickListener(this);
            tv_reply.setOnClickListener(this);
            tv_share.setBackgroundResource(R.drawable.radius_textview_bg);
            tv_reply.setBackgroundResource(R.drawable.radius_textview_bg);
        }

        @Override
        public void onClick(View v) {
            int i = (int) v.getTag();
            Log.d("position ", String.valueOf(i));
            switch (v.getId()){
                case R.id.root_layout_viewholder:
                    System.out.println("单击rootview");
                    break;
                case R.id.iv_user_avatar2:
                    System.out.println("单击头像");
                    break;
                case R.id.tv_user_name:
                    System.out.println("单击用户名");
                    break;
                case R.id.iv_thumbUp_comment:
                    iv_thumbUp_comment.setSelected(true);
                    System.out.println("点赞");
                    break;
                case R.id.tv_share:
                    System.out.println("分享");
                    break;
                case R.id.tv_reply:
                    System.out.println("回复");
                    /*Intent intent=new Intent(v.getContext(),ReplyDialogActivity.class);
                    v.getContext().startActivity(intent);*/
                    break;

            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapterReply(List<HashMap> myDataset,String mteachID) {
        mDataset = myDataset;
        teachID = mteachID;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                          int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_viewholder2, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new MViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull MViewHolder holder, int position) {//设置数据
        holder.itemView.setTag(position);
        holder.root_layout_viewholder.setTag(position);
        holder.iv_user_avatar2.setTag(position);
        holder.tv_username.setTag(position);
        holder.iv_thumbUp_comment.setTag(position);
        holder.tv_share.setTag(position);
        holder.tv_reply.setTag(position);
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.tv_username.setText((CharSequence) mDataset.get(position).get("toUsername"));
        holder.tv_comment.setText((CharSequence) mDataset.get(position).get("content"));
        long timedate= (long) mDataset.get(position).get("comment_time");
        holder.tv_publish_time.setText(TimeFactory.second2TimeStrapString(timedate));
        holder.tv_thumbUp_count_comment.setText(String.valueOf((int) mDataset.get(position).get("like_times")));
        Uri uri = Uri.parse(RetrofitFactory.baseUrl+"/QingXiao/avatar/"+mDataset.get(position).get("avatar_store_name"));
        System.out.println(uri);
        holder.iv_user_avatar2.setImageURI(uri);


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}