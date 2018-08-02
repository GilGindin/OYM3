package com.example.gilvi.oym;

import android.os.Handler;


public class Utils {

    // Delay mechanism
    private static Handler handler;
    private static Runnable runnable;

    public interface DelayCallback{
        void afterDelay();
    }

    public static void delay(int secs, final DelayCallback delayCallback){
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                delayCallback.afterDelay();
            }
        };
        handler.postDelayed(runnable, secs * 1000); // afterDelay will be executed after (secs*1000) milliseconds.
    }

    public static void cancelDelay() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }
}