package com.pg.mikszo.friendlyletters;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.pg.mikszo.friendlyletters.drawing.DrawingInGameView;
import com.pg.mikszo.friendlyletters.settings.Settings;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;

import java.io.IOException;
import java.util.List;

public class GameMainActivity extends BaseActivity {

    private DrawingInGameView drawingInGameView;
    private TextView currentLevelTextView;
    private Settings settings;
    private int currentLevel = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);
        drawingInGameView = findViewById(R.id.drawing_in_game_view);
        currentLevelTextView = findViewById(R.id.game_current_level);

        if (!isAppFolderExists()) {
            copyDefaultImages();
        }

        settings = new SettingsManager(this).getAppSettings();
        String currentLevelText = getResources().getString(R.string.game_level_label) + ": " +
                currentLevel + "/" + settings.numberOfLevels;
        currentLevelTextView.setText(currentLevelText);
        drawingInGameView.setTrackColor(Color.parseColor(settings.trackColor));
    }

    public void cleanScreenOnClick(View view) {
        drawingInGameView.cleanScreen();
    }

    public void analysisScreenOnClick(View view) {
        drawingInGameView.analyzeBackgroundPixels();
    }
}