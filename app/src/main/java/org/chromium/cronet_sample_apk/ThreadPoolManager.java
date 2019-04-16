package org.chromium.cronet_sample_apk;

import android.os.Build;

import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {

    private ThreadPoolExecutor mtaskExecutor = null;
    private static volatile ThreadPoolManager singleton;
    private static final String TAG = SimpleUrlRequestCallback.class.getSimpleName();

    private ThreadPoolManager() {
        mtaskExecutor =
                new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                        60L, TimeUnit.SECONDS,
                        new SynchronousQueue<Runnable>());
        if (Build.VERSION.SDK_INT >= 9) {
            mtaskExecutor.allowCoreThreadTimeOut(true);
        }
    }

    public void execute(Runnable task) {
        try {
            if (null != task) {
                mtaskExecutor.execute(task);
            }
        } catch (Throwable e) {
            org.chromium.base.Log.i(TAG, e.toString());
        }

    }

    public Executor getExcutor() {
        return mtaskExecutor;
    }

    public static ThreadPoolManager getInstance() {
        if (singleton == null) {
            try {
                synchronized (ThreadPoolManager.class) {
                    if (singleton == null) {
                        singleton = new ThreadPoolManager();
                    }
                }
            } catch (Throwable e) {
                org.chromium.base.Log.i(TAG, e.toString());
            }
        }
        return singleton;
    }
}