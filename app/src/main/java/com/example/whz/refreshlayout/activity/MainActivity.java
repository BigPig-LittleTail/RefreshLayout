package com.example.whz.refreshlayout.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.whz.refresh_layout.RefreshLayout;
import com.alibaba.whz.refresh_layout.header.RefreshHeader;
import com.example.whz.refreshlayout.R;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RefreshLayout refreshLayout;

    private RecyclerView recyclerView;

    Handler mHandler = new Handler();

    ArrayList<Integer> test;
    static int k = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refreshLayout = findViewById(R.id.testRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);

        refreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        if((k & 0x1) == 0) {
                            test = new ArrayList<>();
                            for(int i = 100;i>90;i--){
                                test.add(i);
                            }
                        }
                        else {
                            test = new ArrayList<>();
                            for(int i = 0;i<10;i++){
                                test.add(i);
                            }
                        }
                        k++;
                        testAdapter m = new testAdapter(test);
                        recyclerView.setAdapter(m);

                        refreshLayout.setRefreshing(false,true);
                    }
                },6000);


            }
        });


        ArrayList<Integer> test = new ArrayList<>();
        for(int i = 0;i<10;i++){
            test.add(i);
        }

        testAdapter m = new testAdapter(test);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(m);
    }

    class testAdapter extends RecyclerView.Adapter<testAdapter.ViewHolder>{
        ArrayList<Integer> m;


        class ViewHolder extends RecyclerView.ViewHolder{
            TextView textView;
            private ViewHolder(View view){
                super(view);
                textView = view.findViewById(R.id.testText);

            }
        }

        private testAdapter(ArrayList<Integer> m){
            this.m = m;
        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
            ViewHolder holder = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(parent.getContext(),SecondActivity.class);
                    startActivity(intent);
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder,int position){
            holder.textView.setText(String.format(Locale.CHINA,"%d",m.get(position)));
        }

        @Override
        public int getItemCount(){
            return m.size();
        }
    }


}
