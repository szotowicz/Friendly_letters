package com.pg.mikszo.friendlyletters;

import android.annotation.SuppressLint;
import android.app.Activity;
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

@SuppressLint("Registered")
public class BaseActivity extends Activity {
    protected boolean isAppFolderExists() {
        File root = Environment.getExternalStorageDirectory();
        File appFolder = new File(root.getAbsolutePath() + File.separator +
                getString(R.string.resources_parent_dir_name) + File.separator +
                getString(R.string.resources_dir_name));
        return appFolder.exists();
    }

    protected void copyDefaultImages() {
        File root = Environment.getExternalStorageDirectory();
        File appFolder = new File(root.getAbsolutePath() + File.separator +
                getString(R.string.resources_parent_dir_name) + File.separator +
                getString(R.string.resources_dir_name));

        if (!appFolder.exists()) {
            if (!appFolder.mkdirs()) {
                Toast.makeText(this, getString(R.string.error_message_copying_failed),
                        Toast.LENGTH_SHORT).show();
            }
        }

        try {
            AssetManager assetManager = getAssets();
            List<String> images = getMatchesAssetsFile(assetManager.list(""));
            for (String img : images) {
                copyFileAssets(appFolder.getAbsolutePath(), img);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getMatchesAssetsFile(String[] strings) {
        Pattern pattern = Pattern.compile(getString(R.string.prefix_shape_file_name) + "(.*?)png");
        List<String> matches = new ArrayList<>();

        for (String file : strings) {
            if (pattern.matcher(file).matches()) {
                matches.add(file);
            }
        }

        return matches;
    }

    private void copyFileAssets(String appFolder, String filename) {
        AssetManager assetManager = getAssets();
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

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}