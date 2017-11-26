package com.example.xpb.qingcongschool.course.resource.download;

/**
 * Created by lenovo on 2017/10/28 0028.
 */

import com.example.xpb.qingcongschool.course.resource.upload.RetrofitCallback;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 扩展OkHttp的请求体，实现上传时的进度提示
 *
 * @param <T>
 */
public final class FileResponseBody<T> extends ResponseBody {
    /**
     * 实际请求体
     */
    private ResponseBody mResponseBody;
    /**
     * 下载回调接口
     */
    private RetrofitCallback<T> mCallback;
    /**
     * BufferedSource
     */
    private BufferedSource mBufferedSource;
    public FileResponseBody(ResponseBody responseBody, RetrofitCallback<T> callback) {
        super();
        this.mResponseBody = responseBody;
        this.mCallback = callback;
    }
    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return mBufferedSource;
    }
    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }
    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }
    /**
     * 回调进度接口
     * @param source
     * @return Source
     */
    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                mCallback.onLoading(mResponseBody.contentLength(), totalBytesRead);
                return bytesRead;
            }
        };
    }
}

        /*作者：南柯的小屋
        链接：http://www.jianshu.com/p/982a005de665
        來源：简书*/