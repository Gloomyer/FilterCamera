package com.gloomyer.camera.camera.camera.impl2.callback;

import android.annotation.SuppressLint;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;

import androidx.annotation.NonNull;

import com.gloomyer.camera.camera.callback.OnActionCallback;
import com.gloomyer.camera.camera.utils.LG;

/**
 * @Classname GCamera2PreviewCallback
 * @Description 预览回调
 * @Date 2019-09-27 17:01
 * @Created by gloomy
 */
@SuppressLint("NewApi")
public class GCamera2PreviewCallback extends CameraCaptureSession.StateCallback {

    private static final String TAG = GCamera2PreviewCallback.class.getSimpleName();
    private OnActionCallback<CameraCaptureSession> mGCamera2PreviewCallback;

    public void setGCamera2PreviewCallback(@NonNull OnActionCallback<CameraCaptureSession> callback) {
        this.mGCamera2PreviewCallback = callback;
    }

    @Override
    public void onConfigured(@NonNull CameraCaptureSession session) {
        LG.e(TAG, "onConfigured");
        if (mGCamera2PreviewCallback != null) mGCamera2PreviewCallback.acion(session);
    }

    @Override
    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        LG.e(TAG, "onConfigureFailed");
        if (mGCamera2PreviewCallback != null) mGCamera2PreviewCallback.acion(null);
    }
}
