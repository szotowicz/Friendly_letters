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
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.pg.mikszo.friendlyletters.R;
import com.pg.mikszo.friendlyletters.settings.Configuration;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;

public class GameReportActivity extends Activity {

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration configuration = new SettingsManager(this).getActiveConfiguration();
        setContentView(R.layout.activity_game_report);

        TextView textView = findViewById(R.id.game_report_difficulty_level);
        String[] difficultyLevelNames = {
                getString(R.string.settings_tab_learning_difficulty_level_easy),
                getString(R.string.settings_tab_learning_difficulty_level_medium),
                getString(R.string.settings_tab_learning_difficulty_level_hard)};
        textView.setText(getString(R.string.game_report_difficulty_level) + ": "
                + difficultyLevelNames[configuration.difficultyLevel]);

        textView = findViewById(R.id.game_report_number_of_level);
        textView.setText(getString(R.string.game_report_number_of_level) + ": "
                + configuration.numberOfLevels);

        textView = findViewById(R.id.game_report_correctly_solved);
        textView.setText(getString(R.string.game_report_correctly_solved) + ": "
                + getIntent().getIntExtra(getString(R.string.intent_game_report_correctly_solved), 0));

        textView = findViewById(R.id.game_report_effectiveness);
        double effectiveness = getIntent().getIntExtra(getString(R.string.intent_game_report_correctly_solved), 0)
                        * 100;
        textView.setText(getString(R.string.game_report_effectiveness) + ": "
                + String.format("%.2f", effectiveness / configuration.numberOfLevels) + "%");
    }

    public void closeReportOnClick(View view) {
        startActivity(new Intent(getBaseContext(), GameStartActivity.class));
        finish();
    }
}