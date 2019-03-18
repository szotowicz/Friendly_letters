/*
    Part of the master's thesis
    Topic: "Supporting the development of fine motor skills in children using IT tools"

    2019 by Mikolaj Szotowicz : https://github.com/szotowicz
*/
package com.pg.mikszo.friendlyletters.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.google.gson.Gson;
import com.pg.mikszo.friendlyletters.FileHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SettingsManager {

    private Context context;
    private final String sharedPreferencesPackage = "com.pg.mikszo.friendlyletters";
    private final String sharedPreferencesSourceFile = "friendlyletters";
    private final String sharedPreferencesDifficultyLevel = "difficultyLevel";
    private final String sharedPreferencesNumberOfLevels = "numberOfLevels";
    private final String sharedPreferencesNumberOfRepetitions = "numberOfRepetitions";
    private final String sharedPreferencesTimeLimit = "timeLimit";
    private final String sharedPreferencesMaterialColors = "materialColors";
    private final String sharedPreferencesTraceColors = "traceColors";
    private final String sharedPreferencesBackgroundColors = "backgroundColors";
    private final String sharedPreferencesAvailableShapes = "availableShapes";

    public SettingsManager(Context context) {
        this.context = context;
    }

    public Settings getAppSettings() {
        Settings settings = null;
        Settings defaultSettings = getSettingsFromJSON();
        try {
            Context packageContext = context.createPackageContext(sharedPreferencesPackage, Context.MODE_PRIVATE);
            SharedPreferences sharedPreferences = packageContext.getSharedPreferences(sharedPreferencesSourceFile, Context.MODE_PRIVATE);
            if (!areSettingsAdded(sharedPreferences)) {
                addSettingsToSharedPreferences(sharedPreferences, defaultSettings);
            }

            settings = new Settings();
            settings.difficultyLevel = sharedPreferences.getInt(
                    sharedPreferencesDifficultyLevel,
                    defaultSettings.difficultyLevel);
            settings.numberOfLevels = sharedPreferences.getInt(
                    sharedPreferencesNumberOfLevels,
                    defaultSettings.numberOfLevels);
            settings.numberOfRepetitions = sharedPreferences.getInt(
                    sharedPreferencesNumberOfRepetitions,
                    defaultSettings.numberOfRepetitions);
            settings.timeLimit = sharedPreferences.getInt(
                    sharedPreferencesTimeLimit,
                    defaultSettings.timeLimit);
            settings.materialColors = sharedPreferences.getString(
                    sharedPreferencesMaterialColors,
                    convertJsonArrayToString(defaultSettings.materialColors)).split(";");
            settings.traceColors = sharedPreferences.getString(
                    sharedPreferencesTraceColors,
                    convertJsonArrayToString(defaultSettings.traceColors)).split(";");
            settings.backgroundColors = sharedPreferences.getString(
                    sharedPreferencesBackgroundColors,
                    convertJsonArrayToString(defaultSettings.backgroundColors)).split(";");
            settings.availableShapes = sharedPreferences.getString(
                    sharedPreferencesAvailableShapes,
                    convertJsonArrayToString(defaultSettings.availableShapes)).split(";");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return settings;
    }

    public void saveAllSettings(Settings settings) {
        try {
            Context packageContext = context.createPackageContext(sharedPreferencesPackage, Context.MODE_PRIVATE);
            SharedPreferences sharedPreferences = packageContext.getSharedPreferences(sharedPreferencesSourceFile, Context.MODE_PRIVATE);
            addSettingsToSharedPreferences(sharedPreferences, settings);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Settings updateSettingsAvailableShapes(Settings settings) {
        List<String> newSettingsAvailableShapes = new ArrayList<>();
        String[] settingsAvailableShapes = settings.availableShapes;
        File[] availableFiles = FileHelper.getAllFilesFromAppFolder(context);

        for (String shape : settingsAvailableShapes) {
            boolean isAvailable = false;
            for (File file : availableFiles) {
                if (shape.equals(file.getName())) {
                    isAvailable = true;
                    break;
                }
            }

            if (isAvailable) {
                newSettingsAvailableShapes.add(shape);
            }
        }

        if (newSettingsAvailableShapes.size() == 0) {
            Settings defaultSettings = getSettingsFromJSON();
            settings.availableShapes = defaultSettings.availableShapes;
            for (String fileToCopy : settings.availableShapes) {
                FileHelper.copyFileAssets(fileToCopy, context);
            }
        } else {
            settings.availableShapes = newSettingsAvailableShapes.toArray(new String[0]);
        }

        saveAllSettings(settings);
        return settings;
    }

    private Settings getSettingsFromJSON() {
        Settings settings;
        String json = readSettingsFromAsset();
        settings = new Gson().fromJson(json, Settings.class);
        return settings;
    }

    private String getJSONFromSettings(Settings settings) {
        return new Gson().toJson(settings);
    }

    private boolean areSettingsAdded(SharedPreferences sharedPreferences) {
        if (!sharedPreferences.contains(sharedPreferencesDifficultyLevel)) {
            return false;
        }
        if (!sharedPreferences.contains(sharedPreferencesNumberOfLevels)) {
            return false;
        }
        if (!sharedPreferences.contains(sharedPreferencesNumberOfRepetitions)) {
            return false;
        }
        if (!sharedPreferences.contains(sharedPreferencesTimeLimit)) {
            return false;
        }
        if (!sharedPreferences.contains(sharedPreferencesMaterialColors)) {
            return false;
        }
        if (!sharedPreferences.contains(sharedPreferencesTraceColors)) {
            return false;
        }
        if (!sharedPreferences.contains(sharedPreferencesBackgroundColors)) {
            return false;
        }
        if (!sharedPreferences.contains(sharedPreferencesAvailableShapes)) {
            return false;
        }

        return true;
    }

    private void addSettingsToSharedPreferences(SharedPreferences sharedPreferences, Settings settings) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(sharedPreferencesDifficultyLevel, settings.difficultyLevel);
        editor.putInt(sharedPreferencesNumberOfLevels, settings.numberOfLevels);
        editor.putInt(sharedPreferencesNumberOfRepetitions, settings.numberOfRepetitions);
        editor.putInt(sharedPreferencesTimeLimit, settings.timeLimit);
        editor.putString(sharedPreferencesMaterialColors, convertJsonArrayToString(settings.materialColors));
        editor.putString(sharedPreferencesTraceColors, convertJsonArrayToString(settings.traceColors));
        editor.putString(sharedPreferencesBackgroundColors, convertJsonArrayToString(settings.backgroundColors));
        editor.putString(sharedPreferencesAvailableShapes, convertJsonArrayToString(settings.availableShapes));
        editor.apply();
    }

    private String readSettingsFromAsset() {
        String json = null;
        try {
            InputStream is = context.getAssets().open("settings/settings.json");
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

    private String convertJsonArrayToString(String[] elements) {
        StringBuilder result = new StringBuilder();
        for(String element : elements) {
            result.append(element).append(";");
        }
        return result.toString();
    }
}