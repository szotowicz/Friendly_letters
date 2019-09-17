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
    private final boolean IS_LOGGER_ENABLED = false;

    public enum loggerStatus {
        CHECK_TRUE, CHECK_FALSE,
        TIMEOUT_CHECK_TRUE, TIMEOUT_CHECK_FALSE,
        CLICK_NEXT, CLICK_PREVIOUS,
        START_FROM_POINT, START_NOT_FROM_POINT
    }

    public LoggerCSV(Context context) {
        final String baseDir = FileHelper.getAppFolderPath(context).toString();
        final String fileName = "AnalysisData.FriendlyLetters";
        final String filePath = baseDir + File.separator + fileName;
        csvFile = new File(filePath);
    }

    public void addNewRecord(loggerStatus status, GameMaterial material, Configuration settings,
                             int materialPixelsOnStart, int retainedMaterialPixels, int tracePixels,
                             int currentStep, int currentRepetition, long time) {
        if (IS_LOGGER_ENABLED) {
            try {
                Writer writer = new BufferedWriter(new FileWriter(csvFile, true));
                writer.write(getRecordContent(status, material, settings,
                        materialPixelsOnStart, retainedMaterialPixels, tracePixels,
                        currentStep, currentRepetition, time));
                ((BufferedWriter)writer).newLine();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    PROPERTY (EXAMPLE):

        status (CHECK_TRUE) ; date (01.01.2019) ; hour (12:00) ; configuration name (Default Configuration) ;
        test mode (false) ; read commands enable (true) ; display command (true) ; read praises enable (true) ;
        material file name (FLshape_1_W43WH30H_1.png) ; difficulty level (0) ; material pixels on start (42550) ;
        retained material pixels (7080) ; trace pixels (39887) ; color background (0) ; color material (0) ;
        color trace (0) ; current step (1) ; number of steps (40) ; current repetition (1) ;
        number of repetitions (10) ; time [nanoseconds] (1719724145) ; time limit [seconds] (7)
     */
    private String getRecordContent(loggerStatus status, GameMaterial material, Configuration settings,
                                    int materialPixelsOnStart, int retainedMaterialPixels, int tracePixels,
                                    int currentStep, int currentRepetition, long time) {
        String currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.US).format(new Date());
        String currentHour = new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date());

        return status + ";" + currentDate + ";" + currentHour + ";" + settings.configurationName
                + ";" + settings.testMode + ";" + settings.commandsReading + ";" + settings.commandsDisplaying
                + ";" + settings.verbalPraisesReading + ";" + material.filename + ";" + settings.difficultyLevel
                + ";" + materialPixelsOnStart + ";" + retainedMaterialPixels + ";" + tracePixels
                + ";" + material.colorBackground + ";" + material.colorMaterial + ";" + material.colorTrace
                + ";" + currentStep + ";" + settings.numberOfSteps
                + ";" + currentRepetition + ";" + settings.numberOfRepetitions
                + ";" + time + ";" + settings.timeLimit;
    }
}