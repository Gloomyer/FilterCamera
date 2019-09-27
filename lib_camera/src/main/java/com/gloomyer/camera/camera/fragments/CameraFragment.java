package com.gloomyer.camera.camera.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gloomyer.camera.camera.GCameraManager;
import com.gloomyer.camera.camera.R;
import com.gloomyer.camera.camera.callback.OnCameraErrorCallback;
import com.gloomyer.camera.camera.callback.ReadConfigCompileCallback;
import com.gloomyer.camera.camera.camera.GCameraApi;
import com.gloomyer.camera.camera.utils.LG;
import com.tbruyelle.rxpermissions2.RxPermissions;

/**
 * @Classname CameraFragment
 * @Description 摄像机Fragment
 * @Date 2019-09-27 13:22
 * @Created by gloomy
 */
public class CameraFragment extends Fragment implements ReadConfigCompileCallback, OnCameraErrorCallback, TextureView.SurfaceTextureListener {

    private static final String TAG = CameraFragment.class.getSimpleName();
    private View root;
    private TextureView mTextureView;
    private GCameraApi mGCameraApi;
    private int previewWidth;
    private int previewHeight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_camera, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTextureView = root.findViewById(R.id.texture_view);
        mTextureView.setSurfaceTextureListener(this);
    }

    //请求权限
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    private void reqPermiss() {
        new RxPermissions(this)
                .requestEachCombined(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(permission -> {
                    if (permission.granted) {
                        //获取摄像头操作API
                        mGCameraApi = GCameraManager.getService().getCameraApi(this);
                        mGCameraApi.setReadConfigCompileCallback(this);
                        mGCameraApi.setOnCameraErrorCallback(this);
                    } else {
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    }
                });
    }

    @Override
    public void compile(GCameraApi cameraApi) {
        cameraApi.open(GCameraApi.LENS_FACING.LENS_FACING_BACK,
                mTextureView.getSurfaceTexture(), previewWidth, previewHeight);
    }

    @Override
    public void onError(int what, String msg, Throwable e) {
        LG.e(TAG, "what:{0}, msg:{1}", what, msg);
        if (e != null) {
            LG.e(TAG, "Error:{0}", e.toString());
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        this.previewWidth = width;
        this.previewHeight = height;
        reqPermiss();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
