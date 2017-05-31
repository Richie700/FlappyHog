package pl.cezaryregec.flappyhog.pl.cezaryregec.flappyhog.view;

import android.content.Context;
import android.opengl.GLSurfaceView;

import pl.cezaryregec.flappyhog.GameEngine;

public class FHSurfaceView extends GLSurfaceView {

    public FHSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);
        GameEngine.mRenderer = new FHRenderer();

        setRenderer(GameEngine.mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}
