package com.feedback.test.hr_emp_feedback_sys;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pranjul on 15/11/17.
 */

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.MyViewHolder> {
    Context context;
    String[] titles;
    String[] contents;
    String[] givenBys;
    public FeedbackAdapter(Context context, String[] titles, String[] contents, String[] givenBys){
        this.context = context;
        this.titles = titles;
        this.contents = contents;
        this.givenBys = givenBys;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(context).inflate(R.layout.card_feedback_view,parent,false);
        return new FeedbackAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.title.setText(titles[position]);
        holder.content.setText(contents[position]);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("given by :").append(givenBys[position]);
        holder.givenby.setText(stringBuilder.toString());
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title,content,givenby;
        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_title_feedback);
            content = itemView.findViewById(R.id.text_content_feedback);
            givenby = itemView.findViewById(R.id.given_feedback);
        }
    }
}
