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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pg.mikszo.friendlyletters.R;
import com.pg.mikszo.friendlyletters.settings.Configuration;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;

public class GameReportActivity extends BaseActivity {

    @Override
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    // This function is loaded in every BaseActivity child
    protected void loadOnCreateView() {
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
                + configuration.numberOfSteps);

        textView = findViewById(R.id.game_report_correctly_solved);
        textView.setText(getString(R.string.game_report_correctly_solved) + ": "
                + getIntent().getIntExtra(getString(R.string.intent_game_report_correctly_solved), 0));

        textView = findViewById(R.id.game_report_effectiveness);
        double effectiveness = getIntent().getIntExtra(getString(R.string.intent_game_report_correctly_solved), 0)
                * 100;
        textView.setText(getString(R.string.game_report_effectiveness) + ": "
                + String.format("%.2f", effectiveness / configuration.numberOfSteps) + "%");

        final ImageView background = findViewById(R.id.game_report_background);
        final LinearLayout container = findViewById(R.id.game_report_container);
        container.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int width = background.getWidth() / 2;
                        if (container.getWidth() > width) {
                            width = container.getWidth();
                        }

                        int height = (int)(background.getHeight() * 0.6);
                        if (container.getHeight() > height) {
                            height = container.getHeight();
                        }

                        LinearLayout.LayoutParams updatedParams = new LinearLayout.LayoutParams(width, height);
                        container.setLayoutParams(updatedParams);
                    }
                });
    }

    private void runGameStartActivity() {
        startActivity(new Intent(getBaseContext(), GameStartActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        runGameStartActivity();
    }

    public void closeReportOnClick(View view) {
        runGameStartActivity();
    }
}