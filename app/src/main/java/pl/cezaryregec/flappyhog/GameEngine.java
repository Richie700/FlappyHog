package pl.cezaryregec.flappyhog;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.util.Random;

import pl.cezaryregec.flappyhog.objects.Number;
import pl.cezaryregec.flappyhog.objects.Sprite;
import pl.cezaryregec.flappyhog.pl.cezaryregec.flappyhog.view.FHRenderer;
import pl.cezaryregec.flappyhog.pl.cezaryregec.flappyhog.view.FHSurfaceView;

public class GameEngine {

    // Updates
    public static String UPDATE_URL = "https://cezaryregec.pl/cdn/flappyhog";
    public static int VERSION_CODE;
    public static boolean DIALOG_WAITING = false;

    // GAME STATES
    public static final int GAME_NOT_READY = -1;
    public static final int GAME_NOT_STARTED = 0;
    public static final int GAME_PLAYING = 1;
    public static final int GAME_OVER = 2;
    public static final int MAX_FLAMES = 4;

    // SOUND CONTSTS
    public static final int MAX_STREAMS = 10;
    public static final float SFX_VOLUME = 0.4f;
    public static final float BG_VOLUME = 0.6f;

    // Touch lock
    public static final int LOCK_FRAMES = 40;
    public static int lock_timer = 0;

    // Scrolling
    public static final float default_scrolling_speed = 0.004f;
    public static float scrolling_speed = default_scrolling_speed;

    public static float scroll_acceleration = 0.001f;
    public static int scroll_grade_acc = 3;

    public static final float maximum_scrolling_speed = default_scrolling_speed + scroll_acceleration * 8;

    // Flame settings
    public static final float default_flame_distance = 0.9f;
    public static final float default_flame_gap = 1.4f;
    public static final float flame_gap_increase = 0.02f;
    public static final float[] flame_default_position = { -0.1f, 0.8f };
    public static final float flame_distance_step = 0.02f;
    public static final float minimum_distance = default_flame_distance - flame_distance_step * 8;

    public static float flame_distance = default_flame_distance;
    public static float flame_gap = default_flame_gap;
    public static float flame_range = 0.5f;
    public static int last_flame = MAX_FLAMES * 2 - 1;

    // Game vars
    public static int mGameState = GAME_NOT_READY;
    public static int score = 0;
    public static int best_score = 0;

    private static Random randomGenerator = new Random();

    // Back-end stuff crucial for displaying anything
    public static Context mContext;
    public static FHSurfaceView mGameView;
    public static FHRenderer mRenderer;

    // Texture handlers
    public static int mPlayerAliveTexture;
    public static int mPlayerDeadTexture;

    public static int mFlameTexture;

    public static int mLogoTexture;
    public static int mScoreStatusTexture;

    public static int mNumberTexture;

    public static int[] ScoreStatusBlocks = { 2, 8 };

    // Sprites
    public static Sprite mBackground;
    public static Sprite mBottom;
    public static Sprite mLogo;
    public static Sprite mHog;
    public static Sprite[] mFlames = new Sprite[MAX_FLAMES * 2];

    public static Number mScore;
    public static Number mBestScore;

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

    // Sound
    public static SoundPool soundPool;
    public static MediaPlayer mp;

    private static boolean isGameOverPlaying = false;

    public static int aPoint;
    public static int aTap;
    public static int aOver;

    public static void startGame() {
        Intent i = new Intent(mContext, GameScreen.class);
        mContext.startActivity(i);
    }

    public static void updateVersion(Context context) {
        mContext = context;

        // get version
        PackageInfo packageInfo = null;
        try {
            packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        VERSION_CODE = packageInfo.versionCode;
    }

    public static void initEngine(Context context) {
        mContext = context;

        // init GLES20 engine
        mGameView = new FHSurfaceView(mContext);

        // init soundpool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder sp21 = new SoundPool.Builder();
            sp21.setMaxStreams(MAX_STREAMS);
            sp21.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
            soundPool = sp21.build();
        } else {
            soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }

        // load high score
        loadScore();
    }

    public static void initGame() {
        // init textures
        mPlayerAliveTexture = mRenderer.loadTexture(R.drawable.hog, false);
        mPlayerDeadTexture = mRenderer.loadTexture(R.drawable.hog_dead, false);

        mFlameTexture = mRenderer.loadTexture(R.drawable.flame_sprite, false);

        mLogoTexture = mRenderer.loadTexture(R.drawable.flappylogo, false);
        mScoreStatusTexture = mRenderer.loadTexture(R.drawable.text_score, false);

        mNumberTexture = mRenderer.loadTexture(R.drawable.numbers, false);

        // init sounds
        aTap = soundPool.load(mContext, R.raw.tap, 1);
        aPoint = soundPool.load(mContext, R.raw.knurpoint, 1);
        aOver = soundPool.load(mContext, R.raw.gameover, 1);

        initObjects();
    }

    public static void initObjects() {

        mBackground = new Sprite(mRenderer.loadTexture(R.drawable.background, true));
        mBottom = new Sprite(mRenderer.loadTexture(R.drawable.bottom, true));
        mLogo = new Sprite(mLogoTexture);
        mHog = new Sprite(mPlayerAliveTexture);

        for(int i = 0; i < MAX_FLAMES * 2; i++) {
            mFlames[i] = new Sprite(mFlameTexture);
        }

        defaultState();
    }

    public static void defaultState() {
        mGameState = GAME_NOT_STARTED;

        score = 0;
        scrolling_speed = default_scrolling_speed;
        flame_distance = default_flame_distance;
        flame_gap = default_flame_gap;

        // Background
        mBackground.textureBlock(0, 0, 1, 1);
        mBackground.scroll = true;
        mBackground.SCROLL_SPEED = new float[] { scrolling_speed/4, 0.0f };

        // Bottom
        mBottom.position = new float[] { 0.0f, -0.85f, 0.0f };
        mBottom.scale = new float[] { 1.0f, 0.25f, 1.0f };
        mBottom.textureBlock(0, 0, 1, 1);
        mBottom.scroll = true;
        mBottom.SCROLL_SPEED = new float[] { scrolling_speed, 0.0f };

        // Logo
        mLogo.mTextureHandle = mLogoTexture;
        mLogo.textureBlock(0, 0, 1, 1);
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

        // Obstacles
        for(int i = 0; i < MAX_FLAMES * 2; i = i + 2) {
            // get a pair of flames
            Sprite flame1 = mFlames[i];
            Sprite flame2 = mFlames[i+1];

            // get random position
            float pos = randomGenerator.nextFloat() * flame_range;

            // flame 1
            flame1.position = new float[]{
                    (- 1.0f - (flame_distance * ((int) i / 2))),
                    flame_default_position[0] - pos,
                    0.0f
            };
            flame1.textureBlock(0, 0, 2, 1); // bottom texture

            flame1.scale = new float[] { 0.3f, 0.6f, 0.0f };
            flame1.collision_margin = new float[]{ 0.16f, 0.1f };

            // flame 2
            flame2.position = new float[]{
                    (- 1.0f - (flame_distance * ((int) i / 2))),
                    flame_default_position[0] + flame_gap - pos,
                    0.0f};
            flame2.textureBlock(1, 0, 2, 1); // top texture

            flame2.scale = new float[] { 0.3f, 0.6f, 0.0f };
            flame2.collision_margin = new float[]{ 0.15f, 0.1f };

            last_flame = i+1;
        }

        //Music
        if(mp != null) {
            mp.stop();
            mp.release();
        }
        isGameOverPlaying = false;
        mp = MediaPlayer.create(mContext, R.raw.ironhog);
        mp.setLooping(true);
        mp.setVolume(BG_VOLUME, BG_VOLUME);
        mp.start();
    }

    public static void draw(float[] mMVPMatrix) {

        // Background
        if(mGameState == GAME_OVER) {
            mBackground.scroll = false;
            mBackground.color = new float[] { 0.8f, 0.2f, 0.2f, 1.0f};
        } else {
            mBackground.color = new float[]{ 1.0f, 1.0f, 1.0f, 1.0f};
        }

        mBackground.draw(mMVPMatrix);


        // Obstacles:

        // Flames
        for(int i = 0; i < mFlames.length; i = i + 2) {
            Sprite flame1 = mFlames[i]; // select flame
            Sprite flame2 = mFlames[i+1]; // select flame

            if (mGameState == GAME_PLAYING) {
                // Scroll
                flame1.target_position = new float[] { 1.2f, 0.0f, 0.0f };
                flame1.movement_speed = new float[] { scrolling_speed * 2, 0.0f, 0.0f };

                flame2.target_position = new float[] { 1.2f, 0.0f, 0.0f };
                flame2.movement_speed = new float[] { scrolling_speed * 2, 0.0f, 0.0f };

                if(flame1.position[0] > 1.0f || flame2.position[0] > 1.0f ) {

                    flame1.position[0] = mFlames[last_flame].position[0] - flame_distance; // move to the right
                    flame2.position[0] = mFlames[last_flame].position[0] - flame_distance; // move to the right

                    float pos = randomGenerator.nextFloat() * flame_range;

                    // set new height
                    flame1.position[1] = flame_default_position[0] - pos;
                    flame2.position[1] = flame_default_position[0] + flame_gap - pos;

                    last_flame = i+1;
                }
            } else {
                flame1.movement_speed = new float[] { 0.0f, 0.0f, 0.0f }; // stop
                flame2.movement_speed = new float[] { 0.0f, 0.0f, 0.0f }; // stop
            }

            flame1.draw(mMVPMatrix);
            flame2.draw(mMVPMatrix);
        }

        // Bottom
        if(mGameState == GAME_OVER) {
            mBottom.scroll = false;
        }
        mBottom.SCROLL_SPEED = new float[] { scrolling_speed, 0.0f };
        mBottom.draw(mMVPMatrix);


        // Logo
        if(!(mGameState == GAME_PLAYING && score == 0)) {
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
            mHog.movement_acceleration = new float[] { 0.0f, -0.0012f, 0.0f };
        }

        getInBoundaries(mHog);

        mHog.draw(mMVPMatrix);

        // Score
        if(mGameState == GAME_OVER) {
            mScore = new Number(score, mNumberTexture);
            mScore.position = new float[] { 0.05f, 0.0f, 0.0f };
            mScore.update(0.2f);
            mScore.draw(mMVPMatrix);

            mBestScore = new Number(best_score, mNumberTexture);
            mBestScore.position = new float[] { 0.01f, 0.25f, 0.0f };
            mBestScore.update(0.1f);
            mBestScore.draw(mMVPMatrix);

            if(score > best_score) {
                best_score = score;
                saveScore();
            }
        }


        // Collisions
        detectObstacleCollision();

        // Score
        detectScore();

        // Touch lock
        if(lock_timer > 0) lock_timer--;
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
        if (mHog.isTouching(mBottom) || isTouchingArray(mHog, mFlames)) {

            mGameState = GAME_OVER;
            mHog.mTextureHandle = mPlayerDeadTexture;
            stopAnimation(mHog);

            // game over
            postGameOver();
        }
    }


    private static boolean isTouchingArray(Sprite obj, Sprite[] objs) {
        for(Sprite sprite : objs) {
            if(obj.isTouching(sprite)) return true;
        }

        return false;
    }

    private static void postGameOver() {
        if(!isGameOverPlaying) {
            lock_timer = LOCK_FRAMES;

            soundPool.play(aOver, SFX_VOLUME, SFX_VOLUME, 1, 0, 1f);

            try {
                if (mp.isPlaying())
                    mp.stop();
            } catch (Exception e) {
            }

            mp = MediaPlayer.create(mContext, R.raw.sadviolin);
            mp.setLooping(false);
            mp.setVolume(BG_VOLUME, BG_VOLUME);
            mp.start();
            isGameOverPlaying = true;

        }
    }


    private static void detectScore() {

        for(int i = 0; i < MAX_FLAMES * 2; i = i + 2) {

            // if flame is behind player
            if(mFlames[i].position[0] > mHog.position[0] + mHog.scale[0]
                    && mFlames[i].position[0] < mHog.position[0] + mHog.scale[0] + scrolling_speed * 2
                    && mGameState == GAME_PLAYING) {

                // add score
                score++;

                Log.i("POINT", "Pos: " + mFlames[i].position[0]);
                Log.i("POINT", "Score: " + score);

                // play sound
                soundPool.play(aPoint, SFX_VOLUME, SFX_VOLUME, 1, 0, 1f);

                // if a score level is achieved
                if(score % scroll_grade_acc == 0) {
                    // get grade
                    int grade = score / scroll_grade_acc;

                    // faster scrolling
                    scrolling_speed += scroll_acceleration;

                    // limit maximum speed, otherwise crashes
                    if(scrolling_speed > maximum_scrolling_speed) {
                        scrolling_speed = maximum_scrolling_speed;
                    }

                    // closer flames
                    flame_distance -= flame_distance_step;

                    // make it playable
                    if(flame_distance <  minimum_distance) {
                        flame_distance = minimum_distance;
                    }

                    // wider gaps
                    flame_gap += flame_gap_increase;

                    // next score status
                    int x = 0;
                    int y = grade;

                    // if out of range in y axis
                    while(y >= ScoreStatusBlocks[1]) {
                        x++;
                        y -= ScoreStatusBlocks[1];
                    }

                    // if out of range in x axis
                    if(x >= ScoreStatusBlocks[0]) {
                        x = ScoreStatusBlocks[0] - 1;
                        y = ScoreStatusBlocks[1] - 1;
                    }

                    // show score status
                    mLogo.textureBlock(x, y, ScoreStatusBlocks[0], ScoreStatusBlocks[1]);
                }
            }
        }
    }

    public static void tap() {
        if(lock_timer > 0) return;

        if(mGameState == GAME_NOT_STARTED) {
            mGameState = GAME_PLAYING;

            mLogo.mTextureHandle = mScoreStatusTexture;
            mLogo.textureBlock(0, 0, ScoreStatusBlocks[0], ScoreStatusBlocks[1]);
        }

        if(mGameState == GAME_PLAYING) {
            soundPool.play(aTap, SFX_VOLUME, SFX_VOLUME, 1, 0, 1f);

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

    public static void saveScore() {
        SharedPreferences prefs = mContext.getSharedPreferences("FlappyHog", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("succness", best_score);
        editor.commit();
    }

    public static void loadScore() {
        SharedPreferences prefs = mContext.getSharedPreferences("FlappyHog", Context.MODE_PRIVATE);
        best_score = prefs.getInt("succness", 0);
    }

    public static void onPause() {
        mGameView.onPause();
        if(mp != null)
            mp.pause();
    }

    public static void onResume() {
        mGameView.onResume();
        if(mp != null)
            mp.start();
    }
}
