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
package com.pg.mikszo.friendlyletters.activity;

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

import com.pg.mikszo.friendlyletters.FileHelper;
import com.pg.mikszo.friendlyletters.R;
import com.pg.mikszo.friendlyletters.TextReader;
import com.pg.mikszo.friendlyletters.settings.Configuration;
import com.pg.mikszo.friendlyletters.settings.ReinforcementManager;
import com.pg.mikszo.friendlyletters.views.game.DrawingInGameView;
import com.pg.mikszo.friendlyletters.settings.ColorsManager;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameMainActivity extends Activity {

    private Configuration configuration;
    private DrawingInGameView drawingInGameView;
    private RelativeLayout gameMainLayout;

    private TextView commandTextView;
    private String currentCommands = "";
    private String[] availableCommands;
    private String[] availableVerbalPraises;

    private int currentLevel = 1;
    private int currentNumberOfRepetitions = 1;
        //todo: maybe remove?
        private int currentRandomMaterial = -1;
    private String currentMaterialFile;
    private int currentBackgroundColorNumber = 0;
    private int currentMaterialColorNumber = 0;
    private int currentTraceColorNumber = 0;
    private long timeOfStartLevel = 0;
    private Handler timeLimitHandler = new Handler();
    private Handler delayResetHandler = new Handler();
    private Handler delayCheckingHandler = new Handler();
    private int delayCheckingHandlerMillis = 700;

    private TextReader textReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configuration = new SettingsManager(this).getActiveConfiguration();
        this.availableCommands = new ReinforcementManager(this).getAvailableCommands();
        this.availableVerbalPraises = new ReinforcementManager(this).getAvailableVerbalPraises();
        loadGameView();
        textReader = new TextReader(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        textReader.releaseTextReader();
        super.onDestroy();
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
        commandTextView = findViewById(R.id.game_command_text_view);
        if (!configuration.commandsDisplaying || configuration.availableCommands.length == 0 || configuration.testMode) {
            commandTextView.setVisibility(View.INVISIBLE);
        }

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

                        randomCommand();
                        updateCommandTextView();

                        Thread readCommandOnLoad = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    super.run();
                                    sleep(100);
                                } catch (Exception ignored) {

                                } finally {
                                    readCommand();
                                }
                            }
                        };
                        readCommandOnLoad.start();
                    }
                });
    }

    private void setDrawingProperties() {
        //TODO: better generator, without repetition in next level
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
                    if (timeOfStartLevel == 0) {
                        timeOfStartLevel = System.nanoTime();
                        timeLimitHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                /* TODO
                                new LoggerCSV(getApplicationContext()).addNewRecord(currentMaterialFile, "time out",
                                        currentBackgroundColorNumber, currentMaterialColorNumber,
                                        currentTraceColorNumber, currentLevel, currentNumberOfRepetitions,
                                        (System.nanoTime() - timeOfStartLevel) - (delayCheckingHandlerMillis * 1000000), settings); */
                                gameOver();
                            }
                        }, configuration.timeLimit * 1000);
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    delayCheckingHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (checkCorrectnessOfDrawing()) {
                                readVerbalPraises();
                                loadNextLevel();
                            } else {
                                delayResetHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Toast.makeText(getApplication().getBaseContext(), "WRONG!", Toast.LENGTH_SHORT).show();
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
                (System.nanoTime() - timeOfStartLevel) - (delayCheckingHandlerMillis * 1000000), settings); */
        return result;
    }

    private void resetCurrentLevel() {
        timeLimitHandler.removeCallbacksAndMessages(null);
        drawingInGameView.cleanScreen();
        timeOfStartLevel = 0;
        currentNumberOfRepetitions++;

        int limitOfRepetitions = 1;
        if (!configuration.testMode) {
            limitOfRepetitions = configuration.numberOfRepetitions;
        }

        if (currentNumberOfRepetitions > limitOfRepetitions) {
            gameOver();
        } else {
            readCommand();
        }
    }

    private void loadNextLevel() {
        timeLimitHandler.removeCallbacksAndMessages(null);
        drawingInGameView.cleanScreen();
        timeOfStartLevel = 0;
        currentLevel++;

        if (currentLevel > configuration.numberOfLevels) {
            gameOver();
        } else {
            currentNumberOfRepetitions = 1;
            setDrawingProperties();
            drawingInGameView.analyzeBackgroundPixels();

            randomCommand();
            updateCommandTextView();
            readCommand();
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

    private void readVerbalPraises() {
        if (configuration.verbalPraisesReading) {
            int randomVerbalPraisesIndex =
                    Integer.parseInt(configuration.availableVerbalPraises[
                            new Random().nextInt(configuration.availableVerbalPraises.length)]);
            String randomVerbalPraises = availableVerbalPraises[randomVerbalPraisesIndex];
            textReader.read(randomVerbalPraises);
        }
    }

    private void randomCommand() {
        if (configuration.availableCommands.length > 0 && !configuration.testMode) {
            int randomCommandIndex = Integer.parseInt(
                    configuration.availableCommands[new Random().nextInt(
                            configuration.availableCommands.length)]);
            String randomCommand = availableCommands[randomCommandIndex];

            String command = "";
            Pattern pattern = Pattern.compile("(_)(.?)(.png)");
            Matcher matcher = pattern.matcher(currentMaterialFile);
            if (matcher.find()) {
                String mark = matcher.group(2);

                if (mark.length() == 1) {
                    char markCharacter = mark.charAt(0);

                    if (Character.isLetterOrDigit(markCharacter)) {
                        if (Character.isDigit(markCharacter)) {
                            command = randomCommand.replace(getResources().getString(R.string.settings_tab_reinforcement_command_2_letter), "");
                        } else if (Character.isLetter(markCharacter)) {
                            command = randomCommand.replace(getResources().getString(R.string.settings_tab_reinforcement_command_2_digit), "");
                        }
                        command = command.replace("/", "");
                        command = command.replace(getResources().getString(R.string.settings_tab_reinforcement_command_mark_tag), mark);
                    }
                }
            }
            currentCommands = command;
        }
    }

    private void updateCommandTextView() {
        if (configuration.commandsDisplaying && configuration.availableCommands.length > 0 && !configuration.testMode) {
            if (currentCommands.trim().length() == 0){
                commandTextView.setVisibility(View.INVISIBLE);
            } else {
                commandTextView.setVisibility(View.VISIBLE);
                commandTextView.setText(currentCommands);
            }
        }
    }

    private void readCommand() {
        if (configuration.commandsReading && currentCommands.trim().length() > 1 && !configuration.testMode) {
            textReader.read(currentCommands);
        }
    }
}