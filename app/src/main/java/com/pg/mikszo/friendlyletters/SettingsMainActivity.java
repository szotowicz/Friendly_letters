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

import com.pg.mikszo.friendlyletters.drawing.configurationApp.AddingShapeView;

public class SettingsMainActivity extends Activity {

    private AddingShapeView addingShapeView;
    private enum availableTabs { aspect, material, learning, reinforcement}
    private availableTabs selectedTab;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplication().getBaseContext();
        loadMenuTabAspect();

        //Todo: change
        addingShapeView = findViewById(R.id.addingShapeView);

        if (!FileHelper.isAppFolderExists(this)) {
            FileHelper.copyDefaultImages(this);
        }
    }

    public void addShapeToResourcesOnClick(View view) {
        addingShapeView.saveScreenImage();
        addingShapeView.cleanScreen();
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
        createTabAspectSection(materialColors, materialColorsDrawable, materialColorsContainer, true);

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
        createTabAspectSection(traceColors, traceColorsDrawable, traceColorsContainer, true);

        int[] backgroundColors = {
                ContextCompat.getColor(context, R.color.color_settings_background_1),
                ContextCompat.getColor(context, R.color.color_settings_background_2),
                ContextCompat.getColor(context, R.color.color_settings_background_3)};
        Drawable[] backgroundColorsDrawable = {
                ContextCompat.getDrawable(context, R.drawable.settings_background_color_1),
                ContextCompat.getDrawable(context, R.drawable.settings_background_color_2),
                ContextCompat.getDrawable(context, R.drawable.settings_background_color_3)};
        LinearLayout backgroundColorsContainer = findViewById(R.id.settings_background_colors_container);
        createTabAspectSection(backgroundColors, backgroundColorsDrawable, backgroundColorsContainer, false);
    }

    private void createTabAspectSection(final int[] buttonColors, final Drawable[] buttonColorsDrawable, final LinearLayout layoutContainer, final boolean isCircle) {
        if (buttonColors.length != buttonColorsDrawable.length) {
            return;
        }

        for (int colorId = 0; colorId < buttonColors.length; colorId++) {
            Drawable drawable;
            if (isCircle) {
                drawable = ContextCompat.getDrawable(context, R.drawable.settings_circle_drawable);
            } else {
                drawable = ContextCompat.getDrawable(context, R.drawable.settings_square_drawable);
            }
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(buttonColors[colorId], PorterDuff.Mode.MULTIPLY));

                LinearLayout.LayoutParams params;
                if (isCircle) {
                    params = new LinearLayout.LayoutParams(70, 70);
                    params.setMargins(30, 0, 0, 0);
                } else {
                    params = new LinearLayout.LayoutParams(140, 90);
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
                            Drawable drawable;
                            if (isCircle) {
                                drawable = ContextCompat.getDrawable(context, R.drawable.settings_circle_drawable);
                            } else {
                                drawable = ContextCompat.getDrawable(context, R.drawable.settings_square_drawable);
                            }
                            if (drawable != null) {
                                drawable.setColorFilter(new PorterDuffColorFilter(buttonColors[idOfButton], PorterDuff.Mode.MULTIPLY));
                                v.setBackground(drawable);
                            }
                        }
                        else {
                            v.setBackground(buttonColorsDrawable[idOfButton]);
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
        //TODO
    }

    private void loadMenuTabReinforcement() {
        selectedTab = availableTabs.reinforcement;
        setContentView(R.layout.activity_settings_tab_reinforcement);
        //TODO
    }
}