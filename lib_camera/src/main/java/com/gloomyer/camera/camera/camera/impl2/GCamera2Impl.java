package com.gloomyer.camera.camera.camera.impl2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Environment;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.gloomyer.camera.camera.callback.OnCameraErrorCallback;
import com.gloomyer.camera.camera.callback.ReadConfigCompileCallback;
import com.gloomyer.camera.camera.camera.GCameraApi;
import com.gloomyer.camera.camera.camera.impl2.callback.GCamera2CaptureCallback;
import com.gloomyer.camera.camera.camera.impl2.callback.GCamera2OpenCallback;
import com.gloomyer.camera.camera.camera.impl2.handler.GCamera2BackgroundHandler;
import com.gloomyer.camera.camera.camera.impl2.info.GCamera2DeviceInfo;
import com.gloomyer.camera.camera.config.GCameraConfig;
import com.gloomyer.camera.camera.utils.LG;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * @Classname GCamera2Impl
 * @Description GCameraApi 使用camera2 的实现类
 * @Date 2019-09-27 13:33
 * @Created by gloomy
 */
@SuppressLint("NewApi")
public class GCamera2Impl implements GCameraApi, LifecycleObserver {

    private Context mContext;
    private CameraManager mCameraManager;

    private boolean isInitConfigComiple;

    private ReadConfigCompileCallback mReadConfigCompileCallback;
    private OnCameraErrorCallback mOnCameraErrorCallback;

    private boolean isOpenCamera; //是否成功的打开了摄像头
    private CameraDevice mCameraDevice; //当前摄像头对象

    private SurfaceTexture mSurfaceTexture;
    private List<GCamera2DeviceInfo> devices;
    private GCamera2OpenCallback mGCamera2OpenCallback;
    private GCamera2BackgroundHandler mBackgroundHandler;
    private GCamera2DeviceInfo currentGCamera2DeviceInfo;
    private GCamera2CaptureCallback mGCamera2CaptureCallback;
    private Surface mPreviewSurface;

    public GCamera2Impl(@NonNull Context ctx, @NonNull LifecycleOwner owner) {
        this.mContext = ctx;
        owner.getLifecycle().addObserver(this);

    }

    @Override
    public void setReadConfigCompileCallback(ReadConfigCompileCallback callback) {
        this.mReadConfigCompileCallback = callback;
        if (isInitConfigComiple) {
            if (mReadConfigCompileCallback != null) {
                mReadConfigCompileCallback.compile(this);
            }
        }
    }

    @Override
    public void setOnCameraErrorCallback(OnCameraErrorCallback callback) {
        this.mOnCameraErrorCallback = callback;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void open(LENS_FACING lensFacing, SurfaceTexture surface, int w, int h) {
        this.mSurfaceTexture = surface;
        if (mSurfaceTexture == null) {
            error(-1, "surface == null", null);
            return;
        }
        //如果没有初始化完成不执行
        if (isInitConfigComiple) {
            currentGCamera2DeviceInfo = getDevice(lensFacing);
            Size previewSize = currentGCamera2DeviceInfo.getOptimalSize(w, h);
            mSurfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            try {
                mCameraManager.openCamera(currentGCamera2DeviceInfo.getCameraId(), mGCamera2OpenCallback, mBackgroundHandler.getHandler());
                isOpenCamera = true;
            } catch (CameraAccessException e) {
                error(-1, "摄像头打开异常!", e);
            }
        } else {
            error(-1, "初始化未完成!", null);
        }
    }

    @Override
    public String capture() {
        File path = mContext.getExternalFilesDir(Environment.DIRECTORY_DCIM);
        path = new File(path, System.currentTimeMillis() + GCameraConfig.DEFAULT_CAPTURE_IMAGE_END_WITH);
        capture(path.getAbsolutePath());
        return path.getAbsolutePath();
    }

    @Override
    public void capture(String path) {
        if (mCameraDevice != null
                && mPreviewSurface != null) {
          //TODO 拍照
        }
    }

    /**
     * 根据方向获取摄像头
     *
     * @param lensFacing 摄像头方向
     * @return 摄像头设备
     */
    private GCamera2DeviceInfo getDevice(LENS_FACING lensFacing) {
        GCamera2DeviceInfo device = devices.get(0);
        for (GCamera2DeviceInfo d : devices) {
            if (lensFacing == LENS_FACING.LENS_FACING_BACK) {
                if (d.isBack()) {
                    device = d;
                    break;
                }
            }
        }
        return device;
    }


    /**
     * 读取摄像头配置
     */
    @SuppressLint("NewApi")
    private void loadConfig() {
        try {
            devices = new LoadConfig(mCameraManager).load();
            isInitConfigComiple = true;
            if (mReadConfigCompileCallback != null) {
                mReadConfigCompileCallback.compile(this);
            }
        } catch (Exception e) {
            error(-1, "摄像头配置读取失败!", e);
            e.printStackTrace();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate(@NonNull LifecycleOwner owner) {
        LG.e(TAG, "onCreate");

        mGCamera2OpenCallback = new GCamera2OpenCallback(camera -> {
            mCameraDevice = camera;
            if (mCameraDevice != null) {
                setCameraPreview();
            }
        });

        mBackgroundHandler = new GCamera2BackgroundHandler();
        mGCamera2CaptureCallback = new GCamera2CaptureCallback();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
            if (mCameraManager != null) {
                loadConfig();
            } else {
                error(-1, "CameraManager获取失败", null);
            }
        } else {
            error(-1, "5.0以下", null);
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy(@NonNull LifecycleOwner owner) {
        LG.e(TAG, "onDestroy");
        mReadConfigCompileCallback = null;
        mOnCameraErrorCallback = null;
        mBackgroundHandler = null;
        mGCamera2CaptureCallback = null;
        if (mGCamera2OpenCallback != null)
            mGCamera2OpenCallback.onDestroy();
        mGCamera2OpenCallback = null;
        owner.getLifecycle().removeObserver(this);
        if (isOpenCamera
                && mCameraDevice != null) {
            mCameraDevice.close();
        }
        mCameraDevice = null;
    }


    /**
     * 设置摄像头预览
     */
    private void setCameraPreview() {
        try {
            CaptureRequest.Builder mPreviewRequestBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewSurface = new Surface(mSurfaceTexture);
            mPreviewRequestBuilder.addTarget(mPreviewSurface);
            mCameraDevice.createCaptureSession(Collections.singletonList(mPreviewSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            // 相机已经关闭
                            if (null == mCameraDevice) return;
                            // 会话准备好后，我们开始显示预览
                            CameraCaptureSession mCaptureSession = session;
                            try {
                                // 自动对焦应
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // 闪光灯
                                //setAutoFlash(mPreviewRequestBuilder);
                                // 开启相机预览并添加事件
                                CaptureRequest mPreviewRequest = mPreviewRequestBuilder.build();
                                //发送请求
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        null, null);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                        }
                    }, mBackgroundHandler.getHandler());
        } catch (Exception e) {
            e.printStackTrace();
            error(-1, "摄像头预览失败！", e);
        }
    }

    /**
     * 回调通知异常产生了
     *
     * @param what 错误码
     * @param msg  消息
     * @param e    异常
     */
    private void error(int what, String msg, Throwable e) {
        if (mOnCameraErrorCallback != null) {
            mOnCameraErrorCallback.onError(what, msg, e);
        }
    }
}
