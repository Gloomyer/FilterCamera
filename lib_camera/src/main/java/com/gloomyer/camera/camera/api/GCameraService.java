package com.gloomyer.camera.camera.api;


import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.gloomyer.camera.camera.annotation.ServiceImpl;
import com.gloomyer.camera.camera.api.impl.CameraServiceImpl;
import com.gloomyer.camera.camera.camera.GCameraApi;

/**
 * @Classname GCameraService
 * @Description 对外暴露的接口
 * @Date 2019-09-27 11:45
 * @Created by gloomy
 */
@ServiceImpl(clazz = CameraServiceImpl.class)
public interface GCameraService {
    /**
     * 获取摄像机Fragment
     *
     * @param ctx 上下文
     * @return 摄像机Fragment
     */
    Fragment getCameraFragment(@NonNull FragmentActivity ctx);

    /**
     * 展示摄像机Fragment
     *
     * @param ctx 上下文
     * @return Returns the identifier of this transaction's back stack entry,
     * if {@link #androidx.fragment.app.FragmentTransaction.addToBackStack(String)}
     * had been called.  Otherwise, returns
     * a negative number.
     */
    int showCameraFragment(@NonNull FragmentActivity ctx, @IdRes int replaceLayoutId);


    /**
     * 获取摄像机操作接口
     *
     * @param ctx activity
     * @return 摄像机操作接口
     */
    GCameraApi getCameraApi(@NonNull FragmentActivity ctx);

    /**
     * 获取摄像机操作接口
     *
     * @param fragment fragment
     * @return 摄像机操作接口
     */
    GCameraApi getCameraApi(@NonNull Fragment fragment);
}
