package com.pg.mikszo.friendlyletters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
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
    private Handler delayResetHandler = new Handler();
    private Handler delayCheckingHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_main);

        settings = new SettingsManager(this).getAppSettings();
        if (!FileHelper.isAppFolderExists(this)) {
            FileHelper.copyDefaultImages(this);
            settings = new SettingsManager(this).updateSettingsAvailableShapes(settings);
        }

        gameMainLayout = findViewById(R.id.activity_game_main_layout);
        setBackgroundColor();

        drawingInGameView = findViewById(R.id.drawing_in_game_view);
        drawingInGameView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        drawingInGameView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        drawingInGameView.analyzeBackgroundPixels();
                    }
                });
        setDrawingProperties();
        setTouchListener();

        currentLevelTextView = findViewById(R.id.game_current_level);
        setNumberOfLevel();
    }

    public void gameExitOnClick(View view) {
        drawingInGameView.cleanScreen();
        //TODO:
        // 1. Is it needed?
        // 2. Ask about exit
    }

    public void analysisScreenOnClick(View view) {
        drawingInGameView.analyzeTracePixels();
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

        String[] availableTraceColors = settings.traceColors;
        int randomTraceColor = new Random().nextInt(availableTraceColors.length);
        drawingInGameView.setTrackColor(
                new ColorsManager(this).getTraceColorById(availableTraceColors[randomTraceColor]));

        String[] availableShapes = settings.availableShapes;
        if (availableShapes.length == 0) {
            Toast.makeText(this, R.string.information_message_lack_of_materials, Toast.LENGTH_SHORT).show();
        } else {
            try {
                int randomAvailableBackground = new Random().nextInt(availableShapes.length);
                if (randomAvailableBackground == currentRandomMaterial) {
                    randomAvailableBackground = new Random().nextInt(availableShapes.length);
                }
                currentRandomMaterial = randomAvailableBackground;
                File randomAvailableBackgroundFile = FileHelper.getAbsolutePathOfFile(
                        availableShapes[randomAvailableBackground], this);

                if (randomAvailableBackgroundFile == null) {
                    settings = new SettingsManager(this).updateSettingsAvailableShapes(settings);
                    setDrawingProperties();
                    return;
                }

                Drawable randomBackground = Drawable.createFromStream(
                        new FileInputStream(randomAvailableBackgroundFile), null);
                randomBackground.setColorFilter(randomMaterialColor, PorterDuff.Mode.SRC_IN);
                drawingInGameView.setMaterialImage(randomBackground);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener() {
        drawingInGameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    delayCheckingHandler.removeCallbacksAndMessages(null);
                    delayResetHandler.removeCallbacksAndMessages(null);
                    timeLimitHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gameOver();
                        }
                    }, settings.timeLimit * 1000);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    delayCheckingHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (checkCorrectnessOfDrawing()) {
                                Toast.makeText(getApplication().getBaseContext(), "GREAT!", Toast.LENGTH_SHORT).show();
                                loadNextLevel();
                            } else {
                                delayResetHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplication().getBaseContext(), "WRONG!", Toast.LENGTH_SHORT).show();
                                        resetCurrentLevel();
                                    }
                                }, 2000);
                            }
                            delayCheckingHandler.removeCallbacksAndMessages(null);
                        }
                    }, 700);
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

    private boolean checkCorrectnessOfDrawing() {
        int backgroundPixels = drawingInGameView.getBackgroundImagePixels();
        int[] currentPixels = drawingInGameView.analyzeTracePixels();
        int currentTrace = currentPixels[0];
        int currentBackground = currentPixels[1];

        if (settings.difficultyLevel == 1) {
            if (backgroundPixels * 0.4 < currentBackground) {
                return false;
            }

            if (backgroundPixels * 0.7 > currentTrace) {
                return false;
            }

            return !(backgroundPixels * 1.3 < currentTrace);

        } else if (settings.difficultyLevel == 2) {
            if (backgroundPixels * 0.3 < currentBackground) {
                return false;
            }

            if (backgroundPixels * 0.8 > currentTrace) {
                return false;
            }

            return !(backgroundPixels * 1.2 < currentTrace);

        } else {
            if (backgroundPixels * 0.2 < currentBackground) {
                return false;
            }

            if (backgroundPixels * 0.9 > currentTrace) {
                return false;
            }

            return !(backgroundPixels * 1.1 < currentTrace);
        }
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
            drawingInGameView.analyzeBackgroundPixels();
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