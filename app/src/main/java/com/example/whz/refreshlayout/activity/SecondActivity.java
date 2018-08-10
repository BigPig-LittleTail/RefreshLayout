package com.example.whz.refreshlayout.activity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.alibaba.whz.refresh_layout.EasyRefreshLayout;
import com.alibaba.whz.refresh_layout.header.RefreshHeader;
import com.alibaba.whz.refresh_layout.header.progress.AbstractProgressView;
import com.alibaba.whz.refresh_layout.header.HeaderClass;
import com.example.whz.refreshlayout.R;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    private EasyRefreshLayout refreshLayout;
    private ListView listView;
    private ArrayList<Integer> test;
    Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        refreshLayout = findViewById(R.id.RefreshLayout);
        listView = findViewById(R.id.listView);

//        HeaderClass headerClass = new HeaderClass.Builder(refreshLayout.getContext())
//                .setmHeaderHeightDp(120)
//                .setmPaint(Color.BLUE,4.0f)
//                .setmWhenRefresingOneCircleTime(1000)
//                .setWheatherProgressSolid(true)
//                .setmProgressHeightDp(60)
//                .create();

        RefreshHeader headerClass = new RefreshHeader();

        refreshLayout.setHeader(headerClass);

        test = new ArrayList<>();
        for(int i = 0;i<20;i++){
            test.add(i);
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, test);

        listView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(new EasyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        refreshLayout.setRefreshing(false);
                    }
                },2000);

            }
        });

    }
}
