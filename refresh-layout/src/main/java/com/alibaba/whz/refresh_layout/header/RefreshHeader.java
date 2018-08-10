package com.alibaba.whz.refresh_layout.header;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.whz.refresh_layout.R;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class RefreshHeader implements RefreshHeaderInterface {

    private Animation rotate_up;
    private Animation rotate_down;
    private Animation rotate_infinite;
    private TextView textView;
    private View arrowIcon;
    private ImageView successIcon;
    private View loadingIcon;
    private float mPercent;
    public RefreshHeader(){}



    @Override
    public void reset() {
        textView.setText(R.string.qq_header_reset);
        successIcon.setVisibility(INVISIBLE);
        arrowIcon.setVisibility(VISIBLE);
        arrowIcon.clearAnimation();
        loadingIcon.setVisibility(INVISIBLE);
        loadingIcon.clearAnimation();
        mPercent = 0;
    }

    @Override
    public void pull(float persent) {
        if(mPercent == 1.0f)
        {
            arrowIcon.clearAnimation();
            arrowIcon.startAnimation(rotate_down);
        }
        textView.setText(R.string.qq_header_pull);
        mPercent = persent;
    }

    public View sendHeaderView(ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_qq,parent,false);

        rotate_up = AnimationUtils.loadAnimation(view.getContext() , R.anim.rotate_up);
        rotate_down = AnimationUtils.loadAnimation(view.getContext() , R.anim.rotate_down);
        rotate_infinite = AnimationUtils.loadAnimation(view.getContext() , R.anim.rotate_infinite);

        textView =  view.findViewById(R.id.text);
        arrowIcon = view.findViewById(R.id.arrowIcon);
        successIcon = view.findViewById(R.id.successIcon);
        loadingIcon = view.findViewById(R.id.loadingIcon);

        return view;
    }

    @Override
    public void pullFull(){
        if(mPercent < 1.0f)
        {
            textView.setText(R.string.qq_header_pull_over);
            arrowIcon.clearAnimation();
            arrowIcon.startAnimation(rotate_up);
            mPercent = 1.0f;
        }
    }

    @Override
    public void refreshing() {
        arrowIcon.setVisibility(INVISIBLE);
        loadingIcon.setVisibility(VISIBLE);
        textView.setText(R.string.qq_header_refreshing);
        arrowIcon.clearAnimation();
        loadingIcon.startAnimation(rotate_infinite);
    }

    @Override
    public void showRefreshResult(boolean refreshResult){
        if(refreshResult){
            loadingIcon.setVisibility(INVISIBLE);
            loadingIcon.clearAnimation();
            successIcon.setVisibility(VISIBLE);
            successIcon.setImageResource(R.drawable.enm);
            textView.setText(R.string.qq_header_completed);
        }
        else{
            loadingIcon.setVisibility(INVISIBLE);
            loadingIcon.clearAnimation();
            successIcon.setImageResource(R.mipmap.enf);
            successIcon.setVisibility(VISIBLE);
            textView.setText(R.string.qq_header_fail);
        }
    }

    public float sendHeaderHeightDp(){
        return 120;
    }
    public float sendHeightDpWhenRefreshing(){
        return 60;
    }

}