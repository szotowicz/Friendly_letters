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
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
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
    private Button activeConfiguration;

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
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(),
                            R.string.information_message_provided_incorrect_configuration_name,
                            Toast.LENGTH_SHORT).show();
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
            LinearLayout.LayoutParams newConfigurationLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            newConfigurationLayoutParams.setMargins(0, 0, 0, 10);
            newConfiguration.setLayoutParams(newConfigurationLayoutParams);
            newConfiguration.setGravity(Gravity.CENTER);
            newConfiguration.setWeightSum(1.0f);

            final Button configurationNameBtn = new Button(this);
            configurationNameBtn.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.9f));
            configurationNameBtn.setText(allConfigurations[i].configurationName);
            configurationNameBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activateConfiguration(configurationID);
                    activeConfiguration = (Button)view;
                    ((Button)view).setTextColor(getResources().getColor(R.color.color_settings_configuration_active));
                    ((Button)view).setTypeface(null, Typeface.BOLD);
                    view.setBackgroundResource(R.drawable.settings_configuration_active);
                }
            });
            if (allConfigurations[i].configurationActivated) {
                activeConfiguration = configurationNameBtn;
                activeConfiguration.setTextColor(getResources().getColor(R.color.color_settings_configuration_active));
                activeConfiguration.setTypeface(null, Typeface.BOLD);
                activeConfiguration.setBackgroundResource(R.drawable.settings_configuration_active);
            } else {
                configurationNameBtn.setTextColor(getResources().getColor(R.color.color_settings_configuration_nonactive));
                configurationNameBtn.setTypeface(null, Typeface.NORMAL);
                configurationNameBtn.setBackgroundResource(R.drawable.settings_configuration_nonactive);
            }
            newConfiguration.addView(configurationNameBtn);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.08f);

            final Button configurationCopy = new Button(this);
            configurationCopy.setLayoutParams(params);
            configurationCopy.setBackgroundResource(R.drawable.settings_configurations_action_copy);
            configurationCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    duplicateConfiguration(configurationID);
                }
            });
            configurationCopy.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            configurationCopy.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            final int size = configurationCopy.getWidth();
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
                            layoutParams.setMargins(20, 0, 0, 0);
                            configurationCopy.setLayoutParams(layoutParams);

                            configurationNameBtn.setLayoutParams(new LinearLayout.LayoutParams(
                                    0,
                                    size,
                                    0.9f));
                        }
                    });
            newConfiguration.addView(configurationCopy);

            final Button configurationEdit = new Button(this);
            configurationEdit.setLayoutParams(params);
            configurationEdit.setBackgroundResource(R.drawable.settings_configurations_action_edit);
            configurationEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editConfiguration(configurationID);
                }
            });
            configurationEdit.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            configurationEdit.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            final int size = configurationEdit.getWidth();
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
                            layoutParams.setMargins(10, 0, 0, 0);
                            configurationEdit.setLayoutParams(layoutParams);
                        }
                    });
            newConfiguration.addView(configurationEdit);

            final Button configurationRemove = new Button(this);
            configurationRemove.setLayoutParams(params);
            configurationRemove.setBackgroundResource(R.drawable.settings_configurations_action_remove);
            configurationRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeConfiguration(configurationID);
                }
            });
            configurationRemove.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            configurationRemove.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            final int size = configurationRemove.getWidth();
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
                            layoutParams.setMargins(10, 0, 0, 0);
                            configurationRemove.setLayoutParams(layoutParams);
                        }
                    });
            newConfiguration.addView(configurationRemove);

            configurationsContainer.addView(newConfiguration);
        }
    }

    private void activateConfiguration(final int configurationID) {
        activeConfiguration.setTextColor(getResources()
                .getColor(R.color.color_settings_configuration_nonactive));
        activeConfiguration.setTypeface(null, Typeface.NORMAL);
        activeConfiguration.setBackgroundResource(R.drawable.settings_configuration_nonactive);
        for (Configuration configuration : allConfigurations) {
            if (configuration.configurationActivated) {
                configuration.configurationActivated = false;
            }
        }
        allConfigurations[configurationID].configurationActivated = true;
        settingsManager.updateFileWithConfigurations(allConfigurations);
        Toast.makeText(this,
                R.string.information_message_configuration_has_been_activated,
                Toast.LENGTH_SHORT).show();
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
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(),
                            R.string.information_message_provided_incorrect_configuration_name,
                            Toast.LENGTH_SHORT).show();
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
                    Toast.LENGTH_SHORT).show();
        } else if (allConfigurations.length == 1) {
            Toast.makeText(this,
                    R.string.information_message_least_one_configuration_must_be_available,
                    Toast.LENGTH_SHORT).show();
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
                            R.string.information_message_configuration_has_been_removed, Toast.LENGTH_SHORT).show();
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
        if (nameForConfiguration.length() <= 0 || nameForConfiguration.length() > 40) {
            return false;
        }

        Pattern pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        return !pattern.matcher(nameForConfiguration).find();
    }
}