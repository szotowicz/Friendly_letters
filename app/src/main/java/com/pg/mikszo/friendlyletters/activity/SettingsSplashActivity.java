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

import android.content.Intent;

public class SettingsSplashActivity extends BaseActivity {

    @Override
    // This function is loaded in every BaseActivity child
    protected void loadOnCreateView() {
        Thread welcomeThread = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(1200);
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