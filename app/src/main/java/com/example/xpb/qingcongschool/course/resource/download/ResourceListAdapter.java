package com.example.xpb.qingcongschool.course.resource.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xpb.qingcongschool.R;
import com.example.xpb.qingcongschool.RetrofitFactory;
import com.example.xpb.qingcongschool.course.resource.comment.ResourceCommentActivity;
import com.example.xpb.qingcongschool.course.resource.upload.RetrofitCallback;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by lenovo on 2017/10/27 0027.
 */

public class ResourceListAdapter extends RecyclerView.Adapter<ResourceListAdapter.RViewHolder> {

    static List<ResourceInfo> filelist;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    public static class RViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int position;
        Call<ResponseBody> callDownloadFile;
        private CardView cardView;
        private ImageView ivFileType;
        private TextView tvFileType;
        private TextView tvFileName;
        private TextView tvUploadTime;
        private TextView tvFileDescription;
        private ImageView ivComment;
        private TextView tvCommentCount;
        private ImageView ivThumbUp;
        private TextView tvThumbUpCount;
        private ImageView ivMenu;
        private Button btDownload;


        RViewHolder(View v) {
            super(v);
            System.out.println(filelist);
            cardView = v.findViewById(R.id.card_view_course_resource);
            ivFileType = v.findViewById(R.id.imageView_fileType);
            tvFileType = v.findViewById(R.id.textView_fileType);
            tvFileName = v.findViewById(R.id.textView_fileName);
            tvUploadTime = v.findViewById(R.id.textView_uploadTime);
            tvFileDescription = v.findViewById(R.id.textView_fileDescription);
            ivComment = v.findViewById(R.id.imageView_resource_comment);
            tvCommentCount = v.findViewById(R.id.textView_comment_count);
            ivThumbUp = v.findViewById(R.id.imageView_thumbUp_resource);
            tvThumbUpCount = v.findViewById(R.id.textView_thumbUp_count);
            ivMenu = v.findViewById(R.id.imageView_menu);
            btDownload = v.findViewById(R.id.button_download);

            cardView.setOnClickListener(this);
            ivComment.setOnClickListener(this);
            ivThumbUp.setOnClickListener(this);
            ivMenu.setOnClickListener(this);
            btDownload.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            position = (int) v.getTag();
            switch (v.getId()) {
                case R.id.card_view_course_resource:
                    Intent intent = new Intent(v.getContext(), ResourceCommentActivity.class);
                    intent.putExtra("courseResourceID", filelist.get(position).getCourseResourceID());
                    v.getContext().startActivity(intent);
                    System.out.println("跳转到资源评论页面");
                    break;
                case R.id.imageView_resource_comment:
                    System.out.println("222");
                    break;
                case R.id.imageView_thumbUp_resource:
                    System.out.println("333");
                    break;
                case R.id.imageView_menu:
                    System.out.println("444");
                    break;
                case R.id.button_download:
                    System.out.println("555");
                    System.out.println("position " + position);
                    downloadFile(v);
                    break;
            }
        }

        private void downloadFile(View v) {
            final ProgressDialog dialog = new ProgressDialog(v.getContext());
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
            dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
            dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
            dialog.setIcon(R.drawable.ic_launcher_24dp);// 设置提示的title的图标，默认是没有的
            dialog.setTitle("下载文件");
            dialog.setMax(100);
            String path = Environment.getExternalStorageDirectory().getPath() + "/Download";
            final File file = new File(path, filelist.get(position).getFileName());
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            callDownloadFile.cancel();
                            if (file.exists()) {
                                file.delete();
                            }
                        }
                    });
            dialog.show();
            RetrofitCallback<ResponseBody> callback = new RetrofitCallback<ResponseBody>() {
                @Override
                public void onSuccess(@NonNull Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        InputStream is = response.body().byteStream();
                        if (file.exists()) {
                            file.delete();
                        }
                        FileOutputStream fos = new FileOutputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(is);
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = bis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                        fos.flush();
                        fos.close();
                        bis.close();
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.i("onSuccess", "文件下载完了");
                    dialog.cancel();
                    final ProgressDialog dialog1 = new ProgressDialog(v.getContext());
                    dialog1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
                    dialog1.setCancelable(false);// 设置是否可以通过点击Back键取消
                    dialog1.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                    dialog1.setIcon(R.drawable.ic_launcher_24dp);// 设置提示的title的图标，默认是没有的
                    dialog1.setTitle("下载完毕");
                    dialog1.setMax(100);
                    dialog1.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog1.cancel();
                        }
                    });
                    dialog1.show();
                    dialog1.setProgress(100);
                    //获取NotificationManager实例
                    NotificationManager notifyManager = (NotificationManager) v.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    //实例化NotificationCompat.Builde并设置相关属性
                    NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(v.getContext())
                            .setSmallIcon(R.drawable.ic_launcher_24dp)
                            //设置通知标题
                            .setContentTitle("下载完成")
                            //设置通知内容
                            .setContentText("内置存储/Download/"+filelist.get(position).getFileName())
                            .setDefaults(Notification.DEFAULT_SOUND);
                    //设置通知时间，默认为系统发出通知的时间，通常不用设置
                    //.setWhen(System.currentTimeMillis());
                    //通过builder.build()方法生成Notification对象,并发送通知,id=1
                    notifyManager.notify(1, builder.build());

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.println("上传下载异常");
                    t.printStackTrace();
                }

                @Override
                public void onLoading(long total, long progress) {
                    //更新下载进度
                    //Log.w("onLoading", progress + "/" + total);
                    dialog.setProgress((int) (progress*100/total));
                }

            };
            System.out.println("下载的文件存储名："+filelist.get(position).getResourceStoreName());
            callDownloadFile = RetrofitFactory.getRetrofitService(callback).download(filelist.get(position).getResourceStoreName());
            callDownloadFile.enqueue(callback);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    ResourceListAdapter(ArrayList<ResourceInfo> list) {
        //构造函数
        filelist = list;
    }

    //加载服务器上获取的数据
    public void setData(List<ResourceInfo> list) {
        filelist = list;
    }

    ;

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public RViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_resource_viewholder, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new RViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.tv_username.setText(mDataset[position]);

        String fileName = filelist.get(position).getFileName();
        String extName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        //LogUtils.d(extName,"test");
        switch (extName) {
            case "wmv":
            case "asf":
            case "asx":
            case "rm":
            case "rmvb":
            case "mpg":
            case "mpeg":
            case "mpe":
            case "3gp":
            case "mov":
            case "mp4":
            case "m4v":
            case "avi":
            case "dat":
            case "mkv":
            case "flv":
            case "vob":
                holder.ivFileType.setBackgroundColor(Color.parseColor("#ffd43a"));
                //视频橘黄色
                break;
            case "cda":
            case "wav":
            case "aiff":
            case "au":
            case "mp3":
            case "mid":
            case "midi":
            case "wma":
            case "vqf":
            case "ogg":
            case "aac":
            case "ape":
                holder.ivFileType.setBackgroundColor(Color.parseColor("#0dc5d2"));
                //音频天蓝色
                break;
            case "pdf":
                holder.ivFileType.setBackgroundColor(Color.parseColor("#ff6762"));
                //PDF粉红色
                break;
            case "doc":
            case "docx":
                holder.ivFileType.setBackgroundColor(Color.parseColor("#4141b4"));
                //DOC深蓝色
                break;
            case "ppt":
            case "pptx":
                holder.ivFileType.setBackgroundColor(Color.parseColor("#a61a1e"));
                //PPT棕红色
                break;
            case "xls":
            case "xlsx":
                holder.ivFileType.setBackgroundColor(Color.parseColor("#14b43c"));
                //xls深绿色
                break;
            default:
                holder.ivFileType.setBackgroundColor(Color.BLACK);//默认黑色
                break;
        }

        holder.tvFileType.setText(extName);
        holder.tvFileName.setText(fileName);
        holder.tvUploadTime.setText(filelist.get(position).getUploadTime());
        holder.tvFileDescription.setText(filelist.get(position).getResourceDescribe());
        holder.tvCommentCount.setText(String.valueOf(filelist.get(position).getCommentTimes()));
        //holder.tvThumbUpCount.setText(filelist.get(position).get);

        holder.itemView.setTag(position);
        holder.cardView.setTag(position);
        holder.ivComment.setTag(position);
        holder.ivThumbUp.setTag(position);
        holder.ivMenu.setTag(position);
        holder.btDownload.setTag(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return filelist.size();
    }
}
