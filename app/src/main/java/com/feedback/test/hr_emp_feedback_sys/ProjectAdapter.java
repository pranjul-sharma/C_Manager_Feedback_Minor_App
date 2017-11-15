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

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.MyViewHolder> {
    Context context;
    String[] titles;
    String[] contents;
    String[] leaders;
    List<List<String>> mList ;

    public ProjectAdapter(Context context,String[] titles,String[] contents,String[] leaders,List<List<String>> mList){
        this.context = context;
        this.titles = titles;
        this.contents = contents;
        this.leaders = leaders;
        this.mList = mList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(context).inflate(R.layout.card_project_view,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.title.setText(titles[position]);
        String desc = "Description :"+"\n"+contents[position];
        holder.content.setText(desc);
        String leader = "Team Leader : "+leaders[position];
        holder.leader.setText(leader);
        StringBuilder team_info = new StringBuilder();
        team_info.append("team info :").append("\n");
        for(String s:mList.get(position)){
            team_info.append(s).append("\n");
        }
        holder.team.setText(team_info.toString());
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title,content,leader,team;
        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_title_project);
            content = itemView.findViewById(R.id.text_content_project);
            leader = itemView.findViewById(R.id.text_leader_project);
            team = itemView.findViewById(R.id.text_team_project);
        }
    }
}
