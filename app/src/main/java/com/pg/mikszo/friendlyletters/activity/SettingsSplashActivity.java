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
package com.pg.mikszo.friendlyletters.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.pg.mikszo.friendlyletters.R;

public class SettingsSplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread welcomeThread = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(2000);
                } catch (Exception ignored) {

                } finally {
                    startActivity(new Intent(getBaseContext(), SettingsMainActivity.class));
                    finish();
                }
            }
        };
        welcomeThread.start();
    }
}