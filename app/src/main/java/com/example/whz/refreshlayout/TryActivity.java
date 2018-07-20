package com.example.whz.refreshlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.Button;

public class TryActivity extends AppCompatActivity {
    private Button button;
    private MyView myView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.real_try);
        myView = findViewById(R.id.tryMyView);
        button = findViewById(R.id.button);


        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                myView.reDraw(1,interpolatedTime);
                Log.e("interpolatedTime","interpolatedTime"+interpolatedTime);
            }
        };
        animation.setDuration(3000);
        animation.setRepeatCount(Animation.INFINITE);
        myView.startAnimation(animation);

        if(myView.getDegree() == 1.0f) {
            myView.clearAnimation();
        }


    }
}
