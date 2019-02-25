package com.pg.mikszo.friendlyletters.drawing.configurationApp;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pg.mikszo.friendlyletters.R;
import com.pg.mikszo.friendlyletters.settings.ColorsManager;
import com.pg.mikszo.friendlyletters.settings.Settings;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;

import java.util.ArrayList;
import java.util.List;

public class TabMenuAspect {

    private Activity activity;
    private SettingsManager settingsManager;
    private Settings settings;
    private enum availableAspectSections { materialSection, traceSection, backgroundSection }

    public TabMenuAspect(Activity activity, SettingsManager settingsManager, Settings settings) {
        this.activity = activity;
        this.settingsManager = settingsManager;
        this.settings = settings;
        activity.setContentView(R.layout.activity_settings_tab_aspect);

        int[] materialColors = new ColorsManager(activity).getAllMaterialColors();
        Drawable[] materialColorsDrawable = {
                ContextCompat.getDrawable(activity, R.drawable.settings_material_color_1),
                ContextCompat.getDrawable(activity, R.drawable.settings_material_color_2),
                ContextCompat.getDrawable(activity, R.drawable.settings_material_color_3),
                ContextCompat.getDrawable(activity, R.drawable.settings_material_color_4),
                ContextCompat.getDrawable(activity, R.drawable.settings_material_color_5)};
        LinearLayout materialColorsContainer = activity.findViewById(R.id.settings_material_colors_container);
        createTabAspectSection(materialColors, materialColorsDrawable, materialColorsContainer, availableAspectSections.materialSection);

        int[] traceColors = new ColorsManager(activity).getAllTraceColors();
        Drawable[] traceColorsDrawable = {
                ContextCompat.getDrawable(activity, R.drawable.settings_trace_color_1),
                ContextCompat.getDrawable(activity, R.drawable.settings_trace_color_2),
                ContextCompat.getDrawable(activity, R.drawable.settings_trace_color_3),
                ContextCompat.getDrawable(activity, R.drawable.settings_trace_color_4),
                ContextCompat.getDrawable(activity, R.drawable.settings_trace_color_5)};
        LinearLayout traceColorsContainer = activity.findViewById(R.id.settings_trace_colors_container);
        createTabAspectSection(traceColors, traceColorsDrawable, traceColorsContainer, availableAspectSections.traceSection);

        int[] backgroundColors = new ColorsManager(activity).getAllBackgroundColors();
        Drawable[] backgroundColorsDrawable = {
                ContextCompat.getDrawable(activity, R.drawable.settings_background_color_1),
                ContextCompat.getDrawable(activity, R.drawable.settings_background_color_2),
                ContextCompat.getDrawable(activity, R.drawable.settings_background_color_3),
                ContextCompat.getDrawable(activity, R.drawable.settings_background_color_4)};
        LinearLayout backgroundColorsContainer = activity.findViewById(R.id.settings_background_colors_container);
        createTabAspectSection(backgroundColors, backgroundColorsDrawable, backgroundColorsContainer, availableAspectSections.backgroundSection);
    }

    private void createTabAspectSection(final int[] buttonColors, final Drawable[] buttonColorsDrawable,
                                        final LinearLayout layoutContainer, final availableAspectSections section) {
        if (buttonColors.length != buttonColorsDrawable.length) {
            return;
        }

        String[] selectedElements;
        LinearLayout.LayoutParams params;
        if (section == availableAspectSections.materialSection) {
            selectedElements = settings.materialColors;
            params = new LinearLayout.LayoutParams(70, 70);
        } else if (section == availableAspectSections.traceSection) {
            selectedElements = settings.traceColors;
            params = new LinearLayout.LayoutParams(70, 70);
        } else {
            selectedElements = settings.backgroundColors;
            params = new LinearLayout.LayoutParams(140, 90);
        }
        params.setMargins(15, 0, 15, 5);

        //TODO: selected and unselected are not in same height
        for (int colorId = 0; colorId < buttonColors.length; colorId++) {
            Drawable drawable;
            if (section == availableAspectSections.backgroundSection) {
                drawable = ContextCompat.getDrawable(activity, R.drawable.settings_square_drawable);
            } else {
                drawable = ContextCompat.getDrawable(activity, R.drawable.settings_circle_drawable);
            }

            if (drawable != null) {
                final Button newButton = new Button(activity);
                newButton.setLayoutParams(params);
                newButton.setTag(colorId);

                boolean isSelected = false;
                for (String selectedElement : selectedElements) {
                    if (selectedElement.equals(Integer.toString(colorId))) {
                        isSelected = true;
                        break;
                    }
                }

                if (isSelected) {
                    newButton.setBackground(buttonColorsDrawable[colorId]);
                } else {
                    drawable.setColorFilter(new PorterDuffColorFilter(buttonColors[colorId], PorterDuff.Mode.MULTIPLY));
                    newButton.setBackground(drawable);
                }

                newButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        int idOfButton = (int) v.getTag();
                        if (v.getBackground() == buttonColorsDrawable[idOfButton]) {

                            Drawable drawable;
                            int availableElements;
                            if (section == availableAspectSections.materialSection) {
                                drawable = ContextCompat.getDrawable(activity, R.drawable.settings_circle_drawable);
                                availableElements = settings.materialColors.length;
                            } else if (section == availableAspectSections.traceSection) {
                                drawable = ContextCompat.getDrawable(activity, R.drawable.settings_circle_drawable);
                                availableElements = settings.traceColors.length;
                            } else {
                                drawable = ContextCompat.getDrawable(activity, R.drawable.settings_square_drawable);
                                availableElements = settings.backgroundColors.length;
                            }

                            if (availableElements <= 1) {
                                Toast.makeText(activity, R.string.information_message_least_one_element_must_be_available, Toast.LENGTH_SHORT).show();
                            } else if (drawable != null) {
                                drawable.setColorFilter(new PorterDuffColorFilter(buttonColors[idOfButton], PorterDuff.Mode.MULTIPLY));
                                v.setBackground(drawable);
                            }
                        }
                        else {
                            v.setBackground(buttonColorsDrawable[idOfButton]);
                        }

                        updateSettingFromTabAspect(section, buttonColorsDrawable);
                    }
                });

                layoutContainer.addView(newButton);
            }
        }
    }

    private void updateSettingFromTabAspect(final availableAspectSections section, final Drawable[] buttonColorsDrawable) {
        LinearLayout layoutContainer;
        if (section == availableAspectSections.materialSection) {
            layoutContainer = activity.findViewById(R.id.settings_material_colors_container);
        } else if (section == availableAspectSections.traceSection) {
            layoutContainer = activity.findViewById(R.id.settings_trace_colors_container);
        } else {
            layoutContainer = activity.findViewById(R.id.settings_background_colors_container);
        }

        List<String> enabledList = new ArrayList<>();
        for (int i = 0; i < layoutContainer.getChildCount(); i++) {
            View childView = layoutContainer.getChildAt(i);
            int idOfButton = (int) childView.getTag();
            if (childView.getBackground() == buttonColorsDrawable[idOfButton]) {
                enabledList.add(Integer.toString(i));
            }
        }

        if (section == availableAspectSections.materialSection) {
            settings.materialColors = enabledList.toArray(new String[0]);
        } else if (section == availableAspectSections.traceSection) {
            settings.traceColors = enabledList.toArray(new String[0]);
        } else {
            settings.backgroundColors = enabledList.toArray(new String[0]);
        }

        settingsManager.saveAllSettings(settings);
    }
}