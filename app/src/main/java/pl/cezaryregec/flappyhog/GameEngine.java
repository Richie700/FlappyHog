package pl.cezaryregec.flappyhog;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.opengl.GLSurfaceView;
import android.util.SparseArray;
import android.util.SparseIntArray;

import pl.cezaryregec.flappyhog.objects.Sprite;
import pl.cezaryregec.flappyhog.pl.cezaryregec.flappyhog.view.FHRenderer;
import pl.cezaryregec.flappyhog.pl.cezaryregec.flappyhog.view.FHSurfaceView;

public class GameEngine {

    // GAME STATES
    public static final int GAME_NOT_STARTED = 0;
    public static final int GAME_PLAYING = 1;
    public static final int GAME_OVER = 2;

    public static int mGameState = GAME_NOT_STARTED;

    // Back-end stuff crucial for displaying anything
    public static Context mContext;
    public static FHSurfaceView mGameView;
    public static FHRenderer mRenderer;

    // Texture handlers
    public static int mPlayerAliveTexture;
    public static int mPlayerDeadTexture;
    public static int mObstacleTexture;

    // Sprites
    public static Sprite mBackground;
    public static Sprite mLogo;
    public static Sprite mHog;
    public static SparseArray<Sprite> sprites = new SparseArray<Sprite>();

    // Animation
    private static final int JUMP_TIME = 20;
    private static final float MOVEMENT_SPEED = 0.02f;
    private static int animation_timer = -1;

    public static void initEngine(Context context) {
        mContext = context;

        // init GLES20 engine
        mGameView = new FHSurfaceView(mContext);
    }

    public static void initObjects() {
        // init textures
        mPlayerAliveTexture = mRenderer.loadTexture(R.drawable.hog, false);
        mPlayerDeadTexture = mRenderer.loadTexture(R.drawable.hog_dead, false);

        mBackground = new Sprite(mRenderer.loadTexture(R.drawable.background, true));
        mBackground.textureBlock(0, 0, 1, 1);
        mBackground.scroll = true;

        mLogo = new Sprite(mRenderer.loadTexture(R.drawable.flappylogo, false));
        mLogo.position = new float[] { 0.0f, 0.875f, 0.0f };
        mLogo.scale = new float[] { 0.5f, 0.125f, 1.0f };

        mHog = new Sprite(mPlayerAliveTexture);
        mHog.scale = new float[] { 0.1f, 0.1f, 1.0f };
        mHog.position = new float[] { 0.0f, 0.1f, 0f };

        mHog.animation_blocks = new int[]{ 2, 1 };
        mHog.animation = true;

        mHog.rotation_acceleration = new float[] { 0.0f, 0.0f, 0.0f };
        mHog.movement_acceleration = new float[] { 0.0f, 0.0f, 0.0f };
    }

    public static void draw(float[] mMVPMatrix) {

        // Background
        if(mGameState == GAME_OVER) {
            mBackground.scroll = false;
        }

        mBackground.draw(mMVPMatrix);


        // Logo
        if(mGameState == GAME_NOT_STARTED) {
            mLogo.draw(mMVPMatrix);
        }


        // Player
        if(mGameState == GAME_PLAYING) {

            if(animation_timer >= 0 && animation_timer <= JUMP_TIME) {
                mHog.position = new float[]{ 0.0f, mHog.position[1] + MOVEMENT_SPEED, 0.0f };
            } else {
                animation_timer = -1;
            }

            mHog.target_rotation = new float[]{ 0.0f, 0.0f, 60.0f };
            mHog.rotation_acceleration = new float[]{ 0.0f, 0.0f, 0.1f };

            mHog.target_position = new float[] { 0.0f, -1.0f, 0.0f };
            mHog.movement_acceleration = new float[] { 0.0f, -0.001f, 0.0f };
        }

        mHog.draw(mMVPMatrix);


        // Obstacles
    }

    public static void tap() {
        if(mGameState == GAME_NOT_STARTED) {
            mGameState = GAME_PLAYING;
        }

        if(mGameState == GAME_OVER) {
            initObjects();
        }

        mHog.rotation = new float[]{ 0.0f, 0.0f, -30f };

        mHog.movement_speed = new float[]{ 0.0f, 0.0f, 0.0f };
        mHog.rotation_speed = new float[]{ 0.0f, 0.0f, 0.0f };

        animation_timer = 0;

    }

    public static void onPause() {
        mGameView.onPause();
    }

    public static void onResume() {
        mGameView.onResume();
    }
}
