package com.gloomyer.camera.camera.camera.impl2.callback;

import android.annotation.SuppressLint;
import android.hardware.camera2.CameraDevice;

import androidx.annotation.NonNull;

import com.gloomyer.camera.camera.callback.OnActionCallback;

/**
 * @Classname GCamera2OpenCallback
 * @Description 摄像头打开回调
 * @Date 2019-09-27 15:56
 * @Created by gloomy
 */
@SuppressLint("NewApi")
public class GCamera2OpenCallback extends CameraDevice.StateCallback {

    private OnActionCallback<CameraDevice> callback;

    public GCamera2OpenCallback(@NonNull OnActionCallback<CameraDevice> callback) {
        this.callback = callback;
    }

    public void onDestroy() {
        callback = null;
    }

    @Override
    public void onOpened(@NonNull CameraDevice camera) {
        if (callback != null) callback.acion(camera);
    }

    @Override
    public void onDisconnected(@NonNull CameraDevice camera) {
        camera.close();
        if (callback != null) callback.acion(null);
    }

    @Override
    public void onError(@NonNull CameraDevice camera, int error) {
        camera.close();
        if (callback != null) callback.acion(null);

    }

}
