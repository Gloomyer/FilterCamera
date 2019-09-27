package com.gloomyer.camera.camera;

import android.util.Log;

import com.gloomyer.camera.camera.annotation.ServiceImpl;
import com.gloomyer.camera.camera.api.GCameraService;

/**
 * @Classname GCameraManager
 * @Description 管理器
 * @Date 2019-09-27 11:45
 * @Created by gloomy
 */
public class GCameraManager {
    private static GCameraService mService;

    static {
        ServiceImpl annotation = GCameraService.class.getAnnotation(ServiceImpl.class);
        assert annotation != null;
        Class<? extends GCameraService> clazz = annotation.clazz();
        try {
            mService = clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static GCameraService getService() {
        return mService;
    }
}
