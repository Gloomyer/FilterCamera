package com.gloomyer.camera.camera.camera.impl.info;

import android.util.Range;
import android.util.Size;

/**
 * @Classname GCamera2DeviceInfo
 * @Description 摄像头描述类
 * @Date 2019-09-27 15:43
 * @Created by gloomy
 */
public class GCamera2DeviceInfo {

    private String cameraId;
    private boolean available;
    private boolean isFort;//是否是前置摄像头
    private boolean isBack;//是否是后置摄像头
    private int deviceLeave;
    private int sensorOrientation;
    private Size[] outputSizes;
    private Range<Integer>[] fpsRanges;

    public GCamera2DeviceInfo(String cameraId) {
        this.cameraId = cameraId;
    }

    public String getCameraId() {
        return cameraId;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isFort() {
        return isFort;
    }

    public void setFort(boolean fort) {
        isFort = fort;
    }

    public boolean isBack() {
        return isBack;
    }

    public void setBack(boolean back) {
        isBack = back;
    }

    public void setDeviceLeave(int deviceLeave) {
        this.deviceLeave = deviceLeave;
    }

    public int getDeviceLeave() {
        return deviceLeave;
    }

    public void setSensorOrientation(int sensorOrientation) {
        this.sensorOrientation = sensorOrientation;
    }

    public int getSensorOrientation() {
        return sensorOrientation;
    }

    public void setOutputSizes(Size[] outputSizes) {
        this.outputSizes = outputSizes;
    }

    public Size[] getOutputSizes() {
        return outputSizes;
    }

    public void setFpsRanges(Range<Integer>[] fpsRanges) {
        this.fpsRanges = fpsRanges;
    }

    public Range<Integer>[] getFpsRanges() {
        return fpsRanges;
    }
}
