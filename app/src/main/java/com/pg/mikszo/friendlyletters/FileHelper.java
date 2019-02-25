package com.pg.mikszo.friendlyletters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileHelper {

    public static boolean isAppFolderExists(Context context) {
        File root = Environment.getExternalStorageDirectory();
        File appFolder = new File(root.getAbsolutePath() + File.separator +
                context.getString(R.string.resources_parent_dir_name) + File.separator +
                context.getString(R.string.resources_dir_name));
        return appFolder.exists();
    }

    public static int getNumberOfAllFilesInAppFolder(Context context) {
        File appFolder = getAppFolderPath(context);
        List<File> files = FileHelper.getMatchesFiles(appFolder.listFiles(), context);

        return files.size();
    }

    public static File[] getAllFilesFromAppFolder(Context context) {
        File appFolder = getAppFolderPath(context);
        List<File> files = FileHelper.getMatchesFiles(appFolder.listFiles(), context);

        return files.toArray(new File[0]);
    }

    public static File getAbsolutePathOfFile(String filename, Context context) {
        File appFolder = getAppFolderPath(context);
        // TODO: check if exist
        return new File(appFolder + File.separator + filename);
    }

    public static void copyDefaultImages(Context context) {
        File root = Environment.getExternalStorageDirectory();
        File appFolder = new File(root.getAbsolutePath() + File.separator +
                context.getString(R.string.resources_parent_dir_name) + File.separator +
                context.getString(R.string.resources_dir_name));

        if (!appFolder.exists()) {
            if (!appFolder.mkdirs()) {
                Toast.makeText(context, R.string.information_message_copying_failed, Toast.LENGTH_SHORT).show();
            }
        }

        try {
            AssetManager assetManager = context.getAssets();
            List<String> images = getMatchesFiles(assetManager.list(""), context);
            for (String img : images) {
                copyFileAssets(appFolder.getAbsolutePath(), img, context);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getMatchesFiles(String[] files, Context context) {
        Pattern pattern = Pattern.compile(context.getString(R.string.prefix_shape_file_name) + "(.*?)png");
        List<String> matches = new ArrayList<>();

        for (String file : files) {
            if (pattern.matcher(file).matches()) {
                matches.add(file);
            }
        }

        return matches;
    }

    public static List<File> getMatchesFiles(File[] files, Context context) {
        Pattern pattern = Pattern.compile(context.getString(R.string.prefix_shape_file_name) + "(.*?)png");
        List<File> matches = new ArrayList<>();

        for (File file : files) {
            if (pattern.matcher(file.getName()).matches()) {
                matches.add(file);
            }
        }

        return matches;
    }

    public static File getAppFolderPath(Context context) {
        File root = Environment.getExternalStorageDirectory();
        return new File(root.getAbsolutePath() + File.separator +
                context.getString(R.string.resources_parent_dir_name) + File.separator +
                context.getString(R.string.resources_dir_name));
    }

    private static void copyFileAssets(String appFolder, String filename, Context context) {
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            out = new FileOutputStream(appFolder + File.separator + filename);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch(IOException e) {
            Log.e("[ERROR]", "Failed to copy asset file: " + filename);
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}