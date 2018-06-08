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
import android.widget.TextView;

import com.example.xpb.qingcongschool.R;
import com.example.xpb.qingcongschool.RetrofitFactory;
import com.example.xpb.qingcongschool.util.TimeFactory;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lenovo on 2017/10/15 0015.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MViewHolder> {

    public static List<HashMap> mDataset;
    public static String teachID;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CardView cardView;

        private SimpleDraweeView iv_userpicture;
        private TextView tv_username;
        private TextView tv_timedata;
        private TextView tv_comment;
        private ImageView iv_thumbup;
        private TextView tv_thumbup_count;
        private ImageView iv_share;
        private TextView tv_share_count;
        private ImageView iv_comment;
        private TextView tv_comment_count;
        private ImageView iv_menu;
        private MViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.card_view);
            iv_userpicture =v.findViewById(R.id.user_picture);
            iv_userpicture.setHierarchy(new GenericDraweeHierarchyBuilder(v.getResources()).setDesiredAspectRatio(1.0f)
                    .setFailureImage(R.drawable.ic_launcher_24dp)
                    .setRoundingParams(RoundingParams.fromCornersRadius(100f))
                    .build());
            tv_username = v.findViewById(R.id.tv_user_name);//
            //Emojix.wrap(tv_username.getContext());
            tv_timedata=v.findViewById(R.id.tv_timedata);
            tv_comment=v.findViewById(R.id.tv_comment);//
            //Emojix.wrap(tv_comment.getContext());

            iv_thumbup=v.findViewById(R.id.iv_thumbup);
            tv_thumbup_count=v.findViewById(R.id.tv_thumbup_count);
            iv_share =v.findViewById(R.id.iv_share);
            tv_share_count=v.findViewById(R.id.tv_share_count);
            iv_comment=v.findViewById(R.id.iv_comment);
            tv_comment_count=v.findViewById(R.id.tv_comment_count);
            iv_menu =v.findViewById(R.id.iv_menu);

            cardView.setOnClickListener(this);
            iv_userpicture.setOnClickListener(this);
            tv_username.setOnClickListener(this);
            iv_thumbup.setOnClickListener(this);
            iv_share.setOnClickListener(this);
            iv_comment.setOnClickListener(this);
            iv_menu.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int i = (int) v.getTag();
            Log.d("position ", String.valueOf(i));
            switch (v.getId()){
                case R.id.card_view:
                    System.out.println("单击CardView");
                    Intent intent = new Intent(v.getContext(), CommentReplyActivity.class);
                    intent.putExtra("teachID",teachID);
                    intent.putExtra("userTeachComment", mDataset.get(i));
                    v.getContext().startActivity(intent);
                    break;
                case R.id.user_picture:
                    System.out.println("单击头像");
                    break;
                case R.id.tv_user_name:
                    System.out.println("单击用户名");
                    break;
                case R.id.iv_thumbup:
                    iv_thumbup.setSelected(true);
                    System.out.println("点赞");
                    break;
                case R.id.iv_share:
                    System.out.println("分享");
                    break;
                case R.id.iv_comment:
                    System.out.println("评论");
                    /*Intent intent=new Intent(v.getContext(),TopicCommentReplyDialogActivity.class);
                    v.getContext().startActivity(intent);*/
                    break;
                case R.id.iv_menu:
                    Log.d("menu","menu");
                    System.out.println(mDataset.get(i));
                    break;
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<HashMap> myDataset,String mteachID) {
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
                .inflate(R.layout.comment_viewholder, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new MViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull MViewHolder holder, int position) {//设置数据
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.tv_username.setText((CharSequence) mDataset.get(position).get("user_name"));
        holder.tv_comment.setText((CharSequence) mDataset.get(position).get("content"));
        long timedate= (long) mDataset.get(position).get("comment_time");
        holder.tv_timedata.setText(TimeFactory.second2TimeStrapString(timedate));
        holder.tv_thumbup_count.setText(String.valueOf((int) mDataset.get(position).get("like_times")));
        holder.tv_comment_count.setText(String.valueOf((int) mDataset.get(position).get("comment_times")));
        holder.tv_share_count.setText(String.valueOf((int) mDataset.get(position).get("share_times")));

        Uri uri = Uri.parse(RetrofitFactory.baseUrl+"/QingXiao/avatar/"+mDataset.get(position).get("avatar_store_name"));
        System.out.println(uri);
        holder.iv_userpicture.setImageURI(uri);

        holder.itemView.setTag(position);
        holder.cardView.setTag(position);
        holder.iv_userpicture.setTag(position);
        holder.tv_username.setTag(position);
        holder.iv_thumbup.setTag(position);
        holder.iv_share.setTag(position);
        holder.iv_comment.setTag(position);
        holder.iv_menu.setTag(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}