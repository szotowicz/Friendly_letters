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
import android.widget.Toast;

import com.pg.mikszo.friendlyletters.R;
import com.pg.mikszo.friendlyletters.TextReader;
import com.pg.mikszo.friendlyletters.settings.Configuration;
import com.pg.mikszo.friendlyletters.settings.ReinforcementManager;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameStartActivity extends BaseActivity {

    private Button startGameButton;
    private Configuration configuration;
    private TextReader textReader;

    @Override
    @SuppressLint("SetTextI18n")
    // This function is loaded in every BaseActivity child
    protected void loadOnCreateView() {
        this.configuration = new SettingsManager(this).getActiveConfiguration();
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

        this.textReader = new TextReader(this);
        final String materialNameOnStart = generateMaterialNameOnStart();
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (materialNameOnStart.trim().length() > 1) {
                    String commandToRead = "";
                    if (configuration.commandsReading && configuration.availableCommands.length > 0 && !configuration.testMode) {
                        commandToRead = generateCommandOnStart(materialNameOnStart);
                        if (materialNameOnStart.trim().length() > 1) {
                            textReader.read(commandToRead);
                        }
                    }
                    runGameStartActivity(materialNameOnStart, commandToRead);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        textReader.releaseTextReader();
        super.onDestroy();
    }

    private void runGameStartActivity(String materialName, String command) {
        Intent intent = new Intent(getBaseContext(), GameMainActivity.class);
        intent.putExtra(getString(R.string.intent_material_name_on_start_game), materialName);
        intent.putExtra(getString(R.string.intent_command_on_start_game), command);
        startActivity(intent);

        Thread closeActivityAfterReadingThread = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(4000);
                } catch (Exception ignored) {

                } finally {
                    finish();
                }
            }
        };
        closeActivityAfterReadingThread.start();
    }

    private String generateMaterialNameOnStart() {
        String[] availableShapes = configuration.availableShapes;
        if (availableShapes.length == 0) {
            Toast.makeText(this, R.string.information_message_lack_of_materials,
                    Toast.LENGTH_SHORT).show();
            return "";
        } else {
            int randomAvailableBackground = new Random().nextInt(availableShapes.length);
            return availableShapes[randomAvailableBackground];
        }
    }

    private String generateCommandOnStart(String materialName) {
        String[] availableCommands = new ReinforcementManager(this).getAvailableCommands();
        int randomCommandIndex = Integer.parseInt(
                configuration.availableCommands[new Random().nextInt(
                        configuration.availableCommands.length)]);
        String randomCommand = availableCommands[randomCommandIndex];

        String command = "";
        Pattern pattern = Pattern.compile("(_)(.?)(.png)");
        Matcher matcher = pattern.matcher(materialName);
        if (matcher.find()) {
            String mark = matcher.group(2);

            if (mark.length() == 1) {
                char markCharacter = mark.charAt(0);

                if (Character.isLetterOrDigit(markCharacter)) {
                    if (Character.isDigit(markCharacter)) {
                        command = randomCommand.replace(getResources()
                                .getString(R.string.settings_tab_reinforcement_command_2_letter), "");
                    } else if (Character.isLetter(markCharacter)) {
                        command = randomCommand.replace(getResources()
                                .getString(R.string.settings_tab_reinforcement_command_2_digit), "");
                    }
                    command = command.replace("/", "");
                    command = command.replace(getResources()
                            .getString(R.string.settings_tab_reinforcement_command_mark_tag), mark);
                }
            }
        }
        return command;
    }
}