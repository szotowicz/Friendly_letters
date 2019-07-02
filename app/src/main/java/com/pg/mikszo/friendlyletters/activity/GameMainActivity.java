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
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pg.mikszo.friendlyletters.AudioPlayer;
import com.pg.mikszo.friendlyletters.FileHelper;
import com.pg.mikszo.friendlyletters.R;
import com.pg.mikszo.friendlyletters.TextReader;
import com.pg.mikszo.friendlyletters.logger.LoggerCSV;
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

public class GameMainActivity extends BaseActivity {

    private Configuration configuration;
    private DrawingInGameView drawingInGameView;
    private RelativeLayout gameMainLayout;

    private TextView commandTextView;
    private String currentCommands = "";
    private String[] availableCommands;
    private String[] availableVerbalPraises;
    private TextReader textReader;
    private AudioPlayer audioPlayer;

    private int currentStep;
    private int currentRepetition;
    private List<GameMaterial> generatedMaterials = new ArrayList<>();

    private long timeOfStartLevel = 0;
    private Handler timeLimitHandler = new Handler();
    private Handler delayResetHandler = new Handler();
    private Handler delayCheckingHandler = new Handler();
    private int delayCheckingHandlerMillis = 700;
    private Handler delayNextMaterialHandler = new Handler();
    private Handler delayPreviousMaterialHandler = new Handler();

    private List<ImageView> animationImagesCar = new ArrayList<>();
    private List<ImageView> animationImagesBalloon = new ArrayList<>();

    @Override
    // This function is loaded in every BaseActivity child
    protected void loadOnCreateView() {
        this.configuration = new SettingsManager(this).getActiveConfiguration();
        this.availableCommands = new ReinforcementManager(this).getAvailableCommands();
        this.availableVerbalPraises = new ReinforcementManager(this).getAvailableVerbalPraises();
        textReader = new TextReader(this);
        audioPlayer = new AudioPlayer(this);

        currentStep = 1;
        currentRepetition = 1;
        setContentView(R.layout.activity_game_main);

        gameMainLayout = findViewById(R.id.activity_game_main_layout);
        commandTextView = findViewById(R.id.game_command_text_view);
        if (!configuration.commandsDisplaying
                || configuration.testMode
                || configuration.availableCommands.length == 0) {
            commandTextView.setVisibility(View.INVISIBLE);
        }

        new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(2000);
                } catch (Exception ignored) {

                } finally {
                    readCommand();
                }
            }
        }.start();

        // Fill containers with animation images
        animationImagesCar.add((ImageView)findViewById(R.id.game_animation_car_1));
        animationImagesCar.add((ImageView)findViewById(R.id.game_animation_car_2));
        animationImagesCar.add((ImageView)findViewById(R.id.game_animation_car_3));
        animationImagesCar.add((ImageView)findViewById(R.id.game_animation_car_4));
        animationImagesCar.add((ImageView)findViewById(R.id.game_animation_car_5));
        animationImagesBalloon.add((ImageView)findViewById(R.id.game_animation_balloon_1));
        animationImagesBalloon.add((ImageView)findViewById(R.id.game_animation_balloon_2));
        animationImagesBalloon.add((ImageView)findViewById(R.id.game_animation_balloon_3));
        animationImagesBalloon.add((ImageView)findViewById(R.id.game_animation_balloon_4));
        animationImagesBalloon.add((ImageView)findViewById(R.id.game_animation_balloon_5));
        animationImagesBalloon.add((ImageView)findViewById(R.id.game_animation_balloon_6));
        animationImagesBalloon.add((ImageView)findViewById(R.id.game_animation_balloon_7));
        animationImagesBalloon.add((ImageView)findViewById(R.id.game_animation_balloon_8));
        animationImagesBalloon.add((ImageView)findViewById(R.id.game_animation_balloon_9));
        animationImagesBalloon.add((ImageView)findViewById(R.id.game_animation_balloon_10));

        drawingInGameView = findViewById(R.id.drawing_in_game_view);
        drawingInGameView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @SuppressLint("ClickableViewAccessibility")
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

                        final Button previousMaterialButton = findViewById(R.id.game_previous_material);
                        final Button nextMaterialButton = findViewById(R.id.game_next_material);
                        if (configuration.testMode) {
                            previousMaterialButton.setVisibility(View.INVISIBLE);
                            nextMaterialButton.setVisibility(View.INVISIBLE);
                        } else {
                            final int buttonSize = width / 8;
                            previousMaterialButton.getLayoutParams().height = buttonSize;
                            previousMaterialButton.getLayoutParams().width = buttonSize;
                            nextMaterialButton.getLayoutParams().height = buttonSize;
                            nextMaterialButton.getLayoutParams().width = buttonSize;

                            RelativeLayout.LayoutParams pParams = (RelativeLayout.LayoutParams)previousMaterialButton.getLayoutParams();
                            pParams.setMargins(pParams.leftMargin, 0, 0, (height / 2) - (buttonSize / 2));

                            RelativeLayout.LayoutParams nParams = (RelativeLayout.LayoutParams)nextMaterialButton.getLayoutParams();
                            nParams.setMargins(0, 0, nParams.rightMargin, (height / 2) - (buttonSize / 2));

                            previousMaterialButton.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                                        delayPreviousMaterialHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                loadPreviousLevel();
                                            }
                                        }, 500);
                                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                                        delayPreviousMaterialHandler.removeCallbacksAndMessages(null);
                                    }
                                    return false;
                                }
                            });

                            nextMaterialButton.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                                        delayNextMaterialHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                loadNextLevel(true);
                                            }
                                        }, 500);
                                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                                        delayNextMaterialHandler.removeCallbacksAndMessages(null);
                                    }
                                    return false;
                                }
                            });
                        }

                        randomCommand();
                        updateCommandTextView();

                        // Set correct size of animation images
                        for (ImageView imageView : animationImagesCar) {
                            imageView.getLayoutParams().height = height / 5;
                            imageView.getLayoutParams().width = height / 5;
                        }
                        for (ImageView imageView : animationImagesBalloon) {
                            imageView.getLayoutParams().height = width / 10;
                            imageView.getLayoutParams().width = width / 10;
                        }
                    }
                });

        final ImageView animationImage = findViewById(R.id.game_animation_on_start);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.animation_before_game);
        animationImage.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                animationImage.setVisibility(View.INVISIBLE);
                setTouchListener();
                Button previousMaterialButton = findViewById(R.id.game_previous_material);
                Button nextMaterialButton = findViewById(R.id.game_next_material);
                previousMaterialButton.setEnabled(true);
                nextMaterialButton.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
    }

    @Override
    protected void onDestroy() {
        textReader.releaseTextReader();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // Game has disable onBackPressed() function
    }

    @Override
    protected void onResume() {
        super.onResume();
        Configuration activeConfiguration = new SettingsManager(this).getActiveConfiguration();
        if (!configuration.configurationName.equals(activeConfiguration.configurationName)
                || !configuration.lastEdition.equals(activeConfiguration.lastEdition)) {
            startActivity(new Intent(getBaseContext(), GameStartActivity.class));
            finish();
        }
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

        String[] availableMaterials = configuration.availableMaterials;
        if (availableMaterials.length == 0) {
            Toast.makeText(this, R.string.information_message_lack_of_materials,
                    Toast.LENGTH_SHORT).show();
        } else {
            try {
                int randomAvailableBackground = new Random().nextInt(availableMaterials.length);
                String materialFile = availableMaterials[randomAvailableBackground];
                if (availableMaterials.length > 1 && generatedMaterials.size() > 0) {
                    while (true) {
                        randomAvailableBackground = new Random().nextInt(availableMaterials.length);
                        materialFile = availableMaterials[randomAvailableBackground];

                        if (!materialFile.equals(generatedMaterials.get(currentStep - 1).filename)) {
                            if (availableMaterials.length > 2 && generatedMaterials.size() > 1) {
                                if (!materialFile.equals(generatedMaterials.get(currentStep - 2).filename)) {
                                    if (availableMaterials.length > 3 && generatedMaterials.size() > 2) {
                                        if (!materialFile.equals(generatedMaterials.get(currentStep - 3).filename)) {
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
                drawingInGameView.setOffsetOfStartPoint(newGameMaterial);
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

        drawingInGameView.setOffsetOfStartPoint(restoredGameMaterial);
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
                                if (checkCorrectnessOfDrawing(true)) {
                                    delayCheckingHandler.removeCallbacksAndMessages(null);
                                    readVerbalPraises();
                                    //loadNextLevel(false);
                                    startAnimationAndLoadNextLevel();
                                } else {
                                    resetCurrentLevel();
                                }
                                timeLimitHandler.removeCallbacksAndMessages(null);
                            }}, configuration.timeLimit * 1000);
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    if (timeOfStartLevel == 0) {
                        drawingInGameView.isTouchScreenEnabled = false;
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (timeOfStartLevel == 0) {
                        drawingInGameView.isTouchScreenEnabled = true;
                    } else {
                        delayCheckingHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (checkCorrectnessOfDrawing(false)) {
                                    timeLimitHandler.removeCallbacksAndMessages(null);
                                    readVerbalPraises();
                                    //loadNextLevel(false);
                                    startAnimationAndLoadNextLevel();
                                } else {
                                    delayResetHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            resetCurrentLevel();
                                        }
                                    }, 1000);
                                }
                                delayCheckingHandler.removeCallbacksAndMessages(null);
                            }}, delayCheckingHandlerMillis);
                    }
                }
                return false;
            }
        });
    }

    private boolean checkCorrectnessOfDrawing(boolean isEndOfTime) {
        if (!drawingInGameView.startedFromStartPointOnMark) {
            return false;
        }

        boolean result;
        int backgroundPixels = drawingInGameView.getBackgroundImagePixels();
        int[] currentPixels = drawingInGameView.analyzeTracePixels();
        int currentTrace = currentPixels[0];
        int currentBackground = currentPixels[1];

        if (configuration.difficultyLevel == 0) {
            // Easy level policy:
            //    must remain less than 40% of the background
            //    trace cannot be less than 80% of the background
            //    trace cannot be greater than 130% of the background
            if (backgroundPixels * 0.4 < currentBackground) {
                result = false;
            } else if (backgroundPixels * 0.8 > currentTrace) {
                result = false;
            } else {
                result = !(backgroundPixels * 1.30 < currentTrace);
            }
        } else if (configuration.difficultyLevel == 1) {
            // Medium level policy:
            //    must remain less than 30% of the background
            //    trace cannot be less than 85% of the background
            //    trace cannot be greater than 125% of the background
            if (backgroundPixels * 0.3 < currentBackground) {
                result = false;
            } else if (backgroundPixels * 0.85 > currentTrace) {
                result = false;
            } else {
                result = !(backgroundPixels * 1.25 < currentTrace);
            }
        } else if (configuration.difficultyLevel == 2) {
            // Hard level policy:
            //    must remain less than 20% of the background
            //    trace cannot be less than 90% of the background
            //    trace cannot be greater than 120% of the background
            if (backgroundPixels * 0.2 < currentBackground) {
                result = false;
            } else if (backgroundPixels * 0.9 > currentTrace) {
                result = false;
            } else {
                result = !(backgroundPixels * 1.20 < currentTrace);
            }
        } else {
            result = false;
        }

        LoggerCSV.loggerStatus loggerStatus;
        Long time;
        if (isEndOfTime) {
            if (result) {
                loggerStatus = LoggerCSV.loggerStatus.TIMEOUT_CHECK_TRUE;
            } else {
                loggerStatus = LoggerCSV.loggerStatus.TIMEOUT_CHECK_FALSE;
            }
            time = System.nanoTime() - timeOfStartLevel;
        } else {
            if (result) {
                loggerStatus = LoggerCSV.loggerStatus.CHECK_TRUE;
            } else {
                loggerStatus = LoggerCSV.loggerStatus.CHECK_FALSE;
            }
            time = (System.nanoTime() - timeOfStartLevel) - (delayCheckingHandlerMillis * 1000000);
        }
        new LoggerCSV(this).addNewRecord(loggerStatus,
                generatedMaterials.get(currentStep - 1), configuration,
                backgroundPixels, currentBackground, currentTrace,
                currentStep, currentRepetition, time);

        if (result) {
            generatedMaterials.get(currentStep - 1).isCorrectlySolved = true;
        }

        return result;
    }

    private void resetCurrentLevel() {
        timeLimitHandler.removeCallbacksAndMessages(null);
        drawingInGameView.cleanScreen();
        currentRepetition++;
        timeOfStartLevel = 0;

        int limitOfRepetitions = 1;
        if (!configuration.testMode) {
            limitOfRepetitions = configuration.numberOfRepetitions;
        }

        if (currentRepetition > limitOfRepetitions) {
            if (currentStep == configuration.numberOfSteps) {
                gameOver();
            } else {
                loadNextLevel(false);
            }
        } else {
            readCommand();
        }

        drawingInGameView.setCurrentStepAndRepetition(currentStep, currentRepetition);
    }

    private void loadNextLevel(boolean fromOnClick) {
        timeLimitHandler.removeCallbacksAndMessages(null);
        delayResetHandler.removeCallbacksAndMessages(null);
        delayCheckingHandler.removeCallbacksAndMessages(null);

        if (fromOnClick) {
            reportChangeOfMaterial(LoggerCSV.loggerStatus.CLICK_NEXT);
        }

        drawingInGameView.cleanScreen();
        timeOfStartLevel = 0;

        if (currentStep == configuration.numberOfSteps) {
            gameOver();
        } else {
            currentRepetition = 1;
            if (currentStep == generatedMaterials.size()) {
                generateNewGameMaterial();
            } else if (currentStep < generatedMaterials.size()) {
                restoreGeneratedGameMaterial(currentStep);
            } else {
                Log.e("[ERROR - GAME]", "Wrong index of generatedMaterials");
            }
            currentStep++;
            drawingInGameView.analyzeBackgroundPixels();

            randomCommand();
            updateCommandTextView();
            readCommand();
        }
    }

    private void loadPreviousLevel() {
        if (currentStep > 1) {
            timeLimitHandler.removeCallbacksAndMessages(null);
            delayResetHandler.removeCallbacksAndMessages(null);
            delayCheckingHandler.removeCallbacksAndMessages(null);

            reportChangeOfMaterial(LoggerCSV.loggerStatus.CLICK_PREVIOUS);

            drawingInGameView.cleanScreen();
            timeOfStartLevel = 0;
            currentRepetition = 1;

            currentStep--;
            restoreGeneratedGameMaterial(currentStep - 1);
            drawingInGameView.analyzeBackgroundPixels();

            randomCommand();
            updateCommandTextView();
            readCommand();
        }
    }

    private void reportChangeOfMaterial(LoggerCSV.loggerStatus status) {
        int backgroundPixels = drawingInGameView.getBackgroundImagePixels();
        int[] currentPixels = drawingInGameView.analyzeTracePixels();
        int currentTrace = currentPixels[0];
        int currentBackground = currentPixels[1];
        long time = timeOfStartLevel > 0 ? System.nanoTime() - timeOfStartLevel : 0;

        new LoggerCSV(this).addNewRecord(status,
                generatedMaterials.get(currentStep - 1), configuration,
                backgroundPixels, currentBackground, currentTrace,
                currentStep, currentRepetition, time);
    }

    private void gameOver() {
        if (configuration.testMode) {
            int correctlySolved = 0;
            for (GameMaterial gameMaterial : generatedMaterials) {
                if (gameMaterial.isCorrectlySolved) {
                    correctlySolved++;
                }
            }
            Intent gameReport = new Intent(getBaseContext(), GameTestReportActivity.class);
            gameReport.putExtra(getString(R.string.intent_game_report_correctly_solved), correctlySolved);
            startActivity(gameReport);
            finish();
        } else {
            if (audioPlayer.playEndOfTest()) {
                //TODO
                //textReader...
            }
            audioPlayer.playRandomFanfare();
            startActivity(new Intent(getBaseContext(), GameStartActivity.class));
            finish();
        }
    }

    private void randomCommand() {
        if (configuration.availableCommands.length > 0) {
            int randomCommandIndex = Integer.parseInt(
                    configuration.availableCommands[new Random().nextInt(
                            configuration.availableCommands.length)]);
            String randomCommand = availableCommands[randomCommandIndex];

            String command = "";
            Pattern pattern = Pattern.compile("(_)(.?)(.png)");
            Matcher matcher = pattern.matcher(generatedMaterials.get(currentStep - 1).filename);
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
        if (configuration.commandsDisplaying && configuration.availableCommands.length > 0) {
            if (currentCommands.trim().length() == 0){
                commandTextView.setVisibility(View.INVISIBLE);
            } else {
                commandTextView.setVisibility(View.VISIBLE);
                commandTextView.setText(currentCommands);
            }
        }
        drawingInGameView.setCurrentStepAndRepetition(currentStep, currentRepetition);
    }

    private void readCommand() {
        if (configuration.commandsReading && currentCommands.trim().length() > 1) {
            //TODO
            if (!audioPlayer.playCommand(currentCommands))
            {
                textReader.readCommand(currentCommands);
            }
        }
    }

    private void readVerbalPraises() {
        if (configuration.verbalPraisesReading && !configuration.testMode) {
            int randomVerbalPraisesIndex =
                    Integer.parseInt(configuration.availableVerbalPraises[
                            new Random().nextInt(configuration.availableVerbalPraises.length)]);
            String randomVerbalPraises = availableVerbalPraises[randomVerbalPraisesIndex];

            if (!audioPlayer.playRandomPraise(randomVerbalPraises))
            {
                textReader.readPraise(randomVerbalPraises);
            }
        }
    }

    private void startAnimationAndLoadNextLevel() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final int[] animationDrawableCars = {
                R.drawable.anim_blue_car,
                R.drawable.anim_orange_car,
                R.drawable.anim_delivery_truck,
                R.drawable.anim_green_car,
                R.drawable.anim_present_car,
                R.drawable.anim_fire_truck,
                R.drawable.anim_jeep,
                R.drawable.anim_van,
                R.drawable.anim_garbage_truck,
                R.drawable.anim_racing };
        final int[] animationDrawableBalloon = {
                R.drawable.anim_balloon1,
                R.drawable.anim_balloon2,
                R.drawable.anim_balloon3,
                R.drawable.anim_balloon4,
                R.drawable.anim_balloon5,
                R.drawable.anim_balloon6,
                R.drawable.anim_balloon7,
                R.drawable.anim_balloon8,
                R.drawable.anim_balloon9,
                R.drawable.anim_balloon10,
                R.drawable.anim_balloon11,
                R.drawable.anim_balloon12,
                R.drawable.anim_balloon13,
                R.drawable.anim_balloon14 };

        final int animationMode = new Random().nextInt(3);
        final List<ImageView> animationImages;
        if (animationMode == 2) {
            animationImages = animationImagesBalloon;
        } else {
            animationImages = animationImagesCar;
        }

        for (int imageId = 0; imageId < animationImages.size(); imageId++) {
            final ImageView animationImage = animationImages.get(imageId);

            Animation animation = new TranslateAnimation(0, 0, 0, 0);
            switch (animationMode) {
                case 0:
                    animation = new TranslateAnimation(-300, displayMetrics.widthPixels + 300, 0, 0);
                    animationImage.setRotationY(0);
                    animationImage.setBackground(ContextCompat.getDrawable(this,
                            animationDrawableCars[new Random().nextInt(animationDrawableCars.length)]));
                    break;
                case 1:
                    animation = new TranslateAnimation(displayMetrics.widthPixels, -300, 0, 0);
                    animationImage.setRotationY(180);
                    animationImage.setBackground(ContextCompat.getDrawable(this,
                            animationDrawableCars[new Random().nextInt(animationDrawableCars.length)]));
                    break;
                case 2:
                    animation = new TranslateAnimation(0, 0, displayMetrics.heightPixels, -300);
                    animationImage.setBackground(ContextCompat.getDrawable(this,
                            animationDrawableBalloon[new Random().nextInt(animationDrawableBalloon.length)]));
                    break;
                default:
                    Log.e("[ERROR", "Animation mode is not supported");
            }

            final boolean isLastAnimation;
            if (imageId == animationImagesCar.size() - 1)
            {
                animation.setDuration(3000);
                isLastAnimation = true;
            } else {
                animation.setDuration(new Random().nextInt(1200) + 1800);
                isLastAnimation = false;
            }

            animationImage.setVisibility(View.VISIBLE);
            animationImage.setAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    Button previousMaterialButton = findViewById(R.id.game_previous_material);
                    Button nextMaterialButton = findViewById(R.id.game_next_material);
                    previousMaterialButton.setEnabled(false);
                    nextMaterialButton.setEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animationImage.setVisibility(View.INVISIBLE);
                    if (isLastAnimation) {
                        loadNextLevel(false);
                    }

                    Button previousMaterialButton = findViewById(R.id.game_previous_material);
                    Button nextMaterialButton = findViewById(R.id.game_next_material);
                    previousMaterialButton.setEnabled(true);
                    nextMaterialButton.setEnabled(true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) { }
            });
        }
    }
}