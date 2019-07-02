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
package com.pg.mikszo.friendlyletters;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.util.Locale;
import java.util.Random;

public class AudioPlayer {

    private Context context;

    // "fanfare1.mp" made by FunWithSound from https://freesound.org/people/FunWithSound/sounds/456966/
    // "fanfare2.mp" made by ohforheavensake from https://freesound.org/people/ohforheavensake/sounds/423455/
    // "fanfare3.mp" made by Timbre from https://freesound.org/people/Timbre/sounds/110317/ (fragment)
    private final String[] availableFanfares = {"fanfare1.mp3", "fanfare2.mp3", "fanfare3.mp3"};

    // Made by dr inż. Agnieszka Landowska (nailie@pg.edu.pl)
    private final String[] availablePraises_brawo = {"brawo1.mp3", "brawo2.mp3"}; //TODO create and add
    private final String[] availablePraises_swietnie = {"swietnie1.mp3", "swietnie2.mp3", "swietnie3.mp3", "swietnie4.mp3"};
    private final String[] availablePraises_dobrze = {"dobrze1.mp3", "dobrze2.mp3", "dobrze3.mp3", "dobrze4.mp3"};
    private final String[] availablePraises_super = {"super1.mp3", "super2.mp3", "super3.mp3", "super4.mp3"};

    public AudioPlayer(Context context) {
        this.context = context;
    }

    public boolean playEndOfTest() {
        if (!Locale.getDefault().getDisplayLanguage().equals("polski")) {
            return false;
        }

        //TODO
        return false;
    }

    public void playRandomFanfare() {
        int randomFanfareId = new Random().nextInt(availableFanfares.length);
        playAssetSound("fanfares/" + availableFanfares[randomFanfareId]);
    }

    public boolean playRandomPraise(String praise) {
        if (!Locale.getDefault().getDisplayLanguage().equals("polski")) {
            return false;
        }

        String[] availablePraises;
        switch (praise.toLowerCase().trim()) {
            case "brawo":
                availablePraises = availablePraises_brawo;
                break;
            case "świetnie":
                availablePraises = availablePraises_swietnie;
                break;
            case "dobrze":
                availablePraises = availablePraises_dobrze;
                break;
            case "super":
                availablePraises = availablePraises_super;
                break;
            default:
                return false;
        }

        int randomFanfareId = new Random().nextInt(availablePraises.length);
        playAssetSound("praises/" + availablePraises[randomFanfareId]);
        return true;
    }

    public boolean playCommand(String command) {
        if (!Locale.getDefault().getDisplayLanguage().equals("polski")) {
            return false;
        }

        //TODO
        return false;
    }

    private void playAssetSound(String soundFileName) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();

            AssetFileDescriptor descriptor = context.getAssets().openFd("audio/" + soundFileName);
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            mediaPlayer.prepare();
            mediaPlayer.setVolume(1.0f, 1.0f);
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
