package com.pg.mikszo.friendlyletters.settings;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.pg.mikszo.friendlyletters.R;

public class ColorsManager {
    private Context context;

    public ColorsManager(Context context) {
        this.context = context;
    }

    public int getMaterialColor(String id) {
        //TODO:
        return ContextCompat.getColor(context, R.color.color_settings_material_1);
    }

    public int getTraceColor(String id) {
        //TODO:
        return ContextCompat.getColor(context, R.color.color_settings_trace_1);
    }

    public int getBackgroundColor(String id) {
        //TODO:
        return ContextCompat.getColor(context, R.color.color_settings_background_1);
    }
}