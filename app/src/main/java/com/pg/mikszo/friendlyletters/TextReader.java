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
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TextReader {

    private TextToSpeech tts;
    private Context context;

    public TextReader(Context context) {
        this.context = context;
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale currentLanguage = Locale.ENGLISH;
                    if (Locale.getDefault().getDisplayLanguage().equals("polski")) {
                        currentLanguage =  new Locale("pl_PL");
                    }

                    int result = tts.setLanguage(currentLanguage);
                    if (result == TextToSpeech.LANG_NOT_SUPPORTED ||
                            result == TextToSpeech.LANG_MISSING_DATA) {
                        Log.e("[ERROR - TTS]", "Language is not supported");
                    } else {
                        tts.setPitch(0.8f);
                        tts.setSpeechRate(0.9f);
                    }
                } else {
                    Log.e("[ERROR - TTS]", "Initialization failed");
                }
            }
        });
    }

    public void readCommand(String textToRead) {
        textToRead = updateTextToReadAboutExceptionsInTTS(textToRead.toLowerCase());
        tts.speak(textToRead, TextToSpeech.QUEUE_ADD, null);
    }

    public void readPraise(String textToRead) {
        tts.speak(textToRead, TextToSpeech.QUEUE_ADD, null);
    }

    public void releaseTextReader() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    private String updateTextToReadAboutExceptionsInTTS(String textToRead) {
        String charToRead = textToRead.substring(textToRead.length() - 1);
        if (charToRead.length() == 1) {
            switch (charToRead) {
                case "0" :
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_zero));
                    break;
                case "1" :
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_one));
                break;
                case "2" :
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_two));
                break;
                case "3" :
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_three));
                break;
                case "4" :
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_four));
                break;
                case "5" :
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_five));
                break;
                case "6" :
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_six));
                break;
                case "7" :
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_seven));
                break;
                case "8" :
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_eight));
                break;
                case "9" :
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_nine));
                break;
                case "b":
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_b));
                    break;
                case "c":
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_c));
                    break;
                case "ć":
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_cc));
                    break;
                case "d":
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_d));
                    break;
                case "f" :
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_f));
                    break;
                case "ł":
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_ll));
                    break;
                case "m":
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_m));
                    break;
                case "n":
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_n));
                    break;
                case "ń":
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_nn));
                    break;
                case "p":
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_p));
                    break;
                case "t":
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_t));
                    break;
                case "z":
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_z));
                    break;
                case "ź":
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_zz));
                    break;
                case "ż":
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_zzz));
                    break;
                // FIXME : update exceptions also for english version
            }
        }

        return textToRead.substring(0, textToRead.length() - 1) + charToRead;
    }
}