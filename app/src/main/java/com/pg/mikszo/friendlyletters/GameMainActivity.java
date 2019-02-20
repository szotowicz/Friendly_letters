package com.pg.mikszo.friendlyletters;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.pg.mikszo.friendlyletters.drawing.game.DrawingInGameView;
import com.pg.mikszo.friendlyletters.settings.ColorsManager;
import com.pg.mikszo.friendlyletters.settings.Settings;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;

public class GameMainActivity extends Activity {

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

        if (!FileHelper.isAppFolderExists(this)) {
            FileHelper.copyDefaultImages(this);
        }

        settings = new SettingsManager(this).getAppSettings();
        String currentLevelText = getResources().getString(R.string.game_level_label) + ": " +
                currentLevel + "/" + settings.numberOfLevels;
        currentLevelTextView.setText(currentLevelText);

        //TODO: random
        String[] availableTraceColors = settings.traceColors;
        drawingInGameView.setTrackColor(new ColorsManager(this).getTraceColor(availableTraceColors[0]));
    }

    public void cleanScreenOnClick(View view) {
        drawingInGameView.cleanScreen();
    }

    public void analysisScreenOnClick(View view) {
        drawingInGameView.analyzeBackgroundPixels();
    }
}