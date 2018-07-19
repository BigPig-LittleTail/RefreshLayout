package com.example.whz.refreshlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TryActivity extends AppCompatActivity {

    private RefreshView refreshView;
    float persent = (float) 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try);
        refreshView = findViewById(R.id.refreshView);

    }
}
