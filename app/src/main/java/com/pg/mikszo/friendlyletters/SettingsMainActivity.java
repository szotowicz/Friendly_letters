package com.pg.mikszo.friendlyletters;

import android.os.Bundle;
import android.view.View;

import com.pg.mikszo.friendlyletters.drawing.AddingShapeView;

public class SettingsMainActivity extends BaseActivity {

    private AddingShapeView addingShapeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_main);
        addingShapeView = findViewById(R.id.adding_shape_view);

        if (!isAppFolderExists()) {
            copyDefaultImages();
        }
    }

    public void addShapeToResourcesOnClick(View view) {
        addingShapeView.saveScreenImage();
        addingShapeView.cleanScreen();
    }
}