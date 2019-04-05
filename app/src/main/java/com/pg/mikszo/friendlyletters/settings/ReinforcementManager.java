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
package com.pg.mikszo.friendlyletters.settings;

import android.content.Context;

import com.pg.mikszo.friendlyletters.R;

public class ReinforcementManager {
    private Context context;

    public ReinforcementManager(Context context) {
        this.context = context;
    }

    public String[] getAvailableCommands() {
        return new String[]{
                context.getResources().getString(R.string.settings_tab_reinforcement_command_1) + ": " +
                        context.getResources().getString(R.string.settings_tab_reinforcement_command_mark_tag),
                context.getResources().getString(R.string.settings_tab_reinforcement_command_2_letter) + "/" +
                        context.getResources().getString(R.string.settings_tab_reinforcement_command_2_digit) + ": " +
                        context.getResources().getString(R.string.settings_tab_reinforcement_command_mark_tag)
        };
    }

    public String[] getAvailableVerbalPraises() {
        return new String[]{
                context.getResources().getString(R.string.settings_tab_reinforcement_verbal_praise_1),
                context.getResources().getString(R.string.settings_tab_reinforcement_verbal_praise_2),
                context.getResources().getString(R.string.settings_tab_reinforcement_verbal_praise_3),
                context.getResources().getString(R.string.settings_tab_reinforcement_verbal_praise_4)
        };
    }
}
