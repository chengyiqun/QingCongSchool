package com.example.xpb.qingcongschool.comment;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xpb.qingcongschool.R;

/**
 * Created by lenovo on 2017/10/15 0015.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MViewHolder> {

    public static String[] mDataset;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CardView cardView;

        private ImageView iv_userpicture;
        private TextView tv_username;
        private TextView tv_timedata;
        private TextView tv_comment;
        private ImageView iv_thumbup;
        private TextView tv_thumbup_count;
        private ImageView iv_thumpdown;
        private TextView tv_thumpdown_count;
        private ImageView iv_comment;
        private TextView tv_comment_count;
        private ImageView iv_menu;
        private MViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.card_view);
            iv_userpicture =v.findViewById(R.id.user_picture);
            tv_username = v.findViewById(R.id.tv_user_name);//
            //Emojix.wrap(tv_username.getContext());
            tv_timedata=v.findViewById(R.id.tv_timedata);
            tv_comment=v.findViewById(R.id.tv_comment);//
            //Emojix.wrap(tv_comment.getContext());

            iv_thumbup=v.findViewById(R.id.iv_thumbup);
            tv_thumbup_count=v.findViewById(R.id.tv_thumbup_count);
            iv_thumpdown=v.findViewById(R.id.iv_thumbdown);
            tv_thumpdown_count=v.findViewById(R.id.tv_thumbdown_count);
            iv_comment=v.findViewById(R.id.iv_comment);
            tv_comment_count=v.findViewById(R.id.tv_comment_count);
            iv_menu =v.findViewById(R.id.iv_menu);

            cardView.setOnClickListener(this);
            iv_userpicture.setOnClickListener(this);
            tv_username.setOnClickListener(this);
            iv_thumbup.setOnClickListener(this);
            iv_thumpdown.setOnClickListener(this);
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
                    break;
                case R.id.user_picture:
                    break;
                case R.id.tv_user_name:
                    break;
                case R.id.iv_thumbup:
                    iv_thumpdown.setSelected(false);
                    iv_thumbup.setSelected(true);
                    break;
                case R.id.iv_thumbdown:
                    iv_thumbup.setSelected(false);
                    iv_thumpdown.setSelected(true);
                    break;
                case R.id.iv_comment:
                    //new ReplyDialog(v.getContext()).show();
                    Intent intent=new Intent(v.getContext(),ReplyDialogActivity.class);
                    v.getContext().startActivity(intent);
                    break;
                case R.id.iv_menu:
                    Log.d("menu","menu");
                    System.out.println(mDataset[i]);
                    break;
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent,
                                          int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_viewholder, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new MViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.tv_username.setText(mDataset[position]);

        holder.itemView.setTag(position);
        holder.cardView.setTag(position);
        holder.iv_userpicture.setTag(position);
        holder.tv_username.setTag(position);
        holder.iv_thumbup.setTag(position);
        holder.iv_thumpdown.setTag(position);
        holder.iv_comment.setTag(position);
        holder.iv_menu.setTag(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}