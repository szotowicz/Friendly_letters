package com.pg.mikszo.friendlyletters.settings;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

public class SettingsManager {
    private Context context;
    private String settingsFile = "settings/settings.json";

    public SettingsManager(Context context) {
        this.context = context;
    }

    public String getJSONFromSettings(Settings settings) {
        return new Gson().toJson(settings);
    }

    public Settings getSettingsFromJSON() {
        Settings settings = null;
        String json = readSettingsFromAsset();
        settings = new Gson().fromJson(json, Settings.class);
        return settings;
    }

    private String readSettingsFromAsset() {
        String json = null;
        try {
            InputStream is = context.getAssets().open(settingsFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    private void saveSettingsToAsset(Settings settings) {
        String json = getJSONFromSettings(settings);
        // todo: nie można zapisywać w asset to trzeba kopiować gdzie? Czy sharedPref?
    }
}