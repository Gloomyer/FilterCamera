package com.gloomyer.camera.camera.camera.impl2.callback;

import android.annotation.SuppressLint;
import android.hardware.camera2.CameraCaptureSession;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * @Classname GCamera2CaptureCallback
 * @Description 拍照回调
 * @Date 2019-09-27 17:01
 * @Created by gloomy
 */
@SuppressLint("NewApi")
public class GCamera2CaptureCallback extends CameraCaptureSession.StateCallback {
    @Override
    public void onConfigured(@NonNull CameraCaptureSession session) {

    }

    @Override
    public void onConfigureFailed(@NonNull CameraCaptureSession session) {

    }
}
