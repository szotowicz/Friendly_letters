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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pg.mikszo.friendlyletters.settings.Configuration;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;

import java.util.regex.Pattern;

public class SettingsMainActivity extends Activity {

    private SettingsManager settingsManager;
    private Configuration[] allConfigurations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.settingsManager = new SettingsManager(this);
        allConfigurations = settingsManager.getAllConfigurations();

        setContentView(R.layout.activity_settings_main);
        loadAvailableConfigurations();
    }

    public void createNewConfigurationsOnClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle(R.string.check_creating_configuration);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(R.string.check_decisions_positive_button_add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String nameForNewConfiguration = input.getText().toString().trim();
                if (validateNameForConfiguration(nameForNewConfiguration)) {
                    Configuration newConfiguration = settingsManager.getDefaultConfiguration();
                    newConfiguration.configurationName = nameForNewConfiguration;
                    newConfiguration.configurationActivated = false;
                    settingsManager.addNewConfiguration(newConfiguration);
                    allConfigurations = settingsManager.getAllConfigurations();
                    loadAvailableConfigurations();
                    Toast.makeText(getBaseContext(),
                            R.string.information_message_new_configuration_has_been_added,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(),
                            R.string.information_message_provided_incorrect_configuration_name,
                            Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.check_decisions_negative_button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void loadAvailableConfigurations() {
        LinearLayout configurationsContainer = findViewById(R.id.settings_configurations_container);
        configurationsContainer.removeAllViewsInLayout();

        for (int i = 0; i < allConfigurations.length; i++) {
            final int configurationID = i;

            LinearLayout newConfiguration = new LinearLayout(this);
            newConfiguration.setOrientation(LinearLayout.HORIZONTAL);
            newConfiguration.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            newConfiguration.setWeightSum(1.0f);

            Button configurationNameBtn = new Button(this);
            configurationNameBtn.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.79f));
            configurationNameBtn.setText(allConfigurations[i].configurationName);
            configurationNameBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activateConfiguration(configurationID);
                }
            });
            if (allConfigurations[i].configurationActivated) {
                configurationNameBtn.setTextColor(getResources().getColor(R.color.color_settings_configuration_active));
            }
            newConfiguration.addView(configurationNameBtn);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.07f);
            params.setMargins(20, 0, 0, 0);

            Button configurationCopy = new Button(this);
            configurationCopy.setLayoutParams(params);
            configurationCopy.setBackgroundResource(R.drawable.ic_content_copy_24dp);
            configurationCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    duplicateConfiguration(configurationID);
                }
            });
            newConfiguration.addView(configurationCopy);

            Button configurationEdit = new Button(this);
            configurationEdit.setLayoutParams(params);
            configurationEdit.setBackgroundResource(R.drawable.ic_edit_24dp);
            configurationEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editConfiguration(configurationID);
                }
            });
            newConfiguration.addView(configurationEdit);

            Button configurationRemove = new Button(this);
            configurationRemove.setLayoutParams(params);
            configurationRemove.setBackgroundResource(R.drawable.ic_delete_24dp);
            configurationRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeConfiguration(configurationID);
                }
            });
            newConfiguration.addView(configurationRemove);

            configurationsContainer.addView(newConfiguration);
        }
    }

    private void activateConfiguration(final int configurationID) {
        for (Configuration configuration : allConfigurations) {
            if (configuration.configurationActivated) {
                configuration.configurationActivated = false;
            }
        }
        allConfigurations[configurationID].configurationActivated = true;
        settingsManager.updateFileWithConfigurations(allConfigurations);
        loadAvailableConfigurations();
        Toast.makeText(this,
                R.string.information_message_configuration_has_been_activated,
                Toast.LENGTH_LONG).show();
    }

    private void duplicateConfiguration(final int configurationID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle(R.string.check_creating_configuration);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton(R.string.check_decisions_positive_button_add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String nameForNewConfiguration = input.getText().toString().trim();
                if (validateNameForConfiguration(nameForNewConfiguration)) {
                    Configuration newConfiguration = allConfigurations[configurationID];
                    newConfiguration.configurationName = nameForNewConfiguration;
                    newConfiguration.configurationActivated = false;
                    settingsManager.addNewConfiguration(newConfiguration);
                    allConfigurations = settingsManager.getAllConfigurations();
                    loadAvailableConfigurations();

                    Toast.makeText(getBaseContext(),
                            R.string.information_message_new_configuration_has_been_added,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(),
                            R.string.information_message_provided_incorrect_configuration_name,
                            Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.check_decisions_negative_button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void editConfiguration(final int configurationID) {
        Intent intent = new Intent(getBaseContext(), SettingsTabsActivity.class);
        intent.putExtra(getResources().getString(R.string.intent_name_configuration_id), configurationID);
        startActivity(intent);
    }

    private void removeConfiguration(final int configurationID) {
        if (allConfigurations[configurationID].configurationActivated) {
            Toast.makeText(this,
                    R.string.information_message_removing_activated_configuration,
                    Toast.LENGTH_LONG).show();
        } else if (allConfigurations.length == 1) {
            Toast.makeText(this,
                    R.string.information_message_least_one_configuration_must_be_available,
                    Toast.LENGTH_LONG).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            builder.setTitle(R.string.check_removing_decisions_title);
            builder.setMessage(R.string.check_removing_configuration_decisions_message);

            builder.setPositiveButton(R.string.check_decisions_positive_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    settingsManager.removeConfigurationById(configurationID);
                    allConfigurations = settingsManager.getAllConfigurations();
                    loadAvailableConfigurations();
                    Toast.makeText(getBaseContext(),
                            R.string.information_message_configuration_has_been_removed, Toast.LENGTH_LONG).show();
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

    private boolean validateNameForConfiguration(String nameForConfiguration) {
        if (nameForConfiguration.length() <= 1) {
            return false;
        }

        Pattern pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        return !pattern.matcher(nameForConfiguration).find();
    }
}