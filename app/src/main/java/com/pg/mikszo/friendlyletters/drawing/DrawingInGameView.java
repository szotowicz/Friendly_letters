package com.pg.mikszo.friendlyletters.drawing;

import android.content.Context;
import android.util.AttributeSet;

public class DrawingInGameView extends CanvasView {
    public DrawingInGameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public boolean checkCorrectnesOfDrawing() {
        return true;
    }
}