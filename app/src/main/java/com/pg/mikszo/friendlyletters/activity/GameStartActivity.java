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
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pg.mikszo.friendlyletters.R;
import com.pg.mikszo.friendlyletters.settings.Configuration;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;

public class GameStartActivity extends BaseActivity {

    private Button startGameButton;

    @Override
    @SuppressLint("SetTextI18n")
    // This function is loaded in every BaseActivity child
    protected void loadOnCreateView() {
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

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runGameStartActivity();
            }
        });
    }

    private void runGameStartActivity() {
        startActivity(new Intent(getBaseContext(), GameMainActivity.class));
        finish();
    }
}