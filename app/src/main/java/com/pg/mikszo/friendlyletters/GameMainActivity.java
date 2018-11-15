package com.pg.mikszo.friendlyletters;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class GameMainActivity extends Activity {

    private CanvasView canvasView;

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