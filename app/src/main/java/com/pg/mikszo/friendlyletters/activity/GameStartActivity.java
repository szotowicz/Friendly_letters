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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pg.mikszo.friendlyletters.R;
import com.pg.mikszo.friendlyletters.settings.Configuration;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;

public class GameStartActivity extends BaseActivity {

    private Button startGameButton;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration configuration = new SettingsManager(this).getActiveConfiguration();
        setContentView(R.layout.activity_start_game);
        TextView message = findViewById(R.id.start_game_message);
        message.setText(getString(R.string.start_game_message) + ": " + configuration.configurationName);

        final View viewContainer = findViewById(R.id.start_game_container);
        startGameButton = findViewById(R.id.start_game_button);
        startGameButton.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        startGameButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        final int size = viewContainer.getHeight() / 2;
                        LinearLayout.LayoutParams updatedParams = new LinearLayout.LayoutParams(size, size);
                        startGameButton.setLayoutParams(updatedParams);
                    }
                });

        if (hasStoragePermission()) {
            startGameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    runGameStartActivity();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_USE_STORAGE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startGameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        runGameStartActivity();
                    }
                });
            } else {
                //TODO: test
                Toast.makeText(getBaseContext(),
                        getResources().getString(R.string.information_message_permit_must_be_granted),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void runGameStartActivity() {
        startActivity(new Intent(getBaseContext(), GameMainActivity.class));
        finish();
    }
}