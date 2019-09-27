package com.gloomyer.camera.camera.api.impl;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.gloomyer.camera.camera.api.GCameraService;
import com.gloomyer.camera.camera.camera.GCameraApi;
import com.gloomyer.camera.camera.camera.impl2.GCamera2Impl;
import com.gloomyer.camera.camera.fragments.CameraFragment;

import java.util.Objects;

/**
 * @Classname CameraServiceImpl
 * @Description 接口实现类
 * @Date 2019-09-27 11:47
 * @Created by gloomy
 */
public class CameraServiceImpl implements GCameraService {

    @Override
    public Fragment getCameraFragment(@NonNull FragmentActivity ctx) {
        return new CameraFragment();
    }

    @Override
    public int showCameraFragment(@NonNull FragmentActivity ctx, @IdRes int replaceLayoutId) {
        FragmentTransaction transaction = ctx.getSupportFragmentManager().beginTransaction();
        Fragment fragment = getCameraFragment(ctx);
        transaction.add(replaceLayoutId, fragment, fragment.getClass().getSimpleName());
        transaction.show(fragment);
        return transaction.commit();
    }

    @Override
    public GCameraApi getCameraApi(@NonNull FragmentActivity ctx) {
        return new GCamera2Impl(ctx.getApplicationContext(), ctx);
    }

    @Override
    public GCameraApi getCameraApi(@NonNull Fragment fragment) {
        return new GCamera2Impl(Objects.requireNonNull(fragment.getContext()).getApplicationContext(), fragment);
    }
}
