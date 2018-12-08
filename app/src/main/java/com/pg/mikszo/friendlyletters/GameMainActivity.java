package com.pg.mikszo.friendlyletters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.TextView;

import com.pg.mikszo.friendlyletters.drawing.AddingShapeView;
import com.pg.mikszo.friendlyletters.drawing.DrawingInGameView;
import com.pg.mikszo.friendlyletters.settings.Settings;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;

import java.io.IOException;

public class GameMainActivity extends Activity {

    private DrawingInGameView drawingInGameView;
    private TextView currentLevelTextView;
    private Settings settings;
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);
        drawingInGameView = findViewById(R.id.drawing_in_game_view);
        currentLevelTextView = findViewById(R.id.game_current_level);

        settings = new SettingsManager(this).getSettingsFromJSON();
        currentLevel = 1;

        String currentLevelText = getResources().getString(R.string.game_level_label) + ": " +
                currentLevel + "/" + settings.numberOfLevels;
        currentLevelTextView.setText(currentLevelText);
    }

    public void cleanScreenOnClick(View view) {
        drawingInGameView.cleanScreen();
    }

    public void analysisScreenOnClick(View view) {
        drawingInGameView.analyzeBackgroundPixels();
    }
}