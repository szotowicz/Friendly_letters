/*
    Part of the master's thesis
    Topic: "Supporting the development of fine motor skills in children using IT tools"

    2019 by Mikolaj Szotowicz : https://github.com/szotowicz
*/
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