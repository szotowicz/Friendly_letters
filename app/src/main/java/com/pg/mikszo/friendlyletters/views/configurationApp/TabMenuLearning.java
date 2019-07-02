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
package com.pg.mikszo.friendlyletters.views.configurationApp;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pg.mikszo.friendlyletters.R;
import com.pg.mikszo.friendlyletters.settings.Configuration;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;

public class TabMenuLearning {
    private Activity activity;
    private SettingsManager settingsManager;
    private Configuration configuration;
    private int configurationID;

    public TabMenuLearning(Activity activity, SettingsManager settingsManager, int configurationID) {
        this.activity = activity;
        this.settingsManager = settingsManager;
        this.configurationID = configurationID;
        this.configuration = settingsManager.getConfigurationById(configurationID);
        activity.setContentView(R.layout.activity_settings_tab_learning);
        ((TextView)activity.findViewById(R.id.settings_configuration_name_label))
                .setText(configuration.configurationName);

        createViewElements();
    }

    private void createViewElements() {
        createLearningModeView();
        createStartPointView();
        createDifficultyLevelView();
        createLevelCountView();
        createAttemptCountView();
        createTimeLimitView();
    }

    private void createLearningModeView() {
        CheckBox learningModeCheckBox = activity.findViewById(R.id.settings_learning_mode);
        learningModeCheckBox.setChecked(configuration.testMode);
        learningModeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                configuration.testMode = !configuration.testMode;
                settingsManager.updateFileWithConfigurations(configuration, configurationID);
                SeekBar seekBarAttemptCount = activity.findViewById(R.id.seek_bar_attempt_count);
                seekBarAttemptCount.setEnabled(!configuration.testMode);
                CheckBox startPointCheckBox = activity.findViewById(R.id.settings_display_start_point);
                startPointCheckBox.setEnabled(!configuration.testMode);
            }
        });
    }

    private void createStartPointView() {
        CheckBox startPointCheckBox = activity.findViewById(R.id.settings_display_start_point);
        startPointCheckBox.setChecked(configuration.displayStartPoint);
        startPointCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                configuration.displayStartPoint = !configuration.displayStartPoint;
                settingsManager.updateFileWithConfigurations(configuration, configurationID);
            }
        });
    }

    private void createDifficultyLevelView() {
        final Drawable background = ContextCompat.getDrawable(activity, R.drawable.settings_difficulty_level_buttons);
        final Drawable backgroundSelected = ContextCompat.getDrawable(activity, R.drawable.settings_difficulty_level_buttons_selected);
        final Button setEasyLevelButton = activity.findViewById(R.id.set_difficulty_level_easy);
        final Button setMediumLevelButton = activity.findViewById(R.id.set_difficulty_level_medium);
        final Button setHardLevelButton = activity.findViewById(R.id.set_difficulty_level_hard);
        setEasyLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMediumLevelButton.setBackground(background);
                setHardLevelButton.setBackground(background);
                view.setBackground(backgroundSelected);

                configuration.difficultyLevel = 0;
                settingsManager.updateFileWithConfigurations(configuration, configurationID);
            }
        });
        setMediumLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEasyLevelButton.setBackground(background);
                setHardLevelButton.setBackground(background);
                view.setBackground(backgroundSelected);

                configuration.difficultyLevel = 1;
                settingsManager.updateFileWithConfigurations(configuration, configurationID);
            }
        });
        setHardLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEasyLevelButton.setBackground(background);
                setMediumLevelButton.setBackground(background);
                view.setBackground(backgroundSelected);

                configuration.difficultyLevel = 2;
                settingsManager.updateFileWithConfigurations(configuration, configurationID);
            }
        });

        int currentDifficultyLevel = configuration.difficultyLevel;
        if (currentDifficultyLevel == 0) {
            setEasyLevelButton.setBackground(backgroundSelected);
        } else if (currentDifficultyLevel == 1) {
            setMediumLevelButton.setBackground(backgroundSelected);
        } else if (currentDifficultyLevel == 2) {
            setHardLevelButton.setBackground(backgroundSelected);
        }
    }

    private void createLevelCountView() {
        final SeekBar seekBarLevelCount = activity.findViewById(R.id.seek_bar_level_count);
        final TextView levelCountMonitor = activity.findViewById(R.id.level_count_monitor);
        final int levelCountMin = activity.getResources().getInteger(R.integer.settings_learning_level_count_min);
        final int levelCountMax = activity.getResources().getInteger(R.integer.settings_learning_level_count_max);
        seekBarLevelCount.setMax(levelCountMax - levelCountMin);
        seekBarLevelCount.setProgress(configuration.numberOfSteps - levelCountMin);
        levelCountMonitor.setText(String.valueOf(seekBarLevelCount.getProgress() + levelCountMin));
        seekBarLevelCount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                levelCountMonitor.setText(String.valueOf(seekBarLevelCount.getProgress() + levelCountMin));

                configuration.numberOfSteps = seekBarLevelCount.getProgress() + levelCountMin;
                settingsManager.updateFileWithConfigurations(configuration, configurationID);
            }
        });
    }

    private void createAttemptCountView() {
        final SeekBar seekBarAttemptCount = activity.findViewById(R.id.seek_bar_attempt_count);
        final TextView attemptCountMonitor = activity.findViewById(R.id.attempt_count_monitor);
        final int attemptCountMin = activity.getResources().getInteger(R.integer.settings_learning_attempt_count_min);
        final int attemptCountMax = activity.getResources().getInteger(R.integer.settings_learning_attempt_count_max);
        seekBarAttemptCount.setMax(attemptCountMax - attemptCountMin);
        seekBarAttemptCount.setProgress(configuration.numberOfRepetitions - attemptCountMin);
        attemptCountMonitor.setText(String.valueOf(seekBarAttemptCount.getProgress() + attemptCountMin));
        if (configuration.testMode) {
            seekBarAttemptCount.setEnabled(false);
        } else {
            seekBarAttemptCount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    attemptCountMonitor.setText(String.valueOf(seekBarAttemptCount.getProgress() + attemptCountMin));

                    configuration.numberOfRepetitions = seekBarAttemptCount.getProgress() + attemptCountMin;
                    settingsManager.updateFileWithConfigurations(configuration, configurationID);
                }
            });
        }
    }

    private void createTimeLimitView() {
        final SeekBar seekBarTimeLimit = activity.findViewById(R.id.seek_bar_time_limit);
        final TextView timeLimitMonitor = activity.findViewById(R.id.time_limit_monitor);
        final int timeLimitMin = activity.getResources().getInteger(R.integer.settings_learning_time_limit_min);
        final int timeLimitMax = activity.getResources().getInteger(R.integer.settings_learning_time_limit_max);
        seekBarTimeLimit.setMax(timeLimitMax - timeLimitMin);
        seekBarTimeLimit.setProgress(configuration.timeLimit - timeLimitMin);
        timeLimitMonitor.setText(String.valueOf(seekBarTimeLimit.getProgress() + timeLimitMin));
        seekBarTimeLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timeLimitMonitor.setText(String.valueOf(seekBarTimeLimit.getProgress() + timeLimitMin));

                configuration.timeLimit = seekBarTimeLimit.getProgress() + timeLimitMin;
                settingsManager.updateFileWithConfigurations(configuration, configurationID);
            }
        });
    }
}