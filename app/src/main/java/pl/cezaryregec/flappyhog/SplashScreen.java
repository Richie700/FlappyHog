package pl.cezaryregec.flappyhog;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class SplashScreen extends AppCompatActivity {

    // consts
    private static final float UI_ALPHA_GRADE = 0.01f;
    private static final int UI_ANIMATION_DELAY = 10;

    // This view
    private View mLogoView;

    private boolean isLogoShown = false;

    private final Handler mLogoHandler = new Handler();
    private final Runnable mLogoRunnable = new Runnable() {
        @Override
        public void run() {
            show();
        }
    };
    private final Runnable mPostLogoRunnable = new Runnable() {
        @Override
        public void run() {
            nextScreen();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        GameEngine.updateVersion(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        new Updater().execute();

        show();
    }

    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void show() {
        // Get logo
        mLogoView = findViewById(R.id.logoView);

        // If fully visible
        if(mLogoView.getAlpha() >= 1.0f) {
            // just end the splash screen
            isLogoShown = true;
            mLogoHandler.removeCallbacks(mLogoRunnable);
            mLogoHandler.postDelayed(mPostLogoRunnable, UI_ANIMATION_DELAY);
            return;
        }

        // Add alpha by a grade
        float alpha = mLogoView.getAlpha() + UI_ALPHA_GRADE;
        mLogoView.setAlpha(alpha);

        // Schedule a runnable to display UI elements after a delay
        mLogoHandler.removeCallbacks(mLogoRunnable);
        mLogoHandler.postDelayed(mLogoRunnable, UI_ANIMATION_DELAY);
    }

    private void nextScreen() {
        GameEngine.initEngine(this);

        if(!GameEngine.DIALOG_WAITING) {
            GameEngine.startGame();
            finish();
        }
    }
}
