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

    public void read(String textToRead) {
        textToRead = updateTextToReadAboutExceptionsInTTS(textToRead);
        tts.speak(textToRead, TextToSpeech.QUEUE_ADD, null);
    }

    public void releaseTextReader() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    private String updateTextToReadAboutExceptionsInTTS(String textToRead) {
        textToRead = textToRead.replace("1",
                context.getResources().getString(R.string.text_reader_exception_one));

        //TODO : update

        return textToRead;
    }
}