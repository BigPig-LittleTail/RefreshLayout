package com.example.whz.refreshlayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class RefreshView extends View {

    private Paint mPaint;// 绘图画笔

    private float mPercent;

    public RefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth((float)3.0);
        mPaint.setStyle(Paint.Style.STROKE);
    }


    public void setPercent(float Percent){
        this.mPercent = Percent;
        invalidate();
    }


    @Override
    public void onDraw(Canvas canvas){
        float x = (getWidth() - getHeight() / 2) / 2;
        float y = getHeight() / 4;

        canvas.drawColor(Color.WHITE);

        RectF oval = new RectF( x, y,
                getWidth() - x, getHeight() - y);

        canvas.drawArc(oval,270,360*mPercent,false,mPaint);

    }

}
