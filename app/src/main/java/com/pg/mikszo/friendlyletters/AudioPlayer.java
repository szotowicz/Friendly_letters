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
    private final String[] availablePraises_brawo = {"brawo1.mp3", "brawo2.mp3", "brawo3.mp3"};
    private final String[] availablePraises_swietnie = {"swietnie1.mp3", "swietnie2.mp3", "swietnie3.mp3", "swietnie4.mp3"};
    private final String[] availablePraises_dobrze = {"dobrze1.mp3", "dobrze2.mp3", "dobrze3.mp3", "dobrze4.mp3"};
    private final String[] availablePraises_super = {"super1.mp3", "super2.mp3", "super3.mp3", "super4.mp3"};
    private final String[] availableEndgamePraises = {"koniec1.mp3", "koniec2.mp3", "koniec_zadania1.mp3", "koniec_zadania2.mp3"};

    public AudioPlayer(Context context) {
        this.context = context;
    }

    public boolean playEndOfGame() {
        if (!Locale.getDefault().getDisplayLanguage().equals("polski")) {
            return false;
        }

        int randomEndgamePraiseId = new Random().nextInt(availableEndgamePraises.length);
        playAssetSound("endgame/" + availableEndgamePraises[randomEndgamePraiseId]);
        return true;
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

        final String digitsDir = "digits/";
        final String lettersDir = "letters/";
        String[] commandFragments = command.split(" ");
        if (commandFragments.length != 2) {
            return false;
        }

        switch (commandFragments[0].trim().toLowerCase()) {
            case "napisz:":
                commandFragments[0] = "napisz.mp3";
                break;
            case "cyfra:":
                commandFragments[0] = "cyfra.mp3";
                break;
            case "litera:":
                commandFragments[0] = "litera.mp3";
                break;
            default:
                return false;
        }

        switch (commandFragments[1].trim().toLowerCase()) {
            case "0":
                commandFragments[1] = digitsDir + "zero.mp3";
                break;
            case "1":
                commandFragments[1] = digitsDir + "jeden.mp3";
                break;
            case "2":
                commandFragments[1] = digitsDir + "dwa.mp3";
                break;
            case "3":
                commandFragments[1] = digitsDir + "trzy.mp3";
                break;
            case "4":
                commandFragments[1] = digitsDir + "cztery.mp3";
                break;
            case "5":
                commandFragments[1] = digitsDir + "piec.mp3";
                break;
            case "6":
                commandFragments[1] = digitsDir + "szesc.mp3";
                break;
            case "7":
                commandFragments[1] = digitsDir + "siedem.mp3";
                break;
            case "8":
                commandFragments[1] = digitsDir + "osiem.mp3";
                break;
            case "9":
                commandFragments[1] = digitsDir + "dziewiec.mp3";
                break;

            case "a":
                commandFragments[1] = lettersDir + "a.mp3";
                break;
            case "ą":
                commandFragments[1] = lettersDir + "aa.mp3";
                break;
            case "b":
                commandFragments[1] = lettersDir + "b.mp3";
                break;
            case "c":
                commandFragments[1] = lettersDir + "c.mp3";
                break;
            case "ć":
                commandFragments[1] = lettersDir + "cc.mp3";
                break;
            case "d":
                commandFragments[1] = lettersDir + "d.mp3";
                break;
            case "e":
                commandFragments[1] = lettersDir + "e.mp3";
                break;
            case "ę":
                commandFragments[1] = lettersDir + "ee.mp3";
                break;
            case "f":
                commandFragments[1] = lettersDir + "f.mp3";
                break;
            case "g":
                commandFragments[1] = lettersDir + "g.mp3";
                break;
            case "h":
                commandFragments[1] = lettersDir + "h.mp3";
                break;
            case "i":
                commandFragments[1] = lettersDir + "i.mp3";
                break;
            case "j":
                commandFragments[1] = lettersDir + "j.mp3";
                break;
            case "k":
                commandFragments[1] = lettersDir + "k.mp3";
                break;
            case "l":
                commandFragments[1] = lettersDir + "l.mp3";
                break;
            case "ł":
                commandFragments[1] = lettersDir + "ll.mp3";
                break;
            case "m":
                commandFragments[1] = lettersDir + "m.mp3";
                break;
            case "n":
                commandFragments[1] = lettersDir + "n.mp3";
                break;
            case "ń":
                commandFragments[1] = lettersDir + "nn.mp3";
                break;
            case "o":
                commandFragments[1] = lettersDir + "o.mp3";
                break;
            case "ó":
                commandFragments[1] = lettersDir + "oo.mp3";
                break;
            case "p":
                commandFragments[1] = lettersDir + "p.mp3";
                break;
            case "r":
                commandFragments[1] = lettersDir + "r.mp3";
                break;
            case "s":
                commandFragments[1] = lettersDir + "s.mp3";
                break;
            case "ś":
                commandFragments[1] = lettersDir + "ss.mp3";
                break;
            case "t":
                commandFragments[1] = lettersDir + "t.mp3";
                break;
            case "u":
                commandFragments[1] = lettersDir + "u.mp3";
                break;
            case "w":
                commandFragments[1] = lettersDir + "w.mp3";
                break;
            case "y":
                commandFragments[1] = lettersDir + "y.mp3";
                break;
            case "z":
                commandFragments[1] = lettersDir + "z.mp3";
                break;
            case "ź":
                commandFragments[1] = lettersDir + "zz.mp3";
                break;
            case "ż":
                commandFragments[1] = lettersDir + "zzz.mp3";
                break;
            default:
                return false;
        }

        try {
            playAssetSound("commands/" + commandFragments[0]);
            Thread.sleep(800);
            playAssetSound("commands/" + commandFragments[1]);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
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
