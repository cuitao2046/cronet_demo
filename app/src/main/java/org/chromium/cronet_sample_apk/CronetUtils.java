package org.chromium.cronet_sample_apk;

import android.content.Context;
import android.util.Log;

import org.chromium.net.ExperimentalCronetEngine;
import org.chromium.net.UploadDataProviders;
import org.chromium.net.UrlRequest;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.Executor;

public class CronetUtils {
    private static final String TAG = "CronetUtils";

    private static CronetUtils sInstance;

    private ExperimentalCronetEngine mCronetEngine;

    private CronetUtils() {
    }

    public static synchronized CronetUtils getsInstance() {
        if (sInstance == null) {
            sInstance = new CronetUtils();
        }
        return sInstance;
    }

    public synchronized void init(Context context) {
        try {
            File targetDir = new File(context.getCacheDir(), "knet");
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            JSONObject exp = new JSONObject();
            JSONObject bbr = new JSONObject();
            bbr.put("connection_options", "TBBR");
            bbr.put("client_connection_options", "TBBR");
            bbr.put("enable_socket_recv_optimization", true);
            bbr.put("idle_connection_timeout_seconds", 90);
            exp.put("ignore_certificate_errors", true);
            exp.put("QUIC", bbr);
            Log.e(TAG, "experimental options: " + exp.toString());
            String path = targetDir.getAbsolutePath();
            Log.i(TAG, "Prepare to initialize Cronet Engine.");
            if (mCronetEngine == null) {
                ExperimentalCronetEngine.Builder builder = new ExperimentalCronetEngine.Builder(context);
                mCronetEngine = builder.build();
                Log.i(TAG, "Cronet Engine initialized.");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void getHtml(String url, UrlRequest.Callback callback) {
        startWithURL(url, callback);
    }
    public void getHtml(String url, UrlRequest.Callback callback, String postData) {
        startWithURL(url, callback, postData);
    }
    private void startWithURL(String url, UrlRequest.Callback callback) {
        startWithURL(url, callback, null);
    }

    private void startWithURL(String url, UrlRequest.Callback callback, String postData) {
        org.chromium.base.Log.i(TAG, "request url = " + url);
        Executor executor = ThreadPoolManager.getInstance().getExcutor();
        if (callback == null)
            org.chromium.base.Log.i(TAG, "callback is null");
        if (executor == null)
            org.chromium.base.Log.i(TAG, "executor is null");
        UrlRequest.Builder builder = mCronetEngine.newUrlRequestBuilder(url, callback, executor);
        applyPostDataToUrlRequestBuilder(builder, executor, postData);
        builder.build().start();
    }

    private void applyPostDataToUrlRequestBuilder(
            UrlRequest.Builder builder, Executor executor, String postData) {
        if (postData != null && postData.length() > 0) {
            builder.setHttpMethod("POST");
            builder.addHeader("Content-Type", "text/plain; charset=us-ascii");
            builder.setUploadDataProvider(
                    UploadDataProviders.create(postData.getBytes()), executor);
        }
    }
}
