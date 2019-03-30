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

import com.google.gson.Gson;
import com.pg.mikszo.friendlyletters.FileHelper;
import com.pg.mikszo.friendlyletters.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsManager {

    private Context context;

    public SettingsManager(Context context) {
        this.context = context;

        if (!FileHelper.isAppFolderExists(context)) {
            FileHelper.copyDefaultImages(context);
            FileHelper.copyDefaultFileWithConfigurations(context);
        }
    }

    public Configuration getActiveConfiguration() {
        File fileWithConfigurations = FileHelper.getFileWithConfigurations(context);
        if (!fileWithConfigurations.exists() && !fileWithConfigurations.isFile()) {
            FileHelper.copyDefaultFileWithConfigurations(context);
        }

        Configuration[] allConfigurations = getConfigurationsFromJSON();
        if (allConfigurations.length == 0) {
            FileHelper.copyDefaultFileWithConfigurations(context);
            allConfigurations = getConfigurationsFromJSON();
        }

        if (isNoConfigurationActive(allConfigurations)) {
            allConfigurations[0].configurationActivated = true;
            updateFileWithConfigurations(allConfigurations);
            return allConfigurations[0];
        }

        for (Configuration configuration : allConfigurations) {
            if (configuration.configurationActivated) {
                return configuration;
            }
        }

        return null;
    }

    public Configuration[] getAllConfigurations() {
        File fileWithConfigurations = FileHelper.getFileWithConfigurations(context);
        if (!fileWithConfigurations.exists() && !fileWithConfigurations.isFile()) {
            FileHelper.copyDefaultFileWithConfigurations(context);
        }

        Configuration[] allConfigurations = getConfigurationsFromJSON();
        if (allConfigurations.length == 0) {
            FileHelper.copyDefaultFileWithConfigurations(context);
            allConfigurations = getConfigurationsFromJSON();
        }

        if (isNoConfigurationActive(allConfigurations)) {
            allConfigurations[0].configurationActivated = true;
            updateFileWithConfigurations(allConfigurations);
        }

        return allConfigurations;
    }

    public Configuration getConfigurationById(int configurationID) {
        return getAllConfigurations()[configurationID];
    }

    public Configuration getDefaultConfiguration() {
        Configuration defaultConfiguration = new Configuration();
        try {
            InputStream is = context.getAssets().open(
                    context.getString(R.string.file_with_configurations_dir_name)
                    + File.separator + context.getString(R.string.file_with_configurations_file_name));
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            defaultConfiguration = new Gson().fromJson(json, Configuration[].class)[0];
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return defaultConfiguration;
    }

    public void updateFileWithConfigurations(Configuration[] allConfigurations) {
        try {
            FileWriter fileWriter = new FileWriter(FileHelper.getFileWithConfigurations(context));
            new Gson().toJson(allConfigurations, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateFileWithConfigurations(Configuration configurationToUpdate, int configurationID) {
        Configuration[] allConfigurations = getConfigurationsFromJSON();
        allConfigurations[configurationID] = configurationToUpdate;
        updateFileWithConfigurations(allConfigurations);
    }

    public void addNewConfiguration(Configuration newConfiguration) {
        List<Configuration> allConfigurations = new ArrayList<>(Arrays.asList(getConfigurationsFromJSON()));
        allConfigurations.add(newConfiguration);
        updateFileWithConfigurations(allConfigurations.toArray(new Configuration[0]));
    }

    public void removeConfigurationById(int configurationID) {
        List<Configuration> allConfigurations = new ArrayList<>(Arrays.asList(getConfigurationsFromJSON()));
        allConfigurations.remove(configurationID);
        updateFileWithConfigurations(allConfigurations.toArray(new Configuration[0]));
    }

    private boolean isNoConfigurationActive(Configuration[] allConfigurations) {
        for (Configuration configuration : allConfigurations) {
            if (configuration.configurationActivated) {
                return false;
            }
        }
        return true;
    }

    private Configuration[] getConfigurationsFromJSON() {
        String json = readConfigurationsContent();
        return new Gson().fromJson(json, Configuration[].class);
    }

    private String readConfigurationsContent() {
        String json = null;
        try {
            InputStream is = new FileInputStream(FileHelper.getFileWithConfigurations(context));
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
}