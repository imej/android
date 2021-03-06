package com.rocksys.user.myfirstapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;

/**
 * Background View: Draw 4 full-screen RGBY triangles
 */
public class NewBg extends View {
    private int[] mColors = new int[4];

    private final short[] mIndices = {0,1,2,2,5,6,3,5,7,0,3,4}; //Corner points for triangles (with offset = 3)

    private float[] mVertextPoints = null;

    public NewBg(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);

        // retrieve colors for 4 segments from styleable properties
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BackgroundView);
        mColors[0] = a.getColor(R.styleable.BackgroundView_colorSegmentOne, Color.RED);
        mColors[1] = a.getColor(R.styleable.BackgroundView_colorSegmentTwo, Color.YELLOW);
        mColors[2] = a.getColor(R.styleable.BackgroundView_colorSegmentThree, Color.BLUE);
        mColors[3] = a.getColor(R.styleable.BackgroundView_colorSegmentFour, Color.GREEN);

        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        assert(mVertextPoints != null);

        // Colors for each vertex
        int[] mFillColors = new int[mVertextPoints.length];

        // for (int triangle = 0; triangle < mColors.length; triangle++) {
        for (int triangle = 0; triangle < 4; triangle++) {
            // Set color for all vertex points to current triangle color
            Arrays.fill(mFillColors, mColors[1]);

            // Draw one triangle
            canvas.drawVertices(Canvas.VertexMode.TRIANGLES, mVertextPoints.length, mVertextPoints,
                    0, null, 0, // No Textures
                    mFillColors, 0, mIndices,
                    triangle * 3, 3, // Use 3 vertices via Index Array with offset 2
                    new Paint());
            /**
             * drawVertices() draw the arry of vertices, interpreted as triangles (based on mode)
             */
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Construct our center and four corners
        mVertextPoints = new float[] {
                w/4, h/2,
                0, 0,
                w/2, 0,
                w/2, h,
                0, h,
                w/4*3, h/2,
                w, 0,
                w, h
        };
    }
}
