package com.pg.mikszo.friendlyletters.drawing.configurationApp;

import android.widget.Button;
import android.widget.ImageView;

public class Material {
    public ImageView material;
    public Button cloneOfBackground;

    public Material () {
    }

    public Material (ImageView material, Button cloneOfBackground) {
        this.material = material;
        this.cloneOfBackground = cloneOfBackground;
    }
}