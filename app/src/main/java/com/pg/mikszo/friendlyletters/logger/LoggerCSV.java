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
package com.pg.mikszo.friendlyletters.logger;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

import com.pg.mikszo.friendlyletters.FileHelper;
import com.pg.mikszo.friendlyletters.settings.Configuration;
import com.pg.mikszo.friendlyletters.views.game.GameMaterial;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Locale;

public class LoggerCSV {

    private File csvFile;
    public enum loggerStatus {
        CHECK_TRUE, CHECK_FALSE,
        TIMEOUT_CHECK_TRUE, TIMEOUT_CHECK_FALSE,
        CLICK_NEXT, CLICK_PREVIOUS }

    public LoggerCSV(Context context) {
        final String baseDir = FileHelper.getAppFolderPath(context).toString();
        final String fileName = "AnalysisData.FriendlyLetters";
        final String filePath = baseDir + File.separator + fileName;
        csvFile = new File(filePath);
    }

    public void addNewRecord(loggerStatus status, GameMaterial material, Configuration settings,
                             int materialPixelsOnStart, int retainedMaterialPixels, int tracePixels,
                             int currentStep, int currentNumberOfRepetitions, long time) {
        try {
            Writer writer = new BufferedWriter(new FileWriter(csvFile, true));
            writer.write(getRecordContent(status, material, settings,
                    materialPixelsOnStart, retainedMaterialPixels, tracePixels,
                    currentStep, currentNumberOfRepetitions, time));
            ((BufferedWriter)writer).newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRecordContent(loggerStatus status, GameMaterial material, Configuration settings,
                                    int materialPixelsOnStart, int retainedMaterialPixels, int tracePixels,
                                    int currentStep, int currentNumberOfRepetitions, long time) {
        String currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.US).format(new Date());
        String currentHour = new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date());

        return status + ";" + currentDate + ";" + currentHour + ";" + settings.configurationName
                + ";" + settings.testMode + ";" + settings.commandsReading + ";" + settings.commandsDisplaying
                + ";" + settings.verbalPraisesReading + ";" + material.filename + ";" + settings.difficultyLevel
                + ";" + materialPixelsOnStart + ";" + retainedMaterialPixels + ";" + tracePixels
                + ";" + material.colorBackground + ";" + material.colorMaterial + ";" + material.colorTrace
                + ";" + currentStep + ";" + settings.numberOfSteps
                + ";" + currentNumberOfRepetitions + ";" + settings.numberOfRepetitions
                + ";" + time + ";" + settings.timeLimit;
    }
}