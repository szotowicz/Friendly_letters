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
package com.pg.mikszo.friendlyletters.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pg.mikszo.friendlyletters.R;
import com.pg.mikszo.friendlyletters.settings.Configuration;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;

import java.util.regex.Pattern;

import me.grantland.widget.AutofitHelper;

public class SettingsMainActivity extends BaseActivity {

    private SettingsManager settingsManager;
    private Configuration[] allConfigurations;
    private TextView activeConfiguration;
    private Toast activateConfigurationToast;

    @Override
    @SuppressLint("ShowToast")
    // This function is loaded in every BaseActivity child
    protected void loadOnCreateView() {
        this.settingsManager = new SettingsManager(this);
        allConfigurations = settingsManager.getAllConfigurations();
        activateConfigurationToast = Toast.makeText(this,
                R.string.information_message_configuration_has_been_activated,
                Toast.LENGTH_SHORT);

        setContentView(R.layout.activity_settings_main);
        loadAvailableConfigurations();
    }

    public void createNewConfigurationsOnClick(View view) {
        activateConfigurationToast.cancel();
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

            final TextView configurationNameTextView = new TextView(this);
            configurationNameTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.9f));
            configurationNameTextView.setText(allConfigurations[i].configurationName);
            configurationNameTextView.setTextSize(20f);
            configurationNameTextView.setGravity(Gravity.CENTER);
            configurationNameTextView.setSingleLine();
            configurationNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activateConfigurationToast.cancel();
                    activateConfiguration(configurationID);
                    activeConfiguration = (TextView)view;
                    ((TextView)view).setTextColor(getResources().getColor(R.color.color_settings_configuration_active));
                    ((TextView)view).setTypeface(null, Typeface.BOLD);
                    view.setBackgroundResource(R.drawable.settings_configuration_active);
                }
            });
            if (allConfigurations[i].configurationActivated) {
                activeConfiguration = configurationNameTextView;
                activeConfiguration.setTextColor(getResources().getColor(R.color.color_settings_configuration_active));
                activeConfiguration.setTypeface(null, Typeface.BOLD);
                activeConfiguration.setBackgroundResource(R.drawable.settings_configuration_active);
            } else {
                configurationNameTextView.setTextColor(getResources().getColor(R.color.color_settings_configuration_nonactive));
                configurationNameTextView.setTypeface(null, Typeface.NORMAL);
                configurationNameTextView.setBackgroundResource(R.drawable.settings_configuration_nonactive);
            }
            AutofitHelper.create(configurationNameTextView);
            newConfiguration.addView(configurationNameTextView);

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
                    activateConfigurationToast.cancel();
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

                            configurationNameTextView.setLayoutParams(new LinearLayout.LayoutParams(
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
                    activateConfigurationToast.cancel();
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
                    activateConfigurationToast.cancel();
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
        activateConfigurationToast = Toast.makeText(this,
                R.string.information_message_configuration_has_been_activated,
                Toast.LENGTH_SHORT);
        activateConfigurationToast.show();
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
            Toast.makeText(getBaseContext(),
                    R.string.information_message_provided_incorrect_length_configuration_name,
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        Pattern pattern = Pattern.compile("[^a-z0-9ąćęłóśźż ]", Pattern.CASE_INSENSITIVE);
        if (pattern.matcher(nameForConfiguration).find()) {
            Toast.makeText(getBaseContext(),
                    R.string.information_message_provided_incorrect_configuration_name,
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}