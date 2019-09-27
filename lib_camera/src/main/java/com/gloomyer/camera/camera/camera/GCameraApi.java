package com.gloomyer.camera.camera.camera;


import android.graphics.SurfaceTexture;

import com.gloomyer.camera.camera.callback.OnCameraErrorCallback;
import com.gloomyer.camera.camera.callback.ReadConfigCompileCallback;

/**
 * @Classname GCameraApi
 * @Description 相机处理功能接口定义
 * @Date 2019-09-27 13:32
 * @Created by gloomy
 */
public interface GCameraApi {
    String TAG = GCameraApi.class.getSimpleName();

    /**
     * 摄像头方向枚举
     */
    enum LENS_FACING {
        LENS_FACING_FRONT,//前置摄像头
        LENS_FACING_BACK; //后置摄像头
    }

    /**
     * 设置初始化完成回调
     *
     * @param callback 回调
     */
    void setReadConfigCompileCallback(ReadConfigCompileCallback callback);


    /**
     * 设置异常回调
     *
     * @param callback 回调
     */
    void setOnCameraErrorCallback(OnCameraErrorCallback callback);

    /**
     * 打开摄像头
     *
     * @param lensFacing 要打开的摄像头方向
     * @param surface    画布
     * @param w          画布宽度
     * @param h          画布高度
     */
    void open(LENS_FACING lensFacing,
              SurfaceTexture surface, int w, int h);

}
