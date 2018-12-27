package com.pg.mikszo.friendlyletters.drawing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.pg.mikszo.friendlyletters.R;

import java.io.File;

public class CanvasView extends View {
    protected Canvas canvas;
    protected Paint paint = new Paint();
    protected Path path = new Path();
    protected final float STROKE_WIDTH = 25f;
    protected int trackColor = Color.BLACK;

    public CanvasView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        paint.setAntiAlias(true);
        paint.setColor(trackColor);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xPosition = event.getX();
        float yPosition = event.getY();

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            path.moveTo(xPosition, yPosition);
        case MotionEvent.ACTION_MOVE:
            path.lineTo(xPosition, yPosition);
            break;
        case MotionEvent.ACTION_UP:
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
        this.path.reset();
        invalidate();
    }

    public void setTrackColor(int trackColor) {
        this.trackColor = trackColor;
        this.paint.setColor(trackColor);
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

    protected String getAppFolderPath() {
        File root = Environment.getExternalStorageDirectory();
        File appFolder = new File(root.getAbsolutePath() + File.separator +
                getContext().getString(R.string.resources_parent_dir_name) +
                File.separator + getContext().getString(R.string.resources_dir_name));
        if (!appFolder.exists()) {
            if (!appFolder.mkdirs()) {
                Toast.makeText(getContext(), getContext().getString(R.string.error_message_saving_failed),
                        Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        return appFolder.getAbsolutePath();
    }
}