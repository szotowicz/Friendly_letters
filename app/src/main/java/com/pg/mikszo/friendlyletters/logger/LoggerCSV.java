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
import com.pg.mikszo.friendlyletters.settings.Settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Locale;

public class LoggerCSV {

    private File csvFile;

    public LoggerCSV(Context context) {
        String baseDir = FileHelper.getAppFolderPath(context).toString();
        String fileName = "AnalysisData.friendlyletters";
        String filePath = baseDir + File.separator + fileName;
        csvFile = new File(filePath);
    }

    public void addNewRecord(String materialName, boolean passLevel, int materialPixelsOnStart,
                             int materialPixels, int tracePixels, int backgroundColorNumber,
                             int materialColorNumber, int traceColorNumber, int currentLevel,
                             int currentNumberOfRepetitions, long time, Settings settings) {
        try {
            Writer writer = new BufferedWriter(new FileWriter(csvFile, true));
            writer.write(getRecordContent(materialName, String.valueOf(passLevel), materialPixelsOnStart, materialPixels,
                    tracePixels, backgroundColorNumber, materialColorNumber, traceColorNumber,
                    currentLevel, currentNumberOfRepetitions, time, settings));
            ((BufferedWriter)writer).newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addNewRecord(String materialName, String message, int backgroundColorNumber,
                             int materialColorNumber, int traceColorNumber, int currentLevel,
                             int currentNumberOfRepetitions, long time, Settings settings) {
        try {
            Writer writer = new BufferedWriter(new FileWriter(csvFile, true));
            writer.write(getRecordContent(materialName, message, 0, 0,
                    0, backgroundColorNumber, materialColorNumber, traceColorNumber,
                    currentLevel, currentNumberOfRepetitions, time, settings));
            ((BufferedWriter)writer).newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRecordContent(String materialName, String passLevel, int materialPixelsOnStart,
                                    int materialPixels, int tracePixels, int backgroundColorNumber,
                                    int materialColorNumber, int traceColorNumber, int currentLevel,
                                    int currentNumberOfRepetitions, long time, Settings settings) {
        String currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.US).format(new Date());
        String currentHour = new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date());

        return currentDate + ";" + currentHour + ";" + materialName + ";" + settings.difficultyLevel
                + ";" + passLevel + ";" + materialPixelsOnStart + ";" + materialPixels + ";" + tracePixels
                + ";" + backgroundColorNumber + ";" + materialColorNumber + ";" + traceColorNumber
                + ";" + currentLevel + ";" + settings.numberOfLevels + ";" + currentNumberOfRepetitions
                + ";" + settings.numberOfRepetitions + ";" + time + ";" + settings.timeLimit;
    }
}