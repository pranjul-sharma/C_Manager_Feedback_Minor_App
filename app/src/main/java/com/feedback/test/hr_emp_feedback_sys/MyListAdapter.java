package com.feedback.test.hr_emp_feedback_sys;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pranjul on 8/11/17.
 */

class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyViewHolder> {

    Context context;
    ArrayList<List<String>> feeds;
    public MyListAdapter(Context context,ArrayList<List<String>> feeds) {
        this.context = context;
        this.feeds = feeds;

    }



    static class MyViewHolder extends RecyclerView.ViewHolder{

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public MyListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyListAdapter.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
