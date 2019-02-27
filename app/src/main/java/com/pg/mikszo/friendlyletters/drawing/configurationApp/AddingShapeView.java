package com.pg.mikszo.friendlyletters.drawing.configurationApp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.pg.mikszo.friendlyletters.FileHelper;
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
        if (xPosition != thresholdForTurningOffCursor && yPosition != thresholdForTurningOffCursor) {
            canvas.drawCircle(xPosition, yPosition, radiusCursor, paint);
        }
    }

    public void saveScreenImage() {
        if (isDrawnSomething) {
            try {
                final View view = this;
                Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
                view.draw(new Canvas(bitmap));

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
                Toast.makeText(getContext(), R.string.information_message_saving_failed, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), R.string.information_message_new_material_is_not_drawn, Toast.LENGTH_SHORT).show();
        }
    }

    private String getPathForNewImage() {
        String appFolder = FileHelper.getAppFolderPath(getContext()).toString();

        if (appFolder != null && !appFolder.trim().equals("")) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
            String fileName  = getContext().getString(R.string.prefix_shape_file_name) + dateFormat.format(new Date()) + ".png";

            return appFolder + File.separator + fileName;
        } else {
            return null;
        }
    }
}