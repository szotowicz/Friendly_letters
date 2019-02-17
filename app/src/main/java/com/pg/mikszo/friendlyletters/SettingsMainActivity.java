package com.pg.mikszo.friendlyletters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pg.mikszo.friendlyletters.drawing.configurationApp.AddingShapeView;
import com.pg.mikszo.friendlyletters.settings.Material;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingsMainActivity extends Activity {

    private enum availableTabs { aspect, material, learning, reinforcement, addMaterial }
    private enum availableAspectSections { materialSection, traceSection, backgroundSection }
    private availableTabs selectedTab;
    private Context context;
    private List<Button> selectedMaterialColors;
    private List<Button> selectedTraceColors;
    private List<Button> selectedBackgroundColors;
    private List<Material> selectedMaterials = new ArrayList<>();
    private List<Material> enabledMaterials = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplication().getBaseContext();
        loadMenuTabAspect();

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle(R.string.check_decisions_title);
        builder.setMessage(R.string.check_decisions_message);

        builder.setPositiveButton(R.string.check_decisions_positive_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                removeSelectedMaterials();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.check_decisions_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    public void enableMaterialOnClick(View view) {
        for (int i = 0; i < selectedMaterials.size(); i++) {
            boolean isEnabled = false;
            int id;
            for (id = 0; id < enabledMaterials.size(); id++) {
                if (selectedMaterials.get(i).material == enabledMaterials.get(id).material) {
                    isEnabled = true;
                    break;
                }
            }

            if (isEnabled) {
                selectedMaterials.get(i).cloneOfBackground.setBackground(
                        ContextCompat.getDrawable(context, R.drawable.settings_resources_of_materials_disabled_selected));
                enabledMaterials.remove(id);
            } else {
                selectedMaterials.get(i).cloneOfBackground.setBackground(
                        ContextCompat.getDrawable(context, R.drawable.settings_resources_of_materials_enabled_selected));
                enabledMaterials.add(selectedMaterials.get(i));
            }
        }
    }

    public void tabAddNewMaterialOnClick(final View view) {
        selectedTab = availableTabs.addMaterial;
        setContentView(R.layout.activity_settings_add_material);

        final AddingShapeView addingMaterialView = findViewById(R.id.addingShapeView);
        addingMaterialView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                addingMaterialView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                final int size = addingMaterialView.getHeight();
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
                layoutParams.gravity = Gravity.CENTER;
                addingMaterialView.setLayoutParams(layoutParams);
            }
        });
    }

    public void cleanNewMaterialOnClick(View view) {
        AddingShapeView addingMaterialView = findViewById(R.id.addingShapeView);
        addingMaterialView.cleanScreen();
    }

    public void addMaterialToResourcesOnClick(View view) {
        AddingShapeView addingMaterialView = findViewById(R.id.addingShapeView);
        addingMaterialView.saveScreenImage();
        addingMaterialView.cleanScreen();
    }

    private void loadMenuTabAspect() {
        selectedTab = availableTabs.aspect;
        setContentView(R.layout.activity_settings_tab_aspect);

        int[] materialColors = {
                ContextCompat.getColor(context, R.color.color_settings_material_1),
                ContextCompat.getColor(context, R.color.color_settings_material_2),
                ContextCompat.getColor(context, R.color.color_settings_material_3),
                ContextCompat.getColor(context, R.color.color_settings_material_4),
                ContextCompat.getColor(context, R.color.color_settings_material_5)};
        Drawable[] materialColorsDrawable = {
                ContextCompat.getDrawable(context, R.drawable.settings_material_color_1),
                ContextCompat.getDrawable(context, R.drawable.settings_material_color_2),
                ContextCompat.getDrawable(context, R.drawable.settings_material_color_3),
                ContextCompat.getDrawable(context, R.drawable.settings_material_color_4),
                ContextCompat.getDrawable(context, R.drawable.settings_material_color_5)};
        LinearLayout materialColorsContainer = findViewById(R.id.settings_material_colors_container);
        createTabAspectSection(materialColors, materialColorsDrawable, materialColorsContainer, availableAspectSections.materialSection);

        int[] traceColors = {
                ContextCompat.getColor(context, R.color.color_settings_trace_1),
                ContextCompat.getColor(context, R.color.color_settings_trace_2),
                ContextCompat.getColor(context, R.color.color_settings_trace_3),
                ContextCompat.getColor(context, R.color.color_settings_trace_4),
                ContextCompat.getColor(context, R.color.color_settings_trace_5)};
        Drawable[] traceColorsDrawable = {
                ContextCompat.getDrawable(context, R.drawable.settings_trace_color_1),
                ContextCompat.getDrawable(context, R.drawable.settings_trace_color_2),
                ContextCompat.getDrawable(context, R.drawable.settings_trace_color_3),
                ContextCompat.getDrawable(context, R.drawable.settings_trace_color_4),
                ContextCompat.getDrawable(context, R.drawable.settings_trace_color_5)};
        LinearLayout traceColorsContainer = findViewById(R.id.settings_trace_colors_container);
        createTabAspectSection(traceColors, traceColorsDrawable, traceColorsContainer, availableAspectSections.traceSection);

        int[] backgroundColors = {
                ContextCompat.getColor(context, R.color.color_settings_background_1),
                ContextCompat.getColor(context, R.color.color_settings_background_2),
                ContextCompat.getColor(context, R.color.color_settings_background_3),
                ContextCompat.getColor(context, R.color.color_settings_background_4)};
        Drawable[] backgroundColorsDrawable = {
                ContextCompat.getDrawable(context, R.drawable.settings_background_color_1),
                ContextCompat.getDrawable(context, R.drawable.settings_background_color_2),
                ContextCompat.getDrawable(context, R.drawable.settings_background_color_3),
                ContextCompat.getDrawable(context, R.drawable.settings_background_color_4)};
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
                } else {
                    params = new LinearLayout.LayoutParams(70, 70);
                }
                params.setMargins(15, 0, 15, 0);

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

        LinearLayout materialsContainer = findViewById(R.id.settings_resources_of_materials_container);
        LinearLayout materialsCloneContainer = findViewById(R.id.settings_resources_of_materials_clone_container);
        File root = Environment.getExternalStorageDirectory();
        File appFolder = new File(root.getAbsolutePath() + File.separator +
                context.getString(R.string.resources_parent_dir_name) + File.separator +
                context.getString(R.string.resources_dir_name));

        File[] files = appFolder.listFiles();
        //TODO: sort by date?
        //TODO: filtruj tylko z prefixem i png

        final int materialsInRow = context.getResources().getInteger(R.integer.settings_material_number_of_materials_in_row);
        for (int i = 0; i <= files.length / materialsInRow; i++) {
            LinearLayout.LayoutParams paramsOfSubContainer = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            LinearLayout subContainer = new LinearLayout(context);
            subContainer.setLayoutParams(paramsOfSubContainer);
            subContainer.setOrientation(LinearLayout.HORIZONTAL);
            subContainer.setWeightSum(1.0f);

            LinearLayout cloneSubContainer = new LinearLayout(context);
            cloneSubContainer.setLayoutParams(paramsOfSubContainer);
            cloneSubContainer.setOrientation(LinearLayout.HORIZONTAL);
            cloneSubContainer.setWeightSum(1.0f);

            for(int j = 0; j < materialsInRow; j++) {
                if (i * materialsInRow + j >= files.length) {
                    break;
                }

                LinearLayout.LayoutParams paramsOfSingleMaterial = new LinearLayout.LayoutParams(
                        0, 60, 0.2f);

                final Button cloneBackgroundSingleMaterial = new Button(context);
                cloneBackgroundSingleMaterial.setLayoutParams(paramsOfSingleMaterial);
                cloneBackgroundSingleMaterial.setBackground(ContextCompat.getDrawable(context, R.drawable.settings_resources_of_materials_disabled));

                //TODO: read and set enabled (set drawable)

                cloneBackgroundSingleMaterial.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        cloneBackgroundSingleMaterial.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        final int size = cloneBackgroundSingleMaterial.getWidth();
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
                        cloneBackgroundSingleMaterial.setLayoutParams(layoutParams);
                    }
                });

                final Button singleMaterial = new Button(context);
                singleMaterial.setLayoutParams(paramsOfSingleMaterial);
                singleMaterial.setTag(files[i * materialsInRow + j].toString());

                try {
                    Drawable background = Drawable.createFromStream(new FileInputStream(files[i * materialsInRow + j]), null);
                    singleMaterial.setBackground(background);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                singleMaterial.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        singleMaterial.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        final int size = singleMaterial.getWidth();
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
                        singleMaterial.setLayoutParams(layoutParams);
                    }
                });

                singleMaterial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Material material = new Material();
                        material.material = singleMaterial;
                        material.cloneOfBackground = cloneBackgroundSingleMaterial;
                        selectMaterialInTabMaterial(material);
                    }
                });

                subContainer.addView(singleMaterial);
                cloneSubContainer.addView(cloneBackgroundSingleMaterial);
            }

            materialsContainer.addView(subContainer);
            materialsCloneContainer.addView(cloneSubContainer);
        }
    }

    private void selectMaterialInTabMaterial(Material material) {
        boolean isSelected = false;
        int id;
        for (id = 0; id < selectedMaterials.size(); id++) {
            if (selectedMaterials.get(id).material == material.material) {
                isSelected = true;
                break;
            }
        }

        boolean isEnabled = false;
        for (int i = 0; i < enabledMaterials.size(); i++) {
            if (enabledMaterials.get(i).material == material.material) {
                isEnabled = true;
                break;
            }
        }

        if (isSelected) {
            if (isEnabled) {
                material.cloneOfBackground.setBackground(
                        ContextCompat.getDrawable(context, R.drawable.settings_resources_of_materials_enabled));
            } else {
                material.cloneOfBackground.setBackground(
                        ContextCompat.getDrawable(context, R.drawable.settings_resources_of_materials_disabled));
            }
            selectedMaterials.remove(id);
        } else {
            if (isEnabled) {
                material.cloneOfBackground.setBackground(
                        ContextCompat.getDrawable(context, R.drawable.settings_resources_of_materials_enabled_selected));
            } else {
                material.cloneOfBackground.setBackground(
                        ContextCompat.getDrawable(context, R.drawable.settings_resources_of_materials_disabled_selected));
            }
            selectedMaterials.add(material);
        }
    }

    private void removeSelectedMaterials() {
        for (int i = 0; i < selectedMaterials.size(); i++) {
            Button materialToRemove = selectedMaterials.get(i).material;
            String backgroundFilePath = materialToRemove.getTag().toString();

            if (!(new File(backgroundFilePath).delete())) {
                Toast.makeText(context, R.string.error_message_removing_material_failed, Toast.LENGTH_SHORT).show();
            }

            //TODO update shared settings
        }
        selectedMaterials.clear();
        enabledMaterials.clear();
        loadMenuTabMaterial();
    }

    private void loadMenuTabLearning() {
        selectedTab = availableTabs.learning;
        setContentView(R.layout.activity_settings_tab_learning);

        final Drawable background = ContextCompat.getDrawable(context, R.drawable.settings_difficulty_level_buttons);
        final Drawable backgroundSelected = ContextCompat.getDrawable(context, R.drawable.settings_difficulty_level_buttons_selected);
        final Button setEasyLevelButton = findViewById(R.id.set_difficulty_level_easy);
        final Button setMediumLevelButton = findViewById(R.id.set_difficulty_level_medium);
        final Button setHardLevelButton = findViewById(R.id.set_difficulty_level_hard);
        setEasyLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMediumLevelButton.setBackground(background);
                setHardLevelButton.setBackground(background);
                view.setBackground(backgroundSelected);
            }
        });
        setMediumLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEasyLevelButton.setBackground(background);
                setHardLevelButton.setBackground(background);
                view.setBackground(backgroundSelected);
            }
        });
        setHardLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEasyLevelButton.setBackground(background);
                setMediumLevelButton.setBackground(background);
                view.setBackground(backgroundSelected);
            }
        });

        //TODO read and set current level
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
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
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
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
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
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
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