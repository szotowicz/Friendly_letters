package com.pg.mikszo.friendlyletters.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddingShapeView extends CanvasView {
    public AddingShapeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void saveScreenImage() {
        try {
            final View view = this;
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);

            String imagePath = getPathForNewImage();
            if (imagePath != null && !imagePath.trim().equals("")) {
                File imageFile = new File(getPathForNewImage());
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);

                Bitmap transparentBitmap = makeBitmapTransparent(bitmap, Color.WHITE);
                transparentBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.close();
                Toast.makeText(getContext(), "The picture was saved", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Log.e("[ERROR]", ex.getMessage());
        }
    }

    private String getPathForNewImage() {
        String appFolder = getAppFolderPath();

        if (appFolder != null && !appFolder.trim().equals("")) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
            String fileName  = "FLshape_" + dateFormat.format(new Date()) + ".png";

            return appFolder + File.separator + fileName;
        } else {
            return null;
        }
    }
}