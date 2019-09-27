package com.gloomyer.camera.camera.camera.impl2;

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Range;
import android.util.Size;

import androidx.annotation.NonNull;

import com.gloomyer.camera.camera.camera.impl2.info.GCamera2DeviceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname LoadConfig
 * @Description 摄像头配置读取工具类
 * @Date 2019-09-27 15:42
 * @Created by gloomy
 */
class LoadConfig {
    private CameraManager mCameraManager;

    LoadConfig(@NonNull CameraManager mCameraManager) {
        this.mCameraManager = mCameraManager;
    }

    /**
     * 读取属性
     */
    @SuppressLint("NewApi")
    List<GCamera2DeviceInfo> load() throws Exception {
        String[] cameraIdList = mCameraManager.getCameraIdList();
        if (cameraIdList.length == 0) throw new RuntimeException("摄像头列表读取失败！");
        List<GCamera2DeviceInfo> infos = new ArrayList<>();
        for (String cameraId : cameraIdList) {
            GCamera2DeviceInfo info = new GCamera2DeviceInfo(cameraId);
            infos.add(info);

            //摄像头方向
            CameraCharacteristics c = mCameraManager.getCameraCharacteristics(cameraId);
            Integer cameraOrientation = c.get(CameraCharacteristics.LENS_FACING);
            if (cameraOrientation == null) throw new RuntimeException("摄像头方向读取失败！");
            if (cameraOrientation == CameraCharacteristics.LENS_FACING_FRONT) {
                info.setFort(true);
            } else {
                info.setBack(true);
            }

            //摄像头支持级别
            Integer deviceLevel = c.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
            if (deviceLevel == null) throw new RuntimeException("摄像头列表读取失败！");
            info.setDeviceLeave(deviceLevel);


            //传感器方向
            Integer sensorOrientation = c.get(CameraCharacteristics.SENSOR_ORIENTATION);
            if (sensorOrientation == null) {
                sensorOrientation = 0;
            }
            info.setSensorOrientation(sensorOrientation);


            //摄像头支持的尺寸
            StreamConfigurationMap map = c.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (map == null) throw new RuntimeException("摄像头支持的尺寸读取失败！");
            Size[] outputSizes = map.getOutputSizes(SurfaceTexture.class);
            info.setOutputSizes(outputSizes);

            //fps
            Range<Integer>[] fpsRanges = c.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
            info.setFpsRanges(fpsRanges);

            //闪光灯
            Boolean available = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            if (available == null) available = false;
            info.setAvailable(available);
        }

        return infos;
    }
}
