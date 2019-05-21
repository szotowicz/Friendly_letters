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
package com.pg.mikszo.friendlyletters.views.configurationApp;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.pg.mikszo.friendlyletters.R;
import com.pg.mikszo.friendlyletters.settings.Configuration;
import com.pg.mikszo.friendlyletters.settings.ReinforcementManager;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;

public class TabMenuReinforcement {

    private Activity activity;
    private SettingsManager settingsManager;
    private Configuration configuration;
    private int configurationID;
    private enum availableSections { commands, verbalPraises }

    public TabMenuReinforcement(Activity activity, SettingsManager settingsManager, int configurationID) {
        this.activity = activity;
        this.settingsManager = settingsManager;
        this.configurationID = configurationID;
        this.configuration = settingsManager.getConfigurationById(configurationID);
        activity.setContentView(R.layout.activity_settings_tab_reinforcement);
        ((TextView)activity.findViewById(R.id.settings_configuration_name_label))
                .setText(configuration.configurationName);

        createViewElements();
    }

    private void createViewElements() {
        final String[] availableCommands = new ReinforcementManager(activity).getAvailableCommands();
        Button commandButton1 = activity.findViewById(R.id.reinforcement_command_1);
        Button commandButton2 = activity.findViewById(R.id.reinforcement_command_2);
        if (availableCommands.length >= 2) {
            commandButton1.setText(availableCommands[0]);
            commandButton2.setText(availableCommands[1]);
        }
        createTabReinforcementSection(new Button[] {commandButton1, commandButton2}, availableSections.commands);

        CheckBox commandsReading = activity.findViewById(R.id.settings_reinforcement_commands_reading);
        commandsReading.setChecked(configuration.commandsReading);
        commandsReading.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                configuration.commandsReading = !configuration.commandsReading;
                settingsManager.updateFileWithConfigurations(configuration, configurationID);
            }
        });

        CheckBox commandsDisplaying = activity.findViewById(R.id.settings_reinforcement_commands_displaying);
        commandsDisplaying.setChecked(configuration.commandsDisplaying);
        commandsDisplaying.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                configuration.commandsDisplaying = !configuration.commandsDisplaying;
                settingsManager.updateFileWithConfigurations(configuration, configurationID);
            }
        });

        createTabReinforcementSection(new Button[] {
                activity.findViewById(R.id.reinforcement_verbal_praise_1),
                activity.findViewById(R.id.reinforcement_verbal_praise_2),
                activity.findViewById(R.id.reinforcement_verbal_praise_3),
                activity.findViewById(R.id.reinforcement_verbal_praise_4)}, availableSections.verbalPraises);

        CheckBox verbalPraisesReading = activity.findViewById(R.id.settings_reinforcement_verbal_praises_reading);
        verbalPraisesReading.setChecked(configuration.verbalPraisesReading);
        if (configuration.testMode) {
            verbalPraisesReading.setEnabled(false);
        } else {
            verbalPraisesReading.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    configuration.verbalPraisesReading = !configuration.verbalPraisesReading;
                    settingsManager.updateFileWithConfigurations(configuration, configurationID);
                }
            });
        }
    }

    private void createTabReinforcementSection(final Button[] buttonsInSection, final availableSections section) {
        final Drawable activeButtonDrawable = ContextCompat.getDrawable(
                activity, R.drawable.settings_reinforcement_active_buttons);
        final Drawable nonactiveButtonDrawable = ContextCompat.getDrawable(
                activity, R.drawable.settings_reinforcement_nonactive_buttons);
        final String[] activeButtons;
        if (section == availableSections.commands) {
            activeButtons = configuration.availableCommands;
        } else {
            activeButtons = configuration.availableVerbalPraises;
            if (configuration.testMode) {
                for (Button buttonPraise : buttonsInSection) {
                    buttonPraise.setEnabled(false);
                }
            }
        }

        for (int buttonId = 0; buttonId < buttonsInSection.length; buttonId++) {
            final Button newButton = buttonsInSection[buttonId];

            if (isButtonActive(buttonId, activeButtons)) {
                newButton.setBackground(activeButtonDrawable);
                newButton.setTextColor(activity.getResources()
                        .getColor(R.color.color_settings_reinforcement_button_active_font));
            }

            final int finalButtonId = buttonId;
            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] activeButtons;
                    if (section == availableSections.commands) {
                        activeButtons = configuration.availableCommands;
                    } else {
                        activeButtons = configuration.availableVerbalPraises;
                    }

                    String[] newActiveButtons;
                    if (isButtonActive(finalButtonId, activeButtons)) {
                        view.setBackground(nonactiveButtonDrawable);
                        ((Button)view).setTextColor(activity.getResources()
                                .getColor(R.color.color_settings_reinforcement_button_nonactive_font));
                        newActiveButtons = new String[activeButtons.length - 1];
                        int index = 0;
                        for (String activeButton : activeButtons) {
                            if (!activeButton.equals(String.valueOf(finalButtonId))) {
                                newActiveButtons[index++] = activeButton;
                            }
                        }
                    } else {
                        view.setBackground(activeButtonDrawable);
                        ((Button)view).setTextColor(activity.getResources()
                                .getColor(R.color.color_settings_reinforcement_button_active_font));
                        newActiveButtons = new String[activeButtons.length + 1];
                        int index = 0;
                        for (String activeButton : activeButtons) {
                            newActiveButtons[index++] = activeButton;
                        }
                        newActiveButtons[index] = String.valueOf(finalButtonId);
                    }

                    if (section == availableSections.commands) {
                        configuration.availableCommands = newActiveButtons;
                    } else {
                        configuration.availableVerbalPraises = newActiveButtons;
                    }
                    settingsManager.updateFileWithConfigurations(configuration, configurationID);
                }
            });
        }
    }

    private boolean isButtonActive(int buttonId, String[] activeButtons) {
        for (String activeButton : activeButtons) {
            if (activeButton.equals(String.valueOf(buttonId))) {
                return true;
            }
        }
        return false;
    }
}