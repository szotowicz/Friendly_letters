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

import com.pg.mikszo.friendlyletters.logger.LoggerCSV;
import com.pg.mikszo.friendlyletters.settings.Configuration;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;
import com.pg.mikszo.friendlyletters.views.CanvasView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DrawingInGameView extends CanvasView {
    private Drawable materialImage;
    private Configuration configuration;
    private int currentStep;
    private int currentRepetition;
    private GameMaterial currentMaterial;
    private int materialColor;
    private int backgroundImageLeft;
    private int backgroundImageRight;
    private int backgroundImageTop;
    private int backgroundImageBottom;
    private int backgroundImagePixels = -1;
    private float startPointOffsetWidth = -0.1f;
    private float startPointOffsetHeight = -0.1f;
    public boolean startedFromStartPointOnMark = false;
    public boolean isTouchScreenEnabled = false;

    public DrawingInGameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.configuration = new SettingsManager(context).getActiveConfiguration();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            if (isTouchScreenEnabled) {
                isDrawnSomething = true;
                xPosition = event.getX();
                yPosition = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!startedFromStartPointOnMark) {
                            if (doesStartWell(xPosition, yPosition)) {
                                startedFromStartPointOnMark = true;
                                isPathStarted = true;
                                reportStartDrawing(LoggerCSV.loggerStatus.START_FROM_POINT);
                            } else {
                                reportStartDrawing(LoggerCSV.loggerStatus.START_NOT_FROM_POINT);
                            }
                        }

                        if (startedFromStartPointOnMark && doesDrawWell(xPosition, yPosition, materialColor)) {
                            path.moveTo(xPosition, yPosition);
                        }
                    case MotionEvent.ACTION_MOVE:
                        if (startedFromStartPointOnMark && doesDrawWell(xPosition, yPosition, materialColor)) {
                            if (isPathStarted) {
                                path.lineTo(xPosition, yPosition);
                            } else {
                                path.moveTo(xPosition, yPosition);
                                isPathStarted = true;
                            }
                        } else if (doesStartWell(xPosition, yPosition)) {
                            startedFromStartPointOnMark = true;
                            isPathStarted = true;
                            path.moveTo(xPosition, yPosition);
                        } else {
                            isPathStarted = false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isPathStarted && doesDrawWell(xPosition, yPosition, materialColor)) {
                            path.lineTo(xPosition + 0.01f, yPosition + 0.01f);
                        }
                        isPathStarted = false;
                        break;
                    default:
                        return false;
                }
                invalidate();
            }
            return true;
        } catch (Exception e) {
            isPathStarted = false;
            return false;
        }
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        materialImage.draw(canvas);
        if (isTouchScreenEnabled) {
            canvas.drawPath(path, paint);
            if (isPathStarted) {
                canvas.drawCircle(xPosition, yPosition, radiusCursor, paint);
            }
        }

        if (startPointOffsetWidth > 0.0f && startPointOffsetHeight > 0.0f && !startedFromStartPointOnMark &&
                (configuration.displayStartPoint && !configuration.testMode)) {
            canvas.drawCircle((backgroundImageRight - backgroundImageLeft) * startPointOffsetWidth + backgroundImageLeft,
                    (backgroundImageBottom - backgroundImageTop) * startPointOffsetHeight + backgroundImageTop, 1, paint);
        }
    }

    @Override
    public void cleanScreen() {
        super.cleanScreen();
        startedFromStartPointOnMark = false;
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
        isTouchScreenEnabled = false;
        startedFromStartPointOnMark = false;
        this.materialImage = materialImage;
        this.materialImage.setBounds(backgroundImageLeft, backgroundImageTop, backgroundImageRight, backgroundImageBottom);

        // Some time after loading the picture, drawing should not work
        Thread delayTouches = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(1000);
                } catch (Exception ignored) {

                } finally {
                    isTouchScreenEnabled = true;
                }
            }
        };
        delayTouches.start();
    }

    public void setMaterialColor(int materialColor) {
        this.materialColor = materialColor;
    }

    public void setOffsetOfStartPoint(GameMaterial material) {
        this.currentMaterial = material;
        startPointOffsetWidth = -0.1f;
        startPointOffsetHeight = -0.1f;

        Matcher matcherW = Pattern.compile("W(.*?)W").matcher(material.filename);
        if (matcherW.find()) {
            try {
                int offsetWidth = Integer.parseInt(matcherW.group(1));
                startPointOffsetWidth = offsetWidth / 100f;
            } catch (NumberFormatException ignored) {
            }
        }

        Matcher matcherH = Pattern.compile("H(.*?)H").matcher(material.filename);
        if (matcherH.find()) {
            try {
                int offsetHeight = Integer.parseInt(matcherH.group(1));
                startPointOffsetHeight = offsetHeight / 100f;
            } catch (NumberFormatException ignored) {
            }
        }
    }

    public void setCurrentStepAndRepetition(int currentStep, int currentRepetition) {
        this.currentStep = currentStep;
        this.currentRepetition = currentRepetition;
    }

    public void setBackgroundImageDimension(int left, int top, int right, int bottom) {
        backgroundImageLeft = left;
        backgroundImageTop = top;
        backgroundImageRight = right;
        backgroundImageBottom = bottom;
    }

    private boolean doesDrawWell(float xPos, float yPos, int colorNumber) {
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
                if (Color.red(currentPixel) == Color.red(colorNumber) &&
                        Color.green(currentPixel) == Color.green(colorNumber) &&
                        Color.blue(currentPixel) == Color.blue(colorNumber)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean doesStartWell(float xPos, float yPos) {
        float xStartPoint = (backgroundImageRight - backgroundImageLeft) * startPointOffsetWidth + backgroundImageLeft;
        float yStartPoint = (backgroundImageBottom - backgroundImageTop) * startPointOffsetHeight + backgroundImageTop;

        return (xPos < xStartPoint * 1.1f && xPos > xStartPoint * 0.9f &&
                yPos < yStartPoint * 1.1f && yPos > yStartPoint * 0.9f);
    }

    private void reportStartDrawing(LoggerCSV.loggerStatus status) {
        new LoggerCSV(getContext()).addNewRecord(status,
                currentMaterial, configuration,
                0, 0, 0,
                currentStep, currentRepetition, 0);
    }
}