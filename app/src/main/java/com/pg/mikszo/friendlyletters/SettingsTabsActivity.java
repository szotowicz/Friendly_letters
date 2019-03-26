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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.pg.mikszo.friendlyletters.drawing.configurationApp.AddingShapeView;
import com.pg.mikszo.friendlyletters.drawing.configurationApp.TabMenuAspect;
import com.pg.mikszo.friendlyletters.drawing.configurationApp.TabMenuLearning;
import com.pg.mikszo.friendlyletters.drawing.configurationApp.TabMenuMaterial;
import com.pg.mikszo.friendlyletters.drawing.configurationApp.TabMenuReinforcement;
import com.pg.mikszo.friendlyletters.settings.Settings;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;

public class SettingsTabsActivity extends Activity {

    private SettingsManager settingsManager;
    private Settings settings;
    private enum availableTabs { aspect, material, learning, reinforcement, addMaterial }
    private availableTabs selectedTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsManager = new SettingsManager(this);
        settings = settingsManager.getAppSettings();

        if (!FileHelper.isAppFolderExists(this)) {
            FileHelper.copyDefaultImages(this);
            settings = new SettingsManager(this).updateSettingsAvailableShapes(settings);
        }

        new TabMenuAspect(this, settingsManager, settings);
    }

    public void tabAspectOnClick(View view) {
        if (selectedTab != availableTabs.aspect) {
            selectedTab = availableTabs.aspect;
            new TabMenuAspect(this, settingsManager, settings);
        }
    }

    public void tabMaterialOnClick(View view) {
        if (selectedTab != availableTabs.material) {
            selectedTab = availableTabs.material;
            new TabMenuMaterial(this, settingsManager, settings);
        }
    }

    public void tabLearningOnClick(View view) {
        if (selectedTab != availableTabs.learning) {
            selectedTab = availableTabs.learning;
            new TabMenuLearning(this, settingsManager, settings);
        }
    }

    public void tabReinforcementOnClick(View view) {
        if (selectedTab != availableTabs.reinforcement) {
            selectedTab = availableTabs.reinforcement;
            new TabMenuReinforcement(this, settingsManager, settings);
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

                TypedValue typedValue = new TypedValue();
                getResources().getValue(R.dimen.game_track_width_relative_to_size_of_field, typedValue, true);
                addingMaterialView.setStrokeWidth(size * typedValue.getFloat());
                addingMaterialView.setRadiusCursor(size * typedValue.getFloat());
            }
        });
    }

    public void returnToMainSettingsOnClick(View view) {
        startActivity(new Intent(getBaseContext(), SettingsMainActivity.class));
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
}