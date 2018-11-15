package com.pg.mikszo.friendlyletters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class CanvasView extends View {
    private Canvas canvas;
    private Paint paint = new Paint();
    private Path path = new Path();
    private final float STROKE_WIDTH = 25f;

    public CanvasView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
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
        this.canvas.drawPath(path, paint);
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

    public void cleanScreen() {
        path.reset();
        invalidate();
    }

    public void saveScreenImage() {
        try {
            final View view = this;
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            File root = Environment.getExternalStorageDirectory();
            File appFolder = new File(root.getAbsolutePath() + File.separator + "DCIM" + File.separator + "FriendlyLetters");
            if (!appFolder.exists()) {
                if (!appFolder.mkdirs()){
                    Toast.makeText(getContext(), "[ERROR] Problem with saving the file, check the permissions", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            File imageFile = new File(appFolder + File.separator + "FriendlyLetters.jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
            Toast.makeText(getContext(), "The picture was saved: " + appFolder.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Log.e("[ERROR]", ex.getMessage());
        }
    }
}