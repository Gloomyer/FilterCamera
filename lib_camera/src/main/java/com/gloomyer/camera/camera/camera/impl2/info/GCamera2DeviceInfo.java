package com.gloomyer.camera.camera.camera.impl2.info;

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.util.Range;
import android.util.Size;

import com.gloomyer.camera.camera.utils.LG;

/**
 * @Classname GCamera2DeviceInfo
 * @Description 摄像头描述类
 * @Date 2019-09-27 15:43
 * @Created by gloomy
 */
public class GCamera2DeviceInfo {

    private static final String TAG = GCamera2DeviceInfo.class.getSimpleName();
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

    /**
     * 计算最合适的预览尺寸
     *
     * @param maxWidth  view 宽度
     * @param maxHeight view 高度
     * @return 最合适的尺寸
     */
    @SuppressLint("NewApi")
    public Size getOptimalSize(int maxWidth, int maxHeight) {
        float viewRatio = maxWidth * 1.0f / maxHeight;

        int idx = 0;
        float lasMin = 0;
        for (int i = 0; i < outputSizes.length; i++) {
            Size size = outputSizes[i];

            float ratio = size.getWidth() * 1.0f / size.getHeight();
            float min = Math.abs(viewRatio - ratio);

            if (lasMin <= 0) {
                lasMin = min;
                idx = i;
            } else {
                if (min < lasMin
                        && size.getHeight() <= maxHeight
                        && size.getWidth() <= maxWidth) {
                    if (idx > 0) {
                        if (!(size.getWidth() >= outputSizes[idx].getWidth()
                                && size.getHeight() >= outputSizes[idx].getHeight())) {
                            continue;
                        }
                    }
                    lasMin = 0;
                    idx = i;
                }
            }

        }

        LG.e(TAG, "最合适的预览尺寸:{0}", outputSizes[idx]);
        return outputSizes[idx];
    }
}
