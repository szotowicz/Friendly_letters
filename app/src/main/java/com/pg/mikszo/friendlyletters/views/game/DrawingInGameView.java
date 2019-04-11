/*
 ******************************************************************************************
 *
 *    Part of the master's thesis
 *    Topic: "Supporting the development of fine motor skills in children using IT tools"
 *
 *    FRIENDLY LETTERS created by Mikolaj Szotowicz : https://github.com/szotowicz
 *
 ****************************************************************************************
 */
package com.pg.mikszo.friendlyletters.views.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.pg.mikszo.friendlyletters.views.CanvasView;

public class DrawingInGameView extends CanvasView {
    private Drawable materialImage;
    private int materialColor;
    private int backgroundImageLeft;
    private int backgroundImageRight;
    private int backgroundImageTop;
    private int backgroundImageBottom;
    private int backgroundImagePixels = -1;
    private boolean isPathStarted = false;

    public DrawingInGameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isDrawnSomething = true;
        xPosition = event.getX();
        yPosition = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (doesDrawWell(xPosition, yPosition)) {
                    path.moveTo(xPosition, yPosition);
                    isPathStarted = true;
                }
            case MotionEvent.ACTION_MOVE:
                if (doesDrawWell(xPosition, yPosition)) {
                    if (isPathStarted) {
                        path.lineTo(xPosition, yPosition);
                    } else {
                        path.moveTo(xPosition, yPosition);
                        isPathStarted = true;
                    }
                } else {
                    isPathStarted = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (doesDrawWell(xPosition, yPosition)) {
                    path.lineTo(xPosition + 0.01f, yPosition + 0.01f);
                }
                xPosition = positionForTurningOffCursor;
                yPosition = positionForTurningOffCursor;
                isPathStarted = false;
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        materialImage.draw(canvas);
        canvas.drawPath(path, paint);
        if (xPosition != positionForTurningOffCursor && yPosition != positionForTurningOffCursor) {
            canvas.drawCircle(xPosition, yPosition, radiusCursor, paint);
        }
    }

    public void analyzeBackgroundPixels() {
        final View view = this;
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        view.draw(c);

        int pixelsCount = 0;
        for (int x = backgroundImageLeft; x < backgroundImageRight; x++) {
            for (int y = backgroundImageTop; y < backgroundImageBottom; y++) {
                int currentPixel = bitmap.getPixel(x, y);
                if (Color.red(currentPixel) == Color.red(materialColor) &&
                        Color.green(currentPixel) == Color.green(materialColor) &&
                        Color.blue(currentPixel) == Color.blue(materialColor)) {
                    pixelsCount++;
                }
            }
        }
        backgroundImagePixels = pixelsCount;
        //Toast.makeText(getContext(), "Background: " + backgroundImagePixels, Toast.LENGTH_SHORT).show();
    }

    public int[] analyzeTracePixels() {
        final View view = this;
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        view.draw(c);

        int tracePixelsCount = 0;
        int backgroundPixelsCount = 0;
        for (int i = backgroundImageLeft; i < backgroundImageRight; i++) {
            for (int j = backgroundImageTop; j < backgroundImageBottom; j++) {
                int currentPixel = bitmap.getPixel(i, j);
                if (Color.red(currentPixel) == Color.red(materialColor) &&
                        Color.green(currentPixel) == Color.green(materialColor) &&
                        Color.blue(currentPixel) == Color.blue(materialColor)) {
                    backgroundPixelsCount++;
                }
                if (Color.red(currentPixel) == Color.red(traceColor) &&
                        Color.green(currentPixel) == Color.green(traceColor) &&
                        Color.blue(currentPixel) == Color.blue(traceColor)) {
                    tracePixelsCount++;
                }
            }
        }
        // Toast.makeText(getContext(), "Background: " + backgroundPixelsCount + " Trace: " + tracePixelsCount, Toast.LENGTH_LONG).show();
        return new int[]{tracePixelsCount, backgroundPixelsCount};
    }

    public int getBackgroundImagePixels() {
        return backgroundImagePixels;
    }

    public void setMaterialImage(Drawable materialImage) {
        this.materialImage = materialImage;
        this.materialImage.setBounds(backgroundImageLeft, backgroundImageTop, backgroundImageRight, backgroundImageBottom);
    }

    public void setMaterialColor(int materialColor) {
        this.materialColor = materialColor;
    }

    public void setBackgroundImageDimension(int left, int top, int right, int bottom) {
        backgroundImageLeft = left;
        backgroundImageTop = top;
        backgroundImageRight = right;
        backgroundImageBottom = bottom;
    }

    private boolean doesDrawWell(float xPos, float yPos) {
        final View view = this;
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        view.draw(c);

        final float accuracy = 1.0f;
        int startX = (int)(xPos - accuracy * strokeWidth);
        int startY = (int)(yPos - accuracy * strokeWidth);
        int endX = (int)(xPos + accuracy * strokeWidth);
        int endY = (int)(yPos + accuracy * strokeWidth);
        if (startX < 0) {
            startX = 0;
        }
        if (startY < 0) {
            startY = 0;
        }
        if (endX > bitmap.getWidth()) {
            endX = bitmap.getWidth();
        }
        if (endY > bitmap.getHeight()) {
            endX = bitmap.getHeight();
        }

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                int currentPixel = bitmap.getPixel(x, y);
                if (Color.red(currentPixel) == Color.red(materialColor) &&
                        Color.green(currentPixel) == Color.green(materialColor) &&
                        Color.blue(currentPixel) == Color.blue(materialColor)) {
                    return true;
                }
            }
        }

        return false;
    }
}