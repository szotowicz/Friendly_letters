package com.pg.mikszo.friendlyletters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pg.mikszo.friendlyletters.drawing.configurationApp.AddingShapeView;

import java.util.List;

public class SettingsMainActivity extends Activity {

    private AddingShapeView addingShapeView;
    private enum availableTabs { aspect, material, learning, reinforcement, addMaterial }
    private enum availableAspectSections { materialSection, traceSection, backgroundSection }
    private availableTabs selectedTab;
    private Context context;
    private List<Button> selectedMaterialColors;
    private List<Button> selectedTraceColors;
    private List<Button> selectedBackgroundColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplication().getBaseContext();
        loadMenuTabAspect();

        //Todo: change
        //addingShapeView = findViewById(R.id.addingShapeView);

        if (!FileHelper.isAppFolderExists(this)) {
            FileHelper.copyDefaultImages(this);
        }
    }

    public void tabAspectOnClick(View view) {
        if (selectedTab != availableTabs.aspect) {
            loadMenuTabAspect();
        }
    }

    public void tabMaterialOnClick(View view) {
        if (selectedTab != availableTabs.material) {
            loadMenuTabMaterial();
        }
    }

    public void tabLearningOnClick(View view) {
        if (selectedTab != availableTabs.learning) {
            loadMenuTabLearning();
        }
    }

    public void tabReinforcementOnClick(View view) {
        if (selectedTab != availableTabs.reinforcement) {
            loadMenuTabReinforcement();
        }
    }

    public void removeMaterialOnClick(View view) {
        //TODO
    }

    public void enableMaterialOnClick(View view) {
        //TODO
    }

    public void addNewMaterialOnClick(View view) {
        selectedTab = availableTabs.addMaterial;
        setContentView(R.layout.activity_settings_add_material);
        addingShapeView = findViewById(R.id.addingShapeView);
        //TODO height = width
    }

    public void cleanNewMaterialOnClick(View view) {
        addingShapeView.cleanScreen();
    }

    public void addMaterialToResourcesOnClick(View view) {
        addingShapeView.saveScreenImage();
        addingShapeView.cleanScreen();
    }

    public void setLevelOnClick(View view) {
        //TODO from tag?
    }

    private void loadMenuTabAspect() {
        selectedTab = availableTabs.aspect;
        setContentView(R.layout.activity_settings_tab_aspect);

        int[] materialColors = {
                ContextCompat.getColor(context, R.color.color_settings_material_1),
                ContextCompat.getColor(context, R.color.color_settings_material_2),
                ContextCompat.getColor(context, R.color.color_settings_material_3)};
        Drawable[] materialColorsDrawable = {
                ContextCompat.getDrawable(context, R.drawable.settings_material_color_1),
                ContextCompat.getDrawable(context, R.drawable.settings_material_color_2),
                ContextCompat.getDrawable(context, R.drawable.settings_material_color_3)};
        LinearLayout materialColorsContainer = findViewById(R.id.settings_material_colors_container);
        createTabAspectSection(materialColors, materialColorsDrawable, materialColorsContainer, availableAspectSections.materialSection);

        int[] traceColors = {
                ContextCompat.getColor(context, R.color.color_settings_trace_1),
                ContextCompat.getColor(context, R.color.color_settings_trace_2),
                ContextCompat.getColor(context, R.color.color_settings_trace_3),
                ContextCompat.getColor(context, R.color.color_settings_trace_4)};
        Drawable[] traceColorsDrawable = {
                ContextCompat.getDrawable(context, R.drawable.settings_trace_color_1),
                ContextCompat.getDrawable(context, R.drawable.settings_trace_color_2),
                ContextCompat.getDrawable(context, R.drawable.settings_trace_color_3),
                ContextCompat.getDrawable(context, R.drawable.settings_trace_color_4)};
        LinearLayout traceColorsContainer = findViewById(R.id.settings_trace_colors_container);
        createTabAspectSection(traceColors, traceColorsDrawable, traceColorsContainer, availableAspectSections.traceSection);

        int[] backgroundColors = {
                ContextCompat.getColor(context, R.color.color_settings_background_1),
                ContextCompat.getColor(context, R.color.color_settings_background_2),
                ContextCompat.getColor(context, R.color.color_settings_background_3)};
        Drawable[] backgroundColorsDrawable = {
                ContextCompat.getDrawable(context, R.drawable.settings_background_color_1),
                ContextCompat.getDrawable(context, R.drawable.settings_background_color_2),
                ContextCompat.getDrawable(context, R.drawable.settings_background_color_3)};
        LinearLayout backgroundColorsContainer = findViewById(R.id.settings_background_colors_container);
        createTabAspectSection(backgroundColors, backgroundColorsDrawable, backgroundColorsContainer, availableAspectSections.backgroundSection);
    }

    private void createTabAspectSection(final int[] buttonColors, final Drawable[] buttonColorsDrawable,
                                        final LinearLayout layoutContainer, final availableAspectSections section) {
        if (buttonColors.length != buttonColorsDrawable.length) {
            return;
        }

        for (int colorId = 0; colorId < buttonColors.length; colorId++) {
            Drawable drawable;
            if (section == availableAspectSections.backgroundSection) {
                drawable = ContextCompat.getDrawable(context, R.drawable.settings_square_drawable);
            } else {
                drawable = ContextCompat.getDrawable(context, R.drawable.settings_circle_drawable);
            }
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(buttonColors[colorId], PorterDuff.Mode.MULTIPLY));

                LinearLayout.LayoutParams params;
                if (section == availableAspectSections.backgroundSection) {
                    params = new LinearLayout.LayoutParams(140, 90);
                    params.setMargins(30, 0, 0, 0);
                } else {
                    params = new LinearLayout.LayoutParams(70, 70);
                    params.setMargins(30, 0, 0, 0);
                }

                final Button newButton = new Button(context);
                newButton.setLayoutParams(params);
                newButton.setBackground(drawable);
                newButton.setTag(colorId);

                newButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        int idOfButton = (int) v.getTag();
                        if (v.getBackground() == buttonColorsDrawable[idOfButton]) {
                            //TODO: allow if it is not last
                            Drawable drawable;
                            if (section == availableAspectSections.backgroundSection) {
                                drawable = ContextCompat.getDrawable(context, R.drawable.settings_square_drawable);
                            } else {
                                drawable = ContextCompat.getDrawable(context, R.drawable.settings_circle_drawable);
                            }
                            if (drawable != null) {
                                drawable.setColorFilter(new PorterDuffColorFilter(buttonColors[idOfButton], PorterDuff.Mode.MULTIPLY));
                                v.setBackground(drawable);
                            }
                            //TODO: add to selected btns
                        }
                        else {
                            v.setBackground(buttonColorsDrawable[idOfButton]);
                            //TODO: remove from selected btns
                        }
                    }
                });

                layoutContainer.addView(newButton);
            }
        }

        //TODO: read selected material colors
    }

    private void loadMenuTabMaterial() {
        selectedTab = availableTabs.material;
        setContentView(R.layout.activity_settings_tab_material);
        //TODO
    }

    private void loadMenuTabLearning() {
        selectedTab = availableTabs.learning;
        setContentView(R.layout.activity_settings_tab_learning);

        //TODO: read and set current values

        final SeekBar seekBarLevelCount = findViewById(R.id.seek_bar_level_count);
        final TextView levelCountMonitor = findViewById(R.id.level_count_monitor);
        final int levelCountMin = getResources().getInteger(R.integer.settings_learning_level_count_min);
        final int levelCountMax = getResources().getInteger(R.integer.settings_learning_level_count_max);
        seekBarLevelCount.setMax(levelCountMax - levelCountMin);
        levelCountMonitor.setText(String.valueOf(seekBarLevelCount.getProgress() + levelCountMin));
        seekBarLevelCount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                levelCountMonitor.setText(String.valueOf(seekBarLevelCount.getProgress() + levelCountMin));
            }
        });

        final SeekBar seekBarAttemptCount = findViewById(R.id.seek_bar_attempt_count);
        final TextView attemptCountMonitor = findViewById(R.id.attempt_count_monitor);
        final int attemptCountMin = getResources().getInteger(R.integer.settings_learning_attempt_count_min);
        final int attemptCountMax = getResources().getInteger(R.integer.settings_learning_attempt_count_max);
        seekBarAttemptCount.setMax(attemptCountMax - attemptCountMin);
        attemptCountMonitor.setText(String.valueOf(seekBarAttemptCount.getProgress() + attemptCountMin));
        seekBarAttemptCount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                attemptCountMonitor.setText(String.valueOf(seekBarAttemptCount.getProgress() + attemptCountMin));
            }
        });

        final SeekBar seekBarTimeLimit = findViewById(R.id.seek_bar_time_limit);
        final TextView timeLimitMonitor = findViewById(R.id.time_limit_monitor);
        final int timeLimitMin = getResources().getInteger(R.integer.settings_learning_time_limit_min);
        final int timeLimitMax = getResources().getInteger(R.integer.settings_learning_time_limit_max);
        seekBarTimeLimit.setMax(timeLimitMax - timeLimitMin);
        timeLimitMonitor.setText(String.valueOf(seekBarTimeLimit.getProgress() + timeLimitMin));
        seekBarTimeLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                timeLimitMonitor.setText(String.valueOf(seekBarTimeLimit.getProgress() + timeLimitMin));
            }
        });
    }

    private void loadMenuTabReinforcement() {
        selectedTab = availableTabs.reinforcement;
        setContentView(R.layout.activity_settings_tab_reinforcement);
        //TODO
    }
}