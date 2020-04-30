package com.yuansfer.paysdk.okhttp;

import java.io.File;

public abstract class DownloadResponseHandler {
    public DownloadResponseHandler() {
    }

    public abstract void onFinish(File var1);

    public abstract void onProgress(long var1, long var3);

    public abstract void onFailure(String var1);
}
