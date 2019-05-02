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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pg.mikszo.friendlyletters.R;
import com.pg.mikszo.friendlyletters.views.configurationApp.AddNewMaterial;
import com.pg.mikszo.friendlyletters.views.configurationApp.TabMenuAspect;
import com.pg.mikszo.friendlyletters.views.configurationApp.TabMenuLearning;
import com.pg.mikszo.friendlyletters.views.configurationApp.TabMenuMaterial;
import com.pg.mikszo.friendlyletters.views.configurationApp.TabMenuReinforcement;
import com.pg.mikszo.friendlyletters.settings.SettingsManager;

public class SettingsTabsActivity extends BaseActivity {

    private SettingsManager settingsManager;
    private enum availableTabs { aspect, material, learning, reinforcement, addMaterial }
    private availableTabs selectedTab;
    private int configurationID;

    @Override
    // This function is loaded in every BaseActivity child
    protected void loadOnCreateView() {
        settingsManager = new SettingsManager(this);
        this.configurationID = getIntent().getIntExtra(
                getResources().getString(R.string.intent_name_configuration_id), 0);
        new TabMenuAspect(this, settingsManager, configurationID);
    }

    @Override
    public void onBackPressed() {
        if (selectedTab == availableTabs.addMaterial) {
            selectedTab = availableTabs.material;
            new TabMenuMaterial(this, settingsManager, configurationID);
        } else {
            super.onBackPressed();
        }
    }

    public void tabAspectOnClick(View view) {
        if (selectedTab != availableTabs.aspect) {
            selectedTab = availableTabs.aspect;
            new TabMenuAspect(this, settingsManager, configurationID);
        }
    }

    public void tabMaterialOnClick(View view) {
        if (selectedTab != availableTabs.material) {
            selectedTab = availableTabs.material;
            new TabMenuMaterial(this, settingsManager, configurationID);
        }
    }

    public void tabLearningOnClick(View view) {
        if (selectedTab != availableTabs.learning) {
            selectedTab = availableTabs.learning;
            new TabMenuLearning(this, settingsManager, configurationID);
        }
    }

    public void tabReinforcementOnClick(View view) {
        if (selectedTab != availableTabs.reinforcement) {
            selectedTab = availableTabs.reinforcement;
            new TabMenuReinforcement(this, settingsManager, configurationID);
        }
    }

    public void tabAddNewMaterialOnClick(final View view) {
        selectedTab = availableTabs.addMaterial;
        setContentView(R.layout.activity_settings_add_material);

        final AddNewMaterial addingMaterialView = findViewById(R.id.addingShapeView);
        addingMaterialView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                addingMaterialView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                final int size = addingMaterialView.getHeight();
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
                layoutParams.gravity = Gravity.CENTER;
                addingMaterialView.setLayoutParams(layoutParams);

                TypedValue typedValue = new TypedValue();
                getResources().getValue(R.dimen.game_track_width_relative_to_size_of_field, typedValue, true);
                addingMaterialView.setStrokeWidth(size * typedValue.getFloat());
                addingMaterialView.setRadiusCursor(size * typedValue.getFloat());
            }
        });
    }

    public void returnToMainSettingsOnClick(View view) {
        startActivity(new Intent(getBaseContext(), SettingsMainActivity.class));
    }

    public void cleanNewMaterialOnClick(View view) {
        AddNewMaterial addingMaterialView = findViewById(R.id.addingShapeView);
        addingMaterialView.cleanScreen();
    }

    public void addMaterialToResourcesOnClick(View view) {
        final AddNewMaterial addNewMaterial = findViewById(R.id.addingShapeView);
        if (addNewMaterial.isDrawnSomething()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            builder.setTitle(R.string.check_creating_material_title);
            builder.setMessage(R.string.check_creating_material_message);

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton(R.string.check_decisions_positive_button_add, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String markForNewMaterial = input.getText().toString().trim();
                    if (markForNewMaterial.length() == 1) {
                        if (!Character.isLetterOrDigit(markForNewMaterial.charAt(0))) {
                            markForNewMaterial = "";
                        }
                        addNewMaterial.saveScreenImage(markForNewMaterial);
                        addNewMaterial.cleanScreen();
                        dialog.dismiss();
                    } else if (markForNewMaterial.length() == 0) {
                        addNewMaterial.saveScreenImage(markForNewMaterial);
                        addNewMaterial.cleanScreen();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getBaseContext(),
                                R.string.information_message_provided_incorrect_mark,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            builder.setNegativeButton(R.string.check_decisions_negative_button_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();
        } else {
            Toast.makeText(this, R.string.information_message_new_material_is_not_drawn, Toast.LENGTH_SHORT).show();
        }
    }

    public void showInformationAboutTestMode(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle(R.string.settings_tab_learning_learning_mode);
        builder.setMessage(R.string.information_message_test_mode_message);

        builder.setPositiveButton(R.string.information_message_positive_button_close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    public void showInformationAboutCommandsReading(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle(R.string.settings_tab_reinforcement_commands_reading);
        builder.setMessage(R.string.information_message_commands_reading_message);

        builder.setPositiveButton(R.string.information_message_positive_button_close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    public void showInformationAboutCommandsDisplaying(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle(R.string.settings_tab_reinforcement_commands_displaying);
        builder.setMessage(R.string.information_message_commands_displaying_message);

        builder.setPositiveButton(R.string.information_message_positive_button_close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    public void showInformationAboutVerbalPraisesReading(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle(R.string.settings_tab_reinforcement_verbal_praises_reading);
        builder.setMessage(R.string.information_message_verbal_praises_reading_message);

        builder.setPositiveButton(R.string.information_message_positive_button_close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }
}