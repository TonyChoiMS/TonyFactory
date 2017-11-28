package com.example.tony.tonyfactory.utils;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016-11-28.
 */
public abstract class Dispatch<T> implements Runnable {
    // public static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private boolean done;
    private T result;

    public Dispatch() {
        async();
    }

    public Dispatch(long delay) {
        after(delay);
    }

    public void async() {
        after(0);
    }

    public void after(long delay) {
        if (done) result = null;
        done = false;
        execute(this, delay);
    }

    public void sync() {
        if (done) result = null;
        done = false;
        completed = false;
        blocked = false;
        execute(this, 0);
        block();
    }

    @Override
    public void run() {
        if (!done) {

            try {
                result = onWork();
            } catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            done = true;
            post(this);
        } else {
            callback();
        }
    }

    private boolean blocked;
    public void block() {

        if (isUIThread()) {
            throw new IllegalStateException("Cannot block UI thread.");
        }

        if (completed) return;

        try {
            synchronized(this) {
                blocked = true;
                this.wait(TIMEOUT + 5000);
            }
        } catch(Exception e) {
        }
    }

    private boolean completed;
    void callback() {
        completed = true;

        try {
            onComplete(result);
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        wake();
    }

    public abstract T onWork() throws IOException;
    public abstract void onComplete(T result) throws IOException;

    private void wake() {

        if (!blocked) return;

        synchronized(this) {
            try {
                notifyAll();
            } catch (Exception e) {
            }
        }
    }

    private static int TIMEOUT = 30000;
    //private static int THREAD_POOL = 4;
    public static int THREAD_POOL = Runtime.getRuntime().availableProcessors() * 2;

    private static ScheduledExecutorService fetchExe;
    public static void execute(Runnable job, long delay) {

        if (fetchExe == null) {
            fetchExe = Executors.newScheduledThreadPool(THREAD_POOL);
        }

        if (delay > 0) {
            fetchExe.schedule(job, delay, TimeUnit.MILLISECONDS);
        } else {
            fetchExe.execute(job);
        }
    }

    public static void cancel() {

        if (fetchExe != null) {
            fetchExe.shutdownNow();
            fetchExe = null;
        }
    }

    public static int getActiveCount() {

        int result = 0;

        if (fetchExe instanceof ThreadPoolExecutor) {
            result = ((ThreadPoolExecutor) fetchExe).getActiveCount();
        }

        return result;
    }

    private static Handler handler;
    public static Handler getHandler() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }

    public static void post(Runnable run) {
        getHandler().post(run);
    }

    public static void postDelay(Runnable run, long delay) {
        getHandler().postDelayed(run, delay);
    }

    public static boolean isUIThread() {

        long uiId = Looper.getMainLooper().getThread().getId();
        long cId = Thread.currentThread().getId();

        return uiId == cId;
    }
}

