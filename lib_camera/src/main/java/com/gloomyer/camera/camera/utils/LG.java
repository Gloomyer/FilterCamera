package com.gloomyer.camera.camera.utils;

import android.util.Log;

import com.gloomyer.camera.camera.BuildConfig;

import java.text.MessageFormat;

/**
 * @Classname LG
 * @Description log 工具类
 * @Date 2019-09-27 13:38
 * @Created by gloomy
 */
public class LG {
    public static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = LG.class.getSimpleName();

    public static void e(String tag, String format, Object... args) {
        if (DEBUG) {
            Log.e(tag, args.length == 0 ? format : MessageFormat.format(format, args));
        }
    }

    public static void e(String format, Object... args) {
        e(TAG, format, args);
    }
}
