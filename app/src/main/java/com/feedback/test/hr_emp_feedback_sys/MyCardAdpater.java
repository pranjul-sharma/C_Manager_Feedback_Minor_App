package com.feedback.test.hr_emp_feedback_sys;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.toolbox.StringRequest;

/**
 * Created by pranjul on 8/11/17.
 */

public class MyCardAdpater extends RecyclerView.Adapter<MyCardAdpater.MyViewHolder>{
    private String[] numbers;
    private String[] desc;
    private Context context;

    MyCardAdpater(Context context,String[] numbers,String[] desc){
        this.context = context;
        this.numbers = numbers;
        this.desc = desc;
    }
    @Override
    public MyCardAdpater.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view_dashboard,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyCardAdpater.MyViewHolder holder, final int position) {
        int[] drawbles = {R.drawable.project_bg,R.drawable.ratings_bg,R.drawable.employee,R.drawable.todays_feedback_bg};
        String mores[]={"Get My Project","Give Rating","Get User Info","Read Feedbacks"};
        Log.v("pos:",position+" ");
        if (position== 1){
            holder.tt_number.setText(String.valueOf(Float.parseFloat(numbers[position])*10)+" %");
        }
        else
        {
            holder.tt_number.setText(String.valueOf(Integer.parseInt(numbers[position])));
        }
        holder.tt_desc.setText(desc[position]);
        //holder.tt_more.setText(mores[position]);
        holder.llcard.setBackgroundResource(drawbles[position]);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                switch (position){
                    case 0:
                        intent = new Intent(context,FeedProjActivity.class);
                        intent.putExtra("EXTRA_TAG","view project");
                        break;
                    case 1:
                        intent = new Intent(context,RatingActivity.class);
                        break;
                    case 2:
                        Snackbar.make(view,"User Info will be updated shortly.",Snackbar.LENGTH_LONG).show();
                        break;
                    case 3:
                        intent = new Intent(context,FeedProjActivity.class);
                        intent.putExtra("EXTRA_TAG", "view feedback");
                        break;

                }
                if (intent!=null)
                    context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return numbers.length;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        LinearLayout ll,llcard;
        TextView tt_number,tt_desc,tt_more;
        public MyViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_card);
            llcard = itemView.findViewById(R.id.card_card_ll);
            //ll = itemView.findViewById(R.id.card_ll);
            tt_number = itemView.findViewById(R.id.tt_number_card);
            tt_desc = itemView.findViewById(R.id.tt_des_card);
           // tt_more = itemView.findViewById(R.id.place_tt);
        }
    }
}
