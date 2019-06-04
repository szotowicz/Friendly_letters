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
                case "1" :
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_one));
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
                case "g": // does not work
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_g));
                    break;
                case "h": // does not work
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_h));
                    break;
                case "j": // does not work
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_j));
                    break;
                case "k": // does not work
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_k));
                    break;
                case "l" : // does not work
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_l));
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
                case "r": // does not work
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_r));
                    break;
                case "s" : // does not work
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_s));
                    break;
                case "ś": // does not work
                    charToRead = charToRead.replace(charToRead,
                            context.getResources().getString(R.string.text_reader_exception_ss));
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
                //TODO : update also for english version
            }
        }

        return textToRead.substring(0, textToRead.length() - 1) + charToRead;
    }
}