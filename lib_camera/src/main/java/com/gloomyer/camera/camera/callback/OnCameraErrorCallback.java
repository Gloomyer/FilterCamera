package com.gloomyer.camera.camera.callback;

/**
 * @Classname OnCameraErrorCallback
 * @Description 摄像头发生错误的回调
 * @Date 2019-09-27 14:42
 * @Created by gloomy
 */
public interface OnCameraErrorCallback {
    /**
     * 发生了错误
     *
     * @param what 错误码
     * @param msg  错误日志
     * @param e    异常
     */
    void onError(int what, String msg, Throwable e);
}
