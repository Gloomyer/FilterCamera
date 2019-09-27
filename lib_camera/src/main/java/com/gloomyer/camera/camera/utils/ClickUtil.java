package com.gloomyer.camera.camera.utils;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.NonNull;

import com.jakewharton.rxbinding3.view.RxView;

import java.util.concurrent.TimeUnit;

/**
 * @Classname ClickUtil
 * @Description 点击事件处理工具
 * @Date 2019-09-10 11:33
 * @Created by gloomy
 */
public class ClickUtil {
    private static final long DEFAULT_TIME = 400;

    /**
     * 给view 设置禁止重复点击的事件
     *
     * @param view 要设置的view
     * @param cb   回调
     */
    @SuppressLint("all")
    public static void setClick(long time, @NonNull View view, View.OnClickListener cb) {
        RxView.clicks(view).throttleFirst(time,
                TimeUnit.MILLISECONDS).subscribe(unit -> cb.onClick(view));
    }

    /**
     * 给view 设置禁止重复点击的事件
     *
     * @param view 要设置的view
     * @param cb   回调
     */
    @SuppressLint("all")
    public static void setClick(long time, View view, Runnable cb) {
        if (view == null) return;
        RxView.clicks(view).throttleFirst(time,
                TimeUnit.MILLISECONDS).subscribe(unit -> cb.run());
    }


    /**
     * 给view 设置禁止重复点击的事件
     *
     * @param view 要设置的view
     * @param cb   回调
     */
    public static void setClick(@NonNull View view, View.OnClickListener cb) {
        setClick(DEFAULT_TIME, view, cb);
    }

    /**
     * 给view 设置禁止重复点击的事件
     *
     * @param view 要设置的view
     * @param cb   回调
     */
    public static void setClick(@NonNull View view, Runnable cb) {
        setClick(DEFAULT_TIME, view, cb);
    }

    /**
     * 给view 设置禁止重复点击的事件
     *
     * @param cb    回调
     * @param views 要设置的view
     */
    public static void setClicks(Runnable cb, @NonNull View... views) {
        for (View view : views) {
            setClick(DEFAULT_TIME, view, cb);
        }
    }
}
