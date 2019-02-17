package com.pg.mikszo.friendlyletters.drawing.configurationApp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.pg.mikszo.friendlyletters.R;
import com.pg.mikszo.friendlyletters.drawing.CanvasView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddingShapeView extends CanvasView {
    public AddingShapeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        canvas.drawPath(path, paint);
    }

    public void saveScreenImage() {
        //TODO: save only if something is draw on screen
        try {
            final View view = this;
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
            view.draw(c);

            String imagePath = getPathForNewImage();
            if (imagePath != null && !imagePath.trim().equals("")) {
                File imageFile = new File(imagePath);
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);

                Bitmap transparentBitmap = makeBitmapTransparent(bitmap);
                transparentBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.close();

                Toast.makeText(getContext(), R.string.information_message_saving_new_material_success, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Log.e("[ERROR]", ex.getMessage());
            Toast.makeText(getContext(), R.string.error_message_saving_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private String getPathForNewImage() {
        String appFolder = getAppFolderPath();

        if (appFolder != null && !appFolder.trim().equals("")) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
            String fileName  = getContext().getString(R.string.prefix_shape_file_name) + dateFormat.format(new Date()) + ".png";

            return appFolder + File.separator + fileName;
        } else {
            return null;
        }
    }
}