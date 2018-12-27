package com.pg.mikszo.friendlyletters.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DrawingInGameView extends CanvasView {
    private Drawable backgroundImage;
    private int backgroundImageLeft;
    private int backgroundImageRight;
    private int backgroundImageTop;
    private int backgroundImageBottom;
    private int backgroundImagePixels = -1;

    public DrawingInGameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackgroundImageDimension();

        try {
            // TODO: from appfolder random file "FLshape_*.png"
            backgroundImage = Drawable.createFromStream(getContext().getAssets().open("FLshape_y.png"), null);
            backgroundImage.setBounds(backgroundImageLeft, backgroundImageTop, backgroundImageRight, backgroundImageBottom);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        backgroundImage.draw(canvas);
        canvas.drawPath(path, paint);
    }

    public boolean checkCorrectnesOfDrawing() {
        return true;
    }

    public void analyzeBackgroundPixels() {
        final View view = this;
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        view.draw(c);

        int pixelsCount = 0;
        for (int i = backgroundImageLeft; i < backgroundImageRight; i++) {
            for (int j = backgroundImageTop; j < backgroundImageBottom; j++) {
                int currentPixel = bitmap.getPixel(i, j);
                if (Color.red(currentPixel) < 50 && Color.red(currentPixel) > 0 &&
                        Color.green(currentPixel) < 50 && Color.green(currentPixel) > 0 &&
                        Color.blue(currentPixel) < 50 && Color.blue(currentPixel) > 0) {
                    pixelsCount++;
                }
            }
        }
        backgroundImagePixels = pixelsCount;
        Toast.makeText(getContext(), "Tło: " + backgroundImagePixels, Toast.LENGTH_SHORT).show();
    }

    public void analyzePixels() {
        final View view = this;
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        view.draw(c);

        int blackPixelsCount = 0;
        int redPixelsCount = 0;
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                //int aa = bitmap.getPixel(i, j);
                int aaR = Color.red(bitmap.getPixel(i, j));
                int aaG = Color.green(bitmap.getPixel(i, j));
                int aaB = Color.blue(bitmap.getPixel(i, j));

                if (Color.red(bitmap.getPixel(i, j)) != 255 || Color.green(bitmap.getPixel(i, j)) != 255 ||
                        Color.blue(bitmap.getPixel(i, j)) != 255) {
                    blackPixelsCount++;
                }
/*
                // Channel 'R' always bigger than 'G' and 'B'
                if (Color.blue(bitmap.getPixel(i, j)) > Color.green(bitmap.getPixel(i, j)) && Color.blue(bitmap.getPixel(i, j)) > Color.red(bitmap.getPixel(i, j))) {
                    redPixelsCount++;
                }
*/
                //if (Color.red(bitmap.getPixel(i, j)) > 240 && Color.green(bitmap.getPixel(i, j)) < 150 && Color.blue(bitmap.getPixel(i, j)) < 150) {

            }
        }
        Toast.makeText(getContext(), "Black: " + blackPixelsCount + " Blue: " + redPixelsCount, Toast.LENGTH_SHORT).show();

    }

    private void setBackgroundImageDimension() {
        // TODO: calculate it
        backgroundImageLeft = 200;
        backgroundImageRight = 600;
        backgroundImageTop = 100;
        backgroundImageBottom = 450;
    }
}