package pl.cezaryregec.flappyhog.pl.cezaryregec.flappyhog.view;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class FHRenderer implements GLSurfaceView.Renderer {
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 unused) {

    }
}
