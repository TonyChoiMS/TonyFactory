package com.example.tony.tonyfactory.utils;

import android.content.Context;
import android.os.PowerManager;

import com.example.tony.tonyfactory.MyApplication;

/**
 * Created by Administrator on 2017-04-17.
 */

public class AlertWakeLock {

    private static PowerManager.WakeLock sCpuWakeLock;

    public static void acquireCpuWakeLock(Context context) {
//        L.i("Acquiring cpu wake lock");
        if (sCpuWakeLock != null) {
            return;
        }

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        sCpuWakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, MyApplication.class.getSimpleName());
        sCpuWakeLock.acquire();
    }

    public static void releaseCpuLock() {
//        L.i("Releasing cpu wake lock");
        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }
}
