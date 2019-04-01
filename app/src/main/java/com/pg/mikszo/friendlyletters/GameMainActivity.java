/*
 ******************************************************************************************
 *
 *    Part of the master's thesis
 *    Topic: "Supporting the development of fine motor skills in children using IT tools"
 *
 *    FRIENDLY LETTERS created by Mikolaj Szotowicz : https://github.com/szotowicz
 *
 ****************************************************************************************
 */
package com.pg.mikszo.friendlyletters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pg.mikszo.friendlyletters.settings.Configuration;
import com.pg.mikszo.friendlyletters.views.game.DrawingInGameView;
import com.pg.mikszo.friendlyletters.logger.LoggerCSV;
import com.pg.mikszo.friendlyletters.settings.ColorsManager;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

public class GameMainActivity extends Activity {

    private Configuration configuration;
    private DrawingInGameView drawingInGameView;
    private RelativeLayout gameMainLayout;
    private TextView currentLevelTextView;
    private int currentLevel = 1;
    private int currentNumberOfRepetitions = 1;
    //todo: maybe remove?
    private int currentRandomMaterial = -1;
    private String currentMaterialFile;
    private int currentBackgroundColorNumber = 0;
    private int currentMaterialColorNumber = 0;
    private int currentTraceColorNumber = 0;
    private long levelStartTime = 0;
    private Handler timeLimitHandler = new Handler();
    private Handler delayResetHandler = new Handler();
    private Handler delayCheckingHandler = new Handler();
    private int delayCheckingHandlerMillis = 700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configuration = new SettingsManager(this).getActiveConfiguration();
        loadGameView();
    }

    public void restartGameOnClick(View view) {
        loadGameView();
    }

    public void gameExitOnClick(View view) {
        drawingInGameView.cleanScreen();
        //TODO:
        // 1. Is it needed?
        // 2. Ask about exit
        // 3. Background little transparent
    }

    public void analysisScreenOnClick(View view) {
        drawingInGameView.analyzeTracePixels();
    }

    private void loadGameView() {
        currentLevel = 1;
        currentNumberOfRepetitions = 1;
        setContentView(R.layout.activity_game_main);

        gameMainLayout = findViewById(R.id.activity_game_main_layout);
        currentLevelTextView = findViewById(R.id.game_current_level);

        drawingInGameView = findViewById(R.id.drawing_in_game_view);
        drawingInGameView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        drawingInGameView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        final int height = drawingInGameView.getHeight();
                        final int width = drawingInGameView.getWidth();

                        int top = (int)(height * 0.1);
                        int bottom = (int)(height * 0.95);
                        int imageSize = bottom - top;
                        int left = (width / 2) - (imageSize / 2);
                        int right = left + imageSize;

                        drawingInGameView.setBackgroundImageDimension(left, top, right, bottom);
                        TypedValue typedValue = new TypedValue();
                        getResources().getValue(R.dimen.game_track_width_relative_to_size_of_field, typedValue, true);
                        drawingInGameView.setStrokeWidth(imageSize * typedValue.getFloat());
                        drawingInGameView.setRadiusCursor(imageSize * typedValue.getFloat());
                        setDrawingProperties();
                        drawingInGameView.analyzeBackgroundPixels();
                        setTouchListener();
                    }
                });

        setNumberOfLevel();
    }

    private void setDrawingProperties() {
        //TODO: without repetition in next level
        String[] availableBackgroundColors = configuration.backgroundColors;
        currentBackgroundColorNumber = new Random().nextInt(availableBackgroundColors.length);
        gameMainLayout.setBackgroundColor(
                new ColorsManager(this).getBackgroundColorById(availableBackgroundColors[currentBackgroundColorNumber]));

        String[] availableMaterialColors = configuration.materialColors;
        currentMaterialColorNumber = new Random().nextInt(availableMaterialColors.length);
        drawingInGameView.setMaterialColor(
                new ColorsManager(this).getMaterialColorById(availableMaterialColors[currentMaterialColorNumber]));

        String[] availableTraceColors = configuration.traceColors;
        currentTraceColorNumber = new Random().nextInt(availableTraceColors.length);
        drawingInGameView.setTrackColor(
                new ColorsManager(this).getTraceColorById(availableTraceColors[currentTraceColorNumber]));

        String[] availableShapes = configuration.availableShapes;
        if (availableShapes.length == 0) {
            Toast.makeText(this, R.string.information_message_lack_of_materials, Toast.LENGTH_SHORT).show();
        } else {
            try {
                int randomAvailableBackground = new Random().nextInt(availableShapes.length);
                if (randomAvailableBackground == currentRandomMaterial) {
                    randomAvailableBackground = new Random().nextInt(availableShapes.length);
                }
                currentRandomMaterial = randomAvailableBackground;
                currentMaterialFile = availableShapes[randomAvailableBackground];
                File randomAvailableBackgroundFile = FileHelper.getAbsolutePathOfFile(
                        currentMaterialFile, this);

                if (randomAvailableBackgroundFile == null) {
                    configuration = new SettingsManager(this).getActiveConfiguration();
                    setDrawingProperties();
                    return;
                }

                Drawable randomBackground = Drawable.createFromStream(
                        new FileInputStream(randomAvailableBackgroundFile), null);
                randomBackground.setColorFilter(new ColorsManager(this)
                                .getMaterialColorById(availableMaterialColors[currentMaterialColorNumber]),
                        PorterDuff.Mode.SRC_IN);
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
                    if (levelStartTime == 0) {
                        levelStartTime = System.nanoTime();
                        timeLimitHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                /* TODO
                                new LoggerCSV(getApplicationContext()).addNewRecord(currentMaterialFile, "time out",
                                        currentBackgroundColorNumber, currentMaterialColorNumber,
                                        currentTraceColorNumber, currentLevel, currentNumberOfRepetitions,
                                        (System.nanoTime() - levelStartTime) - (delayCheckingHandlerMillis * 1000000), settings); */
                                gameOver();
                            }
                        }, configuration.timeLimit * 1000);
                    }
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
                                }, 1000);
                            }
                            delayCheckingHandler.removeCallbacksAndMessages(null);
                        }
                    }, delayCheckingHandlerMillis);
                }
                return false;
            }
        });
    }

    private void setNumberOfLevel() {
        String currentLevelText = getResources().getString(R.string.game_level_label) + ": " +
                currentLevel + "/" + configuration.numberOfLevels;
        currentLevelTextView.setText(currentLevelText);
    }

    private boolean checkCorrectnessOfDrawing() {
        boolean result;
        int backgroundPixels = drawingInGameView.getBackgroundImagePixels();
        int[] currentPixels = drawingInGameView.analyzeTracePixels();
        int currentTrace = currentPixels[0];
        int currentBackground = currentPixels[1];

        //TODO: analyze it
        if (configuration.difficultyLevel == 1) {
            if (backgroundPixels * 0.4 < currentBackground) {
                result = false;
            } else if (backgroundPixels * 0.7 > currentTrace) {
                result = false;
            } else if (backgroundPixels * 1.3 < currentTrace) {
                result = false;
            } else {
                result = true;
            }
        } else if (configuration.difficultyLevel == 2) {
            if (backgroundPixels * 0.3 < currentBackground) {
                result = false;
            } else if (backgroundPixels * 0.8 > currentTrace) {
                result = false;
            } else if (backgroundPixels * 1.2 < currentTrace) {
                result = false;
            } else {
                result = true;
            }
        } else {
            if (backgroundPixels * 0.2 < currentBackground) {
                result = false;
            } else if (backgroundPixels * 0.9 > currentTrace) {
                result = false;
            } else if (backgroundPixels * 1.1 < currentTrace) {
                result = false;
            } else {
                result = true;
            }
        }
        /* TODO
        new LoggerCSV(this).addNewRecord(currentMaterialFile, result, backgroundPixels,
                currentBackground, currentTrace, currentBackgroundColorNumber, currentMaterialColorNumber,
                currentTraceColorNumber, currentLevel, currentNumberOfRepetitions,
                (System.nanoTime() - levelStartTime) - (delayCheckingHandlerMillis * 1000000), settings); */
        return result;
    }

    private void resetCurrentLevel() {
        timeLimitHandler.removeCallbacksAndMessages(null);
        drawingInGameView.cleanScreen();
        levelStartTime = 0;
        currentNumberOfRepetitions++;

        if (currentNumberOfRepetitions > configuration.numberOfRepetitions) {
            gameOver();
        }
    }

    private void loadNextLevel() {
        timeLimitHandler.removeCallbacksAndMessages(null);
        drawingInGameView.cleanScreen();
        levelStartTime = 0;
        currentLevel++;

        if (currentLevel > configuration.numberOfLevels) {
            gameOver();
        } else {
            currentNumberOfRepetitions = 1;
            setNumberOfLevel();
            setDrawingProperties();
            drawingInGameView.analyzeBackgroundPixels();
        }
    }

    private void gameOver() {
        //TODO implement
        if (currentLevel > configuration.numberOfLevels) {
            Toast.makeText(this, "WINNER!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "GAME OVER", Toast.LENGTH_SHORT).show();
        }
        setContentView(R.layout.activity_game_restart);
    }
}