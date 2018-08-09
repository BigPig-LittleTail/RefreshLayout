package com.example.whz.refreshlayout.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.alibaba.whz.refresh_layout.MyRefreshLayout;
import com.alibaba.whz.refresh_layout.TryRefreshLayout;
import com.example.whz.refreshlayout.R;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    private MyRefreshLayout refreshLayout;
    private ListView listView;
    private ArrayList test;
    Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        refreshLayout = findViewById(R.id.RefreshLayout);
        listView = findViewById(R.id.listView);

        test = new ArrayList<>();
        for(int i = 0;i<20;i++){
            test.add(i);
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(
                this, android.R.layout.simple_list_item_1, test);

        listView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(new MyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        refreshLayout.setRefreshing(false,true);
                    }
                },2000);

            }
        });

    }
}
