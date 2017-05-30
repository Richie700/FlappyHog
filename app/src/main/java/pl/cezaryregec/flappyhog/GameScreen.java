package pl.cezaryregec.flappyhog;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.view.View;

import pl.cezaryregec.flappyhog.pl.cezaryregec.flappyhog.view.FHRenderer;
import pl.cezaryregec.flappyhog.pl.cezaryregec.flappyhog.view.FHSurfaceView;

public class GameScreen extends Activity {
    private FHSurfaceView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FHRenderer.Context = this.getBaseContext();

        // init GLES20 engine
        gameView = new FHSurfaceView(this);
        setContentView(gameView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.onPause();
    }


    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
