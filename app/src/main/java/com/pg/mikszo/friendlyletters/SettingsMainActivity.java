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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.pg.mikszo.friendlyletters.settings.Settings;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;

public class SettingsMainActivity extends Activity {

    private SettingsManager settingsManager;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsManager = new SettingsManager(this);
        settings = settingsManager.getAppSettings();

        if (!FileHelper.isAppFolderExists(this)) {
            FileHelper.copyDefaultImages(this);
            settings = new SettingsManager(this).updateSettingsAvailableShapes(settings);
        }

        setContentView(R.layout.activity_settings_main);
        loadAvailableConfigurations();
    }

    public void createNewConfigurationsOnClick(View view) {
        //todo
    }

    private void loadAvailableConfigurations() {
        LinearLayout configurationsContainer = findViewById(R.id.settings_configurations_container);

        for (int i = 0; i < 7; i++) {
            LinearLayout newConfiguration = new LinearLayout(this);
            newConfiguration.setOrientation(LinearLayout.HORIZONTAL);
            newConfiguration.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            newConfiguration.setWeightSum(1.0f);

            Button configurationName = new Button(this);
            configurationName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f));
            configurationName.setText("Przykładowa konfiguracja");
            configurationName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Load settings
                    startActivity(new Intent(getBaseContext(), SettingsTabsActivity.class));
                }
            });
            newConfiguration.addView(configurationName);

            Button configurationCopy = new Button(this);
            configurationCopy.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));
            configurationCopy.setText("C");
            configurationCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO
                }
            });
            newConfiguration.addView(configurationCopy);

            Button configurationRemove = new Button(this);
            configurationRemove.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));
            configurationRemove.setText("R");
            configurationRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO
                }
            });
            newConfiguration.addView(configurationRemove);

            configurationsContainer.addView(newConfiguration);
        }
    }
}