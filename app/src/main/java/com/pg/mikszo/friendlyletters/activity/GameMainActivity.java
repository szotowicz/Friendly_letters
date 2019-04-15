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
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
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
import com.pg.mikszo.friendlyletters.views.game.GameMaterial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
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
    private TextReader textReader;

    private int currentLevel;
    private int currentNumberOfRepetitions;
    private List<GameMaterial> generatedMaterials = new ArrayList<>();

    private long timeOfStartLevel = 0;
    private Handler timeLimitHandler = new Handler();
    private Handler delayResetHandler = new Handler();
    private Handler delayCheckingHandler = new Handler();
    private int delayCheckingHandlerMillis = 700;

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

    public void nextMaterialOnClick(View view) {
        //TODO : Log it?
        loadNextLevel();
    }

    public void previousMaterialOnClick(View view) {
        //TODO : Log it?
        loadPreviousLevel();
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
                        generateNewGameMaterial();
                        drawingInGameView.analyzeBackgroundPixels();
                        setTouchListener();

                        final Button previousMaterialButton = findViewById(R.id.game_previous_material);
                        final Button nextMaterialButton = findViewById(R.id.game_next_material);
                        if (configuration.testMode) {
                            previousMaterialButton.setVisibility(View.INVISIBLE);
                            nextMaterialButton.setVisibility(View.INVISIBLE);
                        } else {
                            previousMaterialButton.getLayoutParams().height = width / 7;
                            previousMaterialButton.getLayoutParams().width = width / 7;
                            nextMaterialButton.getLayoutParams().height = width / 7;
                            nextMaterialButton.getLayoutParams().width = width / 7;
                        }

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

    private void generateNewGameMaterial() {
        GameMaterial newGameMaterial = new GameMaterial();

        String[] availableBackgroundColors = configuration.backgroundColors;
        int currentBackgroundColorNumber = new Random().nextInt(availableBackgroundColors.length);
        gameMainLayout.setBackgroundColor(new ColorsManager(this).getBackgroundColorById(
                availableBackgroundColors[currentBackgroundColorNumber]));
        newGameMaterial.colorBackground = currentBackgroundColorNumber;

        String[] availableMaterialColors = configuration.materialColors;
        int currentMaterialColorNumber = new Random().nextInt(availableMaterialColors.length);
        drawingInGameView.setMaterialColor(new ColorsManager(this).getMaterialColorById(
                availableMaterialColors[currentMaterialColorNumber]));
        newGameMaterial.colorMaterial = currentMaterialColorNumber;

        String[] availableTraceColors = configuration.traceColors;
        int currentTraceColorNumber = new Random().nextInt(availableTraceColors.length);
        drawingInGameView.setTraceColor(new ColorsManager(this).getTraceColorById(
                availableTraceColors[currentTraceColorNumber]));
        newGameMaterial.colorTrace = currentTraceColorNumber;

        String[] availableShapes = configuration.availableShapes;
        if (availableShapes.length == 0) {
            Toast.makeText(this, R.string.information_message_lack_of_materials,
                    Toast.LENGTH_SHORT).show();
        } else {
            try {
                int randomAvailableBackground = new Random().nextInt(availableShapes.length);
                String materialFile = availableShapes[randomAvailableBackground];
                if (availableShapes.length > 1 && generatedMaterials.size() > 0) {
                    while (true) {
                        randomAvailableBackground = new Random().nextInt(availableShapes.length);
                        materialFile = availableShapes[randomAvailableBackground];

                        if (!materialFile.equals(generatedMaterials.get(currentLevel - 1).filename)) {
                            if (availableShapes.length > 2 && generatedMaterials.size() > 1) {
                                if (!materialFile.equals(generatedMaterials.get(currentLevel - 2).filename)) {
                                    if (availableShapes.length > 3 && generatedMaterials.size() > 2) {
                                        if (!materialFile.equals(generatedMaterials.get(currentLevel - 3).filename)) {
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }

                File randomAvailableBackgroundFile = FileHelper.getAbsolutePathOfFile(
                        materialFile, this);

                if (randomAvailableBackgroundFile == null) {
                    configuration = new SettingsManager(this).getActiveConfiguration();
                    generateNewGameMaterial();
                    return;
                }

                Drawable randomBackground = Drawable.createFromStream(
                        new FileInputStream(randomAvailableBackgroundFile), null);
                randomBackground.setColorFilter(new ColorsManager(this).getMaterialColorById(
                        availableMaterialColors[currentMaterialColorNumber]),
                        PorterDuff.Mode.SRC_IN);
                drawingInGameView.setMaterialImage(randomBackground);

                newGameMaterial.filename = materialFile;
                newGameMaterial.isCorrectlySolved = false;

                generatedMaterials.add(newGameMaterial);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void restoreGeneratedGameMaterial(int index) {
        GameMaterial restoredGameMaterial = generatedMaterials.get(index);

        gameMainLayout.setBackgroundColor(new ColorsManager(this).getBackgroundColorById(
                configuration.backgroundColors[restoredGameMaterial.colorBackground]));
        drawingInGameView.setMaterialColor(new ColorsManager(this).getMaterialColorById(
                configuration.materialColors[restoredGameMaterial.colorMaterial]));
        drawingInGameView.setTraceColor(new ColorsManager(this).getTraceColorById(
                configuration.traceColors[restoredGameMaterial.colorTrace]));

        File randomAvailableBackgroundFile = FileHelper.getAbsolutePathOfFile(
                restoredGameMaterial.filename, this);

        if (randomAvailableBackgroundFile == null) {
            configuration = new SettingsManager(this).getActiveConfiguration();
            generateNewGameMaterial();
            return;
        }

        try {
            Drawable background = Drawable.createFromStream(
                    new FileInputStream(randomAvailableBackgroundFile), null);
            background.setColorFilter(new ColorsManager(this).getMaterialColorById(
                    configuration.materialColors[restoredGameMaterial.colorMaterial]),
                    PorterDuff.Mode.SRC_IN);
            drawingInGameView.setMaterialImage(background);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
                                /* TODO. Remove because is checkCorrectnessOfDrawing()? No, time out status
                                new LoggerCSV(getApplicationContext()).addNewRecord(currentMaterialFile, "time out",
                                        currentBackgroundColorNumber, currentMaterialColorNumber,
                                        currentTraceColorNumber, currentLevel, currentNumberOfRepetitions,
                                        (System.nanoTime() - timeOfStartLevel) - (delayCheckingHandlerMillis * 1000000), settings); */
                                if (checkCorrectnessOfDrawing()) {
                                    readVerbalPraises();
                                    loadNextLevel();
                                } else {
                                    resetCurrentLevel();
                                }
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
        /* TODO logger
        new LoggerCSV(this).addNewRecord(currentMaterialFile, result, backgroundPixels,
                currentBackground, currentTrace, currentBackgroundColorNumber, currentMaterialColorNumber,
                currentTraceColorNumber, currentLevel, currentNumberOfRepetitions,
                (System.nanoTime() - timeOfStartLevel) - (delayCheckingHandlerMillis * 1000000), settings); */

        if (result) {
            generatedMaterials.get(currentLevel - 1).isCorrectlySolved = true;
        }

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
            if (currentLevel == configuration.numberOfLevels) {
                gameOver();
            } else {
                loadNextLevel();
            }
        } else {
            readCommand();
        }
    }

    private void loadNextLevel() {
        timeLimitHandler.removeCallbacksAndMessages(null);
        drawingInGameView.cleanScreen();
        timeOfStartLevel = 0;

        if (currentLevel == configuration.numberOfLevels) {
            gameOver();
        } else {
            currentNumberOfRepetitions = 1;
            if (currentLevel == generatedMaterials.size()) {
                generateNewGameMaterial();
            } else if (currentLevel < generatedMaterials.size()) {
                restoreGeneratedGameMaterial(currentLevel);
            } else {
                Log.e("[ERROR - GAME]", "Wrong index of generatedMaterials");
            }
            drawingInGameView.analyzeBackgroundPixels();
            currentLevel++;

            randomCommand();
            updateCommandTextView();
            readCommand();
        }
    }

    private void loadPreviousLevel() {
        if (currentLevel > 1) {
            timeLimitHandler.removeCallbacksAndMessages(null);
            drawingInGameView.cleanScreen();
            timeOfStartLevel = 0;

            currentLevel--;
            restoreGeneratedGameMaterial(currentLevel - 1);
            drawingInGameView.analyzeBackgroundPixels();

            randomCommand();
            updateCommandTextView();
            readCommand();
        }
    }

    private void gameOver() {
        if (configuration.testMode) {
            int correctlySolved = 0;
            for (GameMaterial gameMaterial : generatedMaterials) {
                if (gameMaterial.isCorrectlySolved) {
                    correctlySolved++;
                }
            }
            Intent gameReport = new Intent(getBaseContext(), GameReportActivity.class);
            gameReport.putExtra(getString(R.string.intent_game_report_correctly_solved), correctlySolved);
            startActivity(gameReport);
            finish();
        } else {
            startActivity(new Intent(getBaseContext(), GameStartActivity.class));
            finish();
        }
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
            Matcher matcher = pattern.matcher(generatedMaterials.get(currentLevel - 1).filename);
            if (matcher.find()) {
                String mark = matcher.group(2);

                if (mark.length() == 1) {
                    char markCharacter = mark.charAt(0);

                    if (Character.isLetterOrDigit(markCharacter)) {
                        if (Character.isDigit(markCharacter)) {
                            command = randomCommand.replace(getResources()
                                    .getString(R.string.settings_tab_reinforcement_command_2_letter), "");
                        } else if (Character.isLetter(markCharacter)) {
                            command = randomCommand.replace(getResources()
                                    .getString(R.string.settings_tab_reinforcement_command_2_digit), "");
                        }
                        command = command.replace("/", "");
                        command = command.replace(getResources()
                                .getString(R.string.settings_tab_reinforcement_command_mark_tag), mark);
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