package com.pg.mikszo.friendlyletters.drawing.configurationApp;

import android.app.Activity;

import com.pg.mikszo.friendlyletters.R;
import com.pg.mikszo.friendlyletters.settings.Settings;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;

public class TabMenuReinforcement {

    private Activity activity;
    private SettingsManager settingsManager;
    private Settings settings;

    public TabMenuReinforcement(Activity activity, SettingsManager settingsManager, Settings settings) {
        this.activity = activity;
        this.settingsManager = settingsManager;
        this.settings = settings;
        activity.setContentView(R.layout.activity_settings_tab_reinforcement);

        //TODO
    }
}