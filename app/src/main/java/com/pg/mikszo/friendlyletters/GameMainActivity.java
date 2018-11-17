package com.pg.mikszo.friendlyletters;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.pg.mikszo.friendlyletters.drawing.AddingShapeView;

public class GameMainActivity extends Activity {

    private AddingShapeView canvasView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);

        canvasView = findViewById(R.id.canvasView);
    }

    public void cleanScreenOnClick(View view) {
        canvasView.cleanScreen();
    }

    public void saveScreenImageOnClick(View view) {
        canvasView.saveScreenImage();
    }
}