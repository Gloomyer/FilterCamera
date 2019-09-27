package com.gloomyer.camera.camera.callback;

import com.gloomyer.camera.camera.camera.GCameraApi;

/**
 * @Classname ReadConfigCompileCallback
 * @Description 摄像头初始化完成回调
 * @Date 2019-09-27 14:31
 * @Created by gloomy
 */
public interface ReadConfigCompileCallback {
    /**
     * 初始化完成
     *
     * @param cameraApi 摄像头操作API
     */
    void compile(GCameraApi cameraApi);
}
