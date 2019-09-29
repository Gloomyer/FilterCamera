package com.gloomyer.camera.camera.view;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.gloomyer.camera.camera.utils.LG;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * @Classname GCameraView
 * @Description 摄像机预览view
 * @Date 2019-09-29 09:46
 * @Created by gloomy
 */
public class GCameraView extends GLSurfaceView {
    private static final String TAG = GCameraView.class.getSimpleName();

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
                LG.e(TAG, "onSurfaceCreated:ThreadName:{0}", Thread.currentThread().getName());
                gl.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);//清空屏幕的颜色，本例为红色
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                LG.e(TAG, "onSurfaceChanged:ThreadName:{0}", Thread.currentThread().getName());
                gl.glViewport(0, 0, width, height);
            }

            @Override
            public void onDrawFrame(GL10 gl) {
                LG.e(TAG, "onDrawFrame:ThreadName:{0}", Thread.currentThread().getName());
                gl.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            }
        });
    }
}
