package pl.cezaryregec.flappyhog;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBar;

public class GameScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

    }


    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
