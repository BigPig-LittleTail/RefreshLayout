package com.example.whz.refreshlayout.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.whz.refreshlayout.R;
public class TryTwoActivity extends AppCompatActivity {

    private Button button;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_two);
        button = findViewById(R.id.tryButton);
        view = findViewById(R.id.tryTwo);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.scrollTo(0,-3);
                Log.e("get",""+view.getScrollY());
                view.scrollTo(0,0);
            }
        });
    }
}
