package pl.cezaryregec.flappyhog;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.view.MotionEvent;
import android.view.View;

import pl.cezaryregec.flappyhog.pl.cezaryregec.flappyhog.view.FHRenderer;
import pl.cezaryregec.flappyhog.pl.cezaryregec.flappyhog.view.FHSurfaceView;

public class GameScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set gles20 view
        setContentView(GameEngine.mGameView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            GameEngine.tap();
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        GameEngine.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        GameEngine.onPause();
    }

    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
