package com.yuansfer.paysdk.okhttp;

import android.content.Context;
import android.os.Handler;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody.Builder;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtils {

    private OkHttpClient client;
    private static OkHttpUtils instance;

    public OkHttpUtils(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            client = new OkHttpClient();
        } else {
            client = okHttpClient;
        }
    }

    public static OkHttpUtils get() {
        if (instance == null) {
            instance = new OkHttpUtils(null);
        }

        return instance;
    }

    public static OkHttpUtils initClient(OkHttpClient okHttpClient) {
        if (instance == null) {
            synchronized (OkHttpUtils.class) {
                if (instance == null) {
                    instance = new OkHttpUtils(okHttpClient);
                }
            }
        }
        return instance;
    }

    public void post(String url, Map<String, String> params, IResponseHandler responseHandler) {
        this.post(null, url, params, responseHandler);
    }

    public void post(Context context, String url, Map<String, String> params, IResponseHandler responseHandler) {
        Builder builder = new Builder();
        if (params != null && params.size() > 0) {
            Iterator var6 = params.entrySet().iterator();

            while (var6.hasNext()) {
                Entry<String, String> entry = (Entry) var6.next();
                builder.add(entry.getKey(), getNonString(entry.getValue()));
            }
        }

        Request request;
        if (context == null) {
            request = (new Request.Builder()).url(url).post(builder.build()).build();
        } else {
            request = (new Request.Builder()).url(url).post(builder.build()).tag(context).build();
        }

        this.client.newCall(request).enqueue(new OkHttpUtils.MyCallback(new Handler(), responseHandler));
    }

    public void get(String url, Map<String, String> params, IResponseHandler responseHandler) {
        this.get(null, url, params, responseHandler);
    }

    public void get(Context context, String url, Map<String, String> params, IResponseHandler responseHandler) {
        String get_url = url;
        if (params != null && params.size() > 0) {
            int i = 0;
            Iterator var7 = params.entrySet().iterator();

            while (var7.hasNext()) {
                Entry<String, String> entry = (Entry) var7.next();
                if (i++ == 0) {
                    get_url = get_url + "?" + entry.getKey() + "=" + getNonString(entry.getValue());
                } else {
                    get_url = get_url + "&" + entry.getKey() + "=" + getNonString(entry.getValue());
                }
            }
        }

        Request request;
        if (context == null) {
            request = (new Request.Builder()).url(url).build();
        } else {
            request = (new Request.Builder()).url(url).tag(context).build();
        }

        this.client.newCall(request).enqueue(new OkHttpUtils.MyCallback(new Handler(), responseHandler));
    }

    public void upload(String url, Map<String, File> files, IResponseHandler responseHandler) {
        this.upload(null, url, null, files, responseHandler);
    }

    public void upload(String url, Map<String, String> params, Map<String, File> files, IResponseHandler responseHandler) {
        this.upload(null, url, params, files, responseHandler);
    }

    public void upload(Context context, String url, Map<String, File> files, IResponseHandler responseHandler) {
        this.upload(context, url, null, files, responseHandler);
    }

    public void upload(Context context, String url, Map<String, String> params, Map<String, File> files, IResponseHandler responseHandler) {
        MultipartBody.Builder multipartBuilder = (new MultipartBody.Builder()).setType(MultipartBody.FORM);
        if (params != null && !params.isEmpty()) {
            Iterator var7 = params.keySet().iterator();

            while (var7.hasNext()) {
                String key = (String) var7.next();
                multipartBuilder.addPart(Headers.of(new String[]{"Content-Disposition", "form-data; name=\"" + key + "\""}), RequestBody.create(null, (String) params.get(key)));
            }
        }

        if (files != null && !files.isEmpty()) {
            Iterator var14 = files.keySet().iterator();

            while (var14.hasNext()) {
                String key = (String) var14.next();
                File file = files.get(key);
                String fileName = file.getName();
                RequestBody fileBody = RequestBody.create(MediaType.parse(this.guessMimeType(fileName)), file);
                multipartBuilder.addPart(Headers.of(new String[]{"Content-Disposition", "form-data; name=\"" + key + "\"; filename=\"" + fileName + "\""}), fileBody);
            }
        }

        Request request;
        if (context == null) {
            request = (new Request.Builder()).url(url).post(new ProgressRequestBody(multipartBuilder.build(), responseHandler)).build();
        } else {
            request = (new Request.Builder()).url(url).post(new ProgressRequestBody(multipartBuilder.build(), responseHandler)).tag(context).build();
        }

        this.client.newCall(request).enqueue(new OkHttpUtils.MyCallback(new Handler(), responseHandler));
    }

    public void download(String url, String filedir, String filename, DownloadResponseHandler downloadResponseHandler) {
        this.download(null, url, filedir, filename, downloadResponseHandler);
    }

    public void download(Context context, String url, String filedir, String filename, final DownloadResponseHandler downloadResponseHandler) {
        Request request;
        if (context == null) {
            request = (new Request.Builder()).url(url).build();
        } else {
            request = (new Request.Builder()).url(url).tag(context).build();
        }

        this.client.newBuilder().addNetworkInterceptor(new Interceptor() {
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder().body(new ResponseProgressBody(originalResponse.body(), downloadResponseHandler)).build();
            }
        }).build().newCall(request).enqueue(new OkHttpUtils.MyDownloadCallback(new Handler(), downloadResponseHandler, filedir, filename));
    }

    public void cancel(Context context) {
        if (this.client != null) {
            Iterator var2 = this.client.dispatcher().queuedCalls().iterator();

            Call call;
            while (var2.hasNext()) {
                call = (Call) var2.next();
                if (call.request().tag().equals(context)) {
                    call.cancel();
                }
            }

            var2 = this.client.dispatcher().runningCalls().iterator();

            while (var2.hasNext()) {
                call = (Call) var2.next();
                if (call.request().tag().equals(context)) {
                    call.cancel();
                }
            }
        }

    }

    private File saveFile(Response response, String filedir, String filename) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        FileOutputStream fos = null;

        try {
            is = response.body().byteStream();
            File dir = new File(filedir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, filename);
            fos = new FileOutputStream(file);

            int len;
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }

            fos.flush();
            File var10 = file;
            return var10;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException var20) {
                ;
            }

            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException var19) {
                ;
            }

        }
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }

        return contentTypeFor;
    }

    private class MyCallback implements Callback {
        private Handler mHandler;
        private IResponseHandler mResponseHandler;

        public MyCallback(Handler handler, IResponseHandler responseHandler) {
            this.mHandler = handler;
            this.mResponseHandler = responseHandler;
        }

        public void onFailure(Call call, final IOException e) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    OkHttpUtils.MyCallback.this.mResponseHandler.onFailure(0, e.toString());
                }
            });
        }

        public void onResponse(Call call, final Response response) throws IOException {
            if (response.isSuccessful()) {
                final String response_body = response.body().string();
                if (this.mResponseHandler instanceof JsonResponseHandler) {
                    try {
                        final JSONObject jsonBody = new JSONObject(response_body);
                        this.mHandler.post(new Runnable() {
                            public void run() {
                                ((JsonResponseHandler) OkHttpUtils.MyCallback.this.mResponseHandler).onSuccess(response.code(), jsonBody);
                            }
                        });
                    } catch (JSONException var5) {
                        this.mHandler.post(new Runnable() {
                            public void run() {
                                OkHttpUtils.MyCallback.this.mResponseHandler.onFailure(response.code(), var5.getMessage());
                            }
                        });
                    }
                } else if (this.mResponseHandler instanceof GsonResponseHandler) {
                    this.mHandler.post(new Runnable() {
                        public void run() {
                            try {
                                Gson gson = new Gson();
                                ((GsonResponseHandler) OkHttpUtils.MyCallback.this.mResponseHandler).onSuccess(response.code(), gson.fromJson(response_body, ((GsonResponseHandler) OkHttpUtils.MyCallback.this.mResponseHandler).getType()));
                            } catch (Exception var2) {
                                OkHttpUtils.MyCallback.this.mResponseHandler.onFailure(response.code(), var2.getMessage());
                            }

                        }
                    });
                } else if (this.mResponseHandler instanceof RawResponseHandler) {
                    this.mHandler.post(new Runnable() {
                        public void run() {
                            ((RawResponseHandler) OkHttpUtils.MyCallback.this.mResponseHandler).onSuccess(response.code(), response_body);
                        }
                    });
                }
            } else {
                this.mHandler.post(new Runnable() {
                    public void run() {
                        OkHttpUtils.MyCallback.this.mResponseHandler.onFailure(0, "fail status=" + response.code());
                    }
                });
            }

        }
    }

    private static String getNonString(String str) {
        return str == null ? "" : str;
    }

    private class MyDownloadCallback implements Callback {
        private Handler mHandler;
        private DownloadResponseHandler mDownloadResponseHandler;
        private String mFileDir;
        private String mFilename;

        public MyDownloadCallback(Handler handler, DownloadResponseHandler downloadResponseHandler, String filedir, String filename) {
            this.mHandler = handler;
            this.mDownloadResponseHandler = downloadResponseHandler;
            this.mFileDir = filedir;
            this.mFilename = filename;
        }

        public void onFailure(Call call, final IOException e) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    OkHttpUtils.MyDownloadCallback.this.mDownloadResponseHandler.onFailure(e.toString());
                }
            });
        }

        public void onResponse(Call call, final Response response) throws IOException {
            if (response.isSuccessful()) {
                File file;

                try {
                    file = OkHttpUtils.this.saveFile(response, this.mFileDir, this.mFilename);
                } catch (final IOException var5) {
                    this.mHandler.post(new Runnable() {
                        public void run() {
                            OkHttpUtils.MyDownloadCallback.this.mDownloadResponseHandler.onFailure("onResponse saveFile fail." + var5.toString());
                        }
                    });
                    return;
                }

                final File finalFile = file;
                this.mHandler.post(new Runnable() {
                    public void run() {
                        OkHttpUtils.MyDownloadCallback.this.mDownloadResponseHandler.onFinish(finalFile);
                    }
                });
            } else {
                this.mHandler.post(new Runnable() {
                    public void run() {
                        OkHttpUtils.MyDownloadCallback.this.mDownloadResponseHandler.onFailure("fail status=" + response.code());
                    }
                });
            }

        }
    }
}
