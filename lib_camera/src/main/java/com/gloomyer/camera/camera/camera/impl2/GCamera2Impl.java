package com.gloomyer.camera.camera.camera.impl2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.media.ImageWriter;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.gloomyer.camera.camera.callback.OnActionCallback;
import com.gloomyer.camera.camera.callback.OnCameraErrorCallback;
import com.gloomyer.camera.camera.callback.ReadConfigCompileCallback;
import com.gloomyer.camera.camera.camera.GCameraApi;
import com.gloomyer.camera.camera.camera.impl2.callback.GCamera2PreviewCallback;
import com.gloomyer.camera.camera.camera.impl2.callback.GCamera2OpenCallback;
import com.gloomyer.camera.camera.camera.impl2.handler.GCamera2BackgroundHandler;
import com.gloomyer.camera.camera.camera.impl2.info.GCamera2DeviceInfo;
import com.gloomyer.camera.camera.config.GCameraConfig;
import com.gloomyer.camera.camera.utils.LG;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
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
    private GCamera2PreviewCallback mGCamera2PreviewCallback;
    private Surface mPreviewSurface;
    private Size previewSize;
    private CameraCaptureSession mCameraSession;
    private ImageReader mImageReader;

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
            previewSize = currentGCamera2DeviceInfo.getOptimalSize(w, h);
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
            try {
                CaptureRequest.Builder captureBuilder =
                        mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);

                mImageReader.setOnImageAvailableListener(reader -> {
                    Image image = reader.acquireNextImage();
                    if (image != null) {
                        LG.e(TAG, "ImageReader捕捉了一帧图像 在:{0}线程运行!", Thread.currentThread().getName());
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] data = new byte[buffer.remaining()];
                        buffer.get(data);
                        File mImageFile = new File(path);
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(mImageFile);
                            fos.write(data, 0, data.length);
                            LG.e(TAG, "拍照成功！{0}", mImageFile.getAbsolutePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (fos != null) {
                                try {
                                    fos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        image.close();
                    }
                }, mBackgroundHandler.getHandler());

                captureBuilder.addTarget(mImageReader.getSurface());
                mCameraSession.capture(captureBuilder.build(), null, mBackgroundHandler.getHandler());
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        mGCamera2PreviewCallback = new GCamera2PreviewCallback();

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
        mGCamera2PreviewCallback = null;
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


            //preview
            CaptureRequest.Builder previewBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewSurface = new Surface(mSurfaceTexture);
            previewBuilder.addTarget(mPreviewSurface);
            mGCamera2PreviewCallback.setGCamera2PreviewCallback(session -> {
                if (null == mCameraDevice) return;
                if (session != null) {
                    mCameraSession = session;
                    try {
                        LG.e(TAG, "session.isReprocessable():{0}", session.isReprocessable());
                        session.setRepeatingRequest(previewBuilder.build(),
                                null, mBackgroundHandler.getHandler());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }//else 说明走了失败
            });
            mImageReader = ImageReader.newInstance(
                    previewSize.getWidth(),
                    previewSize.getHeight(),
                    ImageFormat.JPEG,
                    2
            );
            mCameraDevice.createCaptureSession(Arrays.asList(mPreviewSurface, mImageReader.getSurface()),
                    mGCamera2PreviewCallback,
                    mBackgroundHandler.getHandler());


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
