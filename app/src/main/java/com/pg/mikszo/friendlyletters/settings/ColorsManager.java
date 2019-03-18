/*
    Part of the master's thesis
    Topic: "Supporting the development of fine motor skills in children using IT tools"

    2019 by Mikolaj Szotowicz : https://github.com/szotowicz
*/
package com.pg.mikszo.friendlyletters.settings;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.pg.mikszo.friendlyletters.R;

public class ColorsManager {
    private Context context;

    public ColorsManager(Context context) {
        this.context = context;
    }

    public int getMaterialColorById(String id) {
        switch (id) {
            case "0":
                return ContextCompat.getColor(context, R.color.color_settings_material_1);
            case "1":
                return ContextCompat.getColor(context, R.color.color_settings_material_2);
            case "2":
                return ContextCompat.getColor(context, R.color.color_settings_material_3);
            case "3":
                return ContextCompat.getColor(context, R.color.color_settings_material_4);
            case "4":
                return ContextCompat.getColor(context, R.color.color_settings_material_5);
            default:
                return ContextCompat.getColor(context, R.color.color_settings_material_1);
        }
    }

    public int getTraceColorById(String id) {
        switch (id) {
            case "0":
                return ContextCompat.getColor(context, R.color.color_settings_trace_1);
            case "1":
                return ContextCompat.getColor(context, R.color.color_settings_trace_2);
            case "2":
                return ContextCompat.getColor(context, R.color.color_settings_trace_3);
            case "3":
                return ContextCompat.getColor(context, R.color.color_settings_trace_4);
            case "4":
                return ContextCompat.getColor(context, R.color.color_settings_trace_5);
            default:
                return ContextCompat.getColor(context, R.color.color_settings_trace_1);
        }
    }

    public int getBackgroundColorById(String id) {
        switch (id) {
            case "0":
                return ContextCompat.getColor(context, R.color.color_settings_background_1);
            case "1":
                return ContextCompat.getColor(context, R.color.color_settings_background_2);
            case "2":
                return ContextCompat.getColor(context, R.color.color_settings_background_3);
            case "3":
                return ContextCompat.getColor(context, R.color.color_settings_background_4);
            default:
                return ContextCompat.getColor(context, R.color.color_settings_background_1);
        }
    }

    public int[] getAllMaterialColors() {
        return new int[]{
                ContextCompat.getColor(context, R.color.color_settings_material_1),
                ContextCompat.getColor(context, R.color.color_settings_material_2),
                ContextCompat.getColor(context, R.color.color_settings_material_3),
                ContextCompat.getColor(context, R.color.color_settings_material_4),
                ContextCompat.getColor(context, R.color.color_settings_material_5)};
    }

    public int[] getAllTraceColors() {
        return new int[]{
                ContextCompat.getColor(context, R.color.color_settings_trace_1),
                ContextCompat.getColor(context, R.color.color_settings_trace_2),
                ContextCompat.getColor(context, R.color.color_settings_trace_3),
                ContextCompat.getColor(context, R.color.color_settings_trace_4),
                ContextCompat.getColor(context, R.color.color_settings_trace_5)};
    }

    public int[] getAllBackgroundColors() {
        return new int[]{
                ContextCompat.getColor(context, R.color.color_settings_background_1),
                ContextCompat.getColor(context, R.color.color_settings_background_2),
                ContextCompat.getColor(context, R.color.color_settings_background_3),
                ContextCompat.getColor(context, R.color.color_settings_background_4)};
    }
}