package com.gloomyer.camera.camera.view;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.gloomyer.camera.camera.utils.LG;
import com.gloomyer.camera.camera.utils.GLUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * @Classname GCameraView
 * @Description 摄像机预览view
 * @Date 2019-09-29 09:46
 * @Created by gloomy
 */
public class GCameraView extends GLSurfaceView implements SurfaceTexture.OnFrameAvailableListener {
    private static final String TAG = GCameraView.class.getSimpleName();
    private int mTextureId;
    private SurfaceTexture mSurfaceTexture;

    public GCameraView(Context context) {
        super(context);
        init(context);
    }

    public GCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportES2 = configurationInfo.reqGlEsVersion >= 0x00020000;
        LG.e(TAG, "{0},es:{1}",
                context.getClass().getSimpleName(),
                configurationInfo.reqGlEsVersion);


        setEGLContextClientVersion(2);
        setRenderer(new Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                mTextureId = GLUtils.getExternalOESTextureID();
                mSurfaceTexture = new SurfaceTexture(mTextureId);
                mSurfaceTexture.setOnFrameAvailableListener(GCameraView.this);
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                gl.glViewport(0, 0, width, height);
            }

            @Override
            public void onDrawFrame(GL10 gl) {
                gl.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                GLES20.glClearColor(0, 0, 0, 0);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
                mSurfaceTexture.updateTexImage();
            }
        });
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

    }
}
