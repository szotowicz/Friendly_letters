package com.pg.mikszo.friendlyletters.drawing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CanvasView extends View {
    protected Canvas canvas;
    protected Paint paint = new Paint();
    protected Path path = new Path();
    protected float strokeWidth = 25f;
    protected int trackColor = Color.BLACK;
    protected boolean isDrawnSomething = false;
    protected float radiusCursor = 20.0f;
    protected final float possitionForTurningOffCursor = -1.0f;
    protected float xPosition = possitionForTurningOffCursor;
    protected float yPosition = possitionForTurningOffCursor;

    public CanvasView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        paint.setAntiAlias(true);
        paint.setColor(trackColor);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isDrawnSomething = true;
        xPosition = event.getX();
        yPosition = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(xPosition, yPosition);
            case MotionEvent.ACTION_MOVE:
                path.lineTo(xPosition, yPosition);
                break;
            case MotionEvent.ACTION_UP:
                path.lineTo(xPosition + 0.01f, yPosition + 0.01f);
                xPosition = possitionForTurningOffCursor;
                yPosition = possitionForTurningOffCursor;
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        this.canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
    }

    public void cleanScreen() {
        isDrawnSomething = false;
        this.path.reset();
        invalidate();
    }

    public void setTrackColor(int trackColor) {
        this.trackColor = trackColor;
        this.paint.setColor(trackColor);
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        this.paint.setStrokeWidth(strokeWidth);
    }

    public void setRadiusCursor(float radiusCursor) {
        this.radiusCursor = radiusCursor;
    }

    protected Bitmap makeBitmapTransparent(Bitmap bitmap) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        int[] allPixels = new int[width * height];
        bitmap.getPixels(allPixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < width * height; i++) {
            if (allPixels[i] != trackColor) {
                allPixels[i] = Color.alpha(Color.TRANSPARENT);
            }
        }

        Bitmap transparentBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        transparentBitmap.setPixels(allPixels, 0, width, 0, 0, width, height);
        return transparentBitmap;
    }
}