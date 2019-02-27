package com.pg.mikszo.friendlyletters.drawing.configurationApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pg.mikszo.friendlyletters.FileHelper;
import com.pg.mikszo.friendlyletters.R;
import com.pg.mikszo.friendlyletters.settings.Settings;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TabMenuMaterial {

    private Activity activity;
    private SettingsManager settingsManager;
    private Settings settings;
    private List<Material> selectedMaterials = new ArrayList<>();
    private List<Material> enabledMaterials = new ArrayList<>();

    public TabMenuMaterial(Activity activity, SettingsManager settingsManager, Settings settings) {
        this.activity = activity;
        this.settingsManager = settingsManager;
        this.settings = settings;

        createViewElements();
    }

    private void createViewElements() {
        activity.setContentView(R.layout.activity_settings_tab_material);

        setOnClickActions();
        selectedMaterials.clear();
        enabledMaterials.clear();

        LinearLayout materialsContainer = activity.findViewById(R.id.settings_resources_of_materials_container);
        LinearLayout materialsCloneContainer = activity.findViewById(R.id.settings_resources_of_materials_clone_container);

        File[] files = FileHelper.getAllFilesFromAppFolderOldestFirst(activity);
        if (files.length < activity.getResources().getInteger(R.integer.settings_material_minimum_number_of_materials)) {
            askAboutImportDefaultAssets();
        }

        final int materialsInRow = activity.getResources().getInteger(R.integer.settings_material_number_of_materials_in_row);
        for (int i = 0; i <= files.length / materialsInRow; i++) {
            LinearLayout.LayoutParams paramsOfSubContainer = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            LinearLayout subContainer = new LinearLayout(activity);
            subContainer.setLayoutParams(paramsOfSubContainer);
            subContainer.setOrientation(LinearLayout.HORIZONTAL);
            subContainer.setWeightSum(1.0f);

            LinearLayout cloneSubContainer = new LinearLayout(activity);
            cloneSubContainer.setLayoutParams(paramsOfSubContainer);
            cloneSubContainer.setOrientation(LinearLayout.HORIZONTAL);
            cloneSubContainer.setWeightSum(1.0f);

            for(int j = 0; j < materialsInRow; j++) {
                if (i * materialsInRow + j >= files.length) {
                    break;
                }

                LinearLayout.LayoutParams paramsOfSingleMaterial = new LinearLayout.LayoutParams(
                        0, 60, 0.2f);

                final Button cloneBackgroundSingleMaterial = new Button(activity);
                cloneBackgroundSingleMaterial.setLayoutParams(paramsOfSingleMaterial);

                cloneBackgroundSingleMaterial.getViewTreeObserver().addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                cloneBackgroundSingleMaterial.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                final int size = cloneBackgroundSingleMaterial.getWidth();
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
                                cloneBackgroundSingleMaterial.setLayoutParams(layoutParams);
                            }
                        });

                final Button singleMaterial = new Button(activity);
                singleMaterial.setLayoutParams(paramsOfSingleMaterial);
                singleMaterial.setTag(files[i * materialsInRow + j].toString());

                try {
                    Drawable background = Drawable.createFromStream(new FileInputStream(files[i * materialsInRow + j]), null);
                    singleMaterial.setBackground(background);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                singleMaterial.getViewTreeObserver().addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
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
                        selectMaterialInTabMaterial(new Material(singleMaterial, cloneBackgroundSingleMaterial));
                    }
                });

                if (isMaterialEnabled(files[i * materialsInRow + j], settings.availableShapes)) {
                    cloneBackgroundSingleMaterial.setBackground(
                            ContextCompat.getDrawable(activity, R.drawable.settings_resources_of_materials_enabled));
                    enabledMaterials.add(new Material(singleMaterial, cloneBackgroundSingleMaterial));
                } else {
                    cloneBackgroundSingleMaterial.setBackground(
                            ContextCompat.getDrawable(activity, R.drawable.settings_resources_of_materials_disabled));
                }

                subContainer.addView(singleMaterial);
                cloneSubContainer.addView(cloneBackgroundSingleMaterial);
            }

            materialsContainer.addView(subContainer);
            materialsCloneContainer.addView(cloneSubContainer);
        }

        updateSettingFromTabMaterial();
    }

    private void setOnClickActions() {
        Button removeMaterialButton = activity.findViewById(R.id.tab_material_remove_material);
        removeMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedMaterials.size() == 0) {
                    Toast.makeText(activity, R.string.information_message_some_material_must_be_selected, Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    builder.setTitle(R.string.check_removing_decisions_title);
                    builder.setMessage(R.string.check_removing_decisions_message);

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
            }
        });

        Button enableMaterialButton = activity.findViewById(R.id.tab_material_enable_material);
        enableMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedMaterials.size() == 0) {
                    Toast.makeText(activity, R.string.information_message_some_material_must_be_selected, Toast.LENGTH_SHORT).show();
                } else {
                    // Firstly enable all required and disable rest, but least one element must be available
                    int startPoint = 0;
                    int endPoint = selectedMaterials.size() - 1;
                    List<Material> sortedSelectedMaterials = new ArrayList<>(selectedMaterials.size());
                    for (int i = 0; i < selectedMaterials.size(); i++) {
                        sortedSelectedMaterials.add(i, new Material());
                    }
                    for (int i = 0; i < selectedMaterials.size(); i++) {
                        boolean isEnabled = false;
                        for (int j = 0; j < enabledMaterials.size(); j++) {
                            if (selectedMaterials.get(i).material == enabledMaterials.get(j).material) {
                                isEnabled = true;
                                break;
                            }
                        }

                        if (isEnabled) {
                            sortedSelectedMaterials.remove(endPoint);
                            sortedSelectedMaterials.add(endPoint--, selectedMaterials.get(i));
                        } else {
                            sortedSelectedMaterials.remove(startPoint);
                            sortedSelectedMaterials.add(startPoint++, selectedMaterials.get(i));
                        }
                    }

                    for (int i = 0; i < sortedSelectedMaterials.size(); i++) {
                        boolean isEnabled = false;
                        int enabledMaterialsID;
                        for (enabledMaterialsID = 0; enabledMaterialsID < enabledMaterials.size(); enabledMaterialsID++) {
                            if (sortedSelectedMaterials.get(i).material == enabledMaterials.get(enabledMaterialsID).material) {
                                isEnabled = true;
                                break;
                            }
                        }

                        for (int selectedMaterialsID = 0; selectedMaterialsID < selectedMaterials.size(); selectedMaterialsID++) {
                            if (sortedSelectedMaterials.get(i).material == selectedMaterials.get(selectedMaterialsID).material) {
                                selectedMaterials.remove(selectedMaterialsID);
                                break;
                            }
                        }

                        if (isEnabled) {
                            if (enabledMaterials.size() <= 1) {
                                Toast.makeText(activity, R.string.information_message_least_one_element_must_be_available, Toast.LENGTH_SHORT).show();
                            } else {
                                sortedSelectedMaterials.get(i).cloneOfBackground.setBackground(
                                        ContextCompat.getDrawable(activity, R.drawable.settings_resources_of_materials_disabled));
                                enabledMaterials.remove(enabledMaterialsID);
                            }
                        } else {
                            sortedSelectedMaterials.get(i).cloneOfBackground.setBackground(
                                    ContextCompat.getDrawable(activity, R.drawable.settings_resources_of_materials_enabled));
                            enabledMaterials.add(sortedSelectedMaterials.get(i));
                        }
                    }

                    updateSettingFromTabMaterial();
                }
            }
        });
    }

    private boolean isMaterialEnabled(File material, String[] enabledMaterials) {
        boolean isEnabled = false;
        for (String enabled : enabledMaterials) {
            if (material.toString().toLowerCase().trim().contains(enabled.toLowerCase().trim())) {
                isEnabled = true;
                break;
            }
        }

        return isEnabled;
    }

    private void removeSelectedMaterials() {
        if (selectedMaterials.size() >= FileHelper.getNumberOfAllFilesInAppFolder(activity)) {
            Toast.makeText(activity, R.string.information_message_least_one_element_must_be_available, Toast.LENGTH_SHORT).show();
        } else {
            boolean showMessageAboutRemovingEnabledMaterials = false;
            for (int i = 0; i < selectedMaterials.size(); i++) {
                Button materialToRemove = selectedMaterials.get(i).material;

                boolean isEnabled = false;
                for (int j = 0; j < enabledMaterials.size(); j++) {
                    if (enabledMaterials.get(j).material == materialToRemove) {
                        isEnabled = true;
                        showMessageAboutRemovingEnabledMaterials = true;
                        break;
                    }
                }

                if (!isEnabled) {
                    String backgroundFilePath = materialToRemove.getTag().toString();

                    if (!(new File(backgroundFilePath).delete())) {
                        Toast.makeText(activity, R.string.information_message_removing_material_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            if (showMessageAboutRemovingEnabledMaterials) {
                Toast.makeText(activity, R.string.information_message_removing_is_possible_for_disabled_materials, Toast.LENGTH_LONG).show();
            }

            selectedMaterials.clear();
            enabledMaterials.clear();
            createViewElements();
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
                        ContextCompat.getDrawable(activity, R.drawable.settings_resources_of_materials_enabled));
            } else {
                material.cloneOfBackground.setBackground(
                        ContextCompat.getDrawable(activity, R.drawable.settings_resources_of_materials_disabled));
            }
            selectedMaterials.remove(id);
        } else {
            if (isEnabled) {
                material.cloneOfBackground.setBackground(
                        ContextCompat.getDrawable(activity, R.drawable.settings_resources_of_materials_enabled_selected));
            } else {
                material.cloneOfBackground.setBackground(
                        ContextCompat.getDrawable(activity, R.drawable.settings_resources_of_materials_disabled_selected));
            }
            selectedMaterials.add(material);
        }
    }

    private void updateSettingFromTabMaterial() {
        List<String> enabledList = new ArrayList<>();
        for (Material mat : enabledMaterials) {
            String backgroundFilePath = mat.material.getTag().toString();
            String backgroundFileName = new File(backgroundFilePath).getName();
            enabledList.add(backgroundFileName);
        }

        settings.availableShapes = enabledList.toArray(new String[0]);
        settingsManager.saveAllSettings(settings);
    }

    private void askAboutImportDefaultAssets() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle(R.string.check_importing_decisions_title);
        builder.setMessage(R.string.check_importing_decisions_message);

        builder.setPositiveButton(R.string.check_decisions_positive_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                FileHelper.copyDefaultImages(activity);
                settings = new SettingsManager(activity).updateSettingsAvailableShapes(settings);
                createViewElements();
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
}