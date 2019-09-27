package com.gloomyer.camera.camera.camera.impl.handler;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * @Classname GCamera2BackgroundHandler
 * @Description 后台处理线程
 * @Date 2019-09-27 16:06
 * @Created by gloomy
 */
public class GCamera2BackgroundHandler {
    private static final String TAG = GCamera2BackgroundHandler.class.getSimpleName();
    private final Handler mHandler;

    public GCamera2BackgroundHandler() {
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
    }

    public Handler getHandler() {
        return mHandler;
    }
}
