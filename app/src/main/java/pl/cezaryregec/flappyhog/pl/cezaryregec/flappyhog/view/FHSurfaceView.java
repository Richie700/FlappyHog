package pl.cezaryregec.flappyhog.pl.cezaryregec.flappyhog.view;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class FHSurfaceView extends GLSurfaceView {

    private final FHRenderer mRenderer;

    public FHSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);

        mRenderer = new FHRenderer();
        setRenderer(mRenderer);

        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}
