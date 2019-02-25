package com.pg.mikszo.friendlyletters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pg.mikszo.friendlyletters.drawing.game.DrawingInGameView;
import com.pg.mikszo.friendlyletters.settings.ColorsManager;
import com.pg.mikszo.friendlyletters.settings.Settings;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

public class GameMainActivity extends Activity {

    private Settings settings;
    private DrawingInGameView drawingInGameView;
    private RelativeLayout gameMainLayout;
    private TextView currentLevelTextView;
    private int currentLevel = 1;
    private int currentNumberOfRepetitions = 1;
    private int currentRandomMaterial = -1;
    private Handler timeLimitHandler = new Handler();
    private Handler delayHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);

        this.settings = new SettingsManager(this).getAppSettings();
        if (!FileHelper.isAppFolderExists(this)) {
            FileHelper.copyDefaultImages(this);
        }

        gameMainLayout = findViewById(R.id.activity_game_main_layout);
        setBackgroundColor();

        drawingInGameView = findViewById(R.id.drawing_in_game_view);
        setDrawingProperties();
        setTouchListener();

        currentLevelTextView = findViewById(R.id.game_current_level);
        setNumberOfLevel();
    }

    public void cleanScreenOnClick(View view) {
        drawingInGameView.cleanScreen();
    }

    public void analysisScreenOnClick(View view) {
        drawingInGameView.analyzeBackgroundPixels();
    }

    private void setBackgroundColor() {
        String[] availableBackgroundColors = settings.backgroundColors;
        int randomBackgroundColor = new Random().nextInt(availableBackgroundColors.length);
        gameMainLayout.setBackgroundColor(
                new ColorsManager(this).getBackgroundColorById(availableBackgroundColors[randomBackgroundColor]));
    }

    private void setDrawingProperties() {
        String[] availableMaterialColors = settings.materialColors;
        int randomMaterialColorID = new Random().nextInt(availableMaterialColors.length);
        int randomMaterialColor = new ColorsManager(this).getMaterialColorById(availableMaterialColors[randomMaterialColorID]);
        drawingInGameView.setMaterialColor(randomMaterialColor);

        String[] availableShapes = settings.availableShapes;
        if (availableShapes.length == 0) {
            Toast.makeText(this, R.string.information_message_lack_of_materials, Toast.LENGTH_SHORT).show();
        } else {
            try {
                int randomAvailableBackground = new Random().nextInt(availableShapes.length);
                if (randomAvailableBackground == currentRandomMaterial) {
                    randomAvailableBackground = new Random().nextInt(availableShapes.length);
                }
                currentNumberOfRepetitions = randomAvailableBackground;
                File randomAvailableBackgroundFile = FileHelper.getAbsolutePathOfFile(
                        availableShapes[randomAvailableBackground], this);

                Drawable randomBackground = Drawable.createFromStream(
                        new FileInputStream(randomAvailableBackgroundFile), null);
                randomBackground.setColorFilter(randomMaterialColor, PorterDuff.Mode.SRC_IN);
                drawingInGameView.setMaterialImage(randomBackground);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        String[] availableTraceColors = settings.traceColors;
        int randomTraceColor = new Random().nextInt(availableTraceColors.length);
        drawingInGameView.setTrackColor(
                new ColorsManager(this).getTraceColorById(availableTraceColors[randomTraceColor]));

        drawingInGameView.setDifficultyLevel(settings.difficultyLevel);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener() {
        drawingInGameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    delayHandler.removeCallbacksAndMessages(null);
                    timeLimitHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gameOver();
                        }
                    }, settings.timeLimit * 1000);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (drawingInGameView.checkCorrectnessOfDrawing()) {
                        loadNextLevel();
                    } else {
                        delayHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                resetCurrentLevel();
                            }
                        }, 2000);
                    }
                }
                return false;
            }
        });
    }

    private void setNumberOfLevel() {
        String currentLevelText = getResources().getString(R.string.game_level_label) + ": " +
                currentLevel + "/" + settings.numberOfLevels;
        currentLevelTextView.setText(currentLevelText);
    }

    private void resetCurrentLevel() {
        timeLimitHandler.removeCallbacksAndMessages(null);
        currentNumberOfRepetitions++;
        if (currentNumberOfRepetitions > settings.numberOfRepetitions) {
            gameOver();
        } else {
            drawingInGameView.cleanScreen();
        }
    }

    private void loadNextLevel() {
        timeLimitHandler.removeCallbacksAndMessages(null);
        currentLevel++;
        if (currentLevel > settings.numberOfLevels) {
            gameOver();
        } else {
            drawingInGameView.cleanScreen();
            setNumberOfLevel();
            setDrawingProperties();
        }
    }

    private void gameOver() {
        //TODO implement
        if (currentLevel > settings.numberOfLevels) {
            Toast.makeText(this, "WINNER!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "GAME OVER", Toast.LENGTH_SHORT).show();
        }
    }
}