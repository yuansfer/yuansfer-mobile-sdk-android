package com.yuansfer.paysdk.okhttp;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class ProgressRequestBody extends RequestBody {
    private IResponseHandler mResponseHandler;
    private RequestBody mRequestBody;
    private BufferedSink mBufferedSink;

    public ProgressRequestBody(RequestBody requestBody, IResponseHandler responseHandler) {
        this.mResponseHandler = responseHandler;
        this.mRequestBody = requestBody;
    }

    public MediaType contentType() {
        return this.mRequestBody.contentType();
    }

    public long contentLength() throws IOException {
        return this.mRequestBody.contentLength();
    }

    public void writeTo(BufferedSink sink) throws IOException {
        if (this.mBufferedSink == null) {
            this.mBufferedSink = Okio.buffer(this.sink(sink));
        }

        this.mRequestBody.writeTo(this.mBufferedSink);
        this.mBufferedSink.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            long bytesWritten = 0L;
            long contentLength = 0L;

            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (this.contentLength == 0L) {
                    this.contentLength = ProgressRequestBody.this.contentLength();
                }

                this.bytesWritten += byteCount;
                ProgressRequestBody.this.mResponseHandler.onProgress(this.bytesWritten, this.contentLength);
            }
        };
    }
}
