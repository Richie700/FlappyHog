package pl.cezaryregec.flappyhog;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class SplashScreen extends AppCompatActivity {

    private static final float UI_ALPHA_GRADE = 0.01f;
    private static final int UI_ANIMATION_DELAY = 10;

    private View mContentView;
    private View mControlsView;
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

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        mContentView = findViewById(R.id.splash_screen);

        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(isLogoShown) {
            onBackPressed();
        }

        show();
    }


    private void show() {
        mLogoView = findViewById(R.id.logoView);

        if(mLogoView == null || mLogoView.getAlpha() == 1.0f) {
            isLogoShown = true;
            mLogoHandler.removeCallbacks(mLogoRunnable);
            return;
        }

        float alpha = mLogoView.getAlpha() + UI_ALPHA_GRADE;

        mLogoView.setAlpha(alpha);

        // Schedule a runnable to display UI elements after a delay
        mLogoHandler.removeCallbacks(mLogoRunnable);
        mLogoHandler.postDelayed(mLogoRunnable, UI_ANIMATION_DELAY);
    }

    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
