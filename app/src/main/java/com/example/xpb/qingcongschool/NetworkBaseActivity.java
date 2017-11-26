package com.example.xpb.qingcongschool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.xpb.qingcongschool.util.NetworkUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xpb on 2017/3/3.
 */
public class NetworkBaseActivity extends AppCompatActivity {

    public ProgressDialog  progressDialog;
    public Function<Observable,ObservableSource> composeFunction;
    private final long RETRY_TIMES = 1;
    public boolean showLoading = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init(){
        progressDialog = new ProgressDialog(this);
        composeFunction =new Function<Observable, ObservableSource>() {
            @Override
            public ObservableSource apply(Observable observable) throws Exception {
                return observable.retry(RETRY_TIMES)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                if (NetworkUtil.isNetworkAvailable(NetworkBaseActivity.this)) {
                                    if (showLoading) {
                                        if(progressDialog != null && !progressDialog.isShowing()){
                                            progressDialog.show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(NetworkBaseActivity.this, "网络连接异常，请检查网络", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };

    }

    public void setLoadingFlag(boolean show) {
        showLoading = show;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
