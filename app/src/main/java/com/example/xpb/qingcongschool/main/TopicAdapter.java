package com.example.xpb.qingcongschool.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xpb.qingcongschool.R;
import com.example.xpb.qingcongschool.RetrofitFactory;
import com.example.xpb.qingcongschool.topic.ImageViewActivity;
import com.example.xpb.qingcongschool.topic.comment.TopicCommentActivity;
import com.example.xpb.qingcongschool.util.GlideApp;
import com.example.xpb.qingcongschool.util.GlideCircleTransform;

import java.util.LinkedList;
import java.util.List;

import io.github.rockerhieu.emojicon.EmojiconTextView;

import static com.blankj.utilcode.util.ActivityUtils.startActivity;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.MViewHolder> {
    public GlideCircleTransform glideCircleTransform;
    public static List<Topic> mDataset;
    public Context context;

    public TopicAdapter(List<Topic> myDataset, Context context) {
        glideCircleTransform = new GlideCircleTransform();
        mDataset = myDataset;
        this.context = context;
    }

    public int appendList(List<Topic> newList) {
        int i = 0;
        for (Topic topic : newList) {
            i++;
            mDataset.add(topic);
        }
        this.notifyDataSetChanged();
        return i;
    }

    public void refreshList(List<Topic> newList) {
        mDataset.clear();
        mDataset = newList;
        this.notifyDataSetChanged();
    }

    public void clearList() {
        mDataset.clear();
        this.notifyDataSetChanged();
    }

    public void printList() {
        for (Topic topic : mDataset) {
            System.out.println(topic.toString());
        }
    }

    @NonNull
    @Override
    public MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.topic_viewholder, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new MViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MViewHolder holder, int position) {
        System.out.println("position: " + position);
        holder.tvcomment.setText(mDataset.get(position).getContent());
        holder.tvusername.setText(mDataset.get(position).getUserName());
        holder.tvthumbUpcountcomment.setText(String.valueOf(mDataset.get(position).getLikeTimes()));
        holder.tvpublishtime.setText(mDataset.get(position).getTopicTime());
        holder.tvreply.setText(mDataset.get(position).getCommentTimes() + "回复");
        holder.tvshare.setText(mDataset.get(position).getShareTimes() + "分享");
        GlideApp.with(context)
                .load(RetrofitFactory.baseUrl + "/QingXiao/avatar/" + mDataset.get(position).getAvatarStoreName())
                .transform(glideCircleTransform)
                .into(holder.ivuseravatar);

        for (int i = 0; i < holder.imageViewList.size(); i++) {
            if (holder.imageViewList.get(i).getVisibility() == View.GONE) {
                break;
            }
            holder.imageViewList.get(i).setVisibility(View.GONE);
        }

        List<Topic.TopicImageListBean> imageList = mDataset.get(position).getTopicImageList();
        for (int i = 0; i < imageList.size(); i++) {
            System.out.println(i);
            System.out.println(imageList.get(i));
            holder.imageViewList.get(i).setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < imageList.size(); i++) {
            System.out.println("11111 " + position);
            holder.imageViewList.get(i).setVisibility(View.VISIBLE);
            String imgUri = RetrofitFactory.baseUrl + "/QingXiao/" + (imageList.get(i).getImagePath()).replace('\\', '/');
            /*Uri uri0 = Uri.parse(imgUri );
            int width = 100, height = 100;
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri0)
                    .setResizeOptions(new ResizeOptions(width, height))
                    .build();
            AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(holder.imageViewList.get(i).getController())
                    .setImageRequest(request)
                    .build();*/
            GlideApp.with(context)
                    .load(imgUri)
                    .transform(glideCircleTransform)
                    .into(holder.imageViewList.get(i));
            /*holder.imageViewList.get(i).setController(controller);
            System.out.println(uri0);*/
        }

        holder.rootlayout.setTag(position);
        holder.tvshare.setTag(position);
        holder.tvreply.setTag(position);
        holder.ivreply.setTag(position);
        holder.tvpublishtime.setTag(position);
        holder.picgridlayout.setTag(position);
        for (int i = 0; i < 9; i++) {
            holder.imageViewList.get(i).setTag(position);
        }
        holder.tvcomment.setTag(position);
        holder.tvusername.setTag(position);
        holder.ivthumbUpcomment.setTag(position);
        holder.tvthumbUpcountcomment.setTag(position);
        holder.ivuseravatar.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class MViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout rootlayout;
        TextView tvshare;
        TextView tvreply;
        ImageView ivreply;
        TextView tvpublishtime;
        GridLayout picgridlayout;
        List<ImageView> imageViewList = new LinkedList<>();

        EmojiconTextView tvcomment;
        EmojiconTextView tvusername;
        ImageView ivthumbUpcomment;
        TextView tvthumbUpcountcomment;
        ImageView ivuseravatar;

        public MViewHolder(View v) {
            super(v);
            rootlayout = (RelativeLayout) v.findViewById(R.id.root_layout);
            tvshare = (TextView) v.findViewById(R.id.tv_share);
            tvreply = (TextView) v.findViewById(R.id.tv_reply);
            ivreply = (ImageView) v.findViewById(R.id.iv_reply);
            tvpublishtime = (TextView) v.findViewById(R.id.tv_publish_time);
            picgridlayout = (GridLayout) v.findViewById(R.id.pic_gridlayout);
            imageViewList.add(v.findViewById(R.id.imageView01));
            imageViewList.add(v.findViewById(R.id.imageView02));
            imageViewList.add(v.findViewById(R.id.imageView03));
            imageViewList.add(v.findViewById(R.id.imageView04));
            imageViewList.add(v.findViewById(R.id.imageView05));
            imageViewList.add(v.findViewById(R.id.imageView06));
            imageViewList.add(v.findViewById(R.id.imageView07));
            imageViewList.add(v.findViewById(R.id.imageView08));
            imageViewList.add(v.findViewById(R.id.imageView09));


            tvcomment = (EmojiconTextView) v.findViewById(R.id.tv_comment);
            tvusername = (EmojiconTextView) v.findViewById(R.id.tv_username);
            ivthumbUpcomment = (ImageView) v.findViewById(R.id.iv_thumbUp_comment);
            tvthumbUpcountcomment = (TextView) v.findViewById(R.id.tv_thumbUp_count_comment);
            ivuseravatar = (ImageView) v.findViewById(R.id.iv_user_avatar);

            rootlayout.setOnClickListener(this);
            tvshare.setOnClickListener(this);
            ivreply.setOnClickListener(this);
            for (View imageview : imageViewList) {
                imageview.setOnClickListener(this);

            }
            tvusername.setOnClickListener(this);
            ivthumbUpcomment.setOnClickListener(this);
            ivuseravatar.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int i = (int) v.getTag();
            Log.d("position ", String.valueOf(i));
            switch (v.getId()) {
                case R.id.iv_user_avatar:
                    break;
                case R.id.tv_username:
                    break;
                case R.id.iv_thumbUp_comment:
                    break;
                case R.id.root_layout:
                    System.out.println("回复");
                    Intent intent = new Intent(v.getContext(), TopicCommentActivity.class);
                    intent.putExtra("topicID", mDataset.get(i).getTopicID());
                    intent.putExtra("toUserID", mDataset.get(i).getUserID());
                    startActivity(intent);
                    break;
                case R.id.tv_share:
                    break;
                case R.id.iv_reply:
                    break;
                case R.id.imageView01:
                    String img_uri1 = RetrofitFactory.baseUrl + "/QingXiao/" + (mDataset.get(i).getTopicImageList().get(0).getImagePath()).replace('\\', '/');
                    Intent intent1 = new Intent(v.getContext(), ImageViewActivity.class);
                    intent1.putExtra("img_url", img_uri1);
                    startActivity(intent1);
                    break;
                case R.id.imageView02:
                    String img_uri2 = RetrofitFactory.baseUrl + "/QingXiao/" + (mDataset.get(i).getTopicImageList().get(1).getImagePath()).replace('\\', '/');
                    Intent intent2 = new Intent(v.getContext(), ImageViewActivity.class);
                    intent2.putExtra("img_url", img_uri2);
                    startActivity(intent2);
                    break;
                case R.id.imageView03:
                    String img_uri3 = RetrofitFactory.baseUrl + "/QingXiao/" + (mDataset.get(i).getTopicImageList().get(2).getImagePath()).replace('\\', '/');
                    Intent intent3 = new Intent(v.getContext(), ImageViewActivity.class);
                    intent3.putExtra("img_url", img_uri3);
                    startActivity(intent3);
                    break;
                case R.id.imageView04:
                    String img_uri4 = RetrofitFactory.baseUrl + "/QingXiao/" + (mDataset.get(i).getTopicImageList().get(3).getImagePath()).replace('\\', '/');
                    Intent intent4 = new Intent(v.getContext(), ImageViewActivity.class);
                    intent4.putExtra("img_url", img_uri4);
                    startActivity(intent4);
                    break;
                case R.id.imageView05:
                    String img_uri5 = RetrofitFactory.baseUrl + "/QingXiao/" + (mDataset.get(i).getTopicImageList().get(4).getImagePath()).replace('\\', '/');
                    Intent intent5 = new Intent(v.getContext(), ImageViewActivity.class);
                    intent5.putExtra("img_url", img_uri5);
                    startActivity(intent5);
                    break;
                case R.id.imageView06:
                    String img_uri6 = RetrofitFactory.baseUrl + "/QingXiao/" + (mDataset.get(i).getTopicImageList().get(5).getImagePath()).replace('\\', '/');
                    Intent intent6 = new Intent(v.getContext(), ImageViewActivity.class);
                    intent6.putExtra("img_url", img_uri6);
                    startActivity(intent6);
                    break;
                case R.id.imageView07:
                    String img_uri7 = RetrofitFactory.baseUrl + "/QingXiao/" + (mDataset.get(i).getTopicImageList().get(6).getImagePath()).replace('\\', '/');
                    Intent intent7 = new Intent(v.getContext(), ImageViewActivity.class);
                    intent7.putExtra("img_url", img_uri7);
                    startActivity(intent7);
                    break;
                case R.id.imageView08:
                    String img_uri8 = RetrofitFactory.baseUrl + "/QingXiao/" + (mDataset.get(i).getTopicImageList().get(7).getImagePath()).replace('\\', '/');
                    Intent intent8 = new Intent(v.getContext(), ImageViewActivity.class);
                    intent8.putExtra("img_url", img_uri8);
                    startActivity(intent8);
                    break;
                case R.id.imageView09:
                    String img_uri9 = RetrofitFactory.baseUrl + "/QingXiao/" + (mDataset.get(i).getTopicImageList().get(8).getImagePath()).replace('\\', '/');
                    Intent intent9 = new Intent(v.getContext(), ImageViewActivity.class);
                    intent9.putExtra("img_url", img_uri9);
                    startActivity(intent9);
                    break;
                default:
                    break;
            }
        }
    }
}
