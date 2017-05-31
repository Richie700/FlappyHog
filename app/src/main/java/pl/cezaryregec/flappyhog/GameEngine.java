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

    // Sprites
    public static Sprite mBackground;
    public static Sprite mBottom;
    public static Sprite mLogo;
    public static Sprite mHog;
    public static SparseArray<Sprite> sprites = new SparseArray<Sprite>();

    // Game boundaries
    public static float[] mScreenBoundaries = {
             0.0f, // left
            -1.0f, // top
             0.0f, // right
             1.0f  // bottom
    };

    // Animation
    private static final int JUMP_TIME = 20;
    private static final float MOVEMENT_SPEED = 0.02f;
    private static int animation_timer = -1;

    public static void initEngine(Context context) {
        mContext = context;

        // init GLES20 engine
        mGameView = new FHSurfaceView(mContext);
    }

    public static void initGame() {
        // init textures
        mPlayerAliveTexture = mRenderer.loadTexture(R.drawable.hog, false);
        mPlayerDeadTexture = mRenderer.loadTexture(R.drawable.hog_dead, false);

        initObjects();
    }

    public static void initObjects() {

        mBackground = new Sprite(mRenderer.loadTexture(R.drawable.background, true));

        mBottom = new Sprite(mRenderer.loadTexture(R.drawable.bottom, true));

        mLogo = new Sprite(mRenderer.loadTexture(R.drawable.flappylogo, false));

        mHog = new Sprite(mPlayerAliveTexture);

        defaultState();
    }

    public static void defaultState() {
        mGameState = GAME_NOT_STARTED;

        // Background
        mBackground.textureBlock(0, 0, 1, 1);
        mBackground.scroll = true;
        mBackground.SCROLL_SPEED = new float[] { 0.0015f, 0.0f };

        // Bottom
        mBottom.position = new float[] { 0.0f, -0.85f, 0.0f };
        mBottom.scale = new float[] { 1.0f, 0.25f, 1.0f };
        mBottom.textureBlock(0, 0, 1, 1);
        mBottom.scroll = true;
        mBackground.SCROLL_SPEED = new float[] { 0.003f, 0.0f };

        // Logo
        mLogo.position = new float[] { 0.0f, 0.875f, 0.0f };
        mLogo.scale = new float[] { 0.5f, 0.125f, 1.0f };

        // Player
        mHog.mTextureHandle = mPlayerAliveTexture;

        mHog.rotation = new float[] { 0.0f, 0.0f, 0.0f };
        mHog.scale = new float[] { 0.1f, 0.1f, 1.0f };
        mHog.position = new float[] { 0.0f, 0.1f, 0f };

        mHog.animation_blocks = new int[]{ 2, 1 };
        mHog.animation = true;

        stopAnimation(mHog);
    }

    public static void draw(float[] mMVPMatrix) {

        // Background
        if(mGameState == GAME_OVER) {
            mBackground.scroll = false;
        }
        mBackground.draw(mMVPMatrix);


        // Obstacles
        if(mGameState == GAME_OVER) {
            mBottom.scroll = false;
        }
        mBottom.draw(mMVPMatrix);


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

        getInBoundaries(mHog);

        mHog.draw(mMVPMatrix);

        // Collisions
        detectObstacleCollision();
    }

    private static void getInBoundaries(Sprite player) {
        if(player.position[0] < mScreenBoundaries[0]) {
            player.position[0] = mScreenBoundaries[0];
        }

        if(player.position[0] > mScreenBoundaries[2]) {
            player.position[0] = mScreenBoundaries[2];
        }

        if(player.position[1] < mScreenBoundaries[1]) {
            player.position[1] = mScreenBoundaries[1];
        }

        if(player.position[1] > mScreenBoundaries[3]) {
            player.position[1] = mScreenBoundaries[3];
        }
    }

    private static void detectObstacleCollision() {
        if(mHog.isTouching(mBottom)) {
            mGameState = GAME_OVER;
            mHog.mTextureHandle = mPlayerDeadTexture;
            stopAnimation(mHog);
        }
    }

    public static void tap() {
        if(mGameState == GAME_NOT_STARTED) {
            mGameState = GAME_PLAYING;
        }

        if(mGameState == GAME_PLAYING) {
            mHog.rotation = new float[]{ 0.0f, 0.0f, -30f };

            mHog.movement_speed = new float[]{ 0.0f, 0.0f, 0.0f };
            mHog.rotation_speed = new float[]{ 0.0f, 0.0f, 0.0f };

            animation_timer = 0;
        }


        if(mGameState == GAME_OVER) {
            defaultState();
        }

    }

    public static void stopAnimation(Sprite sprite) {
        sprite.movement_acceleration = new float[] { 0.0f, 0.0f, 0.0f };
        sprite.rotation_acceleration = new float[] { 0.0f, 0.0f, 0.0f };
        sprite.movement_speed = new float[]{ 0.0f, 0.0f, 0.0f };
        sprite.rotation_speed = new float[]{ 0.0f, 0.0f, 0.0f };
    }

    public static void onPause() {
        mGameView.onPause();
    }

    public static void onResume() {
        mGameView.onResume();
    }
}
