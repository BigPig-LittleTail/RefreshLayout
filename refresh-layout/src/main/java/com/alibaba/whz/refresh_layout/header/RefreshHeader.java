package com.alibaba.whz.refresh_layout.header;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.whz.refresh_layout.R;
import com.alibaba.whz.refresh_layout.RefreshHeaderFollowInterface;

public class RefreshHeader extends FrameLayout implements RefreshHeaderFollowInterface {

    private Animation rotate_up;
    private Animation rotate_down;
    private Animation rotate_infinite;
    private TextView textView;
    private View arrowIcon;
    private ImageView successIcon;
    private View loadingIcon;

    public RefreshHeader(Context context) {
        this(context, null);
    }

    public RefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);

        rotate_up = AnimationUtils.loadAnimation(context , R.anim.rotate_up);
        rotate_down = AnimationUtils.loadAnimation(context , R.anim.rotate_down);
        rotate_infinite = AnimationUtils.loadAnimation(context , R.anim.rotate_infinite);

        inflate(context, R.layout.header_qq, this);

        textView =  findViewById(R.id.text);
        arrowIcon = findViewById(R.id.arrowIcon);
        successIcon = findViewById(R.id.successIcon);
        loadingIcon = findViewById(R.id.loadingIcon);
    }

    @Override
    public void reset() {
        textView.setText(getResources().getText(R.string.qq_header_reset));
        successIcon.setVisibility(INVISIBLE);
        arrowIcon.setVisibility(VISIBLE);
        arrowIcon.clearAnimation();
        loadingIcon.setVisibility(INVISIBLE);
        loadingIcon.clearAnimation();
    }

    @Override
    public void pull(float persent) {
        textView.setText(getResources().getText(R.string.qq_header_pull));
        arrowIcon.clearAnimation();
        arrowIcon.startAnimation(rotate_down);
    }

    @Override
    public void pullFull(){
        textView.setText(getResources().getText(R.string.qq_header_pull_over));
        arrowIcon.clearAnimation();
        arrowIcon.startAnimation(rotate_up);
    }

    @Override
    public void refreshing() {
        arrowIcon.setVisibility(INVISIBLE);
        loadingIcon.setVisibility(VISIBLE);
        textView.setText(getResources().getText(R.string.qq_header_refreshing));
        arrowIcon.clearAnimation();
        loadingIcon.startAnimation(rotate_infinite);
    }


    @Override
    public void complete() {
        loadingIcon.setVisibility(INVISIBLE);
        loadingIcon.clearAnimation();
        successIcon.setVisibility(VISIBLE);
        successIcon.setImageResource(R.drawable.enm);
        textView.setText(getResources().getText(R.string.qq_header_completed));
    }

    @Override
    public void fail(){
        loadingIcon.setVisibility(INVISIBLE);
        loadingIcon.clearAnimation();
        successIcon.setImageResource(R.mipmap.enf);
        successIcon.setVisibility(VISIBLE);
        textView.setText(getResources().getText(R.string.qq_header_fail));
    }
    @Override
    public boolean needPercent(){
        return false;
    }
}