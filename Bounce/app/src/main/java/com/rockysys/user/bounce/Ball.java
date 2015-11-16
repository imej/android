package com.rockysys.user.bounce;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

public class Ball extends TileView {

    private static final String TAG = "Bounching";

    /**
     * Current direction the ball is facing
     */
    private int mDirection = UP;
    private static final int UP = 0;
    private static final int DOWN = 1;

    /**
     * Labels for the drawables that will be loaded into the TileView class
     */
    private static final int RED_STAR = 1;
    private static final int GREEN_STAR = 2;

    /**
     * mMoveDelay: number of milliseconds
     * between snake movements. This will decrease as apples are captured.
     */
    private long mMoveDelay = 600;

    /**
     * mLastMove: Tracks the absolute time when the ball last moved, and it used to determine if a
     * move should be make based on mMoveDelay.
     */
    private long mLastMove;

    /**
     * currentPos: Current position of the ball
     */
    private Coordinate mCurrentPos;

    /**
     * Create a simple handler that we can use to cause animation to happen. We set ourselves as a
     * target and we can use the sleep() function to cause an update/invalidate to occur at a later
     * date.
     */

    private RefreshHandler mRedrawHandler = new RefreshHandler();

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            Ball.this.update();
            Ball.this.invalidate();
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };

    /**
     * Constructs a SnakeView based on inflation from XML
     * 
     * @param context
     * @param attrs
     */
    public Ball(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public Ball(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {

        setFocusable(true);

        Resources r = this.getContext().getResources();

        resetTiles(3);
        loadTile(RED_STAR, r.getDrawable(R.drawable.redstar));
        loadTile(GREEN_STAR, r.getDrawable(R.drawable.greenstar));

    }

    public void startBouncing() {
        mCurrentPos = new Coordinate(7, 7);
        mDirection = UP;
    }

    /**
     * Save game state so that the user does not lose anything if the game process is killed while
     * we are in the background.
     * 
     * @return a Bundle with this view's state
     */
    public Bundle saveState() {
        Bundle map = new Bundle();

        map.putInt("mDirection", Integer.valueOf(mDirection));
        map.putLong("mMoveDelay", Long.valueOf(mMoveDelay));
        map.putInt("mX", mCurrentPos.x);
        map.putInt("mY", mCurrentPos.y);

        return map;
    }

    /**
     * Restore game state if our process is being relaunched
     * 
     * @param icicle a Bundle containing the game state
     */
    public void restoreState(Bundle icicle) {
        mDirection = icicle.getInt("mDirection");
        mMoveDelay = icicle.getLong("mMoveDelay");
        mCurrentPos.x = icicle.getInt("mX");
        mCurrentPos.y = icicle.getInt("mY");
    }

    /**
     * Handles the basic update loop, checking to see if we are in the running state, determining if
     * a move should be made, updating the snake's location.
     */
    public void update() {
        long now = System.currentTimeMillis();

        if (now - mLastMove > mMoveDelay) {
            clearTiles();
            updateWalls();
            updateBall();
            mLastMove = now;
        }
        mRedrawHandler.sleep(mMoveDelay);
    }

    /**
     * Draws some walls.
     */
    private void updateWalls() {
        for (int x = 0; x < mXTileCount; x++) {
            setTile(GREEN_STAR, x, 0);
            setTile(GREEN_STAR, x, mYTileCount - 1);
        }
        for (int y = 1; y < mYTileCount - 1; y++) {
            setTile(GREEN_STAR, 0, y);
            setTile(GREEN_STAR, mXTileCount - 1, y);
        }
    }

    /**
     * Figure out which way the snake is going, see if he's run into anything (the walls, himself,
     * or an apple). If he's not going to die, we then add to the front and subtract from the rear
     * in order to simulate motion. If we want to grow him, we don't subtract from the rear.
     */
    private void updateBall() {
        if (mDirection == UP) {
            if (mCurrentPos.y > 1) {
                mCurrentPos.y--;
            } else {
                mCurrentPos.y++;
                mDirection = DOWN;
            }
        } else {
            if (mCurrentPos.y < mYTileCount - 1) {
                mCurrentPos.y++;
            } else {
                mCurrentPos.y--;
                mDirection = UP;
            }
        }

        setTile(RED_STAR, mCurrentPos.x, mCurrentPos.y);
    }

    /**
     * Simple class containing two integer values and a comparison function. There's probably
     * something I should use instead, but this was quick and easy to build.
     */
    private class Coordinate {
        public int x;
        public int y;

        public Coordinate(int newX, int newY) {
            x = newX;
            y = newY;
        }

        public boolean equals(Coordinate other) {
            if (x == other.x && y == other.y) {
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Coordinate: [" + x + "," + y + "]";
        }
    }

}
