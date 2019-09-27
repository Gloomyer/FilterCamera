package com.gloomyer.camera.camera.camera.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.Face;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.text.TextUtils;
import android.util.Range;
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
import com.gloomyer.camera.camera.utils.LG;

import java.util.Arrays;
import java.util.HashMap;


/**
 * @Classname GCamera2Impl
 * @Description GCameraApi 使用camera2 的实现类
 * @Date 2019-09-27 13:33
 * @Created by gloomy
 */
@SuppressLint("NewApi")
public class GCamera2Impl extends CameraDevice.StateCallback implements
        GCameraApi,
        LifecycleObserver {

    private Context mContext;
    private CameraManager mCameraManager;

    private String mFrontCameraId;
    private String mBackCameraId;
    private boolean isInitConfigComiple;

    private ReadConfigCompileCallback mReadConfigCompileCallback;
    private OnCameraErrorCallback mOnCameraErrorCallback;

    private boolean isOpenCamera; //是否成功的打开了摄像头
    private CameraDevice mCameraDevice; //当前摄像头对象

    private HashMap<String, Boolean> availables; //闪光灯支持类标，key为摄像头id
    private Surface surface;

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
    public void open(LENS_FACING lensFacing) {
        if (surface == null) {
            error(-1, "surface == null", null);
            return;
        }
        if (isInitConfigComiple) {
            //如果没有初始化完成不作数
            String openId = lensFacing == LENS_FACING.LENS_FACING_BACK ? mBackCameraId : mFrontCameraId;
            try {
                mCameraManager.openCamera(openId, this, null);
                isOpenCamera = true;
            } catch (CameraAccessException e) {
                error(-1, "摄像头打开异常!", e);
            }
        } else {
            error(-1, "初始化未完成!", null);
        }
    }

    @Override
    public void setSurface(Surface surface) {
        this.surface = surface;
    }


    /**
     * 读取摄像头配置
     */
    @SuppressLint("NewApi")
    private void loadConfig() {
        try {
            String[] cameraIdList = mCameraManager.getCameraIdList();
            for (String cameraId : cameraIdList) {
                availables.put(cameraId, false);
                //摄像头详细
                CameraCharacteristics info = mCameraManager.getCameraCharacteristics(cameraId);

                //摄像头方向
                Integer cameraOrientation = info.get(CameraCharacteristics.LENS_FACING);
                if (cameraOrientation == null) continue;
                if (cameraOrientation == CameraCharacteristics.LENS_FACING_FRONT) {
                    mFrontCameraId = cameraId;
                } else {
                    mBackCameraId = cameraId;
                }

                //摄像头支持级别
                Integer deviceLevel = info.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                if (deviceLevel == null) continue;

                //屏幕方向
                Integer sensorOrientation = info.get(CameraCharacteristics.SENSOR_ORIENTATION);
                if (sensorOrientation == null) {
                    sensorOrientation = 0;
                }

                //摄像机支持的尺寸列表
                StreamConfigurationMap map = info.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) continue;
                Size[] outputSizes = map.getOutputSizes(SurfaceTexture.class);

                //fps
                Range<Integer>[] fpsRanges = info.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);

                // 检查闪光灯是否支持。
                Boolean available = info.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                if (available == null) available = false;
                availables.put(cameraId, available);


                LG.e(TAG, "cameraId:{0},cameraOrientation:{1},sensorOrientation:{2},outputSizes:{3},fpsRanges:{4}",
                        cameraId,
                        cameraOrientation,
                        sensorOrientation,
                        outputSizes,
                        fpsRanges);
                isInitConfigComiple = true;
                if (mReadConfigCompileCallback != null) {
                    mReadConfigCompileCallback.compile(this);
                }
            }
        } catch (Exception e) {
            error(-1, "摄像头配置读取失败!", e);
            e.printStackTrace();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate(@NonNull LifecycleOwner owner) {
        LG.e(TAG, "onCreate");
        availables = new HashMap<>();
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
        availables = null;
        mReadConfigCompileCallback = null;
        mOnCameraErrorCallback = null;
        owner.getLifecycle().removeObserver(this);
        if (isOpenCamera
                && mCameraDevice != null) {
            mCameraDevice.close();
        }
        mCameraDevice = null;
    }

    @Override
    public void onOpened(@NonNull CameraDevice camera) {
        mCameraDevice = camera;
        setCameraPreview();
    }

    @Override
    public void onDisconnected(@NonNull CameraDevice camera) {
        camera.close();
        isOpenCamera = false;
    }

    @Override
    public void onError(@NonNull CameraDevice camera, int error) {
        camera.close();
        isOpenCamera = false;
    }


    /**
     * 设置摄像头预览
     */
    private void setCameraPreview() {
        try {
            CaptureRequest.Builder mPreviewRequestBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);
            mCameraDevice.createCaptureSession(Arrays.asList(surface),
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
                    }, null);
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
