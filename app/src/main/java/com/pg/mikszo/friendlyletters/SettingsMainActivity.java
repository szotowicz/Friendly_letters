package com.pg.mikszo.friendlyletters;

import android.app.Activity;
import android.os.Bundle;
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

public class SettingsMainActivity extends Activity {

    public SettingsManager settingsManager;
    public Settings settings;
    private enum availableTabs { aspect, material, learning, reinforcement, addMaterial }
    private availableTabs selectedTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsManager = new SettingsManager(this);
        settings = settingsManager.getAppSettings();
        new TabMenuAspect(this, settingsManager, settings);

        if (!FileHelper.isAppFolderExists(this)) {
            FileHelper.copyDefaultImages(this);
        }
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
}