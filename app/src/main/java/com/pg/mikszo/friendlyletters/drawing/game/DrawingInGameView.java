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
package com.pg.mikszo.friendlyletters.drawing.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.pg.mikszo.friendlyletters.drawing.CanvasView;

public class DrawingInGameView extends CanvasView {
    private Drawable materialImage;
    private int materialColor;
    private int backgroundImageLeft;
    private int backgroundImageRight;
    private int backgroundImageTop;
    private int backgroundImageBottom;
    private int backgroundImagePixels = -1;

    public DrawingInGameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        materialImage.draw(canvas);
        canvas.drawPath(path, paint);
        if (xPosition != possitionForTurningOffCursor && yPosition != possitionForTurningOffCursor) {
            canvas.drawCircle(xPosition, yPosition, radiusCursor, paint);
        }
    }

    public void analyzeBackgroundPixels() {
        final View view = this;
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        view.draw(c);

        int materialColorR = Color.red(materialColor);
        int materialColorG = Color.green(materialColor);
        int materialColorB = Color.blue(materialColor);

        int pixelsCount = 0;
        for (int i = backgroundImageLeft; i < backgroundImageRight; i++) {
            for (int j = backgroundImageTop; j < backgroundImageBottom; j++) {
                int currentPixel = bitmap.getPixel(i, j);
                if (Color.red(currentPixel) == materialColorR &&
                        Color.green(currentPixel) == materialColorG &&
                        Color.blue(currentPixel) == materialColorB) {
                    pixelsCount++;
                }
            }
        }
        backgroundImagePixels = pixelsCount;
        //Toast.makeText(getContext(), "Tło: " + backgroundImagePixels, Toast.LENGTH_SHORT).show();
    }

    public int[] analyzeTracePixels() {
        final View view = this;
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        view.draw(c);

        int materialColorR = Color.red(materialColor);
        int materialColorG = Color.green(materialColor);
        int materialColorB = Color.blue(materialColor);
        int trackColorR = Color.red(trackColor);
        int trackColorG = Color.green(trackColor);
        int trackColorB = Color.blue(trackColor);

        int tracePixelsCount = 0;
        int backgroundPixelsCount = 0;
        for (int i = backgroundImageLeft; i < backgroundImageRight; i++) {
            for (int j = backgroundImageTop; j < backgroundImageBottom; j++) {
                int currentPixel = bitmap.getPixel(i, j);
                if (Color.red(currentPixel) == materialColorR &&
                        Color.green(currentPixel) == materialColorG &&
                        Color.blue(currentPixel) == materialColorB) {
                    backgroundPixelsCount++;
                }
                if (Color.red(currentPixel) == trackColorR &&
                        Color.green(currentPixel) == trackColorG &&
                        Color.blue(currentPixel) == trackColorB) {
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
}