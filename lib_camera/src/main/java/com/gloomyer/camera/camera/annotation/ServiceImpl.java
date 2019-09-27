package com.gloomyer.camera.camera.annotation;

import com.gloomyer.camera.camera.api.GCameraService;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Classname ServiceImpl
 * @Description 指定
 * @Date 2019-09-27 12:01
 * @Created by gloomy
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceImpl {
    Class<? extends GCameraService> clazz();
}
