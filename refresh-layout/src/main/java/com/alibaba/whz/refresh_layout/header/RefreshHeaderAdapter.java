package com.alibaba.whz.refresh_layout.header;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.whz.refresh_layout.R;
import com.alibaba.whz.refresh_layout.RefreshHeaderInterface;

import static android.view.View.VISIBLE;

public class RefreshHeaderAdapter implements RefreshHeaderInterface{
    private Animation rotate_up;
    private Animation rotate_down;
    private Animation rotate_infinite;
    private TextView textView;
    private View arrowIcon;
    private ImageView successIcon;
    private View loadingIcon;

    public RefreshHeaderAdapter(){

    }

    @Override
    public void reset() {
        textView.setText(R.string.qq_header_reset);
        successIcon.setVisibility(View.INVISIBLE);
        arrowIcon.setVisibility(VISIBLE);
        arrowIcon.clearAnimation();
        loadingIcon.setVisibility(View.INVISIBLE);
        loadingIcon.clearAnimation();
    }

    @Override
    public void pull(float persent) {
        if(persent < 1){
            textView.setText(R.string.qq_header_pull);
            arrowIcon.clearAnimation();
            arrowIcon.startAnimation(rotate_down);
        }
        else{
            textView.setText(R.string.qq_header_pull_over);
            arrowIcon.clearAnimation();
            arrowIcon.startAnimation(rotate_up);
        }
    }

    @Override
    public void pullFull(){

    }

    @Override
    public void refreshing() {
        arrowIcon.setVisibility(View.INVISIBLE);
        loadingIcon.setVisibility(View.VISIBLE);
        textView.setText(R.string.qq_header_refreshing);
        arrowIcon.clearAnimation();
        loadingIcon.startAnimation(rotate_infinite);
    }


    @Override
    public void complete() {
        loadingIcon.setVisibility(View.INVISIBLE);
        loadingIcon.clearAnimation();
        successIcon.setVisibility(VISIBLE);
        successIcon.setImageResource(R.drawable.enm);
        textView.setText(R.string.qq_header_completed);
    }

    @Override
    public void fail(){
        loadingIcon.setVisibility(View.INVISIBLE);
        loadingIcon.clearAnimation();
        successIcon.setImageResource(R.mipmap.enf);
        successIcon.setVisibility(View.VISIBLE);
        textView.setText(R.string.qq_header_fail);
    }
    public View sendHeaderView(ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_qq,parent,false);

        rotate_up = AnimationUtils.loadAnimation(parent.getContext() , R.anim.rotate_up);
        rotate_down = AnimationUtils.loadAnimation(parent.getContext() , R.anim.rotate_down);
        rotate_infinite = AnimationUtils.loadAnimation(parent.getContext() , R.anim.rotate_infinite);

        textView =  view.findViewById(R.id.text);
        arrowIcon = view.findViewById(R.id.arrowIcon);
        successIcon = view.findViewById(R.id.successIcon);
        loadingIcon = view.findViewById(R.id.loadingIcon);

        return view;
    }
    public int sendHeaderHeight(){
        return 360;
    }
    public int sendHeightWhenRefreshing(){
        return 180;
    }

    public boolean sendWeatherNeedPercent(){
        return true;
    }
}
